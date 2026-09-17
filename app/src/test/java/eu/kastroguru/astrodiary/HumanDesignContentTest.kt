package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.humandesign.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards for the Human Design layers added on top of the bodygraph: the four arrows, hanging
 * gates, the connection chart and the daily gate transits.
 *
 * The content grids are small enough to be complete, so incompleteness is a failure rather than a
 * coverage percentage — a missing gate would show up in the app as a silently skipped row.
 */
class HumanDesignContentTest {

    // ── Coverage ──────────────────────────────────────────────────────────────

    @Test fun everyGateHasAHangingText() {
        val missing = (1..64).filter { it !in HdHangingGateTexts.seeking }
        assertTrue("Hanging-gate texts missing for $missing", missing.isEmpty())
    }

    @Test fun everyGateHasATransitText() {
        val missing = (1..64).filter { it !in HdGateTransitTexts.gate }
        assertTrue("Transit-gate texts missing for $missing", missing.isEmpty())
    }

    @Test fun everyTransitEffectHasAText() {
        val missing = HdTransitEffect.values().filter { it !in HdGateTransitTexts.effect }
        assertTrue("Transit effect texts missing for $missing", missing.isEmpty())
    }

    @Test fun everyCentreHasAConditioningText() {
        val missing = HdCenter.values().filter { it !in HdOpenCenterTexts.conditioning }
        assertTrue("Open-centre conditioning missing for $missing", missing.isEmpty())
    }

    @Test fun everyVariableValueHasAText() {
        assertTrue(HdDetermination.values().all { it in HdVariableTexts.determination })
        assertTrue(HdEnvironment.values().all { it in HdVariableTexts.environment })
        assertTrue(HdMotivation.values().all { it in HdVariableTexts.motivation })
        assertTrue(HdPerspective.values().all { it in HdVariableTexts.perspective })
        assertTrue(listOf("determination", "environment", "motivation", "perspective")
            .all { it in HdVariableTexts.intro })
    }

    @Test fun everyConnectionKindHasAText() {
        val missing = HdConnectionKind.values().filter { it !in HdConnectionTexts.state }
        assertTrue("Connection state texts missing for $missing", missing.isEmpty())
    }

    /**
     * Only 13 type/authority pairs can come out of the calculator: a defined Sacral (Generator,
     * Manifesting Generator) rules out Splenic, Ego, Self-Projected and Mental; a Reflector is
     * always Lunar. Every one of the 13 must have parenting text, and nothing else needs it.
     */
    @Test fun everyReachableTypeAuthorityPairHasParentingText() {
        val reachable = buildList {
            for (a in listOf(HdAuthority.SACRAL, HdAuthority.EMOTIONAL)) {
                add(HdType.GENERATOR to a); add(HdType.MANIFESTING_GENERATOR to a)
            }
            for (a in listOf(HdAuthority.EMOTIONAL, HdAuthority.SPLENIC, HdAuthority.EGO)) {
                add(HdType.MANIFESTOR to a)
            }
            for (a in listOf(HdAuthority.EMOTIONAL, HdAuthority.SPLENIC, HdAuthority.EGO,
                             HdAuthority.SELF_PROJECTED, HdAuthority.MENTAL)) {
                add(HdType.PROJECTOR to a)
            }
            add(HdType.REFLECTOR to HdAuthority.LUNAR)
        }
        assertEquals(13, reachable.size)
        val missing = reachable.filter { (t, a) -> HdParentingTexts.keyFor(t, a) !in HdParentingTexts.parenting }
        assertTrue("Parenting text missing for $missing", missing.isEmpty())
        assertEquals("No parenting text should exist for an unreachable pair",
            13, HdParentingTexts.parenting.size)
    }

    // ── Text hygiene ──────────────────────────────────────────────────────────

