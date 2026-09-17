package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.calculator.Aspects
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.domain.synastry.Resonance
import eu.kastroguru.astrodiary.domain.synastry.SymbolicSignature
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The resonance model, pinned to the rules as the owner stated them.
 *
 * Most of these guard against the *plausible wrong version* rather than against a typo: the model
 * is asymmetric in two ways that any later refactor will be tempted to tidy away — houses answer
 * needs but never raise them, and a need belongs to the point, not to the planet that names its
 * colour. Both are tested from both sides.
 */
class SynastryResonanceTest {

    // ── Building charts by hand ───────────────────────────────────────────────

    /** Equal houses from a given ascendant, so a house number is predictable in a test. */
    private fun cusps(ascendant: Double): List<Double> =
        (0 until 12).map { (ascendant + it * 30.0) % 360.0 }

    private fun degreesIn(sign: ZodiacSign, into: Double = 15.0) = (sign.id - 1) * 30.0 + into

    /**
     * A chart holding **only** the bodies named, on equal houses from the given ascendant.
     *
     * Leaving the rest out is deliberate. An earlier version parked every unnamed body at a fixed
     * spread and the spread quietly made trines, quincunxes and oppositions of its own, so tests
     * passed or failed for reasons that had nothing to do with the rule under test. A chart with
     * three bodies in it raises exactly the needs those three bodies raise.
     */
    private fun chart(
        ascendant: Double = degreesIn(ZodiacSign.ARIES, 0.0),
        vararg bodies: Pair<String, Double>,
    ): Resonance.Chart = Resonance.Chart(bodies.toMap(), cusps(ascendant))

    /** The ascendant that drops [sign] into house [house], on equal houses. */
    private fun ascendantPutting(sign: ZodiacSign, into: Int): Double =
        degreesIn(ZodiacSign.fromId((sign.id - into + 12) % 12 + 1), 0.0)

    private fun needFor(chart: Resonance.Chart, point: String, colour: String) =
        Resonance.needs(chart).firstOrNull { it.point == point && it.colour == colour }

    // ── The owner's four worked examples ──────────────────────────────────────

    @Test fun moonInLeoAsksTheMoonForSolarColour() {
        val a = chart(bodies = arrayOf("moon" to degreesIn(ZodiacSign.LEO)))
        val need = needFor(a, "moon", "sun")
        assertNotNull("The Moon in Leo must ask for solar colour", need)

        // Their Moon in Leo.
        assertEquals(
            Resonance.Channel.SIGN,
            Resonance.covers(chart(bodies = arrayOf("moon" to degreesIn(ZodiacSign.LEO))), need!!),
        )
        // Their Moon aspecting their Sun.
        assertEquals(
            Resonance.Channel.ASPECT,
            Resonance.covers(
                chart(
                    bodies = arrayOf(
                        "moon" to degreesIn(ZodiacSign.TAURUS),
                        "sun" to degreesIn(ZodiacSign.TAURUS, 17.0),
                    ),
                ),
                need,
            ),
        )
        // Their Moon in the fifth house, standing in a sign that is not Leo — so only the house
        // can be doing the work.
        val houseOnly = chart(
            ascendant = ascendantPutting(ZodiacSign.CAPRICORN, 5),
            bodies = arrayOf("moon" to degreesIn(ZodiacSign.CAPRICORN)),
        )
        assertEquals(5, houseOnly.houseOf("moon"))
        assertEquals(Resonance.Channel.HOUSE, Resonance.covers(houseOnly, need))
    }

