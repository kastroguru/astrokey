package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.data.backup.AstroKeyArchive
import eu.kastroguru.astrodiary.data.backup.AstroKeyBackup
import eu.kastroguru.astrodiary.data.backup.AstroKeyImport
import eu.kastroguru.astrodiary.data.backup.NewerAstroKeyFile
import eu.kastroguru.astrodiary.data.backup.NotAnAstroKeyFile
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/** The .astrokey export file: what goes in comes back, foreign files are refused, imports only add. */
class AstroKeyArchiveTest {

    /** A row with every required column filled; the computed columns get distinct, checkable values. */
    private fun <T : Any> row(type: KClass<T>, vararg fields: Pair<String, Any?>): T {
        val given = fields.toMap()
        val ctor = type.primaryConstructor!!
        var n = 0
        val args = ctor.parameters.mapNotNull { p ->
            when {
                p.name in given -> p to given[p.name]
                p.isOptional -> null
                p.type.classifier == Int::class -> p to (++n % 12) + 1
                p.type.classifier == Double::class -> p to (++n) * 1.25
                p.type.classifier == String::class -> p to "Sofia"
                else -> error("unhandled column ${p.name}")
            }
        }.toMap()
        return ctor.callBy(args)
    }

    private fun chart(id: Long, name: String, hour: Int = 12) =
        row(BirthDataEntity::class, "id" to id, "name" to name, "hour" to hour,
            "timezone" to "Europe/Sofia", "latitude" to 42.6977, "longitude" to 23.3219, "createdAt" to 1000L + id)

    private fun event(id: Long, name: String, personId: Long? = null, imagePath: String? = null) =
        row(HistoryEventEntity::class, "id" to id, "name" to name, "description" to "note $name",
            "tags" to "work, home", "isGlobal" to (id % 2 == 0L), "personId" to personId, "imagePath" to imagePath,
            "timezone" to "Europe/Sofia", "latitude" to 43.2141, "longitude" to 27.9147, "createdAt" to 2000L + id)

    private fun backup(charts: List<BirthDataEntity>, events: List<HistoryEventEntity>) = AstroKeyBackup(
        format = AstroKeyArchive.FORMAT, version = AstroKeyArchive.VERSION, app = "1.7 (10)",
        exportedAt = 1_759_651_200_000L, charts = charts, events = events,
    )

    private fun zipOf(vararg entries: Pair<String, ByteArray>): ByteArray {
        val bytes = ByteArrayOutputStream()
        ZipOutputStream(bytes).use { zip ->
            for ((name, data) in entries) {
                zip.putNextEntry(ZipEntry(name)); zip.write(data); zip.closeEntry()
            }
        }
        return bytes.toByteArray()
    }

    private fun refused(bytes: ByteArray): Exception =
        try {
            AstroKeyArchive.readData(ByteArrayInputStream(bytes))
            fail("expected the file to be refused"); error("unreachable")
        } catch (e: Exception) {
            e
        }

    @Test
    fun everyRowAndPhotoComesBackAsItWent() {
        val photoEntry = AstroKeyArchive.imageEntryFor(7)
        val photo = ByteArray(5000) { (it * 31 % 251).toByte() }
        val original = backup(
            charts = listOf(chart(3, "Мария"), chart(4, "Иван")),
            events = listOf(event(7, "Сватба", personId = 3, imagePath = photoEntry), event(8, "Ден")),
        )

        val file = ByteArrayOutputStream()
        val written = AstroKeyArchive.write(file, original) { name ->
            if (name == photoEntry) ByteArrayInputStream(photo) else null
        }
        assertEquals(1, written)

        val back = AstroKeyArchive.readData(ByteArrayInputStream(file.toByteArray()))
        assertEquals(original, back)

        val photos = HashMap<String, ByteArray>()
        AstroKeyArchive.readImages(ByteArrayInputStream(file.toByteArray()), setOf(photoEntry)) { name, input ->
            photos[name] = input.readBytes()
        }
        assertEquals(setOf(photoEntry), photos.keys)
        assertArrayEquals(photo, photos[photoEntry])
    }

