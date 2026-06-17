package eu.kastroguru.astrodiary.data

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores user-picked event images inside the app's private storage and hands back a stable absolute
 * path saved on the event. Picked content URIs are short-lived, so we copy the bytes once; the file
 * we own then survives reboots and permission changes. Files are named by UUID (not event id) so they
 * can be created before a new event is saved.
 */
@Singleton
class EventImageStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dir: File by lazy { File(context.filesDir, "event_images").apply { mkdirs() } }

    /** Copies [uri] into internal storage; returns the new absolute path, or null on failure. */
    suspend fun save(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dest = File(dir, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            } ?: return@withContext null
            dest.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /** Deletes a previously saved image file (no-op if path is null/blank or outside our dir). */
    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        val f = File(path)
        if (f.parentFile == dir && f.exists()) f.delete()
    }
}
