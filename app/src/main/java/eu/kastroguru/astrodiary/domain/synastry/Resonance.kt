package eu.kastroguru.astrodiary.domain.synastry

import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.calculator.Aspects
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlin.math.min

/**
 * The resonance score: how easily two charts sit together, as one number out of a hundred and a
 * list of what answers what.
 *
 * The idea in one line: a point in a chart is coloured by the planets that reach it, and the same
 * point in the partner's chart has to carry the same colour, no matter which way it arrives.
 *
 * Two things matter and are easy to get wrong:
 *
 *  - **A need is a colour on a point, never a demand on a planet.** The Moon in Leo does not ask
 *    the partner to put their Sun anywhere; it asks *their Moon* to carry something solar. So both
 *    sides of every test read the same point, and the colouring planet only names the colour.
 *  - **The two sides do not use the same channels.** Standing in a house answers a need but never
 *    creates one, which is why [needs] and [covers] are separate functions rather than one
 *    symmetric relation. Standing in a sign and being aspected create needs; the house only pays
 *    them off.
 *
 * The descendant and the imum coeli are not points in that sense. They are cusps, their need is
 * purely the sign they fall in, and they are answered by a short list of things in the partner's
 * whole chart — see [cuspCover]. They also cost far more than a planet when nothing answers them.
 *
 * The ascendant and the midheaven raise no need at all. They are the person's own path, not a bill
 * for someone else to pay.
 */
object Resonance {

    /**
     * The five that ask, and the only five. Jupiter, Saturn and the outer three are colours
     * only — they are wanted, they never want.
     *
     * So a Moon-Saturn contact raises exactly one need, the Moon's, and never its mirror. The
     * asymmetry is the point: a slow body describes a demand placed on a personal planet, not a
     * demand of its own.
     */
    val POINTS: List<String> = listOf("moon", "sun", "venus", "mercury", "mars")

    /** Every colour a point can want. */
    val COLOURS: List<String> = SymbolicSignature.CLASSICAL + SymbolicSignature.OUTER

    /**
     * The aspects this module counts. **The quincunx is not among them.**
     *
     * It survives elsewhere in the app as an internal aspect, but it does not describe a demand
     * one person places on another, so a Venus quincunx Neptune raises no Neptunian need here.
     * Dropped on the owner's instruction on 2026-09-16.
     */
    val ORBS: Map<Aspects.Kind, Double> =
        Aspects.NATAL_ORBS.filterKeys { it != Aspects.Kind.QUINCUNX }

    /**
     * The Sun's conjunction and opposition run to **10°**, not the 8° of the flat table. The
     * square, trine and sextile keep their ordinary orbs.
     *
     * Added on 2026-09-16 against a real chart where a Sun at 7°12' Leo stood 8°33' from an
     * opposition to Saturn — plainly the aspect, and half a degree outside the table. The widening
     * is deliberately confined to the two aspects the owner named: an eight-degree square to the
     * Sun stays a square that has run out.
     *
     * The Moon does **not** get a wider orb here. That is a separate decision, not an oversight.
     */
    const val SOLAR_ORB = 10.0

    private val SOLAR_ORBS: Map<Aspects.Kind, Double> = ORBS.mapValues { (kind, orb) ->
        if (kind == Aspects.Kind.CONJUNCTION || kind == Aspects.Kind.OPPOSITION) SOLAR_ORB else orb
    }

    /** What counts towards a stellium: the ten planets, not the calculated points. */
    val STELLIUM_BODIES: List<String> = listOf(
        "sun", "moon", "mercury", "venus", "mars",
        "jupiter", "saturn", "uranus", "neptune", "pluto",
    )

    const val STELLIUM_SIZE = 3

    /** What a reading starts from before anything is taken off. See [Direction.score]. */
    const val FULL = 103
    const val DESCENDANT_COST = 10
    const val IMUM_COELI_COST = 5
    private val HEAVY = setOf("sun", "moon")

