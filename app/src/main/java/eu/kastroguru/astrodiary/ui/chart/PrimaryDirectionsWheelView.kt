package eu.kastroguru.astrodiary.ui.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import eu.kastroguru.astrodiary.domain.calculator.PrimaryDirectionsCalculator.DirectedPosition
import eu.kastroguru.astrodiary.domain.model.Element
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * PRIMARY DIRECTIONS biwheel (separate from the transit aspectarian grid).
 *
 *   • Zodiac ring (signs), same width/art as the other charts; Ascendant on the left.
 *   • Natal planets + angles on an inner ring, in element colors.
 *   • Directed points on an outer ring, in per-planet colors: filled = direct, hollow = converse.
 *     Direct is carried WITH the diurnal motion (clockwise, ASC→MC→DESC); converse against it.
 *   • Aspect connectors for active directions, drawn as chords inside the inner aspect circle.
 *   • Glyphs that crowd are spread apart with a thin connector back to the true position.
 *   • Tap a planet to reveal its sweep arc (natal → directed) along the circle.
 */
class PrimaryDirectionsWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0,
) : View(context, attrs, defStyle) {

    data class ActiveLink(
        val promissor: String,
        val significator: String,
        val directLonOfPromissor: Double,
        val aspectAngle: Int,
        val isDirect: Boolean,
    )

    var ascendant: Double = 0.0
        set(v) { field = v; invalidate() }
    var natalLongitudes: Map<String, Double> = emptyMap()
        set(v) { field = v; invalidate() }
    var directed: List<DirectedPosition> = emptyList()
        set(v) { field = v; invalidate() }
    var activeLinks: List<ActiveLink> = emptyList()
        set(v) { field = v; invalidate() }
    var houseCusps: List<Double> = emptyList()   // 12 cusps (absolute degrees), per the house system
        set(v) { field = v; invalidate() }

    private var revealedKey: String? = null

    // Radii (fraction of r) — thin sign ring (like the other charts), lots of room inside.
    private val R_SIGN_OUT = 0.98f
    private val R_SIGN_IN  = 0.88f
    private val R_DIR_GLY  = 0.835f  // directed glyphs — OUTSIDE the marker circle, toward the zodiac
    private val R_DIR_MARK = 0.76f   // directed point markers (smaller circle)
    private val R_NATAL_GLY = 0.645f // natal glyphs (spread, just outside the aspect circle)
    private val R_ASPECT   = 0.56f   // aspect circle — natal POINTS land here; chords drawn on it

    private fun mk(b: Paint.() -> Unit) = Paint(Paint.ANTI_ALIAS_FLAG).apply(b)
    private val bgPnt    = mk { style = Paint.Style.FILL; color = Color.parseColor("#F4F3FA") }
    private val ringPnt  = mk { style = Paint.Style.STROKE; strokeWidth = 2.5f; color = Color.parseColor("#4E4B68") }
    private val circPnt  = mk { style = Paint.Style.STROKE; strokeWidth = 1.4f; color = Color.parseColor("#6E6A8C") }
    private val spokePnt = mk { style = Paint.Style.STROKE; strokeWidth = 1.2f; color = Color.parseColor("#8A86A8") }
    private val connPnt  = mk { style = Paint.Style.STROKE; strokeWidth = 1.2f; color = Color.parseColor("#555273") }
    private val axisPnt  = mk { style = Paint.Style.STROKE; strokeWidth = 2.0f; color = Color.parseColor("#5E5B7A") }  // ASC/MC/IC/DESC
    private val cuspPnt  = mk { style = Paint.Style.STROKE; strokeWidth = 1.0f; color = Color.parseColor("#BCB8D2") }  // intermediate cusps
    private val houseNumPnt = mk { textAlign = Paint.Align.CENTER; color = Color.parseColor("#9591B0") }
    private val glyphPnt = mk { textAlign = Paint.Align.CENTER }
    private val markPnt  = mk {}

    private val natalGlyphColor    = Color.parseColor("#8A6A00")  // dark gold — natal glyphs
    private val directedGlyphColor = Color.parseColor("#1A237E")  // dark blue — directed ("transit") glyphs

    private val aspColor = mapOf(0 to "#1A1ABB", 60 to "#0A6644", 90 to "#CC0000", 120 to "#0055BB", 180 to "#111111")
    private val aspSym   = mapOf(0 to "☌", 60 to "✶", 90 to "□", 120 to "△", 180 to "☍")

    private val hitBoxes = mutableListOf<Triple<Float, Float, String>>()

    override fun onMeasure(w: Int, h: Int) = super.onMeasure(w, w)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f; val cy = width / 2f
        val r = min(cx, cy) * 0.96f
        if (r <= 0) return
        hitBoxes.clear()

        // ── Rings ────────────────────────────────────────────────────────────
        canvas.drawCircle(cx, cy, r * R_SIGN_OUT, bgPnt)
        // Zodiac ring: element sectors + white path glyphs (same as the other charts)
        val outerOval = RectF(cx - r * R_SIGN_OUT, cy - r * R_SIGN_OUT, cx + r * R_SIGN_OUT, cy + r * R_SIGN_OUT)
        val innerOval = RectF(cx - r * R_SIGN_IN, cy - r * R_SIGN_IN, cx + r * R_SIGN_IN, cy + r * R_SIGN_IN)
        val midR = r * (R_SIGN_OUT + R_SIGN_IN) / 2f
        val glyphSize = r * (R_SIGN_OUT - R_SIGN_IN) * 0.50f
        val sectorFill = mk { style = Paint.Style.FILL }
        for (i in 0 until 12) {
            val sign = ZodiacSign.fromId(i + 1)
            val start = chartAngle(i * 30.0).toFloat()
            sectorFill.color = elementColor(sign.element)
            val sect = Path()
            sect.arcTo(outerOval, start, -30f); sect.arcTo(innerOval, start - 30f, 30f); sect.close()
            canvas.drawPath(sect, sectorFill)
            spoke(canvas, cx, cy, r * R_SIGN_OUT, r * R_SIGN_IN, chartAngle(i * 30.0), circPnt)
            val (gx, gy) = pt(cx, cy, midR, chartAngle(i * 30.0 + 15.0))
            canvas.save(); canvas.translate(gx, gy); ZodiacGlyphs.draw(canvas, sign, glyphSize); canvas.restore()
        }
        ringPnt.let { canvas.drawCircle(cx, cy, r * R_SIGN_OUT, it) }
        circPnt.let { canvas.drawCircle(cx, cy, r * R_SIGN_IN, it) }
        circPnt.let { canvas.drawCircle(cx, cy, r * R_DIR_MARK, it) }
        circPnt.let { canvas.drawCircle(cx, cy, r * R_ASPECT, it) }

        // ── House cusps (per the configured house system) ─────────────────────
        if (houseCusps.size == 12) {
            for (i in 0 until 12) {
                val ang = chartAngle(houseCusps[i])
                val isAxis = i == 0 || i == 3 || i == 6 || i == 9  // ASC / IC / DESC / MC
                spoke(canvas, cx, cy, r * R_ASPECT, r * R_SIGN_IN, ang, if (isAxis) axisPnt else cuspPnt)
                var span = houseCusps[(i + 1) % 12] - houseCusps[i]
                if (span < 0) span += 360.0
                val (nx, ny) = pt(cx, cy, r * (R_ASPECT - 0.052f), chartAngle(houseCusps[i] + span / 2.0))
                houseNumPnt.textSize = r * 0.030f
                canvas.drawText("${i + 1}", nx, ny + houseNumPnt.textSize * 0.36f, houseNumPnt)
            }
        }

        // ── Aspect connectors: chords inside the aspect circle ───────────────
        for (link in activeLinks) {
            val natalLon = natalLongitudes[link.significator] ?: continue
            val (x1, y1) = pt(cx, cy, r * R_ASPECT, chartAngle(link.directLonOfPromissor))
            val (x2, y2) = pt(cx, cy, r * R_ASPECT, chartAngle(natalLon))
            val col = Color.parseColor(aspColor[link.aspectAngle] ?: "#333333")
            canvas.drawLine(x1, y1, x2, y2, mk { color = col; style = Paint.Style.STROKE; strokeWidth = 2.4f; alpha = 220 })
            val dotP = mk { color = col; style = Paint.Style.FILL }
            canvas.drawCircle(x1, y1, r * 0.011f, dotP); canvas.drawCircle(x2, y2, r * 0.011f, dotP)
            val mxp = (x1 + x2) / 2f; val myp = (y1 + y2) / 2f
            canvas.drawCircle(mxp, myp, r * 0.026f, mk { color = col; style = Paint.Style.FILL })
            canvas.drawText(aspSym[link.aspectAngle] ?: "?", mxp, myp + r * 0.012f,
                mk { color = Color.WHITE; textAlign = Paint.Align.CENTER; textSize = r * 0.034f })
        }

        // ── Natal planets + angles: POINTS land on the aspect circle; glyphs spread just outside ──
        val natalKeys = natalLongitudes.keys.toList()
        val natalTrue = natalKeys.map { chartAngle(natalLongitudes[it]!!) }
        val natalDisp = spread(natalTrue, 13.0)
        glyphPnt.textSize = r * 0.058f
        natalKeys.forEachIndexed { i, key ->
            val col = natalColor(key, natalLongitudes[key]!!)
            val (mx, my) = pt(cx, cy, r * R_ASPECT, natalTrue[i])     // point ON the aspect circle
            canvas.drawCircle(mx, my, r * 0.013f, mk { color = col; style = Paint.Style.FILL })
            val (px, py) = pt(cx, cy, r * R_NATAL_GLY, natalDisp[i])  // glyph just outside the aspect circle
            if (angDist(natalDisp[i], natalTrue[i]) > 1.5) canvas.drawLine(mx, my, px, py, connPnt)
            glyphPnt.color = natalGlyphColor   // dark gold glyph; the point on the circle keeps its element color
            canvas.drawText(glyphFor(key), px, py + glyphPnt.textSize * 0.36f, glyphPnt)
        }

        // ── Directed points (outer ring): marker at true position, glyph spread ──
        data class D(val key: String, val lon: Double, val direct: Boolean)
        val dGlyphs = directed.flatMap { listOf(D(it.key, it.directLon, true), D(it.key, it.converseLon, false)) }
        val dTrue = dGlyphs.map { chartAngle(it.lon) }
        val dDisp = spread(dTrue, 10.0)
        glyphPnt.textSize = r * 0.050f
        dGlyphs.forEachIndexed { i, d ->
            val col = PlanetColors.of(d.key)
            val (mx, my) = pt(cx, cy, r * R_DIR_MARK, dTrue[i])
            val mr = r * 0.020f
            markPnt.color = col
            if (d.direct) { markPnt.style = Paint.Style.FILL; canvas.drawCircle(mx, my, mr, markPnt) }
            else { markPnt.style = Paint.Style.STROKE; markPnt.strokeWidth = r * 0.0075f; canvas.drawCircle(mx, my, mr, markPnt) }
            val (gx, gy) = pt(cx, cy, r * R_DIR_GLY, dDisp[i])
            if (angDist(dDisp[i], dTrue[i]) > 1.5) canvas.drawLine(mx, my, gx, gy, connPnt)
            glyphPnt.color = directedGlyphColor   // dark blue glyph; the marker on the circle keeps the planet color
            canvas.drawText(glyphFor(d.key), gx, gy + glyphPnt.textSize * 0.36f, glyphPnt)
            hitBoxes += Triple(mx, my, d.key)

            if (revealedKey == d.key) {
                val natalLon = natalLongitudes[d.key] ?: return@forEachIndexed
                drawArcAlongCircle(canvas, cx, cy, r * R_DIR_MARK, chartAngle(natalLon), dTrue[i],
                    mk { color = col; style = Paint.Style.STROKE; strokeWidth = r * 0.012f; alpha = 150 })
            }
        }
    }

    /** Spread display angles so neighbours are at least [minGap]° apart (relaxation around the circle). */
    private fun spread(trueAngles: List<Double>, minGap: Double): DoubleArray {
        val n = trueAngles.size
        val disp = DoubleArray(n) { trueAngles[it] }
        if (n < 2) return disp
        val order = (0 until n).sortedBy { trueAngles[it] }
        repeat(60) {
            for (k in 0 until n) {
                val a = order[k]; val b = order[(k + 1) % n]
                var gap = disp[b] - disp[a]
                if (k == n - 1) gap += 360.0
                if (gap < minGap) {
                    val push = (minGap - gap) / 2.0
                    disp[a] = disp[a] - push
                    disp[b] = disp[b] + push
                }
            }
        }
        return disp
    }

    private fun drawArcAlongCircle(canvas: Canvas, cx: Float, cy: Float, rad: Float, a1: Double, a2: Double, p: Paint) {
        val oval = RectF(cx - rad, cy - rad, cx + rad, cy + rad)
        var sweep = (a2 - a1) % 360.0
        if (sweep > 180) sweep -= 360; if (sweep < -180) sweep += 360
        canvas.drawArc(oval, a1.toFloat(), sweep.toFloat(), false, p)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_UP) {
            var best: String? = null; var bestD = Float.MAX_VALUE
            for ((x, y, key) in hitBoxes) {
                val d = (x - e.x) * (x - e.x) + (y - e.y) * (y - e.y)
                if (d < bestD) { bestD = d; best = key }
            }
            val touchR = width * 0.06f
            if (best != null && bestD < touchR * touchR) {
                revealedKey = if (revealedKey == best) null else best; invalidate()
            } else if (revealedKey != null) { revealedKey = null; invalidate() }
        }
        return true
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private fun chartAngle(ecliptic: Double): Double {
        var v = (180.0 - (ecliptic - ascendant)) % 360.0
        if (v < 0) v += 360.0
        return v
    }
    private fun pt(cx: Float, cy: Float, rad: Float, deg: Double): Pair<Float, Float> {
        val a = Math.toRadians(deg)
        return cx + rad * cos(a).toFloat() to cy + rad * sin(a).toFloat()
    }
    private fun spoke(canvas: Canvas, cx: Float, cy: Float, r1: Float, r2: Float, deg: Double, p: Paint) {
        val a = Math.toRadians(deg); val c = cos(a).toFloat(); val s = sin(a).toFloat()
        canvas.drawLine(cx + r1 * c, cy + r1 * s, cx + r2 * c, cy + r2 * s, p)
    }
    private fun angDist(a: Double, b: Double): Double {
        var d = abs(a - b) % 360.0; if (d > 180) d = 360 - d; return d
    }
    private fun glyphFor(key: String): String = when (key) {
        "asc" -> "Asc"; "desc" -> "Dsc"; "mc" -> "MC"; "ic" -> "IC"
        else -> Planet.values().firstOrNull { it.key == key }?.glyph ?: "?"
    }
    private fun natalColor(key: String, lon: Double): Int =
        if (key in setOf("asc", "desc", "mc", "ic")) Color.parseColor("#455A64")
        else elementColor(ZodiacSign.fromDegree(lon).element)
    private fun elementColor(e: Element) = when (e) {
        Element.FIRE  -> Color.parseColor("#CC3300")
        Element.EARTH -> Color.parseColor("#2A1506")
        Element.WATER -> Color.parseColor("#1144CC")
        Element.AIR   -> Color.parseColor("#C09500")
    }
}
