package eu.kastroguru.astrodiary.data.backup

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * The contents of an Astro Key export file: every natal chart and every event, as whole rows.
 *
 * Whole rows, computed positions and cusps included, rather than only the birth data: several
 * screens read the stored cusps, which were calculated in the house system that was active when the
 * chart was saved, so recalculating on import would quietly change charts on a phone set to another
 * system. Inside the file, ids are the exporting phone's and serve only to link an event to its
 * person; an event's `imagePath` names its photo's entry in the archive.
 */
@JsonClass(generateAdapter = true)
data class AstroKeyBackup(
    val format: String,
    val version: Int,
    val app: String = "",
    val exportedAt: Long = 0L,
    val charts: List<BirthDataEntity> = emptyList(),
    val events: List<HistoryEventEntity> = emptyList(),
)

/** Read before the rest, so a file from a newer app is recognised even if the rows no longer parse. */
@JsonClass(generateAdapter = true)
internal data class AstroKeyHeader(val format: String? = null, val version: Int? = null)

class NotAnAstroKeyFile(cause: Throwable? = null) : IOException("not an Astro Key file", cause)

class NewerAstroKeyFile(val version: Int) :
    IOException("Astro Key file version $version, this app reads up to ${AstroKeyArchive.VERSION}")

/**
 * The `.astrokey` file: a zip holding `astrokey.json` first, then the event photos under `images/`.
 * A zip so the photos travel as they are instead of inflated by base64, and so the data stays
 * readable — unzip it and the JSON is plain, indented text.
 *
 * Pure JVM code; the Android side (files, content URIs, the database) is in [BackupRepository].
 */
object AstroKeyArchive {
    const val FORMAT = "astrokey"
    const val VERSION = 1
    const val EXTENSION = "astrokey"
    /** A custom extension has no registered type, and a zip type makes some pickers append ".zip". */
    const val MIME = "application/octet-stream"

    private const val DATA_ENTRY = "astrokey.json"
    private const val IMAGE_DIR = "images/"

    private val moshi = Moshi.Builder().build()
    private val backupAdapter = moshi.adapter(AstroKeyBackup::class.java)
    private val headerAdapter = moshi.adapter(AstroKeyHeader::class.java)

    fun imageEntryFor(eventId: Long) = "$IMAGE_DIR$eventId.jpg"

    /**
     * Writes [backup] and then every photo its events name, opened through [openImage] (null = the
     * photo is gone, and it is left out). Returns the number of photos written. Closes [out].
     */
    fun write(out: OutputStream, backup: AstroKeyBackup, openImage: (String) -> InputStream?): Int {
        var photos = 0
        ZipOutputStream(out.buffered()).use { zip ->
            zip.putNextEntry(ZipEntry(DATA_ENTRY))
            zip.write(backupAdapter.indent("  ").toJson(backup).toByteArray(Charsets.UTF_8))
            zip.closeEntry()
            // The photos are JPEGs already; deflating them again costs time and saves nothing.
            zip.setLevel(Deflater.BEST_SPEED)
            for (name in backup.events.mapNotNull { it.imagePath }.distinct()) {
                openImage(name)?.use { input ->
                    zip.putNextEntry(ZipEntry(name))
                    input.copyTo(zip)
                    zip.closeEntry()
                    photos++
                }
            }
        }
        return photos
    }

    /** Reads only the data and stops, so the confirmation before an import does not wait on photos. */
    fun readData(input: InputStream): AstroKeyBackup {
        try {
            val zip = ZipInputStream(input.buffered())
            while (true) {
                val entry = zip.nextEntry ?: break
                if (entry.name == DATA_ENTRY) return decode(zip.readBytes())
            }
        } catch (e: ZipException) {
            throw NotAnAstroKeyFile(e)
        }
        throw NotAnAstroKeyFile()
    }

