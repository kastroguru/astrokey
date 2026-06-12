package eu.kastroguru.astrodiary.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.AstroData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: BirthDataRepository,
    private val astroCalculator: AstroCalculator,
    private val chartDisplayPrefs: ChartDisplayPrefs
) : ViewModel() {

    private val _entity = MutableStateFlow<BirthDataEntity?>(null)
    val entity: StateFlow<BirthDataEntity?> = _entity.asStateFlow()

    private val _astroData = MutableStateFlow<AstroData?>(null)
    val astroData: StateFlow<AstroData?> = _astroData.asStateFlow()

    fun load(id: Long) {
        viewModelScope.launch {
            val e = repository.getById(id) ?: return@launch
            _entity.value = e
            _astroData.value = entityToAstroData(e)
        }
    }

    private fun entityToAstroData(e: BirthDataEntity): AstroData =
        astroCalculator.calculate(
            e.yearUtc, e.monthUtc, e.dayUtc,
            e.hourUtc + e.minutesUtc / 60.0,
            e.latitude, e.longitude,
            chartDisplayPrefs.houseSystemChar
        )
}
