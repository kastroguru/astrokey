package eu.kastroguru.astrodiary.ui.now

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skedgo.converter.TimezoneMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.data.LocationCache
import eu.kastroguru.astrodiary.data.network.GeocodingApi
import eu.kastroguru.astrodiary.data.network.NominatimResult
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.ui.transit.TransitStep
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

data class NowState(
    val astroData: AstroData? = null,
    val city: String = "Sofia",
    val country: String = "Bulgaria",
    val latitude: Double = 42.6977,
    val longitude: Double = 23.3219,
    val timezoneId: String = "Europe/Sofia",
    val isLoading: Boolean = false,
    val error: String? = null,
    val geocodingResults: List<NominatimResult> = emptyList(),
    val selectedStep: TransitStep = TransitStep.DAY,
    val isLive: Boolean = true,
    val dateLabel: String = ""
)

@HiltViewModel
class NowViewModel @Inject constructor(
    private val astroCalculator: AstroCalculator,
    private val geocodingApi: GeocodingApi,
    private val locationCache: LocationCache,
    private val chartDisplayPrefs: ChartDisplayPrefs,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("now_settings", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        NowState(
            city       = prefs.getString("city",    "Sofia")        ?: "Sofia",
            country    = prefs.getString("country", "Bulgaria")     ?: "Bulgaria",
            latitude   = prefs.getFloat("lat",  42.6977f).toDouble(),
            longitude  = prefs.getFloat("lon",  23.3219f).toDouble(),
            timezoneId = prefs.getString("tz",  "Europe/Sofia")     ?: "Europe/Sofia"
        )
    )
    val state: StateFlow<NowState> = _state.asStateFlow()

    var selectedTimeMs: Long = System.currentTimeMillis()
        private set

    private val dateFmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private var refreshJob: Job? = null

    init { startRefreshing() }

    private fun startRefreshing() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                if (_state.value.isLive) {
                    selectedTimeMs = System.currentTimeMillis()
                    calculateAt(selectedTimeMs)
                }
                delay(60_000L)
            }
        }
    }

    fun calculateNow() {
        if (_state.value.isLive) selectedTimeMs = System.currentTimeMillis()
        calculateAt(selectedTimeMs)
    }

    fun selectStep(step: TransitStep) {
        _state.value = _state.value.copy(selectedStep = step)
    }

    fun goBack() {
        selectedTimeMs -= _state.value.selectedStep.millis
        _state.value = _state.value.copy(isLive = false)
        calculateAt(selectedTimeMs)
    }

    fun goForward() {
        selectedTimeMs += _state.value.selectedStep.millis
        _state.value = _state.value.copy(isLive = false)
        calculateAt(selectedTimeMs)
    }

    fun goNow() {
        selectedTimeMs = System.currentTimeMillis()
        _state.value = _state.value.copy(isLive = true)
        calculateAt(selectedTimeMs)
    }

    fun setDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(year, month - 1, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }
        selectedTimeMs = cal.timeInMillis
        _state.value = _state.value.copy(isLive = false)
        calculateAt(selectedTimeMs)
    }

    private fun calculateAt(ms: Long) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = ms }
                val data = astroCalculator.calculate(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0,
                    _state.value.latitude, _state.value.longitude,
                    chartDisplayPrefs.houseSystemChar
                )
                val label = dateFmt.format(cal.time) +
                        if (_state.value.isLive) context.getString(R.string.now_live_label) else ""
                _state.value = _state.value.copy(astroData = data, isLoading = false, dateLabel = label)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun searchCity(city: String, country: String) {
        // Update city/country fields for later use in selectGeocodingResult
        _state.value = _state.value.copy(city = city, country = country)
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val cached = locationCache.get(city, country)
                if (cached != null) {
                    applyLocation(city, country, cached.lat, cached.lon, cached.timezoneId)
                } else {
                    val q = if (country.isNotBlank()) "$city, $country" else city
                    _state.value = _state.value.copy(
                        isLoading = false, geocodingResults = geocodingApi.search(q)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun selectGeocodingResult(result: NominatimResult) {
        val lat = result.lat.toDouble(); val lon = result.lon.toDouble()
        val tz  = TimezoneMapper.latLngToTimezoneString(lat, lon).takeIf { it.isNotBlank() } ?: fallbackTz(lon)
        val city = _state.value.city; val country = _state.value.country
        locationCache.put(city, country, lat, lon, tz)
        _state.value = _state.value.copy(geocodingResults = emptyList())
        applyLocation(city, country, lat, lon, tz)
    }

    private fun applyLocation(city: String, country: String, lat: Double, lon: Double, tz: String) {
        _state.value = _state.value.copy(
            city = city, country = country, latitude = lat, longitude = lon, timezoneId = tz,
            isLoading = false
        )
        prefs.edit()
            .putString("city", city).putString("country", country)
            .putFloat("lat", lat.toFloat()).putFloat("lon", lon.toFloat())
            .putString("tz", tz).apply()
        calculateNow()
    }

    private fun fallbackTz(lon: Double): String {
        val h = Math.round(lon / 15.0).toInt()
        return if (h >= 0) "GMT+$h" else "GMT$h"
    }

    override fun onCleared() { super.onCleared(); refreshJob?.cancel() }
}
