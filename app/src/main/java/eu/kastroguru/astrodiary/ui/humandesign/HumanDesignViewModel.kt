package eu.kastroguru.astrodiary.ui.humandesign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.SelectedChartStore
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.humandesign.HdConnectionAnalysis
import eu.kastroguru.astrodiary.domain.humandesign.HdTransitGate
import eu.kastroguru.astrodiary.domain.humandesign.HumanDesignCalculator
import eu.kastroguru.astrodiary.domain.humandesign.HumanDesignChart
import eu.kastroguru.astrodiary.domain.humandesign.analyseConnection
import eu.kastroguru.astrodiary.domain.humandesign.transitGatesFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class HumanDesignUiState(
    val allBirthData: List<BirthDataEntity> = emptyList(),
    val selected: BirthDataEntity? = null,
    val chart: HumanDesignChart? = null,
    /** Today's gate activations read against [chart] — see HdGateTransit.kt. */
    val transitGates: List<HdTransitGate> = emptyList(),
    /** Second person for the connection chart; null until one is picked. */
    val partner: BirthDataEntity? = null,
    val connection: HdConnectionAnalysis? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HumanDesignViewModel @Inject constructor(
    private val repository: BirthDataRepository,
    private val hdCalculator: HumanDesignCalculator,
    private val calculator: AstroCalculator,
    private val selectedChartStore: SelectedChartStore,
) : ViewModel() {

    private val _state = MutableStateFlow(HumanDesignUiState())
    val state: StateFlow<HumanDesignUiState> = _state.asStateFlow()

    init {
        // Same app-wide selection as the natal-chart and transit screens (see SelectedChartStore).
        viewModelScope.launch {
            combine(repository.getAll(), selectedChartStore.selectedId) { list, id -> list to id }
                .collect { (list, id) ->
                    _state.value = _state.value.copy(allBirthData = list)
                    if (list.isEmpty()) return@collect
                    val wanted = list.find { it.id == id } ?: list.first()
                    if (_state.value.selected?.id != wanted.id || _state.value.chart == null) select(wanted)
                    if (wanted.id != id) selectedChartStore.select(wanted.id)
                }
        }
    }

    fun select(entity: BirthDataEntity) {
        selectedChartStore.select(entity.id)
        // The partner belongs to the previous person's comparison — drop it rather than showing a
        // connection chart nobody asked for.
        _state.value = _state.value.copy(
            selected = entity, isLoading = true, error = null, partner = null, connection = null
        )
        viewModelScope.launch {
            try {
                val chart = withContext(Dispatchers.Default) { hdCalculator.compute(entity) }
                val transits = withContext(Dispatchers.Default) { todaysGates(chart) }
                _state.value = _state.value.copy(chart = chart, transitGates = transits, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    /** Pick (or clear, with null) the second person of the connection chart. */
    fun selectPartner(entity: BirthDataEntity?) {
        val mine = _state.value.chart
        if (entity == null || mine == null) {
            _state.value = _state.value.copy(partner = null, connection = null)
            return
        }
        _state.value = _state.value.copy(partner = entity)
        viewModelScope.launch {
            try {
                val analysis = withContext(Dispatchers.Default) {
                    analyseConnection(mine, hdCalculator.compute(entity))
                }
                _state.value = _state.value.copy(connection = analysis)
            } catch (e: Exception) {
                _state.value = _state.value.copy(connection = null, error = e.message)
            }
        }
    }

    /**
     * Today's 13 HD activations. Earth and South Node are derived from the Sun and the North Node
     * exactly as in the natal calculation, so the transit table lines up with the chart's own.
     */
    private fun todaysGates(chart: HumanDesignChart): List<HdTransitGate> {
        val jd = calculator.julianDayFromMs(System.currentTimeMillis())
        fun lon(key: String) = calculator.longitudeAt(key, jd)
        val sun = lon("sun")
        val rahu = lon("rahu")
        val longitudes = buildMap {
            listOf("sun", "moon", "mercury", "venus", "mars", "jupiter", "saturn", "uranus", "neptune", "pluto")
                .forEach { k -> lon(k)?.let { put(k, it) } }
            rahu?.let {
                put("north_node", it)
                put("south_node", (it + 180.0) % 360.0)
            }
            sun?.let { put("earth", (it + 180.0) % 360.0) }
        }
        return transitGatesFor(chart, longitudes)
    }
}
