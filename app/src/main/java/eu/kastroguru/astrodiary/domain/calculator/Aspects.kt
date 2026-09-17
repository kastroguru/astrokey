package eu.kastroguru.astrodiary.domain.calculator

import kotlin.math.abs

/**
 * Aspects inside one chart, as a pure function over longitudes.
 *
 * The same double loop is written out in five other places in this app, each with its own orb
 * table — `ChartUtil.aspectSymbol`, `AspectGridBuilder`, `AstroChartView`, `TransitViewModel` and
 * `TransitFocusWheelView` — and no two of them agree on the orb for a square. This is the one
 * callers should use from here on; the old five are left alone for now because they feed drawing
 * code whose output is pixel-compared by eye, not by test.
 *
 * The orbs are the natal table the wheel already draws with, so a contact this object reports is a
 * contact the user can see on the chart.
 */
object Aspects {

    enum class Kind(val angle: Int) {
        CONJUNCTION(0),
        SEXTILE(60),
        SQUARE(90),
        TRINE(120),
        QUINCUNX(150),
        OPPOSITION(180);

        /** Square, opposition and quincunx: the contacts that ask for relief from the partner. */
        val isHard: Boolean get() = this == SQUARE || this == OPPOSITION || this == QUINCUNX

        /** Trine and sextile. The conjunction is neither — it is its own thing. */
        val isEasy: Boolean get() = this == TRINE || this == SEXTILE
    }

    /** Natal orbs, matching `AstroChartView`'s table, which is what the wheel draws. */
    val NATAL_ORBS: Map<Kind, Double> = mapOf(
        Kind.CONJUNCTION to 8.0,
        Kind.SEXTILE to 6.0,
        Kind.SQUARE to 8.0,
        Kind.TRINE to 8.0,
        Kind.QUINCUNX to 5.0,
        Kind.OPPOSITION to 8.0,
    )

    /** Angular separation folded into 0..180. */
    fun separation(a: Double, b: Double): Double {
        val raw = abs(a - b) % 360.0
        return if (raw > 180.0) 360.0 - raw else raw
    }

    /**
     * The aspect between two longitudes, or null if none is within orb. When two aspects could
     * both match — impossible with these orbs, but the code should not depend on that — the
     * tighter one wins.
     */
    fun between(lonA: Double, lonB: Double, orbs: Map<Kind, Double> = NATAL_ORBS): Kind? {
        val sep = separation(lonA, lonB)
        var best: Kind? = null
        var bestOrb = Double.MAX_VALUE
        for (kind in Kind.values()) {
            val maxOrb = orbs[kind] ?: continue
            val orb = abs(sep - kind.angle)
            if (orb <= maxOrb && orb < bestOrb) {
                best = kind
                bestOrb = orb
            }
        }
        return best
    }

    /** Convenience over a map of longitudes: is there any aspect between these two bodies? */
    fun between(
        longitudes: Map<String, Double>,
        keyA: String,
        keyB: String,
        orbs: Map<Kind, Double> = NATAL_ORBS,
    ): Kind? {
        val a = longitudes[keyA] ?: return null
        val b = longitudes[keyB] ?: return null
        return between(a, b, orbs)
    }
}
