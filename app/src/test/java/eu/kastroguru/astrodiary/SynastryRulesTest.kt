package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.domain.synastry.Resonance
import eu.kastroguru.astrodiary.domain.synastry.SymbolicSignature
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The enumerated rule list and the code must say the same thing.
 *
 * `tools/synastry/rules.py` writes out all 192 rules in Bulgarian so the owner can read and check
 * them. That file is the one a human trusts; [Resonance] is the one the app runs. Nothing stops
 * the two from drifting except this test, which rebuilds the list from the Kotlin tables and
 * compares it to the file line for line. Change the rulerships in Kotlin and this fails until the
 * generator is re-run — which is the point.
 *
 * The Bulgarian labels below are duplicated from the generator on purpose: they are names, not
 * logic, and having them here is what makes the comparison mean anything.
 */
class SynastryRulesTest {

    private val csv = File("../tools/synastry/content/resonance_rules.csv")

    private val signName = mapOf(
        ZodiacSign.ARIES to "Овен", ZodiacSign.TAURUS to "Телец", ZodiacSign.GEMINI to "Близнаци",
        ZodiacSign.CANCER to "Рак", ZodiacSign.LEO to "Лъв", ZodiacSign.VIRGO to "Дева",
        ZodiacSign.LIBRA to "Везни", ZodiacSign.SCORPIO to "Скорпион",
        ZodiacSign.SAGITTARIUS to "Стрелец", ZodiacSign.CAPRICORN to "Козирог",
        ZodiacSign.AQUARIUS to "Водолей", ZodiacSign.PISCES to "Риби",
    )

    private val bodyName = mapOf(
        "sun" to "Слънце", "moon" to "Луна", "mercury" to "Меркурий", "venus" to "Венера",
        "mars" to "Марс", "jupiter" to "Юпитер", "saturn" to "Сатурн",
        "uranus" to "Уран", "neptune" to "Нептун", "pluto" to "Плутон",
    )

    /** "с" becomes "със" before s- and z-; the luminaries take the article. */
    private val withBody = mapOf(
        "sun" to "със Слънцето", "moon" to "с Луната", "mercury" to "с Меркурий",
        "venus" to "с Венера", "mars" to "с Марс", "jupiter" to "с Юпитер",
        "saturn" to "със Сатурн", "uranus" to "с Уран", "neptune" to "с Нептун",
        "pluto" to "с Плутон",
    )

    private val houseName = listOf(
        "", "1-ви", "2-ри", "3-ти", "4-ти", "5-и", "6-и",
        "7-и", "8-и", "9-и", "10-и", "11-и", "12-и",
    )

    private fun either(items: List<String>) = if (items.size == 1) items[0] else items.joinToString(" или ")

    private fun cover(point: String, colour: String): String {
        val p = bodyName.getValue(point)
        val signs = SymbolicSignature.signsOf.getValue(colour).map { signName.getValue(it) }
        val houses = SymbolicSignature.housesOf.getValue(colour).map { houseName[it] }
        return listOf(
            "$p в ${either(signs)}",
            "$p в ${either(houses)} дом",
            "$p в аспект ${withBody.getValue(colour)}",
            "$p в един знак ${withBody.getValue(colour)}",
        ).joinToString("; ")
    }

