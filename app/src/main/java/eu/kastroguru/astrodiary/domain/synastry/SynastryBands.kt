package eu.kastroguru.astrodiary.domain.synastry

/**
 * Which band a score falls into. **The prose lives in `content/band.csv`, not here.**
 *
 * It used to sit in a Kotlin file as string literals, which made it the one part of the synastry
 * corpus that could not go through the ordinary edit round trip — and it is the most-read text in
 * the module, since it is what explains the big number at the top. On the same CSV pipeline as
 * everything else, `tools/synastry/texts.py` can export it for editing and write it back.
 *
 * The cut-offs follow the measured distribution: over 3 000 pairs of charts that could actually
 * occur, the median comes out at 56, the tenth percentile at 38 and the ninetieth at 70, giving
 * band occupancy of 9 / 25 / 30 / 23 / 9 / 1 per cent. **Recalibrate whenever the rules or the
 * weights change** — a stale band table lies quietly where a stale rule would fail a test.
 */
object SynastryBands {

    /** Highest band first, so [keyFor] can take the first that contains the score. */
    val bands: List<Pair<IntRange, String>> = listOf(
        71..Int.MAX_VALUE to "71..max",
        61..70 to "61..70",
        51..60 to "51..60",
        39..50 to "39..50",
        23..38 to "23..38",
        Int.MIN_VALUE..22 to "min..22",
    )

    /** The asset key for this score's band. */
    fun keyFor(score: Int): String = bands.first { score in it.first }.second

    /** The asset key for the paragraph that introduces the whole reading. */
    const val INTRO = "intro"
}
