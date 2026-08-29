package eu.kastroguru.astrodiary.domain.interpretation

import eu.kastroguru.astrodiary.domain.interpretation.written.AscendantInSign
import eu.kastroguru.astrodiary.domain.interpretation.written.MidheavenInSign
import eu.kastroguru.astrodiary.domain.interpretation.written.PlanetInHouseLines
import eu.kastroguru.astrodiary.domain.interpretation.written.RulerHouseKey
import eu.kastroguru.astrodiary.domain.interpretation.written.MoonInSign
import eu.kastroguru.astrodiary.domain.interpretation.written.SunInSign
import eu.kastroguru.astrodiary.domain.RulershipChain
import eu.kastroguru.astrodiary.domain.model.ZodiacSign

/**
 * Interpretation for a single placement, however far the hand-written content has got.
 *
 * There are 156 planet-in-sign combinations, 156 planet-in-house ones and 144 ruler-in-house ones.
 * Writing every one by hand takes a long time, and a screen that says nothing until the last one is
 * finished is useless in the meantime — so each lookup answers from a hand-written text when one
 * exists and otherwise composes an honest, if plainer, one out of the building blocks
 * ([PlanetMeanings], [SignMeanings], [HouseMeanings]). Callers never have to check which they got.
 *
 * No text names the planet, sign or house it is about: the screen already shows that in its heading,
 * and leaving names out of the prose keeps this layer free of both localisation and grammar.
 */
object NatalInterpretations {

    /** Hand-written planet-in-sign texts, key "sun_aries". Filled in batches; composition covers the rest. */
    internal val writtenPlanetInSign: Map<String, Bilingual> = SunInSign.entries + MoonInSign.entries

    /** Hand-written planet-in-house texts, key "sun_10". */
    internal val writtenPlanetInHouse: Map<String, Bilingual> = emptyMap()

    /** Hand-written angle-in-sign texts, key "asc_leo". */
    internal val writtenAngleInSign: Map<String, Bilingual> = AscendantInSign.entries + MidheavenInSign.entries

    /** Hand-written chain texts, key "sun_5_9" (Sun in the 5th, whose ruler sits in the 9th). */
    internal val writtenChain: Map<String, Bilingual> = emptyMap()

    /** Hand-written ruler-of-house-in-house texts, key "7_10" (ruler of 7th sits in 10th). */
    internal val writtenRulerInHouse: Map<String, Bilingual> = emptyMap()

    fun planetInSign(planetKey: String, sign: ZodiacSign): Bilingual? {
        writtenPlanetInSign["${planetKey}_${sign.name.lowercase()}"]?.let { return it }
        val planet = PlanetMeanings.of(planetKey) ?: return null
        val style = SignMeanings.of(sign) ?: return null
        return t(
            "${planet.en}\n\nHere it does that ${style.en}.",
            "${planet.bg}\n\nТук го прави ${style.bg}."
        )
    }

    fun planetInHouse(planetKey: String, house: Int): Bilingual? {
        writtenPlanetInHouse["${planetKey}_$house"]?.let { return it }
        val planet = PlanetMeanings.of(planetKey) ?: return null
        val area = HouseMeanings.of(house) ?: return null
        return t(
            "${planet.en}\n\nWhere it plays out: ${area.en}.",
            "${planet.bg}\n\nРазиграва се тук: ${area.bg}."
        )
    }

    /**
     * The Ascendant or Midheaven in a sign. Separate from [planetInSign] because an angle is a point
     * you are seen from rather than a drive you act on, and it has its own meanings ([AngleMeanings]).
     */
    fun angleInSign(angleKey: String, sign: ZodiacSign): Bilingual? {
        writtenAngleInSign["${angleKey}_${sign.name.lowercase()}"]?.let { return it }
        val angle = AngleMeanings.of(angleKey) ?: return null
        val style = SignMeanings.of(sign) ?: return null
        return t(
            "${angle.en}\n\nHere that happens ${style.en}.",
            "${angle.bg}\n\nТук това става ${style.bg}."
        )
    }