    private fun newContent(): List<Pair<String, Pair<String, String>>> = buildList {
        HdHangingGateTexts.seeking.forEach { (k, v) -> add("hanging $k" to v) }
        HdGateTransitTexts.gate.forEach { (k, v) -> add("transit $k" to v) }
        HdGateTransitTexts.effect.forEach { (k, v) -> add("effect $k" to v) }
        HdOpenCenterTexts.conditioning.forEach { (k, v) -> add("open $k" to v) }
        HdVariableTexts.determination.forEach { (k, v) -> add("determination $k" to v) }
        HdVariableTexts.environment.forEach { (k, v) -> add("environment $k" to v) }
        HdVariableTexts.motivation.forEach { (k, v) -> add("motivation $k" to v) }
        HdVariableTexts.perspective.forEach { (k, v) -> add("perspective $k" to v) }
        HdVariableTexts.intro.forEach { (k, v) -> add("intro $k" to v) }
        HdParentingTexts.parenting.forEach { (k, v) -> add("parenting $k" to v) }
        HdConnectionTexts.state.forEach { (k, v) -> add("connection $k" to v) }
        add("connection intro" to HdConnectionTexts.intro)
        add("connection conditioning" to HdConnectionTexts.conditioning)
        add("connection bothOpen" to HdConnectionTexts.bothOpen)
    }

    /** A Latin letter inside a Cyrillic word is a keyboard slip, and it shipped once already. */
    @Test fun bulgarianHasNoMixedScriptWords() {
        val mixed = Regex("[\\u0400-\\u04FF]+[a-zA-Z]+|[a-zA-Z]+[\\u0400-\\u04FF]+")
        val bad = allHdBulgarian().mapNotNull { (where, t) ->
            mixed.find(t)?.let { "$where: «${it.value}»" }
        }
        assertTrue("Mixed Latin/Cyrillic words:\n" + bad.joinToString("\n"), bad.isEmpty())
    }

    /**
     * A gate is "гейт" in Bulgarian, never "врата" — the literal translation collides with the
     * metaphorical "отваря врати" used all over the astrological corpus. Only the plural and the
     * numbered singular are banned; a real door in an environment description is fine.
     */
    @Test fun bulgarianCallsAGateGeyt() {
        val banned = Regex("врати\\b|врата\\s+\\d|\\d\\s*-?\\s*та врата")
        val bad = newContent().mapNotNull { (where, t) ->
            banned.find(t.second)?.let { "$where: «${it.value}»" }
        }
        assertTrue("A gate must be called «гейт», not «врата»:\n" + bad.joinToString("\n"), bad.isEmpty())
    }

    /** Every Bulgarian half of every HD text object, new files and HdDescriptions alike. */
    private fun allHdBulgarian(): List<Pair<String, String>> = buildList {
        newContent().forEach { (where, t) -> add(where to t.second) }
        HdDescriptions.typeInfo.forEach { (k, ti) ->
            add("type $k aura" to ti.aura.second); add("type $k desc" to ti.description.second)
            add("type $k strategy" to ti.strategy.second); add("type $k signature" to ti.signature.second)
            add("type $k notSelf" to ti.notSelf.second)
        }
        HdDescriptions.authorityInfo.forEach { (k, v) -> add("authority $k" to v.second) }
        HdDescriptions.profileInfo.forEach { (k, v) -> add("profile $k" to v.second) }
        HdDescriptions.definitionInfo.forEach { (k, v) -> add("definition $k" to v.second) }
        HdDescriptions.centerInfo.forEach { (k, v) ->
            add("centre $k defined" to v.defined.second); add("centre $k undefined" to v.undefined.second)
        }
        HdDescriptions.channelInfo.forEach { (k, v) -> add("channel $k" to v.second) }
    }

    /**
     * **The whole HD corpus** addresses the reader as "вие", matching the astrological corpus.
     * `profileInfo` and `channelInfo` were originally written in "ти" and were converted, which
     * mattered once the connection chart started printing channel texts next to new "вие" prose.
     *
     * Quoted speech is stripped first: the parenting texts put words in the parent's mouth to say
     * to a child ("искаш ли да отидем?"), and there the informal address is the correct one. Only
     * the prose around the quotes is checked.
     */
    @Test fun theWholeHdCorpusUsesTheFormalAddress() {
        val quoted = Regex("[\u201E\u201C\u0022][^\u201E\u201C\u0022]*[\u201C\u201D\u0022]")
        // -ш is the 2sg present ending; the pronouns and "теб" are the other giveaways.
        val singular = Regex("\\b(твой|твоя|твоят|твоята|твоите|твоето|теб|тебе|[а-я]{3,}(?:аш|еш|иш|яш|уваш))\\b")
        val notVerbs = setOf("сякаш")
        val bad = allHdBulgarian().mapNotNull { (where, t) ->
            singular.findAll(quoted.replace(t, " "))
                .firstOrNull { it.value !in notVerbs }?.let { "$where: «${it.value}»" }
        }
        assertTrue("HD content must use «вие» outside quoted speech:\n" + bad.joinToString("\n"), bad.isEmpty())
    }