    // ── Inputs ────────────────────────────────────────────────────────────────

    /**
     * One chart, reduced to what the model reads. Longitudes are keyed as everywhere else in the
     * app; [cusps] is the usual twelve, index 0 the ascendant and index 9 the midheaven.
     */
    class Chart(val longitudes: Map<String, Double>, val cusps: List<Double>) {

        val ascendant: Double get() = cusps[0]
        val ascendantSign: ZodiacSign get() = ZodiacSign.fromDegree(norm(cusps[0]))
        val descendantSign: ZodiacSign get() = ZodiacSign.fromDegree(norm(cusps[6]))
        val imumCoeliSign: ZodiacSign get() = ZodiacSign.fromDegree(norm(cusps[3]))

        fun signOf(body: String): ZodiacSign? =
            longitudes[body]?.let { ZodiacSign.fromDegree(norm(it)) }

        fun houseOf(body: String): Int? =
            longitudes[body]?.let { AstroCalculator.planetHouse(norm(it), cusps) }

        fun bodiesInHouse(house: Int): Int =
            STELLIUM_BODIES.count { houseOf(it) == house }

        fun aspect(a: String, b: String): Aspects.Kind? = Aspects.between(
            longitudes, a, b,
            if (a == "sun" || b == "sun") SOLAR_ORBS else ORBS,
        )
    }

    // ── What comes out ────────────────────────────────────────────────────────

    /** How a colour reaches a point. */
    enum class Channel { ASPECT, SIGN, HOUSE, SAME_SIGN }

    /**
     * Which of the cusp's indicators answered it. The two ruler tests carry the body that fired
     * them, because a sign can have two rulers here and the reader should see which one it was.
     */
    enum class CuspIndicator { ASCENDANT_SIGN, STELLIUM, SUN_SIGN, RULER_ASPECTS_SUN, RULER_ON_ASCENDANT }

    enum class Cusp(val cost: Int) { DESCENDANT(DESCENDANT_COST), IMUM_COELI(IMUM_COELI_COST) }

    /**
     * One colour that one point is asking for.
     *
     * [hardOuter] is set only when the colour is Uranus, Neptune or Pluto and the aspect that
     * raised the need is a hard one — that is the case the partner has to answer in an easier
     * form. [weight] is what the miss costs, multipliers already applied.
     */
    data class Need(
        val point: String,
        val colour: String,
        val hardOuter: Boolean,
        val weight: Int,
        val amplified: Boolean,
    )

    data class Match(val need: Need, val channel: Channel)

    data class CuspMatch(
        val cusp: Cusp,
        val sign: ZodiacSign,
        val indicator: CuspIndicator,
        val ruler: String? = null,
    )

    /** One direction: what this person asked for, and what the other one answered. */
    data class Direction(
        val needs: List<Need>,
        val matches: List<Match>,
        val cuspMatches: List<CuspMatch>,
        val lost: Int,
    ) {
        /**
         * [FULL] less what went unanswered, and **deliberately not clamped**.
         *
         * The starting figure is 103 rather than 100 for one reason: at 100 the worst pair in
         * three thousand came out at −3, and the top is unreachable anyway — the best seen was 86.
         * Three points of headroom put the practical floor at zero without pretending a floor
         * exists. A pair bad enough still goes negative, which is the honest outcome.
         */
        val score: Int get() = FULL - lost
    }

    /**
     * Both directions. [score] is the lower of the two: the relationship is only as easy as it is
     * for the less-answered of the pair.
     */
    data class Result(val aToB: Direction, val bToA: Direction) {
        val score: Int get() = min(aToB.score, bToA.score)
    }

    // ── The model ─────────────────────────────────────────────────────────────

    fun analyse(a: Chart, b: Chart): Result =
        Result(aToB = direction(a, b), bToA = direction(b, a))

