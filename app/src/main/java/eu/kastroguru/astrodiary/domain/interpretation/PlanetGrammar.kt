package eu.kastroguru.astrodiary.domain.interpretation

/**
 * Grammatical gender of each body's Bulgarian name.
 *
 * In Bulgarian the gender follows the name itself, not the word "планета": едно Слънце, една Луна,
 * един Меркурий, една Венера, един Марс. Any sentence that puts a possessive in front of a body —
 * "вашата Луна", "вашия Марс", "вашето Слънце" — needs this, and getting it wrong is the first thing
 * a Bulgarian reader notices.
 *
 * English needs none of it, which is why this lives here rather than in the text files.
 */
object PlanetGrammar {

    enum class Gender { MASCULINE, FEMININE, NEUTER }

    private val byKey: Map<String, Gender> = mapOf(
        "sun" to Gender.NEUTER,          // едно Слънце
        "moon" to Gender.FEMININE,       // една Луна
        "mercury" to Gender.MASCULINE,   // един Меркурий
        "venus" to Gender.FEMININE,      // една Венера
        "mars" to Gender.MASCULINE,      // един Марс
        "jupiter" to Gender.MASCULINE,   // един Юпитер
        "saturn" to Gender.MASCULINE,    // един Сатурн
        "uranus" to Gender.MASCULINE,    // един Уран
        "neptune" to Gender.MASCULINE,   // един Нептун
        "pluto" to Gender.MASCULINE,     // един Плутон
        "chiron" to Gender.MASCULINE,    // един Хирон
        "rahu" to Gender.MASCULINE,      // един Раху
        "lilith" to Gender.FEMININE,     // една Лилит
        "earth" to Gender.FEMININE,      // една Земя
        "asc" to Gender.MASCULINE,       // един Асцендент
        "mc" to Gender.MASCULINE,        // един Меридиан
    )

    /** Masculine is the safe default: an unknown body name is far more likely to behave like Марс. */
    fun of(planetKey: String): Gender = byKey[planetKey] ?: Gender.MASCULINE
}