    @Test fun everyTextHasBothLanguagesAndIsSubstantial() {
        val bad = newContent().filter { (_, t) ->
            t.first.length < 60 || t.second.length < 60 || t.first == t.second
        }.map { it.first }
        assertTrue("Thin or untranslated texts: $bad", bad.isEmpty())
    }

    @Test fun noTextIsReusedAcrossTheCorpus() {
        val byBg = newContent().groupBy { it.second.second }.filterValues { it.size > 1 }
        assertTrue("Duplicated Bulgarian text: ${byBg.values.map { g -> g.map { it.first } }}", byBg.isEmpty())
        val byEn = newContent().groupBy { it.second.first }.filterValues { it.size > 1 }
        assertTrue("Duplicated English text: ${byEn.values.map { g -> g.map { it.first } }}", byEn.isEmpty())
    }

    // ── Sub-line maths ────────────────────────────────────────────────────────

    @Test fun subLineAgreesWithTheGateWheel() {
        var lon = 0.0
        while (lon < 360.0) {
            val (gate, line) = gateLineFor(lon)
            val sub = subLineFor(lon)
            assertEquals("gate at $lon", gate, sub.gate)
            assertEquals("line at $lon", line, sub.line)
            assertTrue("colour at $lon", sub.colour in 1..6)
            assertTrue("tone at $lon", sub.tone in 1..6)
            lon += 0.037
        }
    }

