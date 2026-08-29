package eu.kastroguru.astrodiary.ui.humandesign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.SelectedChartStore
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.humandesign.HumanDesignCalculator
import eu.kastroguru.astrodiary.domain.humandesign.HumanDesignChart
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
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HumanDesignViewModel @Inject constructor(
    private val repository: BirthDataRepository,
    private val hdCalculator: HumanDesignCalculator,
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
        _state.value = _state.value.copy(selected = entity, isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val chart = withContext(Dispatchers.Default) { hdCalculator.compute(entity) }
                _state.value = _state.value.copy(chart = chart, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
