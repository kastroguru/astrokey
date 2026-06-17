package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.calculator.PrimaryDirectionsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure-geometry invariants for [PrimaryDirectionsCalculator.directedPositions].
 *
 * These need no Swiss Ephemeris / data files — they exercise the ecliptic↔equatorial projection
 * and the direction convention (direct = clockwise on the wheel = decreasing RA) directly, so they
 * run instantly and pin the behaviour the rest of the feature depends on.
 */
class DirectedPositionsTest {

    private val calc = PrimaryDirectionsCalculator()
    private val obliquity = 23.4393

    /** Latitude-0 ecliptic longitude → right ascension. Mirrors the private eclToEqu in the engine. */
    private fun ra0(lonDeg: Double): Double {
        val l = Math.toRadians(lonDeg)
        val e = Math.toRadians(obliquity)
        return Math.toDegrees(atan2(sin(l) * cos(e), cos(l))).let { (it % 360 + 360) % 360 }
    }

    private fun angDiff(a: Double, b: Double): Double {
        var d = (a - b) % 360.0
        if (d < -180) d += 360.0
        if (d > 180) d -= 360.0
        return d
    }

    private val sampleLons = listOf(0.0, 12.5, 47.0, 89.9, 90.1, 134.0, 180.0, 222.7, 270.0, 315.3, 359.5)

    @Test
    fun arcZeroLeavesEveryPointOnItsNatalDegree() {
        val pos = calc.directedPositions(sampleLons.associateBy { "p$it" }, obliquity, 0.0)
        for (p in pos) {
            assertTrue(
                "arc=0 should not move ${p.key}: natal=${p.natalLon} direct=${p.directLon} converse=${p.converseLon}",
                abs(angDiff(p.directLon, p.natalLon)) < 1e-6 &&
                    abs(angDiff(p.converseLon, p.natalLon)) < 1e-6,
            )
        }
    }

    @Test
    fun directIsDecreasingRaConverseIsIncreasingRa() {
        val arc = 10.0
        val pos = calc.directedPositions(sampleLons.associateBy { "p$it" }, obliquity, arc)
        for (p in pos) {
            val natalRa = ra0(p.natalLon)
            // Direct carries the body WITH diurnal motion (clockwise on the wheel) = RA − arc.
            assertEquals(
                "direct RA should be natalRA−arc for ${p.key}",
                0.0, angDiff(ra0(p.directLon), natalRa - arc), 1e-6,
            )
            // Converse carries it the other way = RA + arc.
            assertEquals(
                "converse RA should be natalRA+arc for ${p.key}",
                0.0, angDiff(ra0(p.converseLon), natalRa + arc), 1e-6,
            )
        }
    }

    @Test
    fun directAndConverseAreSymmetricAboutNatalInRa() {
        val arc = 23.7
        val pos = calc.directedPositions(sampleLons.associateBy { "p$it" }, obliquity, arc)
        for (p in pos) {
            val natalRa = ra0(p.natalLon)
            val behind = angDiff(natalRa, ra0(p.directLon))   // +arc
            val ahead = angDiff(ra0(p.converseLon), natalRa)  // +arc
            assertEquals("direct/converse not symmetric for ${p.key}", behind, ahead, 1e-6)
            assertEquals("symmetric offset should equal arc for ${p.key}", arc, behind, 1e-6)
        }
    }

    @Test
    fun equToEclInvertsEclToEquOnTheEcliptic() {
        // equToEcl(ra0(lon)) must return lon for every sample — the projection round-trips.
        for (lon in sampleLons) {
            val back = calc.equToEcl(ra0(lon), obliquity)
            assertEquals("round-trip failed for lon=$lon", 0.0, angDiff(back, lon), 1e-6)
        }
    }
}