    /** Stepping one tone at a time from a gate's start must walk 1..6 colours × 1..6 tones. */
    @Test fun tonesAndColoursTileTheLine() {
        val start = WHEEL_START_DEG + 3 * GATE_ARC + 0.5 * TONE_ARC   // mid-first-tone of a gate
        val seen = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 36) seen += subLineFor(start + i * TONE_ARC).let { it.colour to it.tone }
        val expected = (1..6).flatMap { c -> (1..6).map { t -> c to t } }
        assertEquals(expected, seen)
    }

    @Test fun arrowSplitsTheToneInHalf() {
        assertEquals(HdArrow.LEFT, arrowOf(1))
        assertEquals(HdArrow.LEFT, arrowOf(3))
        assertEquals(HdArrow.RIGHT, arrowOf(4))
        assertEquals(HdArrow.RIGHT, arrowOf(6))
    }

    @Test fun eachColourMapsToItsOwnDeterminationAndEnvironment() {
        val dets = (1..6).flatMap { c -> listOf(1, 4).map { t -> determinationFor(HdSubLine(1, 1, c, t)) } }
        assertEquals("all 12 determinations reachable", 12, dets.distinct().size)
        val envs = (1..6).flatMap { c -> listOf(1, 4).map { t -> environmentFor(HdSubLine(1, 1, c, t)) } }
        assertEquals("all 12 environments reachable", 12, envs.distinct().size)
    }

    @Test fun roundBirthTimesAreFlaggedAsImprecise() {
        assertTrue(needsExactBirthTime(14, 0))
        assertTrue(needsExactBirthTime(14, 30))
        assertTrue(!needsExactBirthTime(14, 17))
    }

    // ── Hanging gates ─────────────────────────────────────────────────────────

    @Test fun aGateHangsOnlyWhenItsPartnerIsMissing() {
        // Channel 3-60 (Mutation): hold 3, not 60.
        val hanging = hangingGatesOf(setOf(3), definedCentres = emptySet())
        val mutation = hanging.single { it.channel.a == 3 && it.channel.b == 60 }
        assertEquals(3, mutation.gate)
        assertEquals(60, mutation.seeks)
        assertTrue("60 sits in the Root, which is undefined here", mutation.opensNewCentre)

        // Holding both ends is a defined channel, not a hanging gate.
        assertTrue(hangingGatesOf(setOf(3, 60), emptySet()).none { it.channel.a == 3 && it.channel.b == 60 })
        // Holding neither end is nothing at all.
        assertTrue(hangingGatesOf(emptySet(), emptySet()).isEmpty())
    }

    @Test fun openingANewCentreIsFalseWhenTheFarCentreIsAlreadyDefined() {
        val hanging = hangingGatesOf(setOf(3), definedCentres = setOf(HdCenter.ROOT))
        assertTrue(hanging.single { it.seeks == 60 }.opensNewCentre.not())
    }

    // ── Connection ────────────────────────────────────────────────────────────

    private fun chartWith(gates: Set<Int>): HumanDesignChart {
        val channels = CHANNELS.filter { it.a in gates && it.b in gates }
        return HumanDesignChart(
            personality = emptyList(), design = emptyList(),
            activeGates = gates, definedChannels = channels,
            definedCenters = channels.flatMap { listOfNotNull(centerOfGate(it.a), centerOfGate(it.b)) }.toSet(),
            type = HdType.PROJECTOR, authority = HdAuthority.MENTAL,
            profilePersonalityLine = 1, profileDesignLine = 3, definition = HdDefinition.NONE
        )
    }

    @Test fun oneGateEachCreatesAnElectromagneticChannel() {
        val c = analyseConnection(chartWith(setOf(3)), chartWith(setOf(60)))
        val link = c.electromagnetic.single { it.channel.a == 3 }
        assertEquals(3, link.gateFromA)
        assertEquals(60, link.gateFromB)
        assertTrue("the channel exists only together", HdCenter.SACRAL in c.compositeCentres)
    }

    @Test fun theSameSingleGateTwiceBridgesNothing() {
        val c = analyseConnection(chartWith(setOf(3)), chartWith(setOf(3)))
        assertTrue(c.all.isEmpty())
    }

    @Test fun wholeChannelAgainstNothingIsDominance() {
        val c = analyseConnection(chartWith(setOf(3, 60)), chartWith(emptySet()))
        assertEquals(HdConnectionState.DOMINANCE_A, c.dominance.single { it.channel.a == 3 }.state)
        assertEquals(HdConnectionKind.DOMINANCE, c.dominance.single { it.channel.a == 3 }.state.kind)
    }

    @Test fun theOneHoldingASingleGateIsTheOneWhoCompromises() {
        val c = analyseConnection(chartWith(setOf(3, 60)), chartWith(setOf(60)))
        assertEquals(HdConnectionState.COMPROMISE_B, c.compromise.single { it.channel.a == 3 }.state)
    }

    @Test fun bothHoldingTheWholeChannelIsCompanionship() {
        val c = analyseConnection(chartWith(setOf(3, 60)), chartWith(setOf(3, 60)))
        assertEquals(HdConnectionKind.COMPANIONSHIP, c.companionship.single { it.channel.a == 3 }.state.kind)
    }

    @Test fun conditioningRunsFromTheDefinedSideOnly() {
        val a = chartWith(setOf(3, 60))          // defines Sacral + Root
        val b = chartWith(emptySet())            // defines nothing
        val c = analyseConnection(a, b)
        assertEquals(a.definedCenters, c.aConditionsB)
        assertTrue(c.bConditionsA.isEmpty())
        assertTrue("centres nobody defines are the shared blind spot",
            HdCenter.HEAD in c.bothOpenCentres)
    }

    // ── Daily transits ────────────────────────────────────────────────────────

    @Test fun transitEffectsAreClassifiedAgainstTheOwnChart() {
        val chart = chartWith(setOf(3))                       // 3 hangs, seeking 60
        fun lonOfGate(g: Int): Double {
            val idx = GATE_WHEEL.indexOf(g)
            return (WHEEL_START_DEG + idx * GATE_ARC + GATE_ARC / 2) % 360.0
        }
        val completes = transitGatesFor(chart, mapOf("sun" to lonOfGate(60))).single { it.body == HdBody.SUN }
        assertEquals(HdTransitEffect.COMPLETES, completes.effect)
        assertEquals(3, completes.yourGate)

        val amplifies = transitGatesFor(chart, mapOf("sun" to lonOfGate(3))).single { it.body == HdBody.SUN }
        assertEquals(HdTransitEffect.AMPLIFIES, amplifies.effect)

        val open = transitGatesFor(chart, mapOf("sun" to lonOfGate(25))).single { it.body == HdBody.SUN }
        assertEquals(HdTransitEffect.OPEN, open.effect)
    }
}
