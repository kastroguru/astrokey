package eu.kastroguru.astrodiary.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("addressdetails") addressdetails: Int = 1
    ): List<NominatimResult>
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
