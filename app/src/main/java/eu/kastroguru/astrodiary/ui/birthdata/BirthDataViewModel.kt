package eu.kastroguru.astrodiary.ui.birthdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.LocationCache
import eu.kastroguru.astrodiary.data.network.GeocodingApi
import eu.kastroguru.astrodiary.data.network.geocodingMessage
import eu.kastroguru.astrodiary.data.network.searchPlaces
import eu.kastroguru.astrodiary.data.network.NominatimResult
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BirthDataUiState {
    object Loading : BirthDataUiState()
    data class Success(val items: List<BirthDataEntity>) : BirthDataUiState()
    data class Error(val message: String) : BirthDataUiState()
}

sealed class FormState {
    object Idle : FormState()
    object Loading : FormState()
    data class GeocodingResults(val results: List<NominatimResult>) : FormState()
    data class Success(val id: Long) : FormState()
    data class Error(val message: String) : FormState()
}

@HiltViewModel
class BirthDataViewModel @Inject constructor(
    private val repository: BirthDataRepository,
    private val geocodingApi: GeocodingApi,
    private val locationCache: LocationCache,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<BirthDataUiState>(BirthDataUiState.Loading)
    val uiState: StateFlow<BirthDataUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow<FormState>(FormState.Idle)
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    private val _selectedItem = MutableStateFlow<BirthDataEntity?>(null)
    val selectedItem: StateFlow<BirthDataEntity?> = _selectedItem.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() {
        viewModelScope.launch {
            repository.getAll().collect { items ->
                _uiState.value = BirthDataUiState.Success(items)
            }
        }
    }

    fun selectItem(id: Long) {
        viewModelScope.launch {
            _selectedItem.value = repository.getById(id)
        }
    }

    fun delete(entity: BirthDataEntity) {
        viewModelScope.launch { repository.delete(entity) }
    }

    fun restore(entity: BirthDataEntity) {
        viewModelScope.launch { repository.insert(entity) }
    }

    fun searchCity(city: String, country: String) {
        viewModelScope.launch {
            _formState.value = FormState.Loading
            try {
                val cached = locationCache.get(city, country)
                if (cached != null) {
                    _formState.value = FormState.GeocodingResults(listOf(
                        NominatimResult(cached.lat.toString(), cached.lon.toString(), cachedLabel(city, country))
                    ))
                } else {
                    val query = if (country.isNotBlank()) "$city, $country" else city
                    _formState.value = FormState.GeocodingResults(geocodingApi.searchPlaces(query))
                }
            } catch (e: Exception) {
                _formState.value = FormState.Error(e.geocodingMessage(context))
            }
        }
    }

    /** Label for a cache hit — country is optional, so it must not leave a dangling comma. */
    private fun cachedLabel(city: String, country: String) =
        listOf(city, country).filter { it.isNotBlank() }.joinToString(", ") + " (cached)"

    fun cacheLocation(city: String, country: String, lat: Double, lon: Double, tz: String) =
        locationCache.put(city, country, lat, lon, tz)

    fun calculateAndSave(
        name: String,
        year: Int, month: Int, day: Int,
        hour: Int, minutes: Int,
        city: String, country: String,
        timezoneId: String,
        latitude: Double,
        longitude: Double,
        editId: Long = 0L
    ) {
        viewModelScope.launch {
            _formState.value = FormState.Loading
            val result = repository.calculateAndSave(
                name, year, month, day, hour, minutes,
                city, country, timezoneId, latitude, longitude,
                editId = editId
            )
            _formState.value = if (result.isSuccess) {
                FormState.Success(result.getOrThrow())
            } else {
                FormState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetFormState() {
        _formState.value = FormState.Idle
    }
}
