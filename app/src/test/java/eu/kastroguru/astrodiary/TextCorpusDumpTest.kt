package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.domain.humandesign.HdConnectionTexts
import eu.kastroguru.astrodiary.domain.humandesign.HdDescriptions
import eu.kastroguru.astrodiary.domain.humandesign.HdGateTransitTexts
import eu.kastroguru.astrodiary.domain.humandesign.HdHangingGateTexts
import eu.kastroguru.astrodiary.domain.humandesign.HdOpenCenterTexts
import eu.kastroguru.astrodiary.domain.humandesign.HdParentingTexts
import eu.kastroguru.astrodiary.domain.humandesign.HdVariableTexts
import eu.kastroguru.astrodiary.domain.humandesign.TransitInterpretations
import eu.kastroguru.astrodiary.domain.interpretation.AngleMeanings
import eu.kastroguru.astrodiary.domain.interpretation.Bilingual
import eu.kastroguru.astrodiary.domain.interpretation.ChartConcepts
import eu.kastroguru.astrodiary.domain.interpretation.HouseMeanings
import eu.kastroguru.astrodiary.domain.interpretation.PlanetMeanings
import eu.kastroguru.astrodiary.domain.interpretation.SignMeanings
import eu.kastroguru.astrodiary.domain.interpretation.written.AscendantInSign
import eu.kastroguru.astrodiary.domain.interpretation.written.MidheavenInSign
import eu.kastroguru.astrodiary.domain.interpretation.written.MoonInSign
import eu.kastroguru.astrodiary.domain.interpretation.written.PlanetInHouseLines
import eu.kastroguru.astrodiary.domain.interpretation.written.SunInSign
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Dumps every bilingual text that lives in Kotlin source into one CSV, so the whole corpus can be
 * reviewed and edited in one file next to the chain and house-and-sign texts (which are already
 * CSV-backed). Reflection rather than parsing: the texts are multi-line, contain quotes and are
 * assembled from concatenated literals, so reading the values is the only way to get them exactly.
 *
 * Written to `tools/interpretations/kotlin_texts.csv`; `tools/interpretations/corpus.py export`
 * merges it with the content CSVs into the single `all_texts.csv` the owner edits.
 *
 * This is a test only because that is the cheapest way to run app code on the JVM. It asserts what
 * it should: that every source it knows about still yields text, so a renamed or emptied object
 * cannot silently drop out of the export.
 */
class TextCorpusDumpTest {

    private data class Row(val source: String, val key: String, val bg: String, val en: String)

    /** Every object that holds written text. A new text file belongs in this list. */
    private val sources: List<Pair<String, Any>> = listOf(
        "HdDescriptions" to HdDescriptions,
        "HdVariableTexts" to HdVariableTexts,
        "HdConnectionTexts" to HdConnectionTexts,
        "HdOpenCenterTexts" to HdOpenCenterTexts,
        "HdParentingTexts" to HdParentingTexts,
        "HdHangingGateTexts" to HdHangingGateTexts,
        "HdGateTransitTexts" to HdGateTransitTexts,
        "TransitInterpretations" to TransitInterpretations,
        "SunInSign" to SunInSign,
        "MoonInSign" to MoonInSign,
        "AscendantInSign" to AscendantInSign,
        "MidheavenInSign" to MidheavenInSign,
        "PlanetInHouseLines" to PlanetInHouseLines,
        "PlanetMeanings" to PlanetMeanings,
        "SignMeanings" to SignMeanings,
        "HouseMeanings" to HouseMeanings,
        "AngleMeanings" to AngleMeanings,
        "ChartConcepts" to ChartConcepts,
    )

    @Test
    fun dumpEveryTextThatLivesInKotlin() {
        val rows = mutableListOf<Row>()
        val empty = mutableListOf<String>()
        for ((name, obj) in sources) {
            val before = rows.size
            walk(obj, "", name, rows, depth = 0)
            if (rows.size == before) empty += name
        }
        assertTrue("no text came out of: $empty", empty.isEmpty())

        // A pair of texts can legitimately repeat (two gates that say the same thing would be a
        // content bug, but two *keys* pointing at one text object is normal), so keys are what has
        // to be unique — otherwise an edit in the big file would land on the wrong row.
        val keys = rows.map { it.source + "|" + it.key }
        assertTrue("duplicate keys: ${keys.groupingBy { it }.eachCount().filter { it.value > 1 }.keys}",
            keys.size == keys.toSet().size)

        val out = File("../tools/interpretations/kotlin_texts.csv")
        out.parentFile.mkdirs()
        out.bufferedWriter().use { w ->
            w.write("source,key,bg,en\n")
            for (r in rows.sortedWith(compareBy({ it.source }, { it.key }))) {
                w.write(listOf(r.source, r.key, r.bg, r.en).joinToString(",") { csv(it) })
                w.write("\n")
            }
        }
        println("kotlin_texts.csv: ${rows.size} rows from ${sources.size} sources")
    }

    /**
     * Walks whatever shape the content happens to have: a map of enums to pairs, a list, a data
     * class holding several pairs, a `Bilingual`. Anything that is not text is skipped rather than
     * guessed at.
     */
    private fun walk(value: Any?, key: String, source: String, out: MutableList<Row>, depth: Int) {
        if (value == null || depth > 8) return
        when (value) {
            is Bilingual -> out += Row(source, key, value.bg, value.en)
            is Pair<*, *> -> {
                val a = value.first
                val b = value.second
                if (a is String && b is String) {
                    // Some files write the pair as (en to bg) and some as (bg to en); the Cyrillic
                    // is what actually says which is which.
                    if (isBulgarian(a)) out += Row(source, key, a, b)
                    else out += Row(source, key, b, a)
                } else {
                    walk(a, join(key, "1"), source, out, depth + 1)
                    walk(b, join(key, "2"), source, out, depth + 1)
                }
            }
            is Map<*, *> -> value.forEach { (k, v) -> walk(v, join(key, label(k)), source, out, depth + 1) }
            is Iterable<*> -> value.forEachIndexed { i, v -> walk(v, join(key, "$i"), source, out, depth + 1) }
            is String, is Number, is Boolean, is Char, is Enum<*> -> Unit
            else -> for (m in value.javaClass.declaredMethods.sortedBy { it.name }) {
                if (m.parameterCount != 0) continue
                if (!m.name.startsWith("get") || m.name == "getClass") continue
                if (m.returnType == Void.TYPE) continue
                m.isAccessible = true
                val field = m.name.removePrefix("get").replaceFirstChar { it.lowercase() }
                runCatching { walk(m.invoke(value), join(key, field), source, out, depth + 1) }
            }
        }
    }

    private fun join(a: String, b: String) = if (a.isEmpty()) b else "$a|$b"

    private fun label(k: Any?): String = when (k) {
        null -> "null"
        is Enum<*> -> k.name
        is Pair<*, *> -> "${label(k.first)}+${label(k.second)}"
        else -> k.toString()
    }

    private fun isBulgarian(s: String) = s.any { it in 'Ѐ'..'ӿ' }

    private fun csv(s: String): String = "\"" + s.replace("\"", "\"\"") + "\""
}
