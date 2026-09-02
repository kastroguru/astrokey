package eu.kastroguru.astrodiary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.domain.interpretation.Bilingual
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The written interpretations, read from `assets/interpretations/`.
 *
 * The chain dimension (body × house × ruler × ruler's house) has 8,844 combinations that can
 * actually occur — of the 10,080 on paper, the rest are ruled out by self-rulership and by how far
 * Mercury and Venus can stand from the Sun — and all 8,844 are written. The placement dimension
 * (body × house × sign) has 1,440 and is still being written. Each text is written out rather than
 * composed, so they cannot live in Kotlin source: the files would be megabytes of code and every
 * build would pay for it. They ship as one JSON per body per dimension, which keeps a lookup to a
 * single ~400 KB parse, cached after the first use, and keeps the files small enough to diff a
 * body at a time.
 *
 * A miss returns null and the caller falls back to the composed text, so nothing is ever blank.
 */
@Singleton
class InterpretationAssets @Inject constructor(@ApplicationContext private val context: Context) {

    private val loaded = HashMap<String, JSONObject?>()

    private fun file(name: String): JSONObject? = loaded.getOrPut(name) {
        try {
            context.assets.open("interpretations/$name.json").use { input ->
                JSONObject(input.readBytes().toString(Charsets.UTF_8))
            }
        } catch (_: Exception) {
            null            // that body has not been written yet
        }
    }

    private fun lookup(name: String, key: String): Bilingual? {
        val entry = file(name)?.optJSONObject(key) ?: return null
        val bg = entry.optString("bg")
        val en = entry.optString("en")
        return if (bg.isBlank() || en.isBlank()) null else Bilingual(en = en, bg = bg)
    }

    /** A body in a house, whose ruler is a given body standing in a given house. */
    fun chain(planetKey: String, house: Int, rulerKey: String, rulerHouse: Int): Bilingual? =
        lookup("chain_$planetKey", "$house|$rulerKey|$rulerHouse")

    /** A body in a house and a sign. */
    fun placement(planetKey: String, house: Int, sign: String): Bilingual? =
        lookup("placement_$planetKey", "$house|${sign.lowercase()}")

    /** How many combinations are written, for the progress report. */
    fun writtenCount(kind: String, planetKey: String): Int = file("${kind}_$planetKey")?.length() ?: 0
}
