package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Boundary behaviour of [AstroCalculator.planetHouse]. A house spans [cusp, next cusp); the body
 * sitting exactly on a cusp belongs to the house that cusp opens, and the 12th house wraps over 0°.
 */
class PlanetHouseTest {

    // Equal 30° houses starting at 0° Aries — easy to reason about boundaries.
    private val equalCusps = (0 until 12).map { it * 30.0 }

    @Test
    fun pointInsideAHouse() {
        assertEquals(1, AstroCalculator.planetHouse(15.0, equalCusps))
        assertEquals(5, AstroCalculator.planetHouse(125.0, equalCusps))
        assertEquals(12, AstroCalculator.planetHouse(345.0, equalCusps))
    }

    @Test
    fun pointExactlyOnCuspBelongsToTheHouseItOpens() {
        assertEquals(1, AstroCalculator.planetHouse(0.0, equalCusps))
        assertEquals(2, AstroCalculator.planetHouse(30.0, equalCusps))
        assertEquals(12, AstroCalculator.planetHouse(330.0, equalCusps))
    }

    @Test
    fun lastHouseWrapsAcrossZero() {
        // Unequal cusps where the 12th house straddles 0° Aries (cusp12 = 350°, cusp1 = 20°).
        val wrap = listOf(20.0, 55.0, 88.0, 120.0, 150.0, 182.0, 200.0, 235.0, 268.0, 300.0, 330.0, 350.0)
        assertEquals(12, AstroCalculator.planetHouse(355.0, wrap)) // before 0°
        assertEquals(12, AstroCalculator.planetHouse(5.0, wrap))   // after 0°, before cusp1
        assertEquals(1, AstroCalculator.planetHouse(20.0, wrap))   // on cusp1
        assertEquals(1, AstroCalculator.planetHouse(40.0, wrap))
    }
}
