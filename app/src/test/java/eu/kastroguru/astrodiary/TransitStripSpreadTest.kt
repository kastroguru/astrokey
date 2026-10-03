package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.ui.chart.spreadAround
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random

/**
 * Label spreading on the transit strip ([spreadAround]). The tick and the aspect lines always sit on
 * the exact degree; only the label moves, and it should move as little as legibility allows.
 */
class TransitStripSpreadTest {

    private val eps = 1e-9
    private fun spread(vararg pos: Double, half: Double = 0.8) =
        spreadAround(pos.toList(), List(pos.size) { half }, 0.0, 30.0)

    @Test
    fun labelsWithRoomStayExactlyOnTheirDegree() {
        assertArrayEquals(doubleArrayOf(3.0, 10.0, 20.0), spread(3.0, 10.0, 20.0), eps)
    }

    @Test
    fun twoCrowdedLabelsMoveApartEvenly() {
        // 10° and 10°24' need 1.6° between them: each gives way by the same 0.6°.
        assertArrayEquals(doubleArrayOf(9.4, 11.0), spread(10.0, 10.4), eps)
    }

    @Test
    fun aPileUpIsCentredNotPushedOneWay() {
        // Five planets within 2°. Pushing rightwards put the last one 4.4° off; centred, the middle
        // one stays put and nobody moves more than 2.2°.
        val pos = doubleArrayOf(4.0, 4.5, 5.0, 5.5, 6.0)
        val shown = spread(*pos)
        assertArrayEquals(doubleArrayOf(1.8, 3.4, 5.0, 6.6, 8.2), shown, eps)
        assertEquals(2.2, pos.indices.maxOf { abs(shown[it] - pos[it]) }, eps)
    }

    @Test
    fun labelsAtTheEndsStayOnTheScale() {
        val high = spread(29.5, 29.8, 29.9)
        assertTrue(high.all { it <= 30.0 + eps })
        val low = spread(0.0, 0.1)
        assertTrue(low.all { it >= 0.0 - eps })
    }

    @Test
    fun neighboursNeverOverlapAndNeverSwapPlaces() {
        val rnd = Random(7)
        repeat(500) {
            val n = rnd.nextInt(1, 15)
            val pos = List(n) { rnd.nextDouble(0.0, 30.0) }
            val half = List(n) { rnd.nextDouble(0.5, 0.9) }
            val shown = spreadAround(pos, half, 0.0, 30.0)
            val order = pos.indices.sortedBy { pos[it] }
            for ((a, b) in order.zipWithNext()) {
                assertTrue("labels $a and $b overlap", shown[b] - shown[a] >= half[a] + half[b] - eps)
            }
            assertTrue(shown.all { it in -eps..30.0 + eps })
        }
    }
}
