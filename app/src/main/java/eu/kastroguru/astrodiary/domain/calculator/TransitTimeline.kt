package eu.kastroguru.astrodiary.domain.calculator

import kotlin.math.abs

/**
 * When a transit aspect starts, when it is exact, and when it fades.
 *
 * A transit is not a moment but a stretch of time: the planet drifts into orb, perfects the aspect,
 * and drifts out. Outer planets often perfect the same aspect **three times** — direct, then
 * retrograde back over the same degree, then direct again — which is why a theme "comes back"
 * months later. This scans the planet's longitude across a window around a given moment and reports
 * all of it, so the screen can say it in words instead of leaving the user with a bare orb number.
 *
 * Pure: the caller supplies the longitude lookup, so it is testable without an ephemeris.
 */
object TransitTimeline {

    /** The orb the app treats as "active" for transit aspects — shared with the aspect list. */
    const val TRANSIT_ORB_DEG = 2.0

    /** How strong the aspect is at a moment: 1.0 exact, 0.0 at the edge of orb or beyond. */
    data class Sample(val ms: Long, val strength: Double)

    data class Passage(
        /** Start/end of the in-orb stretch containing the reference moment (null = beyond the scan). */
        val enterMs: Long?,
        val exitMs: Long?,
        /** Every exact hit inside the scanned window, chronological. */
        val exactMs: List<Long>,
        val scannedFromMs: Long,
        val scannedToMs: Long,
        /** The whole scanned window as a curve, for drawing when the aspect waxes and wanes. */
        val curve: List<Sample> = emptyList(),
    ) {
        /** True when the planet perfects the aspect more than once (a retrograde loop). */
        val isRepeating: Boolean get() = exactMs.size > 1
    }

    /** How far to look and how finely, per body — a Moon transit lasts hours, a Pluto one years. */
    private data class Scan(val spanDays: Double, val stepDays: Double)

    private val SCANS = mapOf(
        "moon"    to Scan(5.0, 0.02),
        "sun"     to Scan(45.0, 0.25),
        "mercury" to Scan(150.0, 0.25),
        "venus"   to Scan(220.0, 0.5),
        "mars"    to Scan(420.0, 0.5),
        "jupiter" to Scan(540.0, 2.0),
        "saturn"  to Scan(730.0, 2.0),
        "chiron"  to Scan(900.0, 4.0),
        "uranus"  to Scan(900.0, 4.0),
        "neptune" to Scan(900.0, 4.0),
        "pluto"   to Scan(900.0, 4.0),
        "rahu"    to Scan(540.0, 1.0),
        "lilith"  to Scan(540.0, 1.0),
    )
    private val DEFAULT_SCAN = Scan(400.0, 1.0)

    /**
     * @param longitudeAt ecliptic longitude of the transiting body at a Julian day, null if unknown
     * @param natalLongitude the natal point being aspected
     * @param aspectDeg 0/60/90/120/150/180
     * @param orb the orb (in degrees) the app treats as "active"
     * @param isActive checked while sampling, so leaving the screen abandons the scan instead of
     *   burning a few hundred ephemeris calls nobody will see
     */
    fun passage(
        transitKey: String,
        natalLongitude: Double,
        aspectDeg: Int,
        orb: Double,
        nowJd: Double,
        isActive: () -> Boolean = { true },
        longitudeAt: (Double) -> Double?,
    ): Passage? {
        val scan = SCANS[transitKey] ?: DEFAULT_SCAN
        val nowLon = longitudeAt(nowJd) ?: return null

        // An aspect of A degrees is exact on either side of the natal point; follow the side the
        // planet is actually on.
        val target = listOf(natalLongitude + aspectDeg, natalLongitude - aspectDeg)
            .minByOrNull { abs(wrap180(nowLon - it)) } ?: return null

        fun offset(jd: Double): Double? = longitudeAt(jd)?.let { wrap180(it - target) }

        val fromJd = nowJd - scan.spanDays
        val toJd = nowJd + scan.spanDays

        // One coarse pass over the window; everything else is refinement of what it finds.
        val samples = ArrayList<Pair<Double, Double>>()
        var jd = fromJd
        while (jd <= toJd) {
            if (!isActive()) return null
            offset(jd)?.let { samples += jd to it }
            jd += scan.stepDays
        }
        if (samples.size < 2) return null

        val exact = ArrayList<Long>()
        for (i in 0 until samples.size - 1) {
            val (jd1, o1) = samples[i]
            val (jd2, o2) = samples[i + 1]
            // Ignore the ±180° wrap: a real crossing has both samples near zero.
            if (o1 == 0.0) exact += msOf(jd1)
            else if (o1 * o2 < 0 && abs(o1) + abs(o2) < 90.0) {
                exact += msOf(refine(jd1, jd2, ::offset))
            }
        }

        val enter = boundary(samples, nowJd, orb, forward = false, offset = ::offset)
        val exit = boundary(samples, nowJd, orb, forward = true, offset = ::offset)

        return Passage(
            enterMs = enter,
            exitMs = exit,
            exactMs = exact.distinct().sorted(),
            scannedFromMs = msOf(fromJd),
            scannedToMs = msOf(toJd),
            curve = samples.map { (jd, off) ->
                Sample(msOf(jd), (1.0 - abs(off) / orb).coerceAtLeast(0.0))
            },
        )
    }

    /**
     * Walks out from the reference moment to where |offset| crosses the orb — the edge of the
     * stretch the user is currently inside. Null when the planet is still in orb at the scan edge.
     */
    private fun boundary(
        samples: List<Pair<Double, Double>>,
        nowJd: Double,
        orb: Double,
        forward: Boolean,
        offset: (Double) -> Double?,
    ): Long? {
        val nowIndex = samples.indexOfFirst { it.first >= nowJd }.let { if (it < 0) samples.size - 1 else it }
        val range = if (forward) nowIndex until samples.size - 1 else (1..nowIndex).reversed()
        for (i in range) {
            val a = samples[i]
            val b = if (forward) samples[i + 1] else samples[i - 1]
            if (abs(a.second) <= orb && abs(b.second) > orb) {
                val crossing = refine(a.first, b.first) { jd -> offset(jd)?.let { abs(it) - orb } }
                return msOf(crossing)
            }
        }
        return null
    }

    /** Bisection on a function that changes sign between [lo] and [hi]. */
    private fun refine(lo: Double, hi: Double, f: (Double) -> Double?): Double {
        var a = lo
        var b = hi
        val fa = f(a) ?: return lo
        if (fa == 0.0) return a          // already sitting on the crossing
        repeat(40) {
            val mid = (a + b) / 2
            val fm = f(mid) ?: return mid
            if (fm == 0.0) return mid
            if ((fa < 0) == (fm < 0)) a = mid else b = mid
            if (abs(b - a) < 1.0 / 1440.0) return (a + b) / 2      // one minute is plenty
        }
        return (a + b) / 2
    }

    private fun msOf(jd: Double): Long = Math.round((jd - 2440587.5) * 86_400_000.0)

    /** Signed angular difference in (-180, 180]. */
    private fun wrap180(a: Double): Double {
        var x = a % 360.0
        if (x <= -180.0) x += 360.0
        if (x > 180.0) x -= 360.0
        return x
    }
}
