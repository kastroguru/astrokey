package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.ui.help.ScreenHelp
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The "?" in the toolbar must have something behind it on every screen that has a title.
 *
 * This reads the navigation graph itself rather than a hand-kept list, so adding a screen without an
 * explanation fails the build instead of shipping a screen users cannot make sense of — which is the
 * whole point of putting the help in one registry.
 */
class ScreenHelpCoverageTest {

    private val navGraph = File("src/main/res/navigation/nav_graph.xml")

    /**
     * Screens that deliberately show no title, and so no "?": the language picker hides the whole
     * toolbar, and the legal screen is itself a document. Kept as an explicit list — the test below
     * checks they really are untitled, so giving one a title later forces the decision again instead
     * of silently slipping through.
     */
    private val untitledByDesign = setOf(
        "languagePickerFragment", "readingModePickerFragment", "legalFragment"
    )

    /** destination name → whether it carries a title, straight from the graph. */
    private fun titledDestinations(): List<String> {
        val xml = navGraph.readText()
        return Regex("""<(?:fragment|dialog|activity)\b(.*?)(?:/>|>)""", RegexOption.DOT_MATCHES_ALL)
            .findAll(xml)
            .mapNotNull { match ->
                val attrs = match.groupValues[1]
                val id = Regex("""android:id="@\+id/(\w+)"""").find(attrs)?.groupValues?.get(1)
                val label = Regex("""android:label="([^"]*)"""").find(attrs)?.groupValues?.get(1)
                if (id != null && !label.isNullOrBlank()) id else null
            }
            .toList()
    }

    private fun resId(name: String): Int =
        R.id::class.java.getField(name).getInt(null)

    @Test
    fun theNavGraphIsReadable() {
        assertTrue("nav_graph.xml not found at ${navGraph.absolutePath}", navGraph.exists())
        assertTrue("no titled destinations found — the parser is wrong", titledDestinations().size > 5)
    }

    @Test
    fun everyTitledScreenHasAnExplanation() {
        val missing = titledDestinations().filter { ScreenHelp.forDestination(resId(it)) == null }
        assertTrue(
            "These screens show a title but have no entry in ScreenHelp: $missing\n" +
                "Add one — a titled screen without an explanation is exactly what the registry exists to prevent.",
            missing.isEmpty()
        )
    }

    @Test
    fun theScreensExemptedFromHelpReallyHaveNoTitle() {
        val titled = titledDestinations().toSet()
        val nowTitled = untitledByDesign.filter { it in titled }
        assertTrue(
            "These were exempt from help because they had no title, but they have one now: $nowTitled\n" +
                "Either give them an explanation in ScreenHelp or drop them from untitledByDesign.",
            nowTitled.isEmpty()
        )
    }

    @Test
    fun theRegistryHasNoEntriesForScreensThatNoLongerExist() {
        val known = titledDestinations().map { resId(it) }.toSet()
        val stale = ScreenHelp.entries.keys.filterNot { it in known }
        assertTrue("ScreenHelp has entries for destinations not in the graph: $stale", stale.isEmpty())
    }
}
