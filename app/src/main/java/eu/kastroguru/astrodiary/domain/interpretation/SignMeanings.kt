package eu.kastroguru.astrodiary.domain.interpretation

import eu.kastroguru.astrodiary.domain.model.ZodiacSign

/**
 * How each sign colours whatever falls in it — the "style", not the topic. Combined with
 * [PlanetMeanings] (the topic) this already says something sensible about any placement, which is
 * what lets the app cover all 156 planet-in-sign combinations before every one is written by hand.
 */
object SignMeanings {

    val bySign: Map<ZodiacSign, Bilingual> = mapOf(
        ZodiacSign.ARIES to t(
            "directly and immediately — it starts before it plans, and would rather correct course than wait",
            "направо и веднага — започва, преди да планира, и предпочита да коригира курса, отколкото да чака"
        ),
        ZodiacSign.TAURUS to t(
            "slowly and solidly — it wants something it can touch, and once it settles it does not like being moved",
            "бавно и стабилно — иска нещо, което може да пипне, и щом се установи, не обича да го местят"
        ),
        ZodiacSign.GEMINI to t(
            "curiously and in words — it needs variety, gets bored before it gets deep, and thinks by talking",
            "любопитно и през думи — има нужда от разнообразие, отегчава се, преди да е задълбочило, и мисли, докато говори"
        ),
        ZodiacSign.CANCER to t(
            "protectively and through feeling — it moves sideways rather than head-on, and remembers everything",
            "закрилнически и през чувство — движи се странично, а не право напред, и помни всичко"
        ),
        ZodiacSign.LEO to t(
            "warmly and visibly — it wants to be seen doing it, and is generous when it is appreciated",
            "топло и видимо — иска да бъде видяно, докато го прави, и е щедро, когато го оценяват"
        ),
        ZodiacSign.VIRGO to t(
            "carefully and usefully — it notices what is wrong, wants to fix it, and is hardest on itself",
            "внимателно и полезно — забелязва какво не е наред, иска да го поправи и е най-строго към себе си"
        ),
        ZodiacSign.LIBRA to t(
            "considerately and in pairs — it weighs the other person in, and can lose itself keeping the peace",
            "съобразително и по двама — включва другия в сметката и може да се загуби, докато пази мира"
        ),
        ZodiacSign.SCORPIO to t(
            "intensely and all the way — it does not do half measures, keeps its own counsel, and sees through people",
            "интензивно и докрай — не прави нещата наполовина, пази своето за себе си и вижда през хората"
        ),
        ZodiacSign.SAGITTARIUS to t(
            "broadly and with faith — it needs room and a reason, and would rather be free than certain",
            "нашироко и с вяра — има нужда от простор и от смисъл, и предпочита да е свободно, отколкото сигурно"
        ),
        ZodiacSign.CAPRICORN to t(
            "patiently and with a plan — it builds for later, respects what works, and carries more than it admits",
            "търпеливо и с план — строи за после, уважава онова, което работи, и носи повече, отколкото признава"
        ),
        ZodiacSign.AQUARIUS to t(
            "independently and at a distance — it thinks in systems, resists being told, and cares about the principle",
            "независимо и от разстояние — мисли в системи, съпротивлява се да му нареждат и го е грижа за принципа"
        ),
        ZodiacSign.PISCES to t(
            "gently and without clear edges — it absorbs the mood in the room, and gives until there is nothing left",
            "меко и без ясни граници — попива настроението в стаята и дава, докато не остане нищо"
        ),
    )

    fun of(sign: ZodiacSign): Bilingual? = bySign[sign]
}
