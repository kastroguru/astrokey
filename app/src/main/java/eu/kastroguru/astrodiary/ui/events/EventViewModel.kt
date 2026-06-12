package eu.kastroguru.astrodiary.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.LocationCache
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import eu.kastroguru.astrodiary.data.network.GeocodingApi
import eu.kastroguru.astrodiary.data.network.NominatimResult
import eu.kastroguru.astrodiary.data.repository.HistoryEventRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EventSortOrder { DATE_DESC, DATE_ASC, NAME_ASC }
enum class EventFilterType { ALL, TAG, SUN_SIGN, MOON_SIGN, YEAR, GLOBAL, SEARCH }

data class EventFilter(
    val type: EventFilterType = EventFilterType.ALL,
    val tag: String? = null,
    val signId: Int? = null,
    val year: Int? = null,
    val searchQuery: String = "",
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

    private val _availableYears = MutableStateFlow<List<Int>>(emptyList())
    val availableYears: StateFlow<List<Int>> = _availableYears.asStateFlow()

    private val _filter = MutableStateFlow(EventFilter())
    val filter: StateFlow<EventFilter> = _filter.asStateFlow()

    init {
        loadAll()
        watchMetadata()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadAll() {
        viewModelScope.launch {
            _filter.flatMapLatest { f ->
                when (f.type) {
                    EventFilterType.ALL -> repository.getAll()
                    EventFilterType.TAG -> repository.getByTag(f.tag ?: return@flatMapLatest repository.getAll())
                    EventFilterType.SUN_SIGN -> repository.getBySunSign(f.signId ?: return@flatMapLatest repository.getAll())
                    EventFilterType.MOON_SIGN -> repository.getByMoonSign(f.signId ?: return@flatMapLatest repository.getAll())
                    EventFilterType.YEAR -> repository.getByYear(f.year ?: return@flatMapLatest repository.getAll())
                    EventFilterType.GLOBAL -> repository.getGlobalOnly()
                    EventFilterType.SEARCH -> if (f.searchQuery.isBlank()) repository.getAll()
                                              else repository.search(f.searchQuery)
                }
            }.map { items ->
                when (_filter.value.sortOrder) {
                    EventSortOrder.DATE_DESC -> items
                    EventSortOrder.DATE_ASC  -> items.reversed()
                    EventSortOrder.NAME_ASC  -> items.sortedBy { it.name }
                }
            }.collect { items ->
                _uiState.value = EventUiState.Success(items)
            }
        }
    }

    // Derives tags and years reactively from Room so any DB change (insert/delete from
    // any ViewModel scope) automatically updates the chip filter bar.
    private fun watchMetadata() {
        viewModelScope.launch {
            repository.getAll().collect { events ->
                _availableTags.value = events
                    .flatMap { e -> e.tags.split(",").map { it.trim() }.filter { it.isNotBlank() } }
                    .distinct()
                    .sorted()
                _availableYears.value = events.map { it.year }.distinct().sortedDescending()
            }
        }
    }

    fun setFilter(filter: EventFilter) { _filter.value = filter }
    fun clearFilter() { _filter.value = EventFilter() }
    fun setTagFilter(tag: String?) { _filter.value = if (tag == null) EventFilter() else EventFilter(type = EventFilterType.TAG, tag = tag) }
    fun setSearch(query: String) { _filter.value = EventFilter(type = EventFilterType.SEARCH, searchQuery = query) }
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
        description: String, tags: String, isGlobal: Boolean, editId: Long = 0L
    ) {
        viewModelScope.launch {
            _formState.value = EventFormState.Loading
            val result = repository.calculateAndSave(
                name, year, month, day, hour, minutes,
                city, country, timezoneId, latitude, longitude,
                description, tags, isGlobal, editId
            )
            if (result.isSuccess) {
                _formState.value = EventFormState.Success(result.getOrThrow())
            } else {
                _formState.value = EventFormState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun resetFormState() { _formState.value = EventFormState.Idle }

    fun refreshTags() {
        viewModelScope.launch {
            _availableTags.value = repository.getAllTags()
            _availableYears.value = repository.getAllYears()
        }
    }
}