    private fun expected(): List<Pair<String, String>> {
        val rows = ArrayList<Pair<String, String>>()
        val classical = SymbolicSignature.CLASSICAL
        val points = Resonance.POINTS

        // 1 — the sign the point stands in.
        for (point in points) {
            for (sign in ZodiacSign.values()) {
                val colour = SymbolicSignature.rulerOf(sign)
                val left = "${bodyName.getValue(point)} в ${signName.getValue(sign)}"
                rows += left to if (colour == point) {
                    "не ражда нужда — точката стои в собствения си знак"
                } else {
                    cover(point, colour)
                }
            }
        }

        // 2 — an aspect to one of the seven.
        for (point in points) {
            for (colour in classical) {
                if (colour == point) continue
                rows += "${bodyName.getValue(point)} в аспект ${withBody.getValue(colour)}" to
                    cover(point, colour)
            }
        }

        // 3 — an aspect to the outer three, hard and easy.
        for (point in points) {
            for (colour in SymbolicSignature.OUTER) {
                val p = bodyName.getValue(point)
                val w = withBody.getValue(colour)
                rows += "$p в труден аспект $w" to
                    "$p в лесен аспект $w; $p в съвпад $w; $p в един знак $w"
                rows += "$p в лесен аспект или съвпад $w" to
                    "$p в какъвто и да е аспект $w; $p в един знак $w"
            }
        }

        // 4 and 5 — the two cusps.
        for (cusp in listOf("Десцендент", "Имум цели")) {
            for (sign in ZodiacSign.values()) {
                val rulers = SymbolicSignature.rulersOf(sign)
                val s = signName.getValue(sign)
                val parts = ArrayList<String>(7)
                parts += "асцендент на партньора в $s"
                parts += "три или повече планети на партньора в ${houseName[sign.id]} дом"
                parts += "Слънце на партньора в $s"
                for (ruler in rulers) {
                    if (ruler == "sun") continue
                    parts += "${bodyName.getValue(ruler)} на партньора в аспект със Слънцето на партньора"
                }
                for (ruler in rulers) {
                    parts += "${bodyName.getValue(ruler)} на партньора в един знак " +
                        "със собствения му асцендент"
                }
                rows += "$cusp в $s" to parts.joinToString("; ")
            }
        }
        return rows
    }

    private fun actual(): List<Pair<String, String>> {
        assertTrue(
            "Rule list missing — run: python3 tools/synastry/rules.py",
            csv.exists(),
        )
        return csv.readText(Charsets.UTF_8)
            .removePrefix("﻿")
            .lineSequence()
            .filter { it.isNotBlank() }
            .drop(1)                                   // the header
            .map { line ->
                val cells = line.removePrefix("\"").removeSuffix("\"").split("\",\"")
                assertEquals("Two columns per rule, got: $line", 2, cells.size)
                cells[0] to cells[1]
            }
            .toList()
    }

    @Test fun theWrittenRulesAreExactlyWhatTheCodeApplies() {
        val expected = expected()
        val actual = actual()
        assertEquals("Rule count", expected.size, actual.size)
        for ((i, pair) in expected.withIndex()) {
            assertEquals("Rule ${i + 1} — left column", pair.first, actual[i].first)
            assertEquals("Rule ${i + 1} (${pair.first}) — right column", pair.second, actual[i].second)
        }
    }

    @Test fun thereAreOneHundredAndFortyFourRulesAndNoneRepeats() {
        val rows = actual()
        assertEquals(144, rows.size)
        val repeated = rows.map { it.first }.groupingBy { it }.eachCount().filterValues { it > 1 }
        assertTrue("Repeated rules: ${repeated.keys}", repeated.isEmpty())
    }

    @Test fun theSignsWithTwoRulersListMoreIndicators() {
        val cuspRows = actual().filter { it.first.startsWith("Десцендент") || it.first.startsWith("Имум цели") }
        assertEquals(24, cuspRows.size)
        for ((left, right) in cuspRows) {
            val indicators = right.split("; ").size
            // Leo loses the ruler-to-Sun test, since the ruler is the Sun. Scorpio, Aquarius and
            // Pisces gain a second ruler, and with it a second go at both ruler tests.
            val expected = when {
                left.endsWith(" в Лъв") -> 4
                left.endsWith(" в Скорпион") || left.endsWith(" в Водолей") || left.endsWith(" в Риби") -> 7
                else -> 5
            }
            assertEquals("$left should list $expected indicators", expected, indicators)
        }
    }

    @Test fun theCostsInTheListMatchTheCostsInTheCode() {
        assertEquals(10, Resonance.Cusp.DESCENDANT.cost)
        assertEquals(5, Resonance.Cusp.IMUM_COELI.cost)
        assertEquals(3, Resonance.STELLIUM_SIZE)
    }
}
