package eu.kastroguru.astrodiary

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Synastry has to be reachable in **both** reading modes.
 *
 * Plain mode sends a tap on a person straight to the reading and never passes through the data
 * screen, so an entry point that exists only on the data screen leaves every non-astrologer unable
 * to open the feature at all. That shipped in the first synastry build and was caught by hand.
 * One button per route, checked here so it cannot quietly go missing again.
 */
class SynastryReachableTest {

    private val layouts = File("src/main/res/layout")

    private fun layout(name: String) = File(layouts, name).readText()

    @Test fun bothRoutesIntoAPersonOfferSynastry() {
        for (name in listOf("fragment_birth_data_detail.xml", "fragment_chart_reading.xml")) {
            val xml = layout(name)
            assertTrue(
                "$name must carry a synastry button — it is one of the two ways into a person",
                "@+id/buttonSynastry" in xml && "@string/synastry_open" in xml,
            )
        }
    }

    @Test fun bothFragmentsNavigateToIt() {
        val src = File("src/main/java/eu/kastroguru/astrodiary/ui")
        for (path in listOf(
            "birthdata/BirthDataDetailFragment.kt",
            "reading/ChartReadingFragment.kt",
        )) {
            val code = File(src, path).readText()
            assertTrue(
                "$path must wire buttonSynastry to R.id.synastryFragment",
                "buttonSynastry" in code && "R.id.synastryFragment" in code,
            )
        }
    }
}
