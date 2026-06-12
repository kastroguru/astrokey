package eu.kastroguru.astrodiary.domain.model

data class AstroData(
    val julianDay: Double,
    val obliquity: Double,
    val planets: Map<String, PlanetPosition>,
    val cusps: List<Double>
)

data class PlanetPosition(
    val absoluteDegree: Double,
    val sign: Int,         // 1-12 (1=Aries, 2=Taurus, ...)
    val degreeInSign: Int, // 0-29
    val minutes: Int,      // 0-59
    val house: Int,        // 1-12
    val speed: Double = 0.0  // deg/day from Swiss Ephemeris; 0.0 = unknown (manual construction)
)

enum class ZodiacSign(val id: Int, val symbol: String, val englishName: String, val element: Element) {
    ARIES(1, "♈", "Aries", Element.FIRE),
    TAURUS(2, "♉", "Taurus", Element.EARTH),
    GEMINI(3, "♊", "Gemini", Element.AIR),
    CANCER(4, "♋", "Cancer", Element.WATER),
    LEO(5, "♌", "Leo", Element.FIRE),
    VIRGO(6, "♍", "Virgo", Element.EARTH),
    LIBRA(7, "♎", "Libra", Element.AIR),
    SCORPIO(8, "♏", "Scorpio", Element.WATER),
    SAGITTARIUS(9, "♐", "Sagittarius", Element.FIRE),
    CAPRICORN(10, "♑", "Capricorn", Element.EARTH),
    AQUARIUS(11, "♒", "Aquarius", Element.AIR),
    PISCES(12, "♓", "Pisces", Element.WATER);

    companion object {
        fun fromId(id: Int): ZodiacSign = values().first { it.id == id }
        fun fromDegree(degree: Double): ZodiacSign = fromId((degree / 30).toInt() + 1)
    }
}

enum class Element(val color: Long) {
    FIRE(0xFFE53935),
    EARTH(0xFF4CAF50),
    AIR(0xFF039BE5),
    WATER(0xFF7B1FA2)
}

enum class Planet(val key: String, val glyph: String, val displayName: String) {
    SUN("sun", "☉", "Sun"),
    MOON("moon", "☽", "Moon"),
    MERCURY("mercury", "☿", "Mercury"),
    VENUS("venus", "♀", "Venus"),
    MARS("mars", "♂", "Mars"),
    JUPITER("jupiter", "♃", "Jupiter"),
    SATURN("saturn", "♄", "Saturn"),
    URANUS("uranus", "♅", "Uranus"),
    NEPTUNE("neptune", "♆", "Neptune"),
    PLUTO("pluto", "♇", "Pluto"),
    // ⚷ U+26B7 Chiron, ⚸ U+26B8 Black Moon Lilith — Noto Symbols 2 supports both on API 26+.
    // The chart renderer also falls back to custom canvas paths if these render as boxes.
    CHIRON("chiron", "⚷", "Chiron"),
    RAHU("rahu", "☊", "Rahu"),
    LILITH("lilith", "⚸", "Lilith")
}
