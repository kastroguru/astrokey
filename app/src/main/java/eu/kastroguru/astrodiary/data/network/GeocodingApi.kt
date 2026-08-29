package eu.kastroguru.astrodiary.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import android.content.res.Resources
import android.os.Build
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

interface GeocodingApi {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 10,
        @Query("addressdetails") addressdetails: Int = 1
    ): List<NominatimResult>
}

/**
 * Search, then clean up what Nominatim returns: it lists the same place more than once (two OSM
 * objects for one town) and orders by its own relevance, so a Bulgarian user searching "Sofia" was
 * offered Madagascar first and Bulgaria second. Duplicates are collapsed and results in the
 * device's country float to the top; the API's order is kept inside each group.
 */
suspend fun GeocodingApi.searchPlaces(
    query: String,
    countryCode: String? = deviceCountry(),
): List<NominatimResult> = search(query).dedupePlaces().countryFirst(countryCode)

/**
 * The per-app locale is language-only ("bg"), so `Locale.getDefault().country` is empty inside the
 * app and cannot be used on its own — fall back to the system locale, which carries the region.
 */
internal fun deviceCountry(): String? {
    Locale.getDefault().country.takeIf { it.isNotBlank() }?.let { return it }
    // A per-app locale is language-only ("bg"), but the device keeps its regional locale next in
    // the list ("[bg, bg_BG]"), so take the first entry that actually carries a country.
    return runCatching {
        val cfg = Resources.getSystem().configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val list = cfg.locales
            (0 until list.size()).asSequence()
                .map { list[it].country }
                .firstOrNull { it.isNotBlank() }
        } else {
            @Suppress("DEPRECATION") cfg.locale.country.takeIf { it.isNotBlank() }
        }
    }.getOrNull()
}

internal fun List<NominatimResult>.dedupePlaces(): List<NominatimResult> {
    val seen = HashSet<String>()
    return filter { r ->
        val coords = "%.2f,%.2f".format(          // ~1 km grid
            Locale.US, r.lat.toDoubleOrNull() ?: 0.0, r.lon.toDoubleOrNull() ?: 0.0
        )
        seen.add("name:${r.displayName}") && seen.add("at:$coords")
    }
}

internal fun List<NominatimResult>.countryFirst(countryCode: String?): List<NominatimResult> {
    val cc = countryCode?.lowercase()?.takeIf { it.isNotBlank() } ?: return this
    return sortedByDescending { it.address?.countryCode?.lowercase() == cc }
}

@JsonClass(generateAdapter = true)
data class NominatimResult(
    @Json(name = "lat") val lat: String,
    @Json(name = "lon") val lon: String,
    @Json(name = "display_name") val displayName: String,
    @Json(name = "address") val address: NominatimAddress? = null
)

@JsonClass(generateAdapter = true)
data class NominatimAddress(
    @Json(name = "city") val city: String? = null,
    @Json(name = "town") val town: String? = null,
    @Json(name = "village") val village: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "country_code") val countryCode: String? = null
)