    @Test
    fun anythingThatIsNotAnAstroKeyFileIsRefused() {
        assertTrue(refused("just some text".toByteArray()) is NotAnAstroKeyFile)
        assertTrue(refused(zipOf("other.json" to "{}".toByteArray())) is NotAnAstroKeyFile)
        assertTrue(refused(zipOf("astrokey.json" to """{"format":"other","version":1}""".toByteArray())) is NotAnAstroKeyFile)
        assertTrue(refused(zipOf("astrokey.json" to "not json at all".toByteArray())) is NotAnAstroKeyFile)
        assertTrue(refused(zipOf("astrokey.json" to """{"format":"astrokey","version":1,"charts":[{"name":"x"}]}""".toByteArray())) is NotAnAstroKeyFile)
    }

    @Test
    fun aFileFromANewerAppSaysSoEvenWhenItsRowsNoLongerParse() {
        val newer = """{"format":"astrokey","version":${AstroKeyArchive.VERSION + 1},"charts":"changed shape"}"""
        val e = refused(zipOf("astrokey.json" to newer.toByteArray()))
        assertTrue("got $e", e is NewerAstroKeyFile)
    }

    @Test
    fun onAnEmptyPhoneEverythingArrivesIdenticalCopiesIncluded() {
        val file = backup(
            charts = listOf(chart(1, "Мария"), chart(2, "Мария")),   // two identical charts, kept as two
            events = listOf(event(5, "Сватба", personId = 1), event(6, "Сватба", personId = 1)),
        )
        val plan = AstroKeyImport.plan(file, emptyList(), emptyList())
        assertEquals(listOf(1L, 2L), plan.newCharts.map { it.id })
        assertEquals(listOf(5L, 6L), plan.newEvents.map { it.id })
        assertEquals(0, plan.skippedCharts)
        assertEquals(0, plan.skippedEvents)
    }

    @Test
    fun importingTheSameFileAgainAddsNothing() {
        val file = backup(
            charts = listOf(chart(1, "Мария"), chart(2, "Иван")),
            events = listOf(event(5, "Сватба", personId = 1)),
        )
        // The phone after the first import: same rows, the phone's own ids.
        val phoneCharts = file.charts.map { it.copy(id = it.id + 100) }
        val phoneEvents = file.events.map { it.copy(id = it.id + 100, personId = 101) }

        val plan = AstroKeyImport.plan(file, phoneCharts, phoneEvents)
        assertTrue(plan.newCharts.isEmpty())
        assertTrue(plan.newEvents.isEmpty())
        assertEquals(2, plan.skippedCharts)
        assertEquals(1, plan.skippedEvents)
        assertEquals(mapOf(1L to 101L, 2L to 102L), plan.existingChartFor)
    }

    @Test
    fun onlyWhatIsMissingIsAddedAndItsPersonIsTheOneAlreadyOnThePhone() {
        val file = backup(
            charts = listOf(chart(1, "Мария"), chart(2, "Иван")),
            events = listOf(event(5, "Сватба", personId = 1, imagePath = AstroKeyArchive.imageEntryFor(5))),
        )
        val phoneCharts = listOf(chart(40, "Мария"), chart(41, "Мария", hour = 3))   // same name, another birth

        val plan = AstroKeyImport.plan(file, phoneCharts, emptyList())
        assertEquals(listOf(2L), plan.newCharts.map { it.id })
        assertEquals(mapOf(1L to 40L), plan.existingChartFor)
        assertEquals(listOf(5L), plan.newEvents.map { it.id })
        assertEquals(setOf(AstroKeyArchive.imageEntryFor(5)), plan.wantedImages)
    }

    /**
     * Every .astrokey file ever exported has to stay importable. A column added to an entity without
     * a default is missing from older files, and Moshi refuses the whole file — so a new required
     * column must fail here first. Give it a default (and a Room migration), then update the count.
     */
    @Test
    fun aNewColumnNeedsADefaultOrOldFilesStopImporting() {
        fun required(type: KClass<*>) =
            type.primaryConstructor!!.parameters.filterNot { it.isOptional }.map { it.name }.toSet()

        val charts = required(BirthDataEntity::class)
        val events = required(HistoryEventEntity::class)
        assertEquals("required BirthDataEntity columns", 93, charts.size)
        assertEquals("events and charts share the same required columns", charts, events)
    }
}
