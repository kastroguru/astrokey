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
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.Element
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlin.math.abs
import kotlin.math.min

/**
 * Transit visualization as a linear "aspectarian grid":
 *
 *   [sign glyph]          ← top-most, per transit planet
 *   [house number]
 *   ──── TRANSIT PLANETS ─ 0°────────────15°────────────30° ────
 *                                aspect lines
 *   ──── NATAL PLANETS ─── 0°────────────15°────────────30° ────
 *   [house number]
 *   [sign glyph]          ← bottom-most, per natal planet
 *
 * Planets are placed on the 0–30° scale (degree within their sign).
 * Aspect lines are drawn only for aspects with orb ≤ 2°.
 */
class AspectsChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ── Data ──────────────────────────────────────────────────────────────────
    var natalCusps: List<Double> = emptyList()
        set(value) { field = value; rebuild() }
    var natalPlanets: Map<String, Double> = emptyMap()
        set(value) { field = value; rebuild() }
    var transitData: AstroData? = null
        set(value) { field = value; rebuild() }

    /** Called when a natal planet glyph is tapped. */
    var onNatalPlanetClick: ((Planet) -> Unit)? = null

    // ── Constants ─────────────────────────────────────────────────────────────
    private val ORB_MAX = 2.0   // degrees — only aspects within this orb are shown

    // Aspect definitions
    private data class AspDef(val angle: Int, val color: Int)
    private val ASPECTS = listOf(
        AspDef(  0, Color.parseColor("#1A1ABB")),  // conjunction  ☌
        AspDef( 60, Color.parseColor("#0A6644")),  // sextile      ✶
        AspDef( 90, Color.parseColor("#CC0000")),  // square       □
        AspDef(120, Color.parseColor("#0055BB")),  // trine        △
        AspDef(150, Color.parseColor("#886600")),  // quincunx     ⚻
        AspDef(180, Color.parseColor("#111111"))   // opposition   ☍
    )
    private val aspSymbols = mapOf(0 to "☌", 60 to "✶", 90 to "□", 120 to "△", 150 to "⚻", 180 to "☍")

    // Element colours
    private fun elemColor(e: Element) = when (e) {
        Element.FIRE  -> Color.parseColor("#CC3300")
        Element.EARTH -> Color.parseColor("#2A1506")
        Element.WATER -> Color.parseColor("#1144CC")
        Element.AIR   -> Color.parseColor("#C09500")
    }

    // ── Computed layout data ──────────────────────────────────────────────────
    private data class PlanetEntry(
        val planet: Planet,
        val deg: Int,        // degree within sign (0–29) → x position
        val minutes: Int,
        val signId: Int,
        val house: Int,
        val absoluteDeg: Double,
        val elemColor: Int,
        var displayDeg: Double = 0.0  // adjusted for overlaps (0–30)
    )

    private data class AspectEntry(
        val tIdx: Int,   // index in transitEntries
        val nIdx: Int,   // index in natalEntries
        val angle: Int,
        val orb: Double,
        val color: Int
    )

    private var transitEntries = listOf<PlanetEntry>()
    private var natalEntries   = listOf<PlanetEntry>()
    private var aspectEntries  = listOf<AspectEntry>()

    // Hit-test rects for natal planets
    private val natalHitBoxes = mutableListOf<Pair<RectF, Planet>>()

    // ── Paint objects ─────────────────────────────────────────────────────────
    private fun mk(b: Paint.() -> Unit) = Paint(Paint.ANTI_ALIAS_FLAG).apply(b)
    private val linePnt   = mk { style = Paint.Style.STROKE; strokeWidth = 2f; color = Color.parseColor("#AAAAAA") }
    private val axisPnt   = mk { style = Paint.Style.STROKE; strokeWidth = 1f; color = Color.parseColor("#888888") }
    private val glyphPnt  = mk { textAlign = Paint.Align.CENTER }
    private val signPnt   = mk { textAlign = Paint.Align.CENTER; color = Color.parseColor("#888888") }
    private val housePnt  = mk { textAlign = Paint.Align.CENTER; color = Color.parseColor("#555555") }
    private val aspSymPnt = mk { textAlign = Paint.Align.CENTER; color = Color.WHITE }
    private val aspBgPnt  = mk { style = Paint.Style.FILL }

    /** Call this whenever settings change (e.g. on fragment resume). */
    fun refreshSettings() { rebuild() }

    // ── Build internal data ───────────────────────────────────────────────────
    private fun rebuild() {
        val transit = transitData ?: return

        // Read AspectPrefs — same SharedPreferences as AstroChartView / Settings screen
        val prefs = context.getSharedPreferences("aspect_settings", Context.MODE_PRIVATE)
        val excluded = buildSet<String> {
            if (!prefs.getBoolean("chiron", true)) add("chiron")
            if (!prefs.getBoolean("lilith", true)) add("lilith")
            if (!prefs.getBoolean("rahu",   true)) add("rahu")
        }
        val hidePersonal = prefs.getBoolean("hide_personal_transits", false)
        val personalKeys = setOf("sun", "moon", "mercury", "venus", "mars")

        // ── Transit entries (skip excluded planets; optionally skip personal) ──
        transitEntries = Planet.values()
            .filter { it.key !in excluded }
            .filter { !hidePersonal || it.key !in personalKeys }
            .mapNotNull { planet ->
                val pos = transit.planets[planet.key] ?: return@mapNotNull null
                val sign = ZodiacSign.fromId(pos.sign)
                PlanetEntry(
                    planet      = planet,
                    deg         = pos.degreeInSign,
                    minutes     = pos.minutes,
                    signId      = pos.sign,
                    house       = pos.house,
                    absoluteDeg = pos.absoluteDegree,
                    elemColor   = elemColor(sign.element),
                    displayDeg  = pos.degreeInSign.toDouble()
                )
            }

        // ── Natal entries (skip excluded planets) ──
        natalEntries = Planet.values()
            .filter { it.key !in excluded }
            .mapNotNull { planet ->
                val absD = natalPlanets[planet.key] ?: return@mapNotNull null
            val signId = (absD / 30.0).toInt().coerceIn(0, 11) + 1
            val deg    = (absD % 30.0).toInt()
            val mins   = ((absD % 30.0 - deg) * 60.0).toInt()
            val house  = planetHouse(absD, natalCusps)
            val sign   = ZodiacSign.fromId(signId)
            PlanetEntry(
                planet      = planet,
                deg         = deg,
                minutes     = mins,
                signId      = signId,
                house       = house,
                absoluteDeg = absD,
                elemColor   = elemColor(sign.element),
                displayDeg  = deg.toDouble()
            )
        }

        // Resolve overlaps (1-D collision on 0–30 scale)
        resolveLinear(transitEntries)
        resolveLinear(natalEntries)

        // ── Aspects (≤ ORB_MAX degrees) ──
        val aspects = mutableListOf<AspectEntry>()
        transitEntries.forEachIndexed { ti, t ->
            natalEntries.forEachIndexed { ni, n ->
                val raw = ((t.absoluteDeg - n.absoluteDeg + 360.0) % 360.0)
                val diff = if (raw > 180.0) 360.0 - raw else raw
                for (asp in ASPECTS) {
                    val orb = abs(diff - asp.angle)
                    if (orb <= ORB_MAX) {
                        aspects += AspectEntry(ti, ni, asp.angle, orb, asp.color)
                        break
                    }
                }
            }
        }
        aspectEntries = aspects.sortedBy { it.orb }  // tightest first (drawn last = on top)

        invalidate()
        requestLayout()
    }

    /** Spread overlapping planet entries on the 0–30 linear scale. */
    private fun resolveLinear(entries: List<PlanetEntry>, minGap: Double = 1.2) {
        val sorted = entries.sortedBy { it.deg.toDouble() }.toMutableList()
        // Simple forward pass
        for (i in 1 until sorted.size) {
            if (sorted[i].displayDeg - sorted[i - 1].displayDeg < minGap) {
                sorted[i].displayDeg = sorted[i - 1].displayDeg + minGap
            }
        }
        // Clamp to 0–30 (shift back if overflowed)
        val overflow = (sorted.lastOrNull()?.displayDeg ?: 0.0) - 29.5
        if (overflow > 0) sorted.forEach { it.displayDeg -= overflow }
        sorted.forEach { it.displayDeg = it.displayDeg.coerceIn(0.0, 29.5) }
    }

    private fun planetHouse(deg: Double, cusps: List<Double>): Int {
        for (i in 0 until 12) {
            val start = cusps.getOrElse(i) { 0.0 }
            val end   = cusps.getOrElse((i + 1) % 12) { 0.0 }
            val inArc = if (end > start) deg >= start && deg < end else deg >= start || deg < end
            if (inArc) return i + 1
        }
        return 1
    }

    // ── Measure ───────────────────────────────────────────────────────────────
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val w = MeasureSpec.getSize(widthSpec).takeIf { it > 0 } ?: 800
        val h = computeHeight(w.toFloat()).toInt()
        setMeasuredDimension(w, h)
    }

    private fun computeHeight(w: Float): Float {
        val fs = w * FONT_SCALE; val fsSign = w * SIGN_SCALE; val fsHouse = w * HOUSE_SCALE
        val gap = w * 0.012f
        return PADDING_TOP * w +
                fsSign + gap + fsHouse + gap + fs + gap +  // transit block (above top line)
                w * INNER_HEIGHT_RATIO +                   // interior / aspect lines
                gap + fs + gap + fsHouse + gap + fsSign +  // natal block (below bottom line)
                PADDING_BOT * w
    }

    // ── Layout helpers ────────────────────────────────────────────────────────
    private val FONT_SCALE       = 0.048f   // glyph font = w * this
    private val SIGN_SCALE       = 0.038f
    private val HOUSE_SCALE      = 0.034f
    private val ASP_SYM_SCALE    = 0.030f
    private val PADDING_TOP      = 0.04f    // fraction of width
    private val PADDING_BOT      = 0.04f
    private val H_MARGIN_LEFT    = 0.12f    // left margin — wider to fit orb scale labels
    private val H_MARGIN_RIGHT   = 0.04f    // right margin — just a small gutter
    private val INNER_HEIGHT_RATIO= 0.55f   // aspect area / width

    private fun xForDeg(displayDeg: Double, w: Float): Float {
        val lm = w * H_MARGIN_LEFT
        val rm = w * H_MARGIN_RIGHT
        val available = w - lm - rm
        return (lm + (displayDeg / 30.0) * available).toFloat()
    }

    // ── Draw ──────────────────────────────────────────────────────────────────
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        if (w == 0f || transitEntries.isEmpty()) return

        val fs      = w * FONT_SCALE
        val fsSign  = w * SIGN_SCALE
        val fsHouse = w * HOUSE_SCALE
        val fsSym   = w * ASP_SYM_SCALE
        val gap     = w * 0.012f
        val padTop  = PADDING_TOP * w
        val lm      = w * H_MARGIN_LEFT
        val rm      = w * H_MARGIN_RIGHT

        // Transit block ABOVE top line, natal block BELOW bottom line
        val signTopY  = padTop + fsSign
        val houseTopY = signTopY + gap + fsHouse
        val transitY  = houseTopY + gap + fs          // transit planet glyph baseline
        val lineY_t   = transitY + gap + 2f            // ← TOP LINE

        val innerH    = w * INNER_HEIGHT_RATIO
        val lineY_n   = lineY_t + innerH               // ← BOTTOM LINE

        val natalY    = lineY_n + gap + fs             // natal planet glyph baseline
        val houseNatY = natalY + gap + fsHouse
        val signNatY  = houseNatY + gap + fsSign

        // ── Dark backgrounds ──────────────────────────────────────────────────
        val darkBg   = Color.parseColor("#F0EFF6")  // light lavender (matches app surface)
        val innerBg  = Color.parseColor("#E8E6F2")  // slightly deeper lavender for interior
        val bgPnt    = mk { style = Paint.Style.FILL }
        // Full chart background
        bgPnt.color = darkBg
        canvas.drawRect(0f, 0f, w, height.toFloat(), bgPnt)
        // Slightly darker interior (between the two axis lines)
        bgPnt.color = innerBg
        canvas.drawRect(lm, lineY_t, w - rm, lineY_n, bgPnt)

        // ── Main axis lines ───────────────────────────────────────────────────
        axisPnt.strokeWidth = 2.5f
        canvas.drawLine(lm, lineY_t, w - rm, lineY_t, axisPnt)
        canvas.drawLine(lm, lineY_n, w - rm, lineY_n, axisPnt)

        // ── Orb scale on the LEFT side (0.0 at bottom = exact, 2.0 at top = wide) ──
        val scaleX   = lm * 0.55f          // x of the vertical scale line
        val scalePnt = mk { style = Paint.Style.STROKE; strokeWidth = 1.0f; color = Color.parseColor("#999999") }
        val scaleLbl = mk { textSize = fsHouse * 0.72f; textAlign = Paint.Align.RIGHT; color = Color.parseColor("#AAAAAA") }
        canvas.drawLine(scaleX, lineY_t, scaleX, lineY_n, scalePnt)
        for (step in listOf(0.0, 0.5, 1.0, 1.5, 2.0)) {
            // y: orb=0 → lineY_n (bottom); orb=2 → lineY_t (top)
            val orbFrac = (step / ORB_MAX).toFloat()
            val sy = lineY_n - orbFrac * (lineY_n - lineY_t)
            val tickLen = if (step == 0.0 || step == 1.0 || step == 2.0) 10f else 6f
            canvas.drawLine(scaleX, sy, scaleX + tickLen, sy, scalePnt)
            canvas.drawText("%.1f".format(step), scaleX - 3f, sy + scaleLbl.textSize * 0.36f, scaleLbl)
        }

        // ── Degree ruler ticks (every 1° small, every 5° longer) ─────────────
        // Ticks go OUTWARD (away from interior), labels go INWARD (inside the square)
        val tickShort = 8f; val tickLong = 20f   // bold ruler   // more visible
        val degLabelPnt = mk { textSize = fsHouse * 0.92f; textAlign = Paint.Align.CENTER; isFakeBoldText = true; color = Color.parseColor("#444444") }
        val tickPntS = mk { style = Paint.Style.STROKE; strokeWidth = 1.5f; color = Color.parseColor("#777777") }
        val tickPntL = mk { style = Paint.Style.STROKE; strokeWidth = 2.5f; color = Color.parseColor("#333333") }
        for (deg in 0..30) {
            val x  = xForDeg(deg.toDouble(), w)
            val tp = if (deg % 5 == 0) tickPntL else tickPntS
            val th = if (deg % 5 == 0) tickLong else tickShort
            // Top line: ticks go UPWARD (outward, into transit area)
            canvas.drawLine(x, lineY_t, x, lineY_t - th, tp)
            // Bottom line: ticks go DOWNWARD (outward, into natal area)
            canvas.drawLine(x, lineY_n, x, lineY_n + th, tp)
            // Degree labels at 0,5,10,…: top label below the top line (inside), bottom label above bottom line (inside)
            if (deg % 5 == 0) {
                val label = "$deg°"
                // Below top line (inside the square)
                canvas.drawText(label, x, lineY_t + degLabelPnt.textSize * 1.2f, degLabelPnt)
                // Above bottom line (inside the square)
                canvas.drawText(label, x, lineY_n - degLabelPnt.textSize * 0.3f, degLabelPnt)
            }
        }

        // ── Aspect lines (strictly interior — lineY_t to lineY_n) ─────────────
        for (asp in aspectEntries) {
            val t  = transitEntries[asp.tIdx]
            val n  = natalEntries[asp.nIdx]
            val x1 = xForDeg(t.displayDeg, w)
            val x2 = xForDeg(n.displayDeg, w)
            val alpha  = ((1.0 - asp.orb / ORB_MAX) * 180 + 75).toInt().coerceIn(75, 255)
            val strokeW = if (asp.orb < 0.5) 3.5f else if (asp.orb < 1.0) 2.8f else 2.2f
            val p = mk { color = asp.color; this.alpha = alpha; style = Paint.Style.STROKE; strokeWidth = strokeW }
            canvas.drawLine(x1, lineY_t, x2, lineY_n, p)

            // Symbol position along the line: t=0 (orb=max) → near transit; t=1 (orb=0) → near natal
            val orbT = ((ORB_MAX - asp.orb) / ORB_MAX).toFloat().coerceIn(0.05f, 0.95f)
            val mx = x1 + orbT * (x2 - x1)
            val my = lineY_t + orbT * (lineY_n - lineY_t)
            aspBgPnt.color = asp.color; aspBgPnt.alpha = alpha
            canvas.drawCircle(mx, my, fsSym * 0.9f, aspBgPnt)
            aspSymPnt.textSize = fsSym; aspSymPnt.alpha = 255
            canvas.drawText(aspSymbols[asp.angle] ?: "?", mx, my + fsSym * 0.36f, aspSymPnt)
        }

        // ── Transit planets — ABOVE top line ─────────────────────────────────
        glyphPnt.textSize = fs; signPnt.textSize = fsSign; housePnt.textSize = fsHouse
        // House row border lines + "дом" label (transit side)
        val rowBorderPnt = mk { style = Paint.Style.STROKE; strokeWidth = 0.9f; color = Color.parseColor("#BBBBBB") }
        val rowLblPnt    = mk { textSize = fsHouse * 0.72f; color = Color.parseColor("#AAAAAA"); textAlign = Paint.Align.LEFT }
        val hRowTop_t = houseTopY - fsHouse * 1.0f
        val hRowBot_t = houseTopY + fsHouse * 0.4f
        canvas.drawLine(0f, hRowTop_t, w, hRowTop_t, rowBorderPnt)
        canvas.drawLine(0f, hRowBot_t, w, hRowBot_t, rowBorderPnt)
        canvas.drawText(context.getString(R.string.house_row_label), 4f, houseTopY, rowLblPnt)

        for (e in transitEntries) {
            val x = xForDeg(e.displayDeg, w)
            signPnt.color = elemColor(ZodiacSign.fromId(e.signId).element)
            canvas.drawText(ZodiacSign.fromId(e.signId).symbol, x, signTopY, signPnt)
            housePnt.color = Color.parseColor("#555555")
            canvas.drawText("${e.house}", x, houseTopY, housePnt)
            glyphPnt.color = e.elemColor
            canvas.drawText(e.planet.glyph, x, transitY, glyphPnt)
            val dot = mk { color = e.elemColor; style = Paint.Style.FILL }
            canvas.drawCircle(x, lineY_t, 3.5f, dot)
        }

        // ── Natal planets — BELOW bottom line ─────────────────────────────────
        // House row border lines + "дом" label (natal side)
        val hRowTop_n = houseNatY - fsHouse * 1.0f
        val hRowBot_n = houseNatY + fsHouse * 0.4f
        canvas.drawLine(0f, hRowTop_n, w, hRowTop_n, rowBorderPnt)
        canvas.drawLine(0f, hRowBot_n, w, hRowBot_n, rowBorderPnt)
        canvas.drawText(context.getString(R.string.house_row_label), 4f, houseNatY, rowLblPnt)

        natalHitBoxes.clear()
        glyphPnt.textSize = fs
        for (e in natalEntries) {
            val x = xForDeg(e.displayDeg, w)
            glyphPnt.color = e.elemColor
            canvas.drawText(e.planet.glyph, x, natalY, glyphPnt)
            housePnt.color = Color.parseColor("#555555")
            canvas.drawText("${e.house}", x, houseNatY, housePnt)
            signPnt.color = elemColor(ZodiacSign.fromId(e.signId).element)
            canvas.drawText(ZodiacSign.fromId(e.signId).symbol, x, signNatY, signPnt)
            // Dot on the bottom line at planet position
            val dot = mk { color = e.elemColor; style = Paint.Style.FILL }
            canvas.drawCircle(x, lineY_n, 3.5f, dot)
            // Hit box
            natalHitBoxes += android.graphics.RectF(x - fs*0.8f, natalY - fs, x + fs*0.8f, natalY + fs*0.4f) to e.planet
        }
    }

        // ── Touch ─────────────────────────────────────────────────────────────────
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val tx = event.x; val ty = event.y
            for ((rect, planet) in natalHitBoxes) {
                if (rect.contains(tx, ty)) {
                    onNatalPlanetClick?.invoke(planet)
                    return true
                }
            }
        }
        return true
    }
}
