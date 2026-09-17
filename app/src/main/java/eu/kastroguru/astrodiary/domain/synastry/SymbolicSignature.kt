package eu.kastroguru.astrodiary.domain.synastry

import eu.kastroguru.astrodiary.domain.RulershipChain
import eu.kastroguru.astrodiary.domain.model.ZodiacSign

/**
 * What stands for a planet, other than the planet itself.
 *
 * A planet's colour reaches a chart through three symbols: the signs it rules, the houses that
 * carry those signs' numbers, and the planet's own body. The first is the ancient rulership table
 * and is **inverted out of [RulershipChain.rulers]** rather than typed again — a second copy would
 * drift, and the readings elsewhere in the app already depend on that one being the truth.
 *
 * The house is the sign's ordinal: Aries is the first sign, so the first house is Martian; Scorpio
 * is the eighth, so the eighth house is Martian too. Saturn rules Capricorn and Aquarius, the
 * tenth and eleventh signs, and therefore stands behind the tenth and eleventh houses.
 *
 * Uranus, Neptune and Pluto rule nothing under the ancient table, so they have neither sign nor
 * house here. That is not an omission — it is why a need for their colour can only be born from an
 * aspect, and why it can only be answered by an aspect or by sharing a sign.
 */
object SymbolicSignature {

    /**
     * The three bodies with no rulership, and therefore no sign and no house. Ordered by distance
     * from the Sun, not alphabetically — the enumerated rule list is read by a person and reads
     * wrong in any other order.
     */
    val OUTER: List<String> = listOf("uranus", "neptune", "pluto")

    /** The seven that can rule, in the order the reading walks them. */
    val CLASSICAL: List<String> =
        listOf("sun", "moon", "mercury", "venus", "mars", "jupiter", "saturn")

    /** Planet → the signs it rules. Empty for the outer three. */
    val signsOf: Map<String, List<ZodiacSign>> =
        RulershipChain.rulers.entries
            .groupBy({ it.value }, { it.key })
            .mapValues { (_, signs) -> signs.sortedBy { it.id } }

    /** Planet → the houses that carry its signs' numbers. Empty for the outer three. */
    val housesOf: Map<String, List<Int>> =
        signsOf.mapValues { (_, signs) -> signs.map { it.id } }

    fun rulerOf(sign: ZodiacSign): String = RulershipChain.rulerOfSign(sign)

    /**
     * The modern ruler of the three signs that have one, and null everywhere else.
     *
     * The rest of the app is strictly ancient, and this does **not** change that. It is used in
     * one place only — the two cusps — where the owner decided a partner carrying Uranus, Neptune
     * or Pluto answers a descendant in Aquarius, Pisces or Scorpio just as the traditional ruler
     * does. A chart's *points* still raise and answer needs on the ancient table alone, because
     * the outer three rule no sign there and that is what makes their colour aspect-only.
     */
    fun modernRulerOf(sign: ZodiacSign): String? = when (sign) {
        ZodiacSign.SCORPIO -> "pluto"
        ZodiacSign.AQUARIUS -> "uranus"
        ZodiacSign.PISCES -> "neptune"
        else -> null
    }

    /** Both rulers of a sign, traditional first — the order the cusp indicators are tried in. */
    fun rulersOf(sign: ZodiacSign): List<String> =
        listOfNotNull(rulerOf(sign), modernRulerOf(sign))

    fun rules(body: String, sign: ZodiacSign): Boolean = signsOf[body]?.contains(sign) == true

    fun standsBehind(body: String, house: Int): Boolean = housesOf[body]?.contains(house) == true

    fun isOuter(body: String): Boolean = body in OUTER
}
