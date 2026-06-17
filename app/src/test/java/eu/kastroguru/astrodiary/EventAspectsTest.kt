package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.EventAspects
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pure-logic checks for the gallery's "most exact aspect" selection. */
class EventAspectsTest {

    @Test
    fun picksTheSmallestOrbPairAndAngle() {
        // moon–venus are 90.3° apart (square, orb 0.3); every other pair is far from any aspect.
        val points = mapOf(
            "sun" to 0.0,
            "moon" to 100.0,
            "venus" to 190.3,   // 90.3° from moon; sun–moon=100° and sun–venus≈169.7° are both far
        )
        val a = EventAspects.mostExact(points)!!
        assertEquals(90, a.angle)
        assertEquals(0.3, a.orb, 1e-6)
        assertEquals(setOf("moon", "venus"), setOf(a.pointA, a.pointB))
    }

    @Test
    fun conjunctionWinsWhenTightest() {
        val points = mapOf("sun" to 12.0, "mercury" to 12.4, "moon" to 105.0)
        val a = EventAspects.mostExact(points)!!
        assertEquals(0, a.angle)
        assertEquals(setOf("sun", "mercury"), setOf(a.pointA, a.pointB))
        assertTrue(a.orb < 0.5)
    }

    @Test
    fun nullWhenFewerThanTwoPoints() {
        assertNull(EventAspects.mostExact(mapOf("sun" to 10.0)))
        assertNull(EventAspects.mostExact(emptyMap()))
    }
}
