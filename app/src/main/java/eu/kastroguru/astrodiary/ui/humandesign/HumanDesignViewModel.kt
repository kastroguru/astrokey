package eu.kastroguru.astrodiary.ui.humandesign

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.humandesign.HumanDesignCalculator
import eu.kastroguru.astrodiary.domain.humandesign.HumanDesignChart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("human_design_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(HumanDesignUiState())
    val state: StateFlow<HumanDesignUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().collect { list ->
                _state.value = _state.value.copy(allBirthData = list)
                if (_state.value.selected == null && list.isNotEmpty()) {
                    val lastId = prefs.getLong("last_hd_id", -1L)
                    val toSelect = if (lastId >= 0) list.find { it.id == lastId } else null
                    select(toSelect ?: list[0])
                }
            }
        }
    }

    fun select(entity: BirthDataEntity) {
        prefs.edit().putLong("last_hd_id", entity.id).apply()
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