    /**
     * What [asking] wants, measured against what [answering] carries.
     *
     * The cusps are settled first, because they decide the weights. A cusp that has been answered
     * needs no fallback, so its ruler carries ordinary weight; only the ruler of a cusp that went
     * unanswered is doubled. That is why weighting lives here and not in [needs] — it is a fact
     * about the pair, not about one chart.
     */
    fun direction(asking: Chart, answering: Chart): Direction {
        val cuspMatches = ArrayList<CuspMatch>(2)
        val loadBearing = ArrayList<ZodiacSign>(2)
        var lost = 0
        for (cusp in Cusp.values()) {
            val sign = when (cusp) {
                Cusp.DESCENDANT -> asking.descendantSign
                Cusp.IMUM_COELI -> asking.imumCoeliSign
            }
            val hit = cuspCover(answering, sign)
            // The ruler carries double only when it came to rest on the ruler — that is, when the
            // first three indicators all failed and the cusp's whole case now depends on it. An
            // ascendant, a stellium or a Sun in the sign settles the matter directly and leaves
            // the ruler an ordinary planet.
            if (hit == null || hit.indicator in RULER_INDICATORS) loadBearing += sign
            if (hit != null) {
                cuspMatches += CuspMatch(cusp, sign, hit.indicator, hit.ruler)
            } else {
                lost += cusp.cost
            }
        }

        val amplifiers = loadBearing.map { SymbolicSignature.rulerOf(it) }
            .filter { it in POINTS }
            .distinct()

        val needs = needs(asking, amplifiers)
        val matches = ArrayList<Match>(needs.size)
        for (need in needs) {
            val channel = covers(answering, need)
            if (channel != null) matches += Match(need, channel) else lost += need.weight
        }

        return Direction(needs, matches, cuspMatches, lost)
    }

    /**
     * Every colour this chart's points are asking for.
     *
     * A point asks because of the sign it stands in — the need is that sign's ruler — and because
     * of every planet that aspects it. The house it stands in asks for nothing, and neither does
     * sharing a sign with another planet: a point already in a sign already has that sign's need,
     * and a second one is not created. A point in its own sign asks nothing by sign at all.
     */
    @JvmOverloads
    fun needs(chart: Chart, amplifiers: List<String> = emptyList()): List<Need> {
        val out = ArrayList<Need>()

        for (point in POINTS) {
            // colour → is the aspect that raised it a hard one to an outer planet
            val wanted = LinkedHashMap<String, Boolean>()

            chart.signOf(point)?.let { sign ->
                val ruler = SymbolicSignature.rulerOf(sign)
                if (ruler != point) wanted[ruler] = false
            }

            for (colour in COLOURS) {
                if (colour == point) continue
                val kind = chart.aspect(point, colour) ?: continue
                val hardOuter = SymbolicSignature.isOuter(colour) && kind.isHard
                // A colour already wanted by sign stays one need; a hard outer contact still has
                // to register, because it is the stricter of the two.
                wanted[colour] = (wanted[colour] ?: false) || hardOuter
            }

            val amplified = point in amplifiers
            for ((colour, hardOuter) in wanted) {
                out += Need(
                    point = point,
                    colour = colour,
                    hardOuter = hardOuter,
                    weight = weight(point, colour, amplified),
                    amplified = amplified,
                )
            }
        }
        return out
    }

    /**
     * What a miss costs: two for an ordinary colour, four when the Sun or the Moon is either side
     * of it, doubled again for a load-bearing angle ruler.
     *
     * The numbers are larger than they look like they need to be, and that is the point. At one
     * and two the typical loss came to about 28 points, so every pair landed between 60 and 85 and
     * the scale used a quarter of itself. At two and four the typical loss is near 47, the median
     * pair sits at 53, and the tenth and ninetieth percentiles fall at 35 and 67 — the number
     * finally spreads over the range it is printed on.
     */
    private fun weight(point: String, colour: String, amplified: Boolean): Int {
        val base = if (point in HEAVY || colour in HEAVY) 4 else 2
        return if (amplified) base * 2 else base
    }

