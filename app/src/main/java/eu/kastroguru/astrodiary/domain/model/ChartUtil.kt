package eu.kastroguru.astrodiary.domain.model

import kotlin.math.abs

object ChartUtil {

    private val MAX_SPEEDS = mapOf(
        "sun"     to 1.02,  "moon"    to 15.4,  "mercury" to 2.2,
        "venus"   to 1.26,  "mars"    to 0.72,  "jupiter" to 0.24,
        "saturn"  to 0.12,  "uranus"  to 0.06,  "neptune" to 0.04,
        "pluto"   to 0.03,  "chiron"  to 0.08
        // rahu and lilith are always retrograde — no R/S indicator
    )

    /**
     * Returns "SR" (retrograde + stationary), "R" (retrograde), "S" (stationary direct), or null.
     * Returns null when speed == 0.0, which signals an unknown speed (manually-constructed position).
     */
    fun retrogradeStatus(planetKey: String, speed: Double): String? {
        if (speed == 0.0) return null
        val max = MAX_SPEEDS[planetKey] ?: return null
        val stationary = abs(speed) < max * 0.05
        return when {
            speed < 0 && stationary -> "SR"
            speed < 0               -> "R"
            stationary              -> "S"
            else                    -> null
        }
    }

    /** Returns "Dm", "Ex", "Dt", "Fl", or null. Traditional 7-planet dignities only. */
    fun dignityCode(planetKey: String, sign: Int): String? = when (planetKey) {
        "sun"     -> when (sign) { 5 -> "Dm"; 1 -> "Ex"; 11 -> "Dt"; 7 -> "Fl"; else -> null }
        "moon"    -> when (sign) { 4 -> "Dm"; 2 -> "Ex"; 10 -> "Dt"; 8 -> "Fl"; else -> null }
        "mercury" -> when (sign) { 3 -> "Dm"; 6 -> "Ex"; 9 -> "Dt"; 12 -> "Fl"; else -> null }
        "venus"   -> when (sign) { 2 -> "Dm"; 7 -> "Dm"; 12 -> "Ex"; 1 -> "Dt"; 8 -> "Dt"; 6 -> "Fl"; else -> null }
        "mars"    -> when (sign) { 1 -> "Dm"; 8 -> "Dm"; 10 -> "Ex"; 7 -> "Dt"; 2 -> "Dt"; 4 -> "Fl"; else -> null }
        "jupiter" -> when (sign) { 9 -> "Dm"; 12 -> "Dm"; 4 -> "Ex"; 3 -> "Dt"; 6 -> "Dt"; 10 -> "Fl"; else -> null }
        "saturn"  -> when (sign) { 10 -> "Dm"; 11 -> "Dm"; 7 -> "Ex"; 4 -> "Dt"; 5 -> "Dt"; 1 -> "Fl"; else -> null }
        else      -> null
    }

    /**
     * Part of Fortune calculation.
     * Day chart (Sun in houses 7–12): ASC + Moon − Sun.
     * Night chart (Sun in houses 1–6): ASC + Sun − Moon.
     */
    fun partOfFortune(asc: Double, sun: Double, moon: Double, sunHouse: Int, cusps: List<Double>): PlanetPosition {
        val isDay = sunHouse in 7..12
        val pof   = norm(if (isDay) asc + moon - sun else asc + sun - moon)
        val si    = (pof / 30.0).toInt().coerceIn(0, 11)
        val deg   = (pof % 30.0).toInt()
        val min   = ((pof % 30.0 - deg) * 60.0).toInt()
        return PlanetPosition(pof, si + 1, deg, min, house(pof, cusps))
    }

    /** Returns the Unicode glyph for a major aspect, or null if no major aspect in range. */
    fun aspectSymbol(deg1: Double, deg2: Double): String? {
        val diff = abs(deg1 - deg2).let { if (it > 180.0) 360.0 - it else it }
        return when {
            diff          <= 8.0 -> "☌"
            abs(diff -  60.0) <= 4.0 -> "⚹"
            abs(diff -  90.0) <= 6.0 -> "□"
            abs(diff - 120.0) <= 7.0 -> "△"
            abs(diff - 150.0) <= 2.0 -> "⚻"
            abs(diff - 180.0) <= 8.0 -> "☍"
            else -> null
        }
    }

    private fun norm(a: Double): Double { var x = a % 360.0; if (x < 0) x += 360.0; return x }

    private fun house(lon: Double, cusps: List<Double>): Int {
        for (i in 0 until 12) {
            val s = cusps[i]; val e = cusps[(i + 1) % 12]
            if (if (e > s) lon >= s && lon < e else lon >= s || lon < e) return i + 1
        }
        return 1
    }
}
