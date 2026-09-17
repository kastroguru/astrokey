package eu.kastroguru.astrodiary.ui.synastry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.InterpretationAssets
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.synastry.Resonance
import eu.kastroguru.astrodiary.domain.synastry.SymbolicSignature
import eu.kastroguru.astrodiary.domain.synastry.SynastryBands
import eu.kastroguru.astrodiary.domain.interpretation.Bilingual
import eu.kastroguru.astrodiary.domain.synastry.SynastryCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * How easily two people sit together, as a number and a list of what already works.
 *
 * Only the matches reach the screen. The misses are in the number and nowhere else — a reading
 * that itemises what someone is not getting is a different product, and a worse one.
 */
@HiltViewModel
class SynastryViewModel @Inject constructor(
    private val repository: BirthDataRepository,
    private val written: InterpretationAssets,
) : ViewModel() {

    /**
     * One card. [detail] is the traceable chart fact, kept deliberately small and secondary: the
     * text is written to read as ordinary life, and leading with the placement would undo that.
     */
    data class Card(
        val card: SynastryCard,
        val detail: Detail,
        val group: Group,
        val met: Boolean,
    )

    /**
     * The traceable fact behind a card, handed over unformatted so the screen can say it in the
     * reader's language and in small grey type. Kept structured rather than pre-joined because the
     * body names are already localised elsewhere in the app.
     */
    sealed interface Detail {
        /** [channel] is null when nothing answered the need — there is no route to name. */
        data class Point(val point: String, val colour: String, val channel: Resonance.Channel?) : Detail
        data class Cusp(
            val cusp: Resonance.Cusp,
            val sign: eu.kastroguru.astrodiary.domain.model.ZodiacSign,
            val indicator: Resonance.CuspIndicator?,
        ) : Detail
    }

    /**
     * The four sections, in the order they are shown. What works comes first: a reader who opens
     * on their shortfalls takes the rest as a complaint, while a reader who opens on what they
     * already have can take the shortfalls as information.
     */
    enum class Group { CUSP_MET, POINT_MET, CUSP_MISS, POINT_MISS }

    data class State(
        val person: BirthDataEntity? = null,
        val partner: BirthDataEntity? = null,
        val others: List<BirthDataEntity> = emptyList(),
        val score: Int = 0,
        val scoreForPerson: Int = 0,
        val scoreForPartner: Int = 0,
        val cards: List<Card> = emptyList(),
        val isLoading: Boolean = true,
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun load(personId: Long) {
        viewModelScope.launch {
            val person = repository.getById(personId) ?: return@launch
            val all = repository.getAll().first()
            _state.value = State(
                person = person,
                others = all.filter { it.id != personId },
                isLoading = false,
            )
        }
    }

    fun selectPartner(partner: BirthDataEntity?) {
        val person = _state.value.person ?: return
        if (partner == null) {
            _state.value = _state.value.copy(partner = null, cards = emptyList())
            return
        }
        viewModelScope.launch {
            // Pure arithmetic over stored longitudes — no ephemeris call — but the card list is
            // built off the main thread anyway, because it opens two JSON assets on first use.
            val next = withContext(Dispatchers.Default) {
                val a = chartOf(person)
                val b = chartOf(partner)
                val result = Resonance.analyse(a, b)
                Triple(result, cardsFor(a, result.aToB), a)
            }
            val (result, cards, _) = next
            _state.value = _state.value.copy(
                partner = partner,
                score = result.score,
                scoreForPerson = result.aToB.score,
                scoreForPartner = result.bToA.score,
                cards = cards,
            )
        }
    }

    /**
     * The cards for one direction, in four blocks: the cusps and then the points, answered first
     * and unanswered after.
     *
     * The order is the whole argument of the screen. A reader who opens on their shortfalls reads
     * the rest as a complaint; a reader who opens on what works can take the shortfalls as
     * information. So what is there comes first, always, however little of it there is.
     */
    private fun cardsFor(
        asking: Resonance.Chart,
        direction: Resonance.Direction,
    ): List<Card> {
        val out = ArrayList<Card>()

        // Answered cusps.
        for (match in direction.cuspMatches) {
            val card = written.cusp(kindOf(match.cusp), match.sign.englishName, met = true) ?: continue
            out += Card(card, detailFor(match), Group.CUSP_MET, met = true)
        }

        // Answered needs, walked in the reading's order.
        val order = Resonance.POINTS
        for (point in order) {
            for (match in direction.matches.filter { it.need.point == point }) {
                val card = written.resonance(match.need.point, match.need.colour, met = true) ?: continue
                out += Card(card, detailFor(match), Group.POINT_MET, met = true)
            }
        }

        // Unanswered cusps — the two heaviest single losses in the model.
        for (cusp in Resonance.Cusp.values()) {
            if (direction.cuspMatches.any { it.cusp == cusp }) continue
            val sign = when (cusp) {
                Resonance.Cusp.DESCENDANT -> asking.descendantSign
                Resonance.Cusp.IMUM_COELI -> asking.imumCoeliSign
            }
            val card = written.cusp(kindOf(cusp), sign.englishName, met = false) ?: continue
            out += Card(card, Detail.Cusp(cusp, sign, null), Group.CUSP_MISS, met = false)
        }

        // Unanswered needs, heaviest first: a two-point miss matters more than a one-point one.
        val missed = direction.needs.filter { need -> direction.matches.none { it.need == need } }
        for (need in missed.sortedWith(compareByDescending<Resonance.Need> { it.weight }
            .thenBy { order.indexOf(it.point) })) {
            val card = written.resonance(need.point, need.colour, met = false) ?: continue
            out += Card(card, Detail.Point(need.point, need.colour, null), Group.POINT_MISS, met = false)
        }
        return out
    }

    private fun kindOf(cusp: Resonance.Cusp) = when (cusp) {
        Resonance.Cusp.DESCENDANT -> "descendant"
        Resonance.Cusp.IMUM_COELI -> "imum_coeli"
    }

    /** The small print under a card: enough to check the score against, and no more. */
    private fun detailFor(match: Resonance.Match): Detail =
        Detail.Point(match.need.point, match.need.colour, match.channel)

    private fun detailFor(match: Resonance.CuspMatch): Detail =
        Detail.Cusp(match.cusp, match.sign, match.indicator)

    /** What this score means, and the line that introduces the whole reading. */
    fun bandFor(score: Int): Bilingual? = written.band(SynastryBands.keyFor(score))

    val intro: Bilingual? get() = written.band(SynastryBands.INTRO)

    /**
     * A stored chart, read straight off the row. The longitudes and the cusps were written when
     * the person was saved, so nothing here needs Swiss Ephemeris.
     */
    private fun chartOf(e: BirthDataEntity): Resonance.Chart = Resonance.Chart(
        longitudes = mapOf(
            "sun" to e.sunD, "moon" to e.moonD, "mercury" to e.mercuryD, "venus" to e.venusD,
            "mars" to e.marsD, "jupiter" to e.jupiterD, "saturn" to e.saturnD,
            "uranus" to e.uranusD, "neptune" to e.neptuneD, "pluto" to e.plutoD,
        ),
        cusps = listOf(
            e.cusp1, e.cusp2, e.cusp3, e.cusp4, e.cusp5, e.cusp6,
            e.cusp7, e.cusp8, e.cusp9, e.cusp10, e.cusp11, e.cusp12,
        ),
    )
}
