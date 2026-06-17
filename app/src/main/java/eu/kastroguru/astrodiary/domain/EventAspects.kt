package eu.kastroguru.astrodiary.domain

import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import kotlin.math.abs

/**
 * Finds the tightest (smallest-orb) aspect within an event's chart — used to generate the default
 * gallery thumbnail. Aspect points are the 13 planets plus the Ascendant and MC; aspects are the six
 * majors (conjunction, sextile, square, trine, quincunx, opposition). Pure, so it recomputes for free
 * whenever the event's stored positions change (i.e. after a date/time edit).
 */
object EventAspects {

    val ASPECT_ANGLES = intArrayOf(0, 60, 90, 120, 150, 180)

    data class TightAspect(val pointA: String, val pointB: String, val angle: Int, val orb: Double)

    /** Aspect points for an event: the 13 planets plus the Ascendant (cusp 1) and MC (cusp 10). */
    fun pointsOf(e: HistoryEventEntity): LinkedHashMap<String, Double> = linkedMapOf(
        "sun" to e.sunD, "moon" to e.moonD, "mercury" to e.mercuryD, "venus" to e.venusD,
        "mars" to e.marsD, "jupiter" to e.jupiterD, "saturn" to e.saturnD, "uranus" to e.uranusD,
        "neptune" to e.neptuneD, "pluto" to e.plutoD, "chiron" to e.chironD, "rahu" to e.rahuD,
        "lilith" to e.lilithD, "asc" to e.cusp1, "mc" to e.cusp10,
    )

    fun mostExact(e: HistoryEventEntity): TightAspect? = mostExact(pointsOf(e))

    /** The pair of points forming the tightest major aspect, or null if fewer than 2 points. */
    fun mostExact(points: Map<String, Double>): TightAspect? {
        val keys = points.keys.toList()
        var best: TightAspect? = null
        for (i in keys.indices) for (j in i + 1 until keys.size) {
            val sep = separation(points.getValue(keys[i]), points.getValue(keys[j]))
            for (angle in ASPECT_ANGLES) {
                val orb = abs(sep - angle)
                if (best == null || orb < best.orb) best = TightAspect(keys[i], keys[j], angle, orb)
            }
        }
        return best
    }

    /** Angular separation 0..180° between two ecliptic longitudes. */
    private fun separation(a: Double, b: Double): Double {
        val raw = abs(a - b) % 360.0
        return if (raw > 180.0) 360.0 - raw else raw
    }
}
