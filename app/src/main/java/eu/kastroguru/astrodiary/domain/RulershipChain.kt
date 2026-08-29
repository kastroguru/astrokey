package eu.kastroguru.astrodiary.domain

import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.ZodiacSign

/**
 * "Planet in a house, whose ruler sits in another house" — the chain that makes a placement belong
 * to one chart instead of being generic.
 *
 * The steps, in order, and independent of the house system (the cusps are handed in, so Placidus,
 * Koch or anything else behaves the same):
 *
 *  1. which house the planet falls in, regardless of the sign it is in;
 *  2. which sign sits on the cusp of that house;
 *  3. which planet rules that sign — the ruler of the house;
 *  4. which house that ruler itself falls in.
 *
 * The result says where a part of life is actually decided: the house asks the question, the ruler's
 * house is where the answer comes from. Two people with the Sun in the 5th have little in common once
 * the ruler's placement is taken into account.
 *
 * **Rulerships are the ancient ones only** — Mars for Scorpio, Jupiter for Sagittarius and Pisces,
 * Saturn for Capricorn and Aquarius. There is no modern table here on purpose: an unused option is a
 * trap waiting for someone to switch it on. This matches [eu.kastroguru.astrodiary.domain.model.ChartUtil.dignityCode],
 * which has always used the traditional seven.
 */
object RulershipChain {

    /** Sign → its ruling planet. The classical assignment, with no outer planets given rulership. */
    val rulers: Map<ZodiacSign, String> = mapOf(
        ZodiacSign.ARIES to "mars",
        ZodiacSign.TAURUS to "venus",
        ZodiacSign.GEMINI to "mercury",
        ZodiacSign.CANCER to "moon",
        ZodiacSign.LEO to "sun",
        ZodiacSign.VIRGO to "mercury",
        ZodiacSign.LIBRA to "venus",
        ZodiacSign.SCORPIO to "mars",
        ZodiacSign.SAGITTARIUS to "jupiter",
        ZodiacSign.CAPRICORN to "saturn",
        ZodiacSign.AQUARIUS to "saturn",
        ZodiacSign.PISCES to "jupiter",
    )

    fun rulerOfSign(sign: ZodiacSign): String = rulers.getValue(sign)

    data class Link(
        val planetKey: String,
        /** House the planet stands in. */
        val houseOfPlanet: Int,
        /** Sign on the cusp of that house — the step people skip. */
        val cuspSign: ZodiacSign,
        /** Ruler of that sign, i.e. the ruler of the house the planet stands in. */
        val rulerKey: String,
        /** House that ruler itself stands in — where this part of life is decided. */
        val houseOfRuler: Int,
    ) {
        /** The planet rules the house it stands in: it answers to itself here. */
        val isItsOwnRuler: Boolean get() = planetKey == rulerKey

        /** The ruler stands in the same house as the planet: the matter is self-contained. */
        val rulerIsAtHome: Boolean get() = houseOfRuler == houseOfPlanet
    }

    /**
     * @param planetLongitudes every body's ecliptic longitude, keyed as elsewhere in the app
     * @param cusps the twelve house cusps in order, as stored on a chart
     */
    fun linkFor(
        planetKey: String,
        planetLongitudes: Map<String, Double>,
        cusps: List<Double>,
    ): Link? {
        if (cusps.size < 12) return null
        val lon = planetLongitudes[planetKey] ?: return null
        val houseOfPlanet = AstroCalculator.planetHouse(norm(lon), cusps)
        val cuspSign = ZodiacSign.fromDegree(norm(cusps[houseOfPlanet - 1]))
        val rulerKey = rulerOfSign(cuspSign)
        val rulerLon = planetLongitudes[rulerKey] ?: return null
        return Link(
            planetKey = planetKey,
            houseOfPlanet = houseOfPlanet,
            cuspSign = cuspSign,
            rulerKey = rulerKey,
            houseOfRuler = AstroCalculator.planetHouse(norm(rulerLon), cusps),
        )
    }

    /** Every body's chain for one chart, in the order the longitudes were given. */
    fun linksFor(
        planetLongitudes: Map<String, Double>,
        cusps: List<Double>,
    ): List<Link> = planetLongitudes.keys.mapNotNull { linkFor(it, planetLongitudes, cusps) }

    private fun norm(deg: Double): Double = ((deg % 360.0) + 360.0) % 360.0
}
