package eu.kastroguru.astrodiary.ui.chart

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.ChartUtil
import eu.kastroguru.astrodiary.domain.model.Planet

private val GRID_KEYS = listOf(
    "sun", "moon", "mercury", "venus", "mars",
    "jupiter", "saturn", "uranus", "neptune", "pluto"
)

// Same colors as AstroChartView aspect lines
private val ASPECT_COLORS = mapOf(
    "☌" to Color.parseColor("#1A1ABB"),   // conjunction  – deep blue
    "⚹" to Color.parseColor("#0A6644"),   // sextile      – dark teal
    "□" to Color.parseColor("#CC0000"),   // square       – dark red
    "△" to Color.parseColor("#0055BB"),   // trine        – strong blue
    "⚻" to Color.parseColor("#886600"),   // quincunx     – dark amber
    "☍" to Color.parseColor("#111111")    // opposition   – near black
)

private val GLYPH_COLOR  = Color.parseColor("#1A1A3A")
private val BORDER_COLOR = Color.parseColor("#DDDDDD")

fun buildAspectGrid(container: ViewGroup, data: AstroData, ctx: Context) {
    container.removeAllViews()
    val positions = GRID_KEYS.map { data.planets[it] }
    val glyphs    = GRID_KEYS.map { key ->
        Planet.values().find { it.key == key }?.glyph ?: key.take(2)
    }

    val dp     = ctx.resources.displayMetrics.density
    val cellPx = (32 * dp).toInt()

    val table = TableLayout(ctx).apply {
        setBackgroundColor(Color.WHITE)
    }

    // Header row — planet glyphs
    val header = TableRow(ctx)
    header.addView(cornerCell(ctx, cellPx))          // empty top-left corner
    glyphs.forEach { g -> header.addView(headerCell(ctx, g, cellPx)) }
    table.addView(header)

    // Lower-triangle data rows
    GRID_KEYS.forEachIndexed { row, _ ->
        val pos1 = positions[row] ?: return@forEachIndexed
        val tr   = TableRow(ctx)
        tr.addView(headerCell(ctx, glyphs[row], cellPx))  // row header

        GRID_KEYS.forEachIndexed { col, _ ->
            val symbol = if (col < row) {
                val pos2 = positions[col]
                if (pos2 != null) ChartUtil.aspectSymbol(pos1.absoluteDegree, pos2.absoluteDegree) else null
            } else null
            tr.addView(dataCell(ctx, symbol, cellPx))
        }
        table.addView(tr)
    }

    container.addView(table)
}

private fun cornerCell(ctx: Context, size: Int): TextView =
    TextView(ctx).apply {
        layoutParams = TableRow.LayoutParams(size, size)
        background   = borderBg(Color.parseColor("#F5F5F5"))
    }

private fun headerCell(ctx: Context, text: String, size: Int): TextView =
    TextView(ctx).apply {
        this.text = text
        textSize  = 13f
        setTextColor(GLYPH_COLOR)
        gravity   = Gravity.CENTER
        layoutParams = TableRow.LayoutParams(size, size)
        background = borderBg(Color.parseColor("#F5F5F5"))
    }

private fun dataCell(ctx: Context, symbol: String?, size: Int): TextView =
    TextView(ctx).apply {
        val color = if (symbol != null) ASPECT_COLORS[symbol] ?: GLYPH_COLOR else Color.TRANSPARENT
        text      = symbol ?: ""
        textSize  = 15f
        setTextColor(color)
        gravity   = Gravity.CENTER
        layoutParams = TableRow.LayoutParams(size, size)
        background = borderBg(Color.WHITE)
    }

private fun borderBg(fill: Int): GradientDrawable =
    GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(fill)
        setStroke(1, BORDER_COLOR)
    }
