package eu.kastroguru.astrodiary.data.backup

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.BuildConfig
import eu.kastroguru.astrodiary.data.EventImageStore
import eu.kastroguru.astrodiary.data.db.AppDatabase
import eu.kastroguru.astrodiary.data.db.dao.BirthDataDao
import eu.kastroguru.astrodiary.data.db.dao.HistoryEventDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Export to and import from an `.astrokey` file the user picked through the system file picker.
 * The app itself sends nothing anywhere: the file lands wherever the user chose to save it.
 */
@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase,
    private val chartDao: BirthDataDao,
    private val eventDao: HistoryEventDao,
    private val imageStore: EventImageStore,
) {
    class ExportResult(val charts: Int, val events: Int, val photos: Int)

    class ImportResult(val addedCharts: Int, val addedEvents: Int, val skippedCharts: Int, val skippedEvents: Int)

    suspend fun hasData(): Boolean = withContext(Dispatchers.IO) {
        chartDao.getAllOnce().isNotEmpty() || eventDao.getAllOnce().isNotEmpty()
    }

    suspend fun export(uri: Uri): ExportResult = withContext(Dispatchers.IO) {
        val charts = chartDao.getAllOnce()
        val photoFiles = HashMap<String, File>()
        val events = eventDao.getAllOnce().map { e ->
            val photo = e.imagePath?.let(::File)?.takeIf { it.isFile }
            if (photo == null) e.copy(imagePath = null)
            else AstroKeyArchive.imageEntryFor(e.id).let { entry ->
                photoFiles[entry] = photo
                e.copy(imagePath = entry)
            }
        }
        val backup = AstroKeyBackup(
            format = AstroKeyArchive.FORMAT,
            version = AstroKeyArchive.VERSION,
            app = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            exportedAt = System.currentTimeMillis(),
            charts = charts,
            events = events,
        )
        val photos = try {
            val out = context.contentResolver.openOutputStream(uri) ?: throw FileNotFoundException(uri.toString())
            out.use { AstroKeyArchive.write(it, backup) { entry -> photoFiles[entry]?.inputStream() } }
        } catch (e: Exception) {
            // A half-written file would look like a backup and fail only when it is needed.
            runCatching { DocumentsContract.deleteDocument(context.contentResolver, uri) }
            throw e
        }
        ExportResult(charts.size, events.size, photos)
    }

    /** The file's data without its photos — what the confirmation before an import shows. */
    suspend fun inspect(uri: Uri): AstroKeyBackup = withContext(Dispatchers.IO) {
        open(uri).use(AstroKeyArchive::readData)
    }

    /** Adds what the phone does not have yet; one transaction, so a failure adds nothing at all. */
    suspend fun import(uri: Uri): ImportResult = withContext(Dispatchers.IO) {
        val backup = open(uri).use(AstroKeyArchive::readData)
        val plan = AstroKeyImport.plan(backup, chartDao.getAllOnce(), eventDao.getAllOnce())

        val savedPhotos = HashMap<String, String>()
        try {
            val wanted = plan.wantedImages
            if (wanted.isNotEmpty()) {
                open(uri).use { input ->
                    AstroKeyArchive.readImages(input, wanted) { entry, photo ->
                        imageStore.saveFrom(photo)?.let { savedPhotos[entry] = it }
                    }
                }
            }
            db.withTransaction {
                val phoneIdFor = HashMap(plan.existingChartFor)
                for (c in plan.newCharts) phoneIdFor[c.id] = chartDao.insert(c.copy(id = 0))
                for (e in plan.newEvents) {
                    eventDao.insert(
                        e.copy(
                            id = 0,
                            // A link to a chart that is not in the file is dropped, not guessed.
                            personId = e.personId?.let { phoneIdFor[it] },
                            imagePath = e.imagePath?.let { savedPhotos[it] },
                        )
                    )
                }
            }
        } catch (e: Exception) {
            savedPhotos.values.forEach(imageStore::delete)
            throw e
        }
        ImportResult(plan.newCharts.size, plan.newEvents.size, plan.skippedCharts, plan.skippedEvents)
    }

    private fun open(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri) ?: throw FileNotFoundException(uri.toString())
}
