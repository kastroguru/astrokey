package eu.kastroguru.astrodiary.domain.interpretation

/**
 * One piece of interpretation text in both languages the app ships in.
 *
 * The interpretation layer is deliberately free of Android and of any screen: it is plain data that
 * can be shown in a natal chart screen, a bottom sheet, a report or a future "what am I like" page
 * without being rewritten. Picking the language stays with the UI, as it already does for
 * TransitInterpretations.
 */
data class Bilingual(val en: String, val bg: String) {
    /** True when both languages are actually written — used by the coverage tests. */
    val isComplete: Boolean get() = en.isNotBlank() && bg.isNotBlank()
}

/** Shorthand so the content files read as content and not as code. */
internal fun t(en: String, bg: String) = Bilingual(en, bg)
