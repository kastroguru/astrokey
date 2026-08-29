package eu.kastroguru.astrodiary.data.network

import android.content.Context
import eu.kastroguru.astrodiary.R
import retrofit2.HttpException
import java.io.IOException

/**
 * Why a city search failed, in words the user can act on.
 *
 * Nominatim is a free service with a usage policy: when it decides it has had enough it answers 403
 * or 429. Passing the raw exception message to a snackbar told the user nothing ("HTTP 403"), so the
 * search simply looked broken.
 */
fun Throwable.geocodingMessage(context: Context): String = context.getString(
    when {
        this is HttpException && code() in listOf(403, 429) -> R.string.error_geocoding_busy
        this is IOException -> R.string.error_geocoding_offline
        else -> R.string.error_geocoding_failed
    }
)