    /**
     * The reading that belongs to one chart rather than to everybody: a planet in a house, with the
     * ruler of that house standing somewhere else (see [RulershipChain] for how it is worked out).
     *
     * 13 bodies x 12 houses x 12 ruler houses is 1,872 combinations, so these are composed and a
     * hand-written text takes over wherever one is added. Written plainly on purpose: the point is
     * what it means for the person's week, not the terminology.
     */
    fun planetInHouseWithRuler(link: RulershipChain.Link): Bilingual? {
        writtenChain["${link.planetKey}_${link.houseOfPlanet}_${link.houseOfRuler}"]?.let { return it }

        // First half: what the body is doing in that house. Written where it exists, composed otherwise.
        val head = PlanetInHouseLines.of(link.planetKey, link.houseOfPlanet) ?: run {
            val planet = PlanetMeanings.of(link.planetKey) ?: return null
            val here = HouseMeanings.of(link.houseOfPlanet) ?: return null
            t("${planet.en}\n\nWhere it plays out: ${here.en}.",
              "${planet.bg}\n\nРазиграва се тук: ${here.bg}.")
        }

        // Second half: where it is actually decided.
        val tail = when {
            link.isItsOwnRuler -> t(
                "And this body runs the very house it stands in, so here it answers to nobody but itself. A free hand — and nothing outside to correct you when you get it wrong.",
                "И тази планета управлява самия дом, в който стои, тоест тук не отговаря на никого освен на себе си. Свободна ръка — и нищо отвън, което да ви поправи, когато сгрешите."
            )
            link.rulerIsAtHome -> t(
                "The body that runs this house is standing in it too, so the matter is settled where it happens, without leaning on another part of life.",
                "Планетата, която управлява този дом, стои и в него, тоест въпросът се решава там, където се случва, без да опира на друга част от живота."
            )
            else -> RulerHouseKey.of(link.houseOfRuler) ?: run {
                val thereShort = HouseMeanings.shortOf(link.houseOfRuler) ?: return null
                t("It is not decided there, though. The key is ${thereShort.en}.",
                  "Но не се решава там. Ключът е в ${thereShort.bg}.")
            }
        }
        return t("${head.en}\n\n${tail.en}", "${head.bg}\n\n${tail.bg}")
    }

    /** The ruler of [ofHouse] placed in [inHouse] — where that part of life gets decided. */
    fun rulerOfHouseInHouse(ofHouse: Int, inHouse: Int): Bilingual? {
        writtenRulerInHouse["${ofHouse}_$inHouse"]?.let { return it }
        val from = HouseMeanings.of(ofHouse) ?: return null
        val toShort = HouseMeanings.shortOf(inHouse) ?: return null
        if (ofHouse == inHouse) {
            return t(
                "${ChartConcepts.houseRuler.en}\n\nHere the ruler stays at home. The area: ${from.en}. It is settled on its own terms, without depending on another part of life.",
                "${ChartConcepts.houseRuler.bg}\n\nТук владетелят си остава у дома. Областта е тази: ${from.bg}. Решава се по свои правила, без да зависи от друга част от живота."
            )
        }
        return t(
            "${ChartConcepts.houseRuler.en}\n\nThe area: ${from.en}.\n\nIt is decided through ${toShort.en}.",
            "${ChartConcepts.houseRuler.bg}\n\nОбластта е тази: ${from.bg}.\n\nРешава се през ${toShort.bg}."
        )
    }

    /** A sign wholly contained in one house, touching neither cusp. */
    fun interceptedHouse(house: Int, sign: ZodiacSign): Bilingual? {
        val area = HouseMeanings.of(house) ?: return null
        val style = SignMeanings.of(sign) ?: return null
        return t(
            "${ChartConcepts.interceptedHouse.en}\n\nHere that way of doing things — ${style.en} — reaches you only indirectly. The area: ${area.en}.",
            "${ChartConcepts.interceptedHouse.bg}\n\nТук този начин на действие — ${style.bg} — ви стига само косвено. Областта е тази: ${area.bg}."
        )
    }

    /** How much of each table is hand-written, for progress reporting. */
    data class Coverage(val written: Int, val total: Int) {
        val percent: Int get() = if (total == 0) 0 else written * 100 / total
    }

    fun coverage(): Map<String, Coverage> = mapOf(
        "planet in sign" to Coverage(writtenPlanetInSign.size, PlanetMeanings.byKey.size * 12),
        "planet in house" to Coverage(writtenPlanetInHouse.size, PlanetMeanings.byKey.size * 12),
        "ruler of house in house" to Coverage(writtenRulerInHouse.size, 144),
        "angle in sign" to Coverage(writtenAngleInSign.size, AngleMeanings.byKey.size * 12),
        // A chain reading counts as written when both halves are: the planet-in-house line and the
        // ruler-house key. All twelve keys exist, so each written line covers twelve readings.
        "planet in house with ruler" to Coverage(
            PlanetInHouseLines.entries.size * RulerHouseKey.byHouse.size + writtenChain.size,
            PlanetMeanings.byKey.size * 12 * 12,
        ),
    )
}
