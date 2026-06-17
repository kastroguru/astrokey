package eu.kastroguru.astrodiary.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.LocationCache
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import eu.kastroguru.astrodiary.data.network.GeocodingApi
import eu.kastroguru.astrodiary.data.network.NominatimResult
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.data.repository.HistoryEventRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EventSortOrder { DATE_DESC, DATE_ASC, NAME_ASC }

/**
 * Gallery filter: an optional person (natal chart) AND an optional set of tags. An event matches if
 * (no person selected OR it belongs to that person) AND (no tags selected OR it carries any of them).
 */
data class EventFilter(
    val personId: Long? = null,
    val tags: Set<String> = emptySet(),
    val sortOrder: EventSortOrder = EventSortOrder.DATE_DESC
)

sealed class EventUiState {
    object Loading : EventUiState()
    data class Success(val items: List<HistoryEventEntity>) : EventUiState()
    data class Error(val message: String) : EventUiState()
}

sealed class EventFormState {
    object Idle : EventFormState()
    object Loading : EventFormState()
    data class GeocodingResults(val results: List<NominatimResult>) : EventFormState()
    data class Success(val id: Long) : EventFormState()
    data class Error(val message: String) : EventFormState()
}

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: HistoryEventRepository,
    private val birthDataRepository: BirthDataRepository,
    private val geocodingApi: GeocodingApi,
    private val locationCache: LocationCache
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.Loading)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow<EventFormState>(EventFormState.Idle)
    val formState: StateFlow<EventFormState> = _formState.asStateFlow()

    private val _selectedItem = MutableStateFlow<HistoryEventEntity?>(null)
    val selectedItem: StateFlow<HistoryEventEntity?> = _selectedItem.asStateFlow()

    private val _availableTags = MutableStateFlow<List<String>>(emptyList())
    val availableTags: StateFlow<List<String>> = _availableTags.asStateFlow()

    private val _filter = MutableStateFlow(EventFilter())
    val filter: StateFlow<EventFilter> = _filter.asStateFlow()

    // Natal charts available to assign as an event's "person".
    private val _availablePersons = MutableStateFlow<List<BirthDataEntity>>(emptyList())
    val availablePersons: StateFlow<List<BirthDataEntity>> = _availablePersons.asStateFlow()

    init {
        loadAll()
        watchMetadata()
        watchPersons()
    }

    private fun watchPersons() {
        viewModelScope.launch {
            birthDataRepository.getAll().collect { _availablePersons.value = it }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadAll() {
        viewModelScope.launch {
            _filter.flatMapLatest { f ->
                val base = if (f.personId != null) repository.getByPerson(f.personId) else repository.getAll()
                base.map { items ->
                    val byTags = if (f.tags.isEmpty()) items
                                 else items.filter { e -> tagsOf(e).any { it in f.tags } }
                    when (f.sortOrder) {
                        EventSortOrder.DATE_DESC -> byTags
                        EventSortOrder.DATE_ASC  -> byTags.reversed()
                        EventSortOrder.NAME_ASC  -> byTags.sortedBy { it.name }
                    }
                }
            }.collect { items -> _uiState.value = EventUiState.Success(items) }
        }
    }

    // The tags offered in the filter depend on the selected person: when one is chosen, only that
    // person's events' tags are shown. Reactive to Room so it stays in sync with any change.
    private fun watchMetadata() {
        viewModelScope.launch {
            combine(repository.getAll(), _filter.map { it.personId }.distinctUntilChanged()) { events, personId ->
                val scoped = if (personId != null) events.filter { it.personId == personId } else events
                scoped.flatMap { tagsOf(it) }.distinct().sorted()
            }.collect { _availableTags.value = it }
        }
    }

    private fun tagsOf(e: HistoryEventEntity): List<String> =
        e.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }

    fun clearFilter() { _filter.value = EventFilter() }
    /** Selecting a person resets tags, since the available tag set changes with the person. */
    fun setPersonFilter(personId: Long?) { _filter.value = _filter.value.copy(personId = personId, tags = emptySet()) }
    fun setTags(tags: Set<String>) { _filter.value = _filter.value.copy(tags = tags) }
    fun setSortOrder(order: EventSortOrder) { _filter.value = _filter.value.copy(sortOrder = order) }

    fun selectItem(id: Long) {
        viewModelScope.launch { _selectedItem.value = repository.getById(id) }
    }

    fun delete(entity: HistoryEventEntity) {
        viewModelScope.launch { repository.delete(entity) }
    }

    fun restore(entity: HistoryEventEntity) {
        viewModelScope.launch { repository.insert(entity) }
    }

    fun deleteTag(tag: String, deleteEvents: Boolean) {
        viewModelScope.launch { repository.deleteTag(tag, deleteEvents) }
    }

    fun searchCity(city: String, country: String) {
        viewModelScope.launch {
            _formState.value = EventFormState.Loading
            try {
                // Check local cache first to avoid repeat API calls
                val cached = locationCache.get(city, country)
                if (cached != null) {
                    val fakeResult = NominatimResult(
                        lat = cached.lat.toString(), lon = cached.lon.toString(),
                        displayName = "$city, $country (cached)",
                        address = null
                    )
                    _formState.value = EventFormState.GeocodingResults(listOf(fakeResult))
                } else {
                    val query = if (country.isNotBlank()) "$city, $country" else city
                    _formState.value = EventFormState.GeocodingResults(geocodingApi.search(query))
                }
            } catch (e: Exception) {
                _formState.value = EventFormState.Error(e.message ?: "Geocoding failed")
            }
        }
    }

    fun cacheLocation(city: String, country: String, lat: Double, lon: Double, tz: String) =
        locationCache.put(city, country, lat, lon, tz)

    fun calculateAndSave(
        name: String, year: Int, month: Int, day: Int, hour: Int, minutes: Int,
        city: String, country: String, timezoneId: String, latitude: Double, longitude: Double,
        description: String, tags: String, isGlobal: Boolean, personId: Long? = null,
        imagePath: String? = null, editId: Long = 0L
    ) {
        viewModelScope.launch {
            _formState.value = EventFormState.Loading
            val result = repository.calculateAndSave(
                name, year, month, day, hour, minutes,
                city, country, timezoneId, latitude, longitude,
                description, tags, isGlobal, personId, imagePath, editId
            )
            if (result.isSuccess) {
                _formState.value = EventFormState.Success(result.getOrThrow())
            } else {
                _formState.value = EventFormState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun resetFormState() { _formState.value = EventFormState.Idle }

    /** Copies a picked image into internal storage; returns its stored path (null on failure). */
    suspend fun saveImage(uri: android.net.Uri): String? = repository.saveImage(uri)

    /** Removes an owned image file (on replace, on image delete, or once a delete is permanent). */
    fun deleteImageFile(path: String?) = repository.deleteImageFile(path)
}
