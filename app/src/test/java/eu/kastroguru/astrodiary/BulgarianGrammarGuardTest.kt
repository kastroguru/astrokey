package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.RulershipChain
import eu.kastroguru.astrodiary.domain.interpretation.NatalInterpretations
import eu.kastroguru.astrodiary.domain.interpretation.PlanetMeanings
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Composed text joins written blocks with a template, and Bulgarian punishes that: a phrase that
 * reads fine on its own becomes wrong after a preposition ("в как излизате", "в домът", "в връзките").
 * Found exactly that in the first draft, so the patterns are now checked over every combination
 * rather than spot-read.
 */
class BulgarianGrammarGuardTest {

    /**
     * Only the joins are checked, not the prose. "В какво вярвате" is perfectly good Bulgarian inside
     * a written sentence — it is wrong only where a template puts it after a preposition, so the
     * checks below target the insertion points and the phrases that land in them.
     */
    private val brokenJoins = listOf(
        "ключът е в как", "ключът е в какво", "ключът е в къде",
        "решава се през как", "решава се през какво",
        "тук го прави как", "тук това става как",
        // Preposition plus a full definite article, or "в" where "във" is required. The leading space
        // matters: without it "във връзките" contains "в връзки" and the check fires on correct text,
        // which is exactly what happened the first time.
        " в домът", " в светът", " в начинът", " в по-широкият", " през домът", " през по-широкият",
        " в връзк", " в въпрос", " в възможност", " в форм",
        // punctuation of the joins themselves
        "тук: .", "областта е тази: .", "разиграва се тук: .",
    )

    private fun allComposedBulgarian(): List<Pair<String, String>> {
        val out = mutableListOf<Pair<String, String>>()
        for (planet in PlanetMeanings.byKey.keys) {
            for (sign in ZodiacSign.values()) {
                NatalInterpretations.planetInSign(planet, sign)?.let { out += "$planet in $sign" to it.bg }
            }
            for (house in 1..12) {
                NatalInterpretations.planetInHouse(planet, house)?.let { out += "$planet in h$house" to it.bg }
                for (rulerHouse in 1..12) {
                    val link = RulershipChain.Link(
                        planet, house, ZodiacSign.ARIES,
                        if (rulerHouse == house) planet else "venus", rulerHouse
                    )
                    NatalInterpretations.planetInHouseWithRuler(link)
                        ?.let { out += "$planet h$house ruler h$rulerHouse" to it.bg }
                }
            }
        }
        for (angle in listOf("asc", "mc")) {
            for (sign in ZodiacSign.values()) {
                NatalInterpretations.angleInSign(angle, sign)?.let { out += "$angle in $sign" to it.bg }
            }
        }
        for (from in 1..12) {
            for (to in 1..12) {
                NatalInterpretations.rulerOfHouseInHouse(from, to)?.let { out += "ruler $from in $to" to it.bg }
            }
            for (sign in ZodiacSign.values()) {
                NatalInterpretations.interceptedHouse(from, sign)?.let { out += "$sign intercepted h$from" to it.bg }
            }
        }
        return out
    }

    @Test
    fun noComposedTextContainsABrokenJoin() {
        val problems = allComposedBulgarian().flatMap { (where, text) ->
            brokenJoins.filter { text.lowercase().contains(it) }.map { "$where: «$it»" }
        }.distinct()
        assertTrue(
            "Broken Bulgarian in composed text (${problems.size}):\n" + problems.take(20).joinToString("\n"),
            problems.isEmpty()
        )
    }

    @Test
    fun sentencesDoNotRunTogetherOrEndAbruptly() {
        val problems = allComposedBulgarian().filter { (_, text) ->
            text.contains(" .") || text.contains("..") || text.contains("  ") ||
                text.contains(": .") || text.trimEnd().endsWith(",")
        }.map { it.first }
        assertTrue("Punctuation problems in: ${problems.take(10)}", problems.isEmpty())
    }

    /**
     * The root cause of the broken joins: a label that is a clause rather than a noun phrase cannot
     * follow "в" or "през". Checking the labels themselves catches it before any text is composed.
     */
    @Test
    fun shortHouseLabelsCanFollowAPreposition() {
        val clauseStarts = listOf("как", "какво", "къде", "кога", "който", "онова")
        val bad = eu.kastroguru.astrodiary.domain.interpretation.HouseMeanings.shortByHouse
            .filterValues { label -> clauseStarts.any { label.bg.lowercase().startsWith(it) } }
            .map { "${it.key}: ${it.value.bg}" }
        assertTrue("These labels are clauses and break after a preposition: $bad", bad.isEmpty())
    }

    @Test
    fun signStylesCanFollowTheVerbTheyAreJoinedTo() {
        // They land after "Тук го прави …", so they have to begin adverbially.
        val clauseStarts = listOf("как", "какво", "където", "който")
        val bad = eu.kastroguru.astrodiary.domain.interpretation.SignMeanings.bySign
            .filterValues { style -> clauseStarts.any { style.bg.lowercase().startsWith(it) } }
            .map { "${it.key}: ${it.value.bg}" }
        assertTrue("These sign styles do not fit their sentence: $bad", bad.isEmpty())
    }

    @Test
    fun everyCombinationIsCheckedNotJustAFew() {
        // 13 planets x (12 signs + 12 houses + 144 chains) + 24 angles + 144 rulers + 144 intercepted
        assertTrue("too few combinations checked", allComposedBulgarian().size > 2000)
    }
}
