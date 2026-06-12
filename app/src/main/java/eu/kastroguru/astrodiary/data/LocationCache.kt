package eu.kastroguru.astrodiary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class CachedLocation(val lat: Double, val lon: Double, val timezoneId: String)

/**
 * Persists geocoded city results in SharedPreferences so we don't repeat Nominatim API calls
 * for the same city+country combination.
 */
@Singleton
class LocationCache @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences("location_cache", Context.MODE_PRIVATE)

    fun get(city: String, country: String): CachedLocation? {
        val key = cacheKey(city, country)
        val value = prefs.getString(key, null) ?: return null
        val parts = value.split("|")
        return if (parts.size >= 3) {
            try { CachedLocation(parts[0].toDouble(), parts[1].toDouble(), parts[2]) }
            catch (_: NumberFormatException) { null }
        } else null
    }

    fun put(city: String, country: String, lat: Double, lon: Double, timezoneId: String) {
        prefs.edit().putString(cacheKey(city, country), "$lat|$lon|$timezoneId").apply()
    }

    private fun cacheKey(city: String, country: String) =
        "${city.trim().lowercase()}_${country.trim().lowercase()}"
}
