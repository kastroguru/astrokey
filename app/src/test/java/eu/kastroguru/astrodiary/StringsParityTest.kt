package eu.kastroguru.astrodiary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The app ships in Bulgarian and English, and a string added to only one of them is invisible until
 * a user in the other language hits that screen. This compares the two files directly, so forgetting
 * a translation fails the build instead of shipping an English sentence into a Bulgarian screen.
 */
class StringsParityTest {

    private val en = File("src/main/res/values/strings.xml")
    private val bg = File("src/main/res/values-bg/strings.xml")

    private fun strings(file: File): Map<String, String> =
        Regex("""<string name="(\w+)"[^>]*>(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
            .findAll(file.readText())
            .associate { it.groupValues[1] to it.groupValues[2] }

    /** %1$s, %2$d … — the arguments a string expects, in order. */
    private fun placeholders(value: String): List<String> =
        Regex("""%(\d+\$)?[a-zA-Z]""").findAll(value).map { it.value }.toList()

    @Test
    fun bothFilesAreReadable() {
        assertTrue("missing ${en.absolutePath}", en.exists())
        assertTrue("missing ${bg.absolutePath}", bg.exists())
        assertTrue("suspiciously few strings", strings(en).size > 100)
    }

    @Test
    fun everyEnglishStringHasABulgarianOne() {
        val missing = (strings(en).keys - strings(bg).keys).sorted()
        assertTrue("Not translated into Bulgarian: $missing", missing.isEmpty())
    }

    @Test
    fun everyBulgarianStringHasAnEnglishOne() {
        val missing = (strings(bg).keys - strings(en).keys).sorted()
        assertTrue("Present only in Bulgarian: $missing", missing.isEmpty())
    }

    @Test
    fun formatArgumentsMatchBetweenLanguages() {
        val e = strings(en)
        val b = strings(bg)
        val mismatched = (e.keys intersect b.keys).filter {
            placeholders(e.getValue(it)).sorted() != placeholders(b.getValue(it)).sorted()
        }.sorted()
        // A translation that drops or renumbers %1$s crashes at runtime rather than reading oddly.
        assertEquals("Format arguments differ between languages for: $mismatched", emptyList<String>(), mismatched)
    }
}
