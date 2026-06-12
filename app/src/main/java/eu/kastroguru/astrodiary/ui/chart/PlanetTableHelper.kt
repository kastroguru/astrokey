package eu.kastroguru.astrodiary.ui.chart

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.ChartUtil
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign

fun setupPlanetTable(
    recyclerView: RecyclerView,
    headerDignity: View,
    prefs: ChartDisplayPrefs
): PlanetRowAdapter {
    val adapter = PlanetRowAdapter(prefs.showDignities)
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter
    recyclerView.isNestedScrollingEnabled = false
    headerDignity.visibility = if (prefs.showDignities) View.VISIBLE else View.GONE
    return adapter
}

fun populatePlanetTable(
    adapter: PlanetRowAdapter,
    cardAspectGrid: View,
    containerAspectGrid: ViewGroup,
    data: AstroData,
    prefs: ChartDisplayPrefs,
    ctx: Context
) {
    val rows = mutableListOf<PlanetRow>()
    Planet.values().forEach { planet ->
        val pos  = data.planets[planet.key] ?: return@forEach
        val sign = try { ZodiacSign.fromId(pos.sign) } catch (_: Exception) { null }
        val dignity = if (prefs.showDignities) ChartUtil.dignityCode(planet.key, pos.sign) else null
        rows += PlanetRow(
            planetName   = planet.localizedName(ctx),
            glyph        = planet.glyph,
            signName     = sign?.localizedName(ctx) ?: "?",
            signGlyph    = sign?.symbol ?: "?",
            degreeInSign = pos.degreeInSign,
            minutes      = pos.minutes,
            house        = pos.house,
            dignity      = dignity,
            retroStatus  = ChartUtil.retrogradeStatus(planet.key, pos.speed)
        )
    }
    if (prefs.showPartOfFortune) {
        val sun  = data.planets["sun"]
        val moon = data.planets["moon"]
        if (sun != null && moon != null && data.cusps.isNotEmpty()) {
            val pof  = ChartUtil.partOfFortune(data.cusps[0], sun.absoluteDegree, moon.absoluteDegree, sun.house, data.cusps)
            val sign = try { ZodiacSign.fromId(pof.sign) } catch (_: Exception) { null }
            rows += PlanetRow(
                ctx.getString(R.string.part_of_fortune), "⊕",
                sign?.localizedName(ctx) ?: "?", sign?.symbol ?: "?",
                pof.degreeInSign, pof.minutes, pof.house
            )
        }
    }
    adapter.submitList(rows)

    if (prefs.showAspectGrid) {
        cardAspectGrid.visibility = View.VISIBLE
        buildAspectGrid(containerAspectGrid, data, ctx)
    } else {
        cardAspectGrid.visibility = View.GONE
    }
}
