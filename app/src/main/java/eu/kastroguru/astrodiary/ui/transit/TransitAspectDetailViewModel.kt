package eu.kastroguru.astrodiary.ui.transit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.AstroData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

data class FocusAspectState(
    val natal: BirthDataEntity? = null,
    val transit: AstroData? = null,
    val natalCusps: List<Double> = emptyList(),
    val focusNatalKey: String = "",
    val focusTransitKey: String = "",
    val natalRulerKey: String = "",
    val transitRulerKey: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class TransitAspectDetailViewModel @Inject constructor(
    private val repository: BirthDataRepository,
    private val calculator: AstroCalculator,
    private val chartDisplayPrefs: ChartDisplayPrefs,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(FocusAspectState())
    val state: StateFlow<FocusAspectState> = _state.asStateFlow()

    fun load(natalDataId: Long, transitMs: Long, focusNatalKey: String, focusTransitKey: String) {
        viewModelScope.launch {
            val natal = repository.getAll().first().find { it.id == natalDataId } ?: return@launch
            val hs = chartDisplayPrefs.houseSystemChar
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = transitMs }
            val transit = calculator.calculate(
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0,
                natal.latitude, natal.longitude, hs
            )
            val natalCusps = calculator.recalculateCusps(
                natal.yearUtc, natal.monthUtc, natal.dayUtc, natal.hourUtc, natal.minutesUtc,
                natal.latitude, natal.longitude, hs
            )
            val natalFocusDeg = natalDeg(natal, focusNatalKey)
            val natalSignId   = ((natalFocusDeg / 30.0).toInt() % 12) + 1
            val transitSignId = transit.planets[focusTransitKey]?.sign ?: 1

            _state.value = FocusAspectState(
                natal           = natal,
                transit         = transit,
                natalCusps      = natalCusps,
                focusNatalKey   = focusNatalKey,
                focusTransitKey = focusTransitKey,
                natalRulerKey   = signRuler(natalSignId),
                transitRulerKey = signRuler(transitSignId),
                isLoading       = false
            )
        }
    }

    private fun natalDeg(e: BirthDataEntity, key: String) = when (key) {
        "sun"     -> e.sunD;  "moon"    -> e.moonD;  "mercury" -> e.mercuryD
        "venus"   -> e.venusD; "mars"   -> e.marsD;  "jupiter" -> e.jupiterD
        "saturn"  -> e.saturnD; "uranus"-> e.uranusD; "neptune"-> e.neptuneD
        "pluto"   -> e.plutoD; "chiron" -> e.chironD; "rahu"   -> e.rahuD
        "lilith"  -> e.lilithD; else    -> 0.0
    }

    private fun signRuler(signId: Int): String = when (signId) {
        1 -> "mars";    2 -> "venus";   3 -> "mercury"; 4 -> "moon"
        5 -> "sun";     6 -> "mercury"; 7 -> "venus";   8 -> "pluto"
        9 -> "jupiter"; 10 -> "saturn"; 11 -> "uranus"; 12 -> "neptune"
        else -> "sun"
    }
}