    /**
     * Whether this chart carries the colour that [need] is asking for, on the same point — and by
     * which channel. Null means it does not.
     *
     * The house channel lives here and only here: it answers a need, it never raises one.
     */
    fun covers(chart: Chart, need: Need): Channel? {
        val point = need.point
        val colour = need.colour
        val pointSign = chart.signOf(point)
        val outer = SymbolicSignature.isOuter(colour)

        val aspect = chart.aspect(point, colour)
        if (aspect != null) {
            // A hard contact with an outer planet is answered only by an easier one. Sharing a
            // sign is checked below and counts as a conjunction, so it is not lost here.
            val easyEnough = !need.hardOuter || aspect == Aspects.Kind.CONJUNCTION || aspect.isEasy
            if (easyEnough) return Channel.ASPECT
        }

        if (!outer) {
            if (pointSign != null && SymbolicSignature.rules(colour, pointSign)) return Channel.SIGN
            val house = chart.houseOf(point)
            if (house != null && SymbolicSignature.standsBehind(colour, house)) return Channel.HOUSE
        }

        val colourSign = chart.signOf(colour)
        if (pointSign != null && pointSign == colourSign) return Channel.SAME_SIGN

        return null
    }

    /**
     * Whether this chart answers a cusp standing in [sign], and by which indicator. One is
     * enough; the first that hits is reported.
     *
     * Everything measured against a cusp is measured by sign, never by degree across a boundary.
     *
     * When the sign is Leo its ruler is the Sun, so [CuspIndicator.RULER_ASPECTS_SUN] would ask
     * the Sun to aspect itself and is skipped. Skipping it is not a free pass — the other four
     * still have to produce something, or the cusp costs its full weight.
     */
    fun cuspCover(chart: Chart, sign: ZodiacSign): CuspMatchDetail? {
        // Same sign, and only same sign. Nothing is measured against a cusp across a sign
        // boundary: a conjunction that straddles one does not count here, which also makes a
        // separate degree test redundant.
        if (chart.ascendantSign == sign) {
            return CuspMatchDetail(CuspIndicator.ASCENDANT_SIGN, null)
        }
        if (chart.bodiesInHouse(sign.id) >= STELLIUM_SIZE) {
            return CuspMatchDetail(CuspIndicator.STELLIUM, null)
        }
        if (chart.signOf("sun") == sign) {
            return CuspMatchDetail(CuspIndicator.SUN_SIGN, null)
        }

        // Both rulers are tried, traditional first. This is the one place in the model where the
        // modern rulerships are admitted at all.
        val rulers = SymbolicSignature.rulersOf(sign)
        for (ruler in rulers) {
            if (ruler != "sun" && chart.aspect(ruler, "sun") != null) {
                return CuspMatchDetail(CuspIndicator.RULER_ASPECTS_SUN, ruler)
            }
        }
        // Same rule again: the ruler counts as being on the angle only by sharing its sign.
        for (ruler in rulers) {
            if (chart.signOf(ruler) == chart.ascendantSign) {
                return CuspMatchDetail(CuspIndicator.RULER_ON_ASCENDANT, ruler)
            }
        }

        return null
    }

    /** The two indicators that rest on the ruler rather than settling the cusp outright. */
    private val RULER_INDICATORS =
        setOf(CuspIndicator.RULER_ASPECTS_SUN, CuspIndicator.RULER_ON_ASCENDANT)

    /** An answered cusp: which test fired, and — for the two ruler tests — which body did it. */
    data class CuspMatchDetail(val indicator: CuspIndicator, val ruler: String?)

    private fun norm(a: Double): Double {
        var x = a % 360.0
        if (x < 0) x += 360.0
        return x
    }
}