    @Test fun aMoonMercuryAspectIsAnsweredBySignHouseOrAspect() {
        val a = chart(
            bodies = arrayOf(
                "moon" to degreesIn(ZodiacSign.TAURUS),
                "mercury" to degreesIn(ZodiacSign.TAURUS, 17.0),
            ),
        )
        val need = needFor(a, "moon", "mercury")
        assertNotNull(need)
        assertFalse("Mercury is not an outer planet", need!!.hardOuter)

        for (sign in listOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO)) {
            assertEquals(
                "Their Moon in $sign should answer it",
                Resonance.Channel.SIGN,
                Resonance.covers(chart(bodies = arrayOf("moon" to degreesIn(sign))), need),
            )
        }
        for (house in listOf(3, 6)) {
            val b = chart(
                ascendant = ascendantPutting(ZodiacSign.CAPRICORN, house),
                bodies = arrayOf("moon" to degreesIn(ZodiacSign.CAPRICORN)),
            )
            assertEquals(house, b.houseOf("moon"))
            assertEquals(
                "Their Moon in house $house should answer it",
                Resonance.Channel.HOUSE,
                Resonance.covers(b, need),
            )
        }
    }

    @Test fun aMoonSaturnAspectWantsCapricornAquariusTenthEleventhOrAnAspect() {
        assertEquals(listOf(ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS), SymbolicSignature.signsOf["saturn"])
        assertEquals(listOf(10, 11), SymbolicSignature.housesOf["saturn"])
    }

    @Test fun aMoonMarsAspectWantsAriesScorpioFirstEighthOrAnAspect() {
        assertEquals(listOf(ZodiacSign.ARIES, ZodiacSign.SCORPIO), SymbolicSignature.signsOf["mars"])
        assertEquals(listOf(1, 8), SymbolicSignature.housesOf["mars"])
    }

    // ── The asymmetries ───────────────────────────────────────────────────────

    @Test fun standingInAHouseRaisesNoNeed() {
        // The Moon in the fifth house is Solar by house, and by nothing else.
        val a = chart(
            ascendant = ascendantPutting(ZodiacSign.CAPRICORN, 5),
            bodies = arrayOf("moon" to degreesIn(ZodiacSign.CAPRICORN)),
        )
        assertEquals(5, a.houseOf("moon"))
        assertNull("A house must not raise a need", needFor(a, "moon", "sun"))
        // It still answers one — the same placement, read from the other side.
        val need = needFor(
            chart(bodies = arrayOf("moon" to degreesIn(ZodiacSign.LEO))), "moon", "sun",
        )!!
        assertEquals(Resonance.Channel.HOUSE, Resonance.covers(a, need))
    }

    @Test fun theNeedBelongsToThePointNotToThePlanetNamingTheColour() {
        val a = chart(bodies = arrayOf("moon" to degreesIn(ZodiacSign.LEO)))
        val need = needFor(a, "moon", "sun")!!

        // Their Sun sitting in Leo answers nothing: the need is on the Moon.
        val sunInLeoOnly = chart(
            bodies = arrayOf(
                "sun" to degreesIn(ZodiacSign.LEO),
                "moon" to degreesIn(ZodiacSign.SAGITTARIUS, 3.0),
            ),
        )
        assertNull("Where their Sun sits is irrelevant", Resonance.covers(sunInLeoOnly, need))
    }

    @Test fun twoPlanetsInOneSignDoNotRaiseASecondNeed() {
        val a = chart(
            bodies = arrayOf(
                "moon" to degreesIn(ZodiacSign.CAPRICORN, 2.0),
                "saturn" to degreesIn(ZodiacSign.CAPRICORN, 26.0),   // same sign, 24° apart: no aspect
            ),
        )
        val saturnNeeds = Resonance.needs(a).filter { it.point == "moon" && it.colour == "saturn" }
        assertEquals("One Saturnian need from the sign, not two", 1, saturnNeeds.size)
    }

    @Test fun anOuterPlanetRaisesNoNeedBySign() {
        // Mercury in Scorpio is Martian, never Plutonian.
        val a = chart(bodies = arrayOf("mercury" to degreesIn(ZodiacSign.SCORPIO)))
        assertNull(needFor(a, "mercury", "pluto"))
        assertNotNull(needFor(a, "mercury", "mars"))

        // An aspect does raise it.
        val b = chart(
            bodies = arrayOf(
                "mercury" to degreesIn(ZodiacSign.TAURUS),
                "pluto" to degreesIn(ZodiacSign.TAURUS, 18.0),
            ),
        )
        assertNotNull(needFor(b, "mercury", "pluto"))
    }

    @Test fun aPointInItsOwnSignAsksNothingBySign() {
        val a = chart(bodies = arrayOf("moon" to degreesIn(ZodiacSign.CANCER)))
        assertTrue(
            "The Moon in Cancer raises no need by sign",
            Resonance.needs(a).none { it.point == "moon" && it.colour == "moon" },
        )
    }

    @Test fun ascendantAndMidheavenRaiseNoNeed() {
        val a = chart(ascendant = degreesIn(ZodiacSign.CAPRICORN, 0.0))
        assertTrue(Resonance.needs(a).none { it.point == "asc" || it.point == "mc" })
    }

    // ── The easier form, for the outer three ──────────────────────────────────

    @Test fun aHardOuterContactIsAnsweredOnlyByAnEasierOne() {
        val a = chart(
            bodies = arrayOf(
                "venus" to degreesIn(ZodiacSign.ARIES, 10.0),
                "pluto" to degreesIn(ZodiacSign.CANCER, 10.0),    // square
            ),
        )
        val need = needFor(a, "venus", "pluto")!!
        assertTrue("A square to Pluto is the hard case", need.hardOuter)

        val square = chart(
            bodies = arrayOf(
                "venus" to degreesIn(ZodiacSign.LIBRA, 4.0),
                "pluto" to degreesIn(ZodiacSign.CAPRICORN, 4.0),
            ),
        )
        assertNull("Hard against hard does not answer it", Resonance.covers(square, need))

        val easier = listOf(
            "conjunction" to Pair(degreesIn(ZodiacSign.LIBRA, 4.0), degreesIn(ZodiacSign.LIBRA, 6.0)),
            "trine" to Pair(degreesIn(ZodiacSign.LIBRA, 4.0), degreesIn(ZodiacSign.AQUARIUS, 4.0)),
            "sextile" to Pair(degreesIn(ZodiacSign.LIBRA, 4.0), degreesIn(ZodiacSign.SAGITTARIUS, 4.0)),
        )
        for ((label, pair) in easier) {
            val b = chart(bodies = arrayOf("venus" to pair.first, "pluto" to pair.second))
            assertNotNull("A $label should answer it", Resonance.covers(b, need))
        }

        // Sharing a sign works as a conjunction even with no aspect in orb.
        val sameSign = chart(
            bodies = arrayOf(
                "venus" to degreesIn(ZodiacSign.LIBRA, 2.0),
                "pluto" to degreesIn(ZodiacSign.LIBRA, 27.0),
            ),
        )
        assertEquals(Resonance.Channel.SAME_SIGN, Resonance.covers(sameSign, need))
    }

    // ── The cusps ─────────────────────────────────────────────────────────────

    /** An ascendant that puts the descendant in [sign]. */
    private fun ascendantFacing(sign: ZodiacSign) = degreesIn(ZodiacSign.fromId((sign.id + 5) % 12 + 1), 0.0)

    @Test fun eachDescendantIndicatorAnswersOnItsOwn() {
        val sign = ZodiacSign.ARIES     // ruler Mars, house 1

        val byAscendant = chart(ascendant = degreesIn(ZodiacSign.ARIES, 5.0))
        assertEquals(Resonance.CuspIndicator.ASCENDANT_SIGN, Resonance.cuspCover(byAscendant, sign)?.indicator)

        val byStellium = chart(
            ascendant = degreesIn(ZodiacSign.CANCER, 0.0),
            bodies = arrayOf(
                "venus" to degreesIn(ZodiacSign.CANCER, 5.0),
                "mercury" to degreesIn(ZodiacSign.CANCER, 12.0),
                "jupiter" to degreesIn(ZodiacSign.CANCER, 24.0),
            ),
        )
        assertEquals(1, byStellium.houseOf("venus"))
        assertEquals(Resonance.CuspIndicator.STELLIUM, Resonance.cuspCover(byStellium, sign)?.indicator)

        val bySun = chart(
            ascendant = degreesIn(ZodiacSign.CANCER, 0.0),
            bodies = arrayOf("sun" to degreesIn(ZodiacSign.ARIES, 20.0)),
        )
        assertEquals(Resonance.CuspIndicator.SUN_SIGN, Resonance.cuspCover(bySun, sign)?.indicator)

        val byRulerAspect = chart(
            ascendant = degreesIn(ZodiacSign.CANCER, 0.0),
            bodies = arrayOf(
                "sun" to degreesIn(ZodiacSign.GEMINI, 10.0),
                "mars" to degreesIn(ZodiacSign.LIBRA, 12.0),     // trine, and neither is in Aries
            ),
        )
        assertEquals(Resonance.CuspIndicator.RULER_ASPECTS_SUN, Resonance.cuspCover(byRulerAspect, sign)?.indicator)
    }

    @Test fun nothingIsMeasuredAgainstACuspAcrossASignBoundary() {
        // An ascendant at 2° Scorpio sits two degrees from a descendant at the end of Libra. That
        // is a conjunction by degree and it deliberately does not count: cusps answer by sign.
        val b = chart(ascendant = degreesIn(ZodiacSign.SCORPIO, 2.0))
        assertNull(Resonance.cuspCover(b, ZodiacSign.LIBRA)?.indicator)
    }

    @Test fun twoPlanetsInTheHouseAreNotAStellium() {
        val two = chart(
            ascendant = degreesIn(ZodiacSign.CANCER, 0.0),
            bodies = arrayOf(
                "venus" to degreesIn(ZodiacSign.CANCER, 5.0),
                "jupiter" to degreesIn(ZodiacSign.CANCER, 24.0),
                "sun" to degreesIn(ZodiacSign.VIRGO, 3.0),
                "mars" to degreesIn(ZodiacSign.SAGITTARIUS, 20.0),
            ),
        )
        assertEquals(2, Resonance.STELLIUM_BODIES.count { two.houseOf(it) == 1 })
        assertNull("Two is not a stellium", Resonance.cuspCover(two, ZodiacSign.ARIES))
    }

    @Test fun aDescendantInLeoSkipsTheRulerAspectButGetsNoFreePass() {
        // Nothing solar anywhere: no Leo ascendant, no Leo Sun, no stellium in the fifth, and the
        // Sun is not on the ascendant. The fourth indicator cannot save it either.
        val barren = chart(
            ascendant = degreesIn(ZodiacSign.TAURUS, 0.0),
            bodies = arrayOf(
                "sun" to degreesIn(ZodiacSign.PISCES, 2.0),
                "moon" to degreesIn(ZodiacSign.GEMINI, 9.0),
                "mercury" to degreesIn(ZodiacSign.AQUARIUS, 17.0),
                "venus" to degreesIn(ZodiacSign.CAPRICORN, 21.0),
                "mars" to degreesIn(ZodiacSign.SCORPIO, 8.0),
                "jupiter" to degreesIn(ZodiacSign.LIBRA, 26.0),
                "saturn" to degreesIn(ZodiacSign.GEMINI, 29.0),
                "uranus" to degreesIn(ZodiacSign.VIRGO, 14.0),
                "neptune" to degreesIn(ZodiacSign.SAGITTARIUS, 1.0),
                "pluto" to degreesIn(ZodiacSign.CAPRICORN, 3.0),
            ),
        )
        assertNull(
            "Leo losing its fourth indicator is not automatic cover",
            Resonance.cuspCover(barren, ZodiacSign.LEO),
        )
    }

    @Test fun anUnansweredDescendantCostsTenAndAnImumCoeliFive() {
        val asking = chart(ascendant = ascendantFacing(ZodiacSign.LEO))
        assertEquals(ZodiacSign.LEO, asking.descendantSign)

        val answering = chart(
            ascendant = degreesIn(ZodiacSign.TAURUS, 0.0),
            bodies = arrayOf(
                "sun" to degreesIn(ZodiacSign.PISCES, 2.0),
                "moon" to degreesIn(ZodiacSign.GEMINI, 9.0),
                "mercury" to degreesIn(ZodiacSign.AQUARIUS, 17.0),
                "venus" to degreesIn(ZodiacSign.CAPRICORN, 21.0),
                "mars" to degreesIn(ZodiacSign.SCORPIO, 8.0),
                "jupiter" to degreesIn(ZodiacSign.LIBRA, 26.0),
                "saturn" to degreesIn(ZodiacSign.GEMINI, 29.0),
                "uranus" to degreesIn(ZodiacSign.VIRGO, 14.0),
                "neptune" to degreesIn(ZodiacSign.SAGITTARIUS, 1.0),
                "pluto" to degreesIn(ZodiacSign.CAPRICORN, 3.0),
            ),
        )
        val direction = Resonance.direction(asking, answering)
        val cuspLoss = Resonance.Cusp.values()
            .filter { cusp -> direction.cuspMatches.none { it.cusp == cusp } }
            .sumOf { it.cost }
        val needLoss = direction.needs
            .filter { need -> direction.matches.none { it.need == need } }
            .sumOf { it.weight }
        assertEquals(direction.lost, cuspLoss + needLoss)
        assertEquals(10, Resonance.Cusp.DESCENDANT.cost)
        assertEquals(5, Resonance.Cusp.IMUM_COELI.cost)
    }

    // ── Weights ───────────────────────────────────────────────────────────────

    @Test fun theSunAndMoonWeighDouble() {
        val a = chart(
            ascendant = degreesIn(ZodiacSign.TAURUS, 0.0),
            bodies = arrayOf("moon" to degreesIn(ZodiacSign.LEO)),
        )
        assertEquals(4, needFor(a, "moon", "sun")!!.weight)
    }

    @Test fun onlyTheFivePersonalBodiesAsk() {
        // A Moon-Saturn opposition raises the Moon's need and never Saturn's: the slow bodies are
        // colours, not askers.
        val a = chart(
            bodies = arrayOf(
                "moon" to degreesIn(ZodiacSign.CANCER, 10.0),
                "saturn" to degreesIn(ZodiacSign.CAPRICORN, 10.0),
            ),
        )
        assertNotNull(needFor(a, "moon", "saturn"))
        assertTrue(
            "Saturn must raise nothing of its own",
            Resonance.needs(a).none { it.point == "saturn" },
        )
        assertTrue(
            "Only the five ask",
            Resonance.needs(a).all { it.point in Resonance.POINTS },
        )
    }

    @Test fun theRulerOfAnUnansweredCuspWeighsDoubleAndAnAnsweredOneDoesNot() {
        // Descendant in Taurus, so Venus is the fallback route to it.
        val asking = chart(
            ascendant = ascendantFacing(ZodiacSign.TAURUS),
            bodies = arrayOf("venus" to degreesIn(ZodiacSign.CANCER, 11.0)),
        )
        assertEquals(ZodiacSign.TAURUS, asking.descendantSign)

        // Nothing answers either cusp: Venus is doubled.
        val barren = chart(
            ascendant = degreesIn(ZodiacSign.GEMINI, 0.0),
            bodies = arrayOf("sun" to degreesIn(ZodiacSign.PISCES, 2.0)),
        )
        val hard = Resonance.direction(asking, barren)
        val doubled = hard.needs.first { it.point == "venus" && it.colour == "moon" }
        assertTrue(doubled.amplified)
        assertEquals("Heavy colour times the amplifier", 8, doubled.weight)

        // Their ascendant in Taurus answers the descendant, so Venus needs no fallback.
        val answering = chart(ascendant = degreesIn(ZodiacSign.TAURUS, 5.0))
        val easy = Resonance.direction(asking, answering)
        val plain = easy.needs.first { it.point == "venus" && it.colour == "moon" }
        assertFalse("An answered cusp stops doubling its ruler", plain.amplified)
        assertEquals(4, plain.weight)
    }

    @Test fun theSunsConjunctionAndOppositionRunToTenDegrees() {
        // A real pair: the Sun at 7°12' Leo stands 8°33' from an opposition to Saturn at 15°45'
        // Aquarius. Outside the flat table, inside the Sun's.
        val withSun = chart(
            bodies = arrayOf(
                "sun" to degreesIn(ZodiacSign.LEO, 7.2),
                "saturn" to degreesIn(ZodiacSign.AQUARIUS, 15.75),
            ),
        )
        assertEquals(Aspects.Kind.OPPOSITION, withSun.aspect("sun", "saturn"))

        // The same separation between two other bodies is not an aspect.
        val withoutSun = chart(
            bodies = arrayOf(
                "venus" to degreesIn(ZodiacSign.LEO, 7.2),
                "saturn" to degreesIn(ZodiacSign.AQUARIUS, 15.75),
            ),
        )
        assertNull(withoutSun.aspect("venus", "saturn"))

        // Only the conjunction and the opposition are widened. A square nine degrees out stays out.
        val wideSquare = chart(
            bodies = arrayOf(
                "sun" to degreesIn(ZodiacSign.LEO, 0.0),
                "mars" to degreesIn(ZodiacSign.SCORPIO, 9.0),
            ),
        )
        assertNull("The Sun's square keeps its ordinary orb", wideSquare.aspect("sun", "mars"))
    }

    @Test fun theQuincunxIsNotAnAspectHere() {
        // 150 degrees apart: an aspect everywhere else in the app, and nothing in this module.
        val a = chart(
            bodies = arrayOf(
                "venus" to degreesIn(ZodiacSign.ARIES, 10.0),
                "neptune" to degreesIn(ZodiacSign.VIRGO, 10.0),
            ),
        )
        assertNull(needFor(a, "venus", "neptune"))
    }

    @Test fun theScoreIsNotFlooredAtZero() {
        // Enough unanswered weight and the number goes below zero on purpose; clamping it would
        // hide the pairs most worth seeing.
        val direction = Resonance.Direction(
            needs = emptyList(), matches = emptyList(), cuspMatches = emptyList(), lost = 130,
        )
        assertEquals(Resonance.FULL - 130, direction.score)
        assertEquals(103, Resonance.FULL)
    }

    @Test fun theScoreIsTheLowerOfTheTwoDirections() {
        val a = chart(bodies = arrayOf("moon" to degreesIn(ZodiacSign.LEO)))
        val b = chart(bodies = arrayOf("moon" to degreesIn(ZodiacSign.CAPRICORN, 11.0)))
        val result = Resonance.analyse(a, b)
        assertEquals(minOf(result.aToB.score, result.bToA.score), result.score)
    }

    @Test fun theSymbolicTableIsTheAncientOneAndTheOuterThreeAreEmpty() {
        assertEquals(listOf(ZodiacSign.LEO), SymbolicSignature.signsOf["sun"])
        assertEquals(listOf(5), SymbolicSignature.housesOf["sun"])
        assertEquals(listOf(ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES), SymbolicSignature.signsOf["jupiter"])
        for (outer in SymbolicSignature.OUTER) {
            assertNull("$outer rules nothing", SymbolicSignature.signsOf[outer])
            assertNull("$outer stands behind no house", SymbolicSignature.housesOf[outer])
        }
    }
}
