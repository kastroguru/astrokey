package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.calculator.TransitTimeline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

/**
 * The timeline is fed a synthetic ephemeris, so the expected dates are exact arithmetic rather than
 * "whatever Swiss Ephemeris said" — including the retrograde loop that makes an outer planet perfect
 * the same aspect three times.
 */
class TransitTimelineTest {

    private val nowJd = 2460000.5
    private val day = 86_400_000.0
    private fun msOf(jd: Double) = Math.round((jd - 2440587.5) * day)
    private fun daysFromNow(ms: Long) = (ms - msOf(nowJd)) / day

    @Test
    fun directMotionEntersPerfectsAndLeaves() {
        // 0.5°/day, exact on the natal degree right now; orb 2° ⇒ four days either side.
        val passage = TransitTimeline.passage(
            transitKey = "mars", natalLongitude = 100.0, aspectDeg = 0, orb = 2.0, nowJd = nowJd,
            longitudeAt = { jd -> 100.0 + 0.5 * (jd - nowJd) }
        )!!
        assertEquals(1, passage.exactMs.size)
        assertEquals(0.0, daysFromNow(passage.exactMs[0]), 0.01)
        assertEquals(-4.0, daysFromNow(passage.enterMs!!), 0.02)
        assertEquals(4.0, daysFromNow(passage.exitMs!!), 0.02)
        assertTrue(!passage.isRepeating)
    }

    @Test
    fun retrogradeLoopPerfectsTheAspectThreeTimes() {
        // Slow forward drift with a retrograde loop on top: crosses the natal degree three times.
        val passage = TransitTimeline.passage(
            transitKey = "uranus", natalLongitude = 100.0, aspectDeg = 0, orb = 2.0, nowJd = nowJd,
            longitudeAt = { jd ->
                val t = jd - nowJd
                100.0 + 0.01 * t - 2.0 * sin(2 * PI * t / 200.0)
            }
        )!!
        assertEquals(3, passage.exactMs.size)
        assertTrue(passage.isRepeating)
        val d = passage.exactMs.map { daysFromNow(it) }
        assertTrue("hits should straddle now: $d", d[0] < -30 && d[2] > 30)
        assertEquals(0.0, d[1], 0.05)                       // the middle hit is the current one
    }

    @Test
    fun picksTheSideOfTheNatalPointThePlanetIsOn() {
        // Natal 10°, square: exact at 100° and at 280°. The planet sits near 100°, so that is the
        // side to report — not a date months away on the other side.
        val passage = TransitTimeline.passage(
            transitKey = "venus", natalLongitude = 10.0, aspectDeg = 90, orb = 2.0, nowJd = nowJd,
            longitudeAt = { jd -> 100.0 + 0.5 * (jd - nowJd) }
        )!!
        assertEquals(1, passage.exactMs.size)
        assertEquals(0.0, daysFromNow(passage.exactMs[0]), 0.01)
    }

    @Test
    fun reportsTheWindowEvenWhenTheAspectNeverPerfects() {
        // Comes within 1° and pulls away again — active, but never exact.
        val passage = TransitTimeline.passage(
            transitKey = "saturn", natalLongitude = 100.0, aspectDeg = 0, orb = 2.0, nowJd = nowJd,
            longitudeAt = { jd ->
                val t = (jd - nowJd) / 100.0
                100.0 + 1.0 + t * t
            }
        )!!
        assertTrue(passage.exactMs.isEmpty())
        assertEquals(-100.0, daysFromNow(passage.enterMs!!), 0.5)
        assertEquals(100.0, daysFromNow(passage.exitMs!!), 0.5)
    }

    @Test
    fun theCurvePeaksAtEveryExactHitAndFallsToZeroOutsideOrb() {
        val passage = TransitTimeline.passage(
            transitKey = "uranus", natalLongitude = 100.0, aspectDeg = 0, orb = 2.0, nowJd = nowJd,
            longitudeAt = { jd ->
                val t = jd - nowJd
                100.0 + 0.01 * t - 2.0 * sin(2 * PI * t / 200.0)
            }
        )!!
        assertTrue("the curve should span the scan", passage.curve.size > 100)
        assertTrue("strength never leaves 0..1", passage.curve.all { it.strength in 0.0..1.0 })

        // Each exact hit sits under a peak of the curve…
        for (hit in passage.exactMs) {
            val nearest = passage.curve.minByOrNull { kotlin.math.abs(it.ms - hit) }!!
            assertTrue("curve should be near 1 at an exact hit, was ${nearest.strength}",
                nearest.strength > 0.9)
        }
        // …and three separate peaks means three windows of activity, not one long one.
        val peaks = passage.curve.zipWithNext().count { (a, b) -> a.strength == 0.0 && b.strength > 0.0 }
        assertEquals(3, peaks)
        assertEquals(0.0, passage.curve.first().strength, 0.0001)
        assertEquals(0.0, passage.curve.last().strength, 0.0001)
    }

    @Test
    fun leavesTheEdgeOpenWhenTheAspectOutlastsTheScan() {
        // Barely moving: still in orb at both ends of the scanned window.
        val passage = TransitTimeline.passage(
            transitKey = "pluto", natalLongitude = 100.0, aspectDeg = 0, orb = 2.0, nowJd = nowJd,
            longitudeAt = { jd -> 100.0 + 0.0001 * (jd - nowJd) }
        )!!
        assertNull(passage.enterMs)
        assertNull(passage.exitMs)
    }
}
