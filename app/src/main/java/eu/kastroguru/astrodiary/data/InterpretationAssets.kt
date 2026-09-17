package eu.kastroguru.astrodiary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.domain.interpretation.Bilingual
import eu.kastroguru.astrodiary.domain.synastry.SynastryCard
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

    /**
     * A card in the synastry reading: a heading and a body, both in two languages.
     *
     * These carry a title of their own because the synastry texts are deliberately written to read
     * as ordinary life rather than as astrology, so the usual heading — the placement, spelled out
     * above the paragraph — would give away the very thing the text is avoiding.
     */
    private fun card(name: String, key: String): SynastryCard? {
        val entry = file(name)?.optJSONObject(key) ?: return null
        val bg = entry.optString("bg")
        val en = entry.optString("en")
        val titleBg = entry.optString("title_bg")
        val titleEn = entry.optString("title_en")
        if (bg.isBlank() || en.isBlank() || titleBg.isBlank() || titleEn.isBlank()) return null
        return SynastryCard(
            title = Bilingual(en = titleEn, bg = titleBg),
            body = Bilingual(en = en, bg = bg),
        )
    }

    /**
     * What a point wants, written twice: [met] when the partner carries the colour and again for
     * when they do not. Keyed point → colour → outcome.
     */
    fun resonance(point: String, colour: String, met: Boolean): SynastryCard? =
        card("resonance", "$point|$colour|${outcome(met)}")

    /** What the descendant or the imum coeli wants, by the sign it falls in, met or not. */
    fun cusp(kind: String, sign: String, met: Boolean): SynastryCard? =
        card("cusp", "$kind|${sign.lowercase()}|${outcome(met)}")

    private fun outcome(met: Boolean) = if (met) "met" else "miss"

    /** A score band, or the paragraph that introduces the synastry reading. */
    fun band(key: String): Bilingual? = lookup("band", key)

    /** How many combinations are written, for the progress report. */
    fun writtenCount(kind: String, planetKey: String): Int = file("${kind}_$planetKey")?.length() ?: 0
}