    /**
     * Hands every photo entry in [wanted] to [save], in archive order. [save] must not close the
     * stream it is given; closing it is harmless here, but it is the archive's stream.
     */
    fun readImages(input: InputStream, wanted: Set<String>, save: (String, InputStream) -> Unit) {
        try {
            val zip = ZipInputStream(input.buffered())
            val shielded = object : FilterInputStream(zip) { override fun close() {} }
            while (true) {
                val entry = zip.nextEntry ?: break
                if (!entry.isDirectory && entry.name in wanted) save(entry.name, shielded)
            }
        } catch (e: ZipException) {
            throw NotAnAstroKeyFile(e)
        }
    }

    private fun decode(bytes: ByteArray): AstroKeyBackup {
        val json = String(bytes, Charsets.UTF_8)
        val header = runCatching { headerAdapter.fromJson(json) }.getOrNull() ?: throw NotAnAstroKeyFile()
        val version = header.version
        if (header.format != FORMAT || version == null) throw NotAnAstroKeyFile()
        if (version > VERSION) throw NewerAstroKeyFile(version)
        return try {
            backupAdapter.fromJson(json) ?: throw NotAnAstroKeyFile()
        } catch (e: IOException) {
            throw e as? NotAnAstroKeyFile ?: NotAnAstroKeyFile(e)
        } catch (e: RuntimeException) {
            throw NotAnAstroKeyFile(e)
        }
    }
}

/** What an import will add. Nothing on the phone is ever changed or deleted by it. */
class ImportPlan(
    /** Charts to insert, still carrying their file ids. */
    val newCharts: List<BirthDataEntity>,
    /** File chart id → phone chart id, for the charts the phone already has. */
    val existingChartFor: Map<Long, Long>,
    /** Events to insert; `personId` is still a file id and `imagePath` an archive entry. */
    val newEvents: List<HistoryEventEntity>,
    val skippedCharts: Int,
    val skippedEvents: Int,
) {
    val wantedImages: Set<String> get() = newEvents.mapNotNull { it.imagePath }.toSet()
}

object AstroKeyImport {

    /**
     * Adds what the phone does not have yet. Matching is by identity (name, moment, place, and for
     * an event its description) and it counts copies: importing the same file twice adds nothing
     * the second time, yet two identical charts in a file still arrive as two on an empty phone.
     */
    fun plan(
        backup: AstroKeyBackup,
        phoneCharts: List<BirthDataEntity>,
        phoneEvents: List<HistoryEventEntity>,
    ): ImportPlan {
        val chartsOnPhone = phoneCharts.groupByTo(HashMap(), ::chartKey) { it.id }
        val newCharts = ArrayList<BirthDataEntity>()
        val existingChartFor = HashMap<Long, Long>()
        for (c in backup.charts) {
            val match = chartsOnPhone[chartKey(c)]?.removeFirstOrNull()
            if (match != null) existingChartFor[c.id] = match else newCharts += c
        }

        val eventsOnPhone = phoneEvents.groupingBy(::eventKey).eachCountTo(HashMap())
        val newEvents = ArrayList<HistoryEventEntity>()
        for (e in backup.events) {
            val key = eventKey(e)
            val left = eventsOnPhone[key] ?: 0
            if (left > 0) eventsOnPhone[key] = left - 1 else newEvents += e
        }

        return ImportPlan(
            newCharts = newCharts,
            existingChartFor = existingChartFor,
            newEvents = newEvents,
            skippedCharts = backup.charts.size - newCharts.size,
            skippedEvents = backup.events.size - newEvents.size,
        )
    }

    private fun place(lat: Double, lon: Double) = "${Math.round(lat * 1e4)},${Math.round(lon * 1e4)}"

    internal fun chartKey(c: BirthDataEntity) = listOf(
        c.name.trim(), c.year, c.month, c.day, c.hour, c.minutes, c.timezone, place(c.latitude, c.longitude)
    ).joinToString("|")

    internal fun eventKey(e: HistoryEventEntity) = listOf(
        e.name.trim(), e.year, e.month, e.day, e.hour, e.minutes, e.timezone, place(e.latitude, e.longitude),
        e.description.trim()
    ).joinToString("|")
}
