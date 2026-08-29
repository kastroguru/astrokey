package eu.kastroguru.astrodiary.ui.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.RulershipChain
import eu.kastroguru.astrodiary.domain.interpretation.Bilingual
import eu.kastroguru.astrodiary.domain.interpretation.NatalInterpretations
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A whole natal chart said in words: who the person is, and then how each body plays out and where
 * it is decided. The screen that opens first for anyone who does not read charts — and the same text
 * an astrologer can pull up next to the wheel.
 */
@HiltViewModel
class ChartReadingViewModel @Inject constructor(
    private val repository: BirthDataRepository,
) : ViewModel() {

    /** One block of the reading: a heading the screen builds names for, and the text itself. */
    data class Section(
        val planetKey: String?,
        val sign: ZodiacSign?,
        val house: Int?,
        val rulerKey: String?,
        val rulerHouse: Int?,
        val text: Bilingual,
        val kind: Kind,
    ) {
        enum class Kind { WHO_YOU_ARE, IN_THE_WORLD }
    }

    data class State(
        val entity: BirthDataEntity? = null,
        val sections: List<Section> = emptyList(),
        val isLoading: Boolean = true,
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun load(id: Long) {
        viewModelScope.launch {
            val e = repository.getById(id) ?: return@launch
            _state.value = State(entity = e, sections = build(e), isLoading = false)
        }
    }

    private fun build(e: BirthDataEntity): List<Section> {
        val longitudes = longitudesOf(e)
        val cusps = cuspsOf(e)
        val sections = mutableListOf<Section>()

        // Who you are: the three placements people recognise themselves in first.
        for (key in listOf("sun", "moon")) {
            val sign = ZodiacSign.fromDegree(norm(longitudes.getValue(key)))
            NatalInterpretations.planetInSign(key, sign)?.let {
                sections += Section(key, sign, null, null, null, it, Section.Kind.WHO_YOU_ARE)
            }
        }
        val ascSign = ZodiacSign.fromDegree(norm(e.cusp1))
        NatalInterpretations.angleInSign("asc", ascSign)?.let {
            sections += Section("asc", ascSign, null, null, null, it, Section.Kind.WHO_YOU_ARE)
        }

        // How each body plays out, and where it is actually decided.
        for (key in longitudes.keys) {
            val link = RulershipChain.linkFor(key, longitudes, cusps) ?: continue
            NatalInterpretations.planetInHouseWithRuler(link)?.let {
                sections += Section(
                    planetKey = key,
                    sign = ZodiacSign.fromDegree(norm(longitudes.getValue(key))),
                    house = link.houseOfPlanet,
                    rulerKey = link.rulerKey,
                    rulerHouse = link.houseOfRuler,
                    text = it,
                    kind = Section.Kind.IN_THE_WORLD,
                )
            }
        }
        return sections
    }

    private fun longitudesOf(e: BirthDataEntity): Map<String, Double> = linkedMapOf(
        "sun" to e.sunD, "moon" to e.moonD, "mercury" to e.mercuryD, "venus" to e.venusD,
        "mars" to e.marsD, "jupiter" to e.jupiterD, "saturn" to e.saturnD, "uranus" to e.uranusD,
        "neptune" to e.neptuneD, "pluto" to e.plutoD, "chiron" to e.chironD, "rahu" to e.rahuD,
        "lilith" to e.lilithD,
    )

    private fun cuspsOf(e: BirthDataEntity): List<Double> = listOf(
        e.cusp1, e.cusp2, e.cusp3, e.cusp4, e.cusp5, e.cusp6,
        e.cusp7, e.cusp8, e.cusp9, e.cusp10, e.cusp11, e.cusp12,
    )

    private fun norm(d: Double) = ((d % 360.0) + 360.0) % 360.0
}
