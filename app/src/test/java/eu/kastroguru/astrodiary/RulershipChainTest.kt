package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.RulershipChain
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The chain the whole "planet in a house whose ruler sits elsewhere" reading rests on, plus the
 * rulerships themselves — pinned, because the app is to use the ancient assignment only and a silent
 * drift back to Pluto/Uranus/Neptune would change every one of those readings without a word.
 */
class RulershipChainTest {

    /** Twelve equal houses starting at 0° Aries, so house N spans (N-1)*30 .. N*30. */
    private val equalCusps = (0 until 12).map { it * 30.0 }

    @Test
    fun rulershipsAreTheAncientOnes() {
        val expected = mapOf(
            ZodiacSign.ARIES to "mars", ZodiacSign.TAURUS to "venus", ZodiacSign.GEMINI to "mercury",
            ZodiacSign.CANCER to "moon", ZodiacSign.LEO to "sun", ZodiacSign.VIRGO to "mercury",
            ZodiacSign.LIBRA to "venus", ZodiacSign.SCORPIO to "mars",
            ZodiacSign.SAGITTARIUS to "jupiter", ZodiacSign.CAPRICORN to "saturn",
            ZodiacSign.AQUARIUS to "saturn", ZodiacSign.PISCES to "jupiter",
        )
        expected.forEach { (sign, ruler) ->
            assertEquals("ruler of $sign", ruler, RulershipChain.rulerOfSign(sign))
        }
    }

    @Test
    fun noOuterPlanetEverRulesASign() {
        val outers = setOf("uranus", "neptune", "pluto", "chiron", "rahu", "lilith")
        val wrong = RulershipChain.rulers.filterValues { it in outers }
        assertTrue("Modern rulerships crept back in: $wrong", wrong.isEmpty())
    }

    @Test
    fun followsAllFourStepsOfTheChain() {
        // Sun at 125° = Leo, which falls in house 5 (120–150). House 5's cusp is 120° = Leo, ruled by
        // the Sun itself — so the Sun rules the house it stands in.
        val longitudes = mapOf("sun" to 125.0, "moon" to 200.0, "mars" to 15.0)
        val sun = RulershipChain.linkFor("sun", longitudes, equalCusps)!!
        assertEquals(5, sun.houseOfPlanet)
        assertEquals(ZodiacSign.LEO, sun.cuspSign)
        assertEquals("sun", sun.rulerKey)
        assertEquals(5, sun.houseOfRuler)
        assertTrue(sun.isItsOwnRuler)
        assertTrue(sun.rulerIsAtHome)
    }

    @Test
    fun pointsAtTheHouseWhereTheRulerActuallyStands() {
        // Moon at 200° = Libra, house 7 (180–210). Cusp of 7 is 180° = Libra, ruled by Venus.
        // Venus is at 40° = house 2 — so the 7th-house matter is decided in the 2nd.
        val longitudes = mapOf("moon" to 200.0, "venus" to 40.0)
        val moon = RulershipChain.linkFor("moon", longitudes, equalCusps)!!
        assertEquals(7, moon.houseOfPlanet)
        assertEquals(ZodiacSign.LIBRA, moon.cuspSign)
        assertEquals("venus", moon.rulerKey)
        assertEquals(2, moon.houseOfRuler)
        assertFalse(moon.isItsOwnRuler)
        assertFalse(moon.rulerIsAtHome)
    }

    @Test
    fun scorpioCuspLeadsToMarsAndNotToPluto() {
        // A house with Scorpio on the cusp: 210° = Scorpio, house 8 under equal houses.
        val longitudes = mapOf("saturn" to 220.0, "mars" to 95.0, "pluto" to 300.0)
        val saturn = RulershipChain.linkFor("saturn", longitudes, equalCusps)!!
        assertEquals(ZodiacSign.SCORPIO, saturn.cuspSign)
        assertEquals("mars", saturn.rulerKey)
        assertEquals(4, saturn.houseOfRuler)      // Mars at 95° is in house 4 (90–120)
    }

    @Test
    fun worksWithUnequalHousesThatWrapPastZero() {
        // Cusps starting late in the zodiac, so the last houses cross 0° Aries.
        val cusps = listOf(300.0, 330.0, 5.0, 35.0, 65.0, 95.0, 120.0, 150.0, 185.0, 215.0, 245.0, 275.0)
        val longitudes = mapOf("jupiter" to 350.0, "saturn" to 100.0)
        val jupiter = RulershipChain.linkFor("jupiter", longitudes, cusps)!!
        assertEquals(2, jupiter.houseOfPlanet)                 // 330–5 wraps across 0°
        assertEquals(ZodiacSign.PISCES, jupiter.cuspSign)      // cusp 2 at 330° = Pisces
        assertEquals("jupiter", jupiter.rulerKey)              // ancient ruler of Pisces
        assertEquals(2, jupiter.houseOfRuler)
    }

    @Test
    fun missingDataIsHandledRatherThanGuessed() {
        assertEquals(null, RulershipChain.linkFor("sun", mapOf("sun" to 10.0), listOf(0.0, 30.0)))
        assertEquals(null, RulershipChain.linkFor("venus", mapOf("sun" to 10.0), equalCusps))
        // Cusp in Leo needs the Sun's position to finish the chain; without it there is no answer.
        assertEquals(null, RulershipChain.linkFor("moon", mapOf("moon" to 125.0), equalCusps))
    }
}
