package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.RulershipChain
import eu.kastroguru.astrodiary.domain.interpretation.AngleMeanings
import eu.kastroguru.astrodiary.domain.interpretation.ChartConcepts
import eu.kastroguru.astrodiary.domain.interpretation.HouseMeanings
import eu.kastroguru.astrodiary.domain.interpretation.NatalInterpretations
import eu.kastroguru.astrodiary.domain.interpretation.PlanetMeanings
import eu.kastroguru.astrodiary.domain.interpretation.SignMeanings
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The interpretation layer is written over time, so this guards the two things that must hold at
 * every point along the way: the building blocks are complete in both languages, and no lookup can
 * ever hand a screen an empty explanation — a blank card is worse than a plain one.
 */
class InterpretationCoverageTest {

    private val houses = 1..12
    private val signs = ZodiacSign.values().toList()

    @Test
    fun everyBuildingBlockIsWrittenInBothLanguages() {
        val gaps = mutableListOf<String>()
        PlanetMeanings.byKey.forEach { (k, v) -> if (!v.isComplete) gaps += "planet $k" }
        SignMeanings.bySign.forEach { (k, v) -> if (!v.isComplete) gaps += "sign $k" }
        HouseMeanings.byHouse.forEach { (k, v) -> if (!v.isComplete) gaps += "house $k" }
        ChartConcepts.all.forEach { (k, v) -> if (!v.isComplete) gaps += "concept $k" }
        AngleMeanings.byKey.forEach { (k, v) -> if (!v.isComplete) gaps += "angle $k" }
        assertTrue("Written in only one language: $gaps", gaps.isEmpty())
    }

    @Test
    fun allThirteenBodiesAndTwelveSignsAndHousesAreCovered() {
        assertEquals(13, PlanetMeanings.byKey.size)
        assertEquals(12, SignMeanings.bySign.size)
        assertEquals(12, HouseMeanings.byHouse.size)
    }

    @Test
    fun everyPlanetInEverySignSaysSomething() {
        val empty = PlanetMeanings.byKey.keys.flatMap { planet ->
            signs.mapNotNull { sign ->
                val text = NatalInterpretations.planetInSign(planet, sign)
                if (text == null || !text.isComplete) "$planet in $sign" else null
            }
        }
        assertTrue("No interpretation available for: $empty", empty.isEmpty())
    }

    @Test
    fun everyPlanetInEveryHouseSaysSomething() {
        val empty = PlanetMeanings.byKey.keys.flatMap { planet ->
            houses.mapNotNull { house ->
                val text = NatalInterpretations.planetInHouse(planet, house)
                if (text == null || !text.isComplete) "$planet in house $house" else null
            }
        }
        assertTrue("No interpretation available for: $empty", empty.isEmpty())
    }

    @Test
    fun bothAnglesInEverySignSayeSomething() {
        val empty = AngleMeanings.byKey.keys.flatMap { angle ->
            signs.mapNotNull { sign ->
                val text = NatalInterpretations.angleInSign(angle, sign)
                if (text == null || !text.isComplete) "$angle in $sign" else null
            }
        }
        assertTrue("No interpretation available for: $empty", empty.isEmpty())
    }

    @Test
    fun everyPlanetInEveryHouseWithEveryRulerHouseSaysSomething() {
        // 13 bodies x 12 houses x 12 ruler houses = 1,872 readings. None may come back blank.
        val empty = mutableListOf<String>()
        for (planet in PlanetMeanings.byKey.keys) {
            for (house in houses) {
                for (rulerHouse in houses) {
                    val link = RulershipChain.Link(
                        planetKey = planet,
                        houseOfPlanet = house,
                        cuspSign = ZodiacSign.ARIES,
                        rulerKey = if (rulerHouse == house) planet else "venus",
                        houseOfRuler = rulerHouse,
                    )
                    val text = NatalInterpretations.planetInHouseWithRuler(link)
                    if (text == null || !text.isComplete) empty += "$planet h$house ruler h$rulerHouse"
                }
            }
        }
        assertTrue("No interpretation available for: ${empty.take(10)} (${empty.size} total)", empty.isEmpty())
    }

    @Test
    fun theThreeShapesOfTheChainReadDifferently() {
        fun link(planet: String, house: Int, ruler: String, rulerHouse: Int) = RulershipChain.Link(
            planet, house, ZodiacSign.ARIES, ruler, rulerHouse
        )
        val elsewhere = NatalInterpretations.planetInHouseWithRuler(link("mars", 5, "venus", 9))!!
        val ownRuler = NatalInterpretations.planetInHouseWithRuler(link("mars", 5, "mars", 5))!!
        val atHome = NatalInterpretations.planetInHouseWithRuler(link("mars", 5, "venus", 5))!!
        assertTrue(elsewhere.bg != ownRuler.bg && ownRuler.bg != atHome.bg && elsewhere.bg != atHome.bg)
        println("── пример: Марс в 5 дом, владетелят в 9 ──")
        println(elsewhere.bg)
    }

    @Test
    fun everyRulerPlacementSaysSomething() {
        val empty = houses.flatMap { from ->
            houses.mapNotNull { to ->
                val text = NatalInterpretations.rulerOfHouseInHouse(from, to)
                if (text == null || !text.isComplete) "ruler of $from in $to" else null
            }
        }
        assertTrue("No interpretation available for: $empty", empty.isEmpty())
    }

    @Test
    fun everyInterceptedSignSaysSomething() {
        val empty = houses.flatMap { house ->
            signs.mapNotNull { sign ->
                val text = NatalInterpretations.interceptedHouse(house, sign)
                if (text == null || !text.isComplete) "$sign intercepted in $house" else null
            }
        }
        assertTrue("No interpretation available for: $empty", empty.isEmpty())
    }

    @Test
    fun handWrittenEntriesAreNeverHalfTranslated() {
        val half = (NatalInterpretations.writtenChain +
            NatalInterpretations.writtenPlanetInSign +
            NatalInterpretations.writtenPlanetInHouse +
            NatalInterpretations.writtenAngleInSign +
            NatalInterpretations.writtenRulerInHouse)
            .filterValues { !it.isComplete }.keys
        assertTrue("Hand-written but missing a language: $half", half.isEmpty())
    }

    @Test
    fun reportProgress() {
        println("── interpretation coverage ──")
        NatalInterpretations.coverage().forEach { (name, c) ->
            println("   %-24s %4d / %-4d  %3d%%".format(name, c.written, c.total, c.percent))
        }
        println("   building blocks: %d bodies, %d signs, %d houses, %d concepts"
            .format(PlanetMeanings.byKey.size, SignMeanings.bySign.size,
                HouseMeanings.byHouse.size, ChartConcepts.all.size))
    }
}
