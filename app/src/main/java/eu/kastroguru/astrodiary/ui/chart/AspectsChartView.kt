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
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.Element
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlin.math.abs
import kotlin.math.min

/**
 * Transit visualization as a linear "aspectarian grid":
 *
 *   [sign badge]          ← top-most, per transit planet (ZodiacGlyphs, never the emoji)
 *   [house number]       ← inside a small house outline, so it never reads as a degree
 *   ──── TRANSIT PLANETS ─ 0°────────────15°────────────30° ────
 *                                aspect lines
 *   ──── NATAL PLANETS ─── 0°────────────15°────────────30° ────
 *   [house number]
 *   [sign glyph]          ← bottom-most, per natal planet
 *
 * Every planet has a coloured tick on the axis at its exact position within its sign (0–30°), and the
 * aspect lines start from that tick. Crowded labels are spread sideways to stay legible, and a thin
 * connector runs from each label to its own tick, so the label never has to sit on its degree.
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
    private fun elemColor(e: Element) = ZodiacGlyphs.elementColor(e)

    // ── Computed layout data ──────────────────────────────────────────────────
    private data class PlanetEntry(
        val planet: Planet,
        val deg: Int,        // degree within sign (0–29) → x position
        val minutes: Int,
        val signId: Int,
        val house: Int,
        val absoluteDeg: Double,
        val elemColor: Int,
        var shown: Double = 0.0  // where the label is drawn (0–30); differs from pos only when crowded
    ) {
        /** Exact position within the sign (0–30) — where the tick and the aspect lines sit. */
        val pos: Double get() = absoluteDeg.mod(30.0)
    }

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
    private val leadPnt   = mk { style = Paint.Style.STROKE; strokeWidth = 1.6f; strokeCap = Paint.Cap.ROUND }
    private val tickPnt   = mk { style = Paint.Style.STROKE; strokeWidth = 3.5f }
    private val dotPnt    = mk { style = Paint.Style.FILL }
    private val houseLinePnt = mk { style = Paint.Style.STROKE; strokeWidth = 1.5f; color = Color.parseColor("#8C8AA0") }
    private val houseFillPnt = mk { style = Paint.Style.FILL; color = Color.parseColor("#FAF9FE") }
    private val housePath    = Path()
    private val captionPnt   = mk { color = Color.parseColor("#555555"); isFakeBoldText = true; textAlign = Paint.Align.LEFT }

    /** Call this whenever settings change (e.g. on fragment resume). */
    fun refreshSettings() { rebuild() }

    // ── Build internal data ───────────────────────────────────────────────────
    private fun rebuild() {
        val transit = transitData ?: return
        if (natalCusps.size != 12) return

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
                    // The natal house the transit is moving through. `pos.house` is the house in a
                    // chart cast for the transit moment, whose houses turn through all twelve every
                    // day: shown here it put neighbouring planets one house off, as if swapped.
                    house       = AstroCalculator.planetHouse(pos.absoluteDegree, natalCusps),
                    absoluteDeg = pos.absoluteDegree,
                    elemColor   = elemColor(sign.element)
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
            val house  = AstroCalculator.planetHouse(absD, natalCusps)
            val sign   = ZodiacSign.fromId(signId)
            PlanetEntry(
                planet      = planet,
                deg         = deg,
                minutes     = mins,
                signId      = signId,
                house       = house,
                absoluteDeg = absD,
                elemColor   = elemColor(sign.element)
            )
        }

        // Spread crowded labels; ticks and aspect lines stay on the exact degree
        spreadLabels(transitEntries)
        spreadLabels(natalEntries)

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

    /**
     * Sets where each label is drawn. A label is the column of planet glyph, house number and sign
     * glyph, so its width is the widest of the three. Every size in this view is a fraction of the
     * view's width, so a label's width in degrees is the same at any width — it is measured once at
     * a nominal one.
     */
    private fun spreadLabels(entries: List<PlanetEntry>) {
        val w = 1000f
        val pxPerDeg = w * (1f - H_MARGIN_LEFT - H_MARGIN_RIGHT) / 30f
        val m = Paint(Paint.ANTI_ALIAS_FLAG)
        fun width(text: String, scale: Float) = m.apply { textSize = w * scale }.measureText(text)
        val half = entries.map { e ->
            val px = maxOf(
                width(e.planet.glyph, FONT_SCALE),
                w * HOUSE_BOX,
                w * SIGN_SCALE * BADGE_R * 2
            ) + w * FONT_SCALE * LABEL_PAD
            px / 2.0 / pxPerDeg
        }
        val shown = spreadAround(entries.map { it.pos }, half, 0.0, 30.0)
        entries.forEachIndexed { i, e -> e.shown = shown[i] }
    }

    // ── Measure ───────────────────────────────────────────────────────────────
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val w = MeasureSpec.getSize(widthSpec).takeIf { it > 0 } ?: 800
        val h = computeHeight(w.toFloat()).toInt()
        setMeasuredDimension(w, h)
    }

    private fun computeHeight(w: Float): Float {
        val fs = w * FONT_SCALE; val fsSign = w * SIGN_SCALE; val houseH = w * HOUSE_BOX * 1.5f
        val gap = w * GAP_SCALE
        val lead = w * (LEAD_SCALE + TICK_SCALE)
        return PADDING_TOP * w +
                fsSign + gap + houseH + gap + fs + fs * GLYPH_BELOW + lead +   // transit block + connectors
                w * INNER_HEIGHT_RATIO +                                       // interior / aspect lines
                lead + fs * GLYPH_ABOVE + gap + fs * GLYPH_DESCENT + houseH +  // connectors + natal block
                gap + fsSign +
                PADDING_BOT * w
    }

    // ── Layout helpers ────────────────────────────────────────────────────────
    private val FONT_SCALE       = 0.048f   // glyph font = w * this
    private val SIGN_SCALE       = 0.038f
    private val HOUSE_SCALE      = 0.034f
    private val BADGE_R          = 0.55f    // sign badge radius, × SIGN_SCALE font
    private val HOUSE_BOX        = 0.044f   // side of the house outline's square; the roof adds half of it
    private val HOUSE_NUM        = 0.85f    // house number font, × HOUSE_SCALE — "12" has to fit the square
    private val ASP_SYM_SCALE    = 0.030f
    private val PADDING_TOP      = 0.04f    // fraction of width
    private val PADDING_BOT      = 0.04f
    private val H_MARGIN_LEFT    = 0.12f    // left margin — wider to fit orb scale labels
    private val H_MARGIN_RIGHT   = 0.04f    // right margin — just a small gutter
    private val INNER_HEIGHT_RATIO= 0.55f   // aspect area / width
    private val GAP_SCALE        = 0.012f   // gap between rows of one label
    private val LABEL_PAD        = 0.10f    // horizontal clearance between labels, × glyph font
    private val TICK_SCALE       = 0.028f   // planet tick on the axis — longer than the 20px ruler ticks
    private val LEAD_SCALE       = 0.036f   // height of the slanted connector from label to tick
    private val GLYPH_BELOW      = 0.25f    // transit glyph: baseline → where its connector starts, × font
    private val GLYPH_ABOVE      = 0.95f    // natal glyph: where its connector ends → baseline, × font
    private val GLYPH_DESCENT    = 0.15f    // natal glyph: baseline → the roof of the house below it, × font

    private fun xForDeg(deg: Double, w: Float): Float {
        val lm = w * H_MARGIN_LEFT
        val rm = w * H_MARGIN_RIGHT
        val available = w - lm - rm
        return (lm + (deg / 30.0) * available).toFloat()
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
        val gap     = w * GAP_SCALE
        val padTop  = PADDING_TOP * w
        val lm      = w * H_MARGIN_LEFT
        val rm      = w * H_MARGIN_RIGHT
        val tickH   = w * TICK_SCALE
        val lead    = w * (LEAD_SCALE + TICK_SCALE)

        // Transit block ABOVE top line, natal block BELOW bottom line; connectors in between
        val box       = w * HOUSE_BOX
        val houseH    = box * 1.5f                    // square + roof
        val signTopY  = padTop + fsSign
        val houseBotT = signTopY + gap + houseH       // bottom edge of the transit house outlines
        val transitY  = houseBotT + gap + fs          // transit planet glyph baseline
        val leadT     = transitY + fs * GLYPH_BELOW   // transit connectors start here
        val lineY_t   = leadT + lead                  // ← TOP LINE

        val innerH    = w * INNER_HEIGHT_RATIO
        val lineY_n   = lineY_t + innerH              // ← BOTTOM LINE

        val leadN     = lineY_n + lead                // natal connectors end here
        val natalY    = leadN + fs * GLYPH_ABOVE      // natal planet glyph baseline
        val houseBotN = natalY + gap + fs * GLYPH_DESCENT + houseH  // bottom edge of the natal house outlines
        val signNatY  = houseBotN + gap + fsSign

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
            val x1 = xForDeg(t.pos, w)
            val x2 = xForDeg(n.pos, w)
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
        glyphPnt.textSize = fs; signPnt.textSize = fsSign; housePnt.textSize = fsHouse * HOUSE_NUM
        // House row border lines + "дом" label, level with the numbers (transit side)
        val rowBorderPnt = mk { style = Paint.Style.STROKE; strokeWidth = 0.9f; color = Color.parseColor("#BBBBBB") }
        val rowLblPnt    = mk { textSize = fsHouse * 0.8f; color = Color.parseColor("#777777"); textAlign = Paint.Align.LEFT }
        val houseLabel   = context.getString(R.string.house_row_label)
        fun houseRow(bottom: Float) {
            val top = bottom - houseH - gap * 0.4f
            canvas.drawLine(0f, top, w, top, rowBorderPnt)
            canvas.drawLine(0f, bottom + gap * 0.4f, w, bottom + gap * 0.4f, rowBorderPnt)
            canvas.drawText(houseLabel, 4f, bottom - box / 2 + rowLblPnt.textSize * 0.36f, rowLblPnt)
        }
        houseRow(houseBotT)

        for (e in transitEntries) {
            val x = xForDeg(e.shown, w)
            ZodiacGlyphs.drawBadge(canvas, ZodiacSign.fromId(e.signId), x, signTopY - fsSign * 0.38f, fsSign * BADGE_R)
            drawHouse(canvas, x, houseBotT, box, e.house)
            glyphPnt.color = e.elemColor
            canvas.drawText(e.planet.glyph, x, transitY, glyphPnt)
            drawLead(canvas, e.elemColor, x, leadT, xForDeg(e.pos, w), lineY_t - tickH, lineY_t)
        }

        // ── Natal planets — BELOW bottom line ─────────────────────────────────
        // House row border lines + "дом" label (natal side)
        houseRow(houseBotN)

        // "Транзит" / "Натал" in the left margin beside each connector band. Connectors never reach
        // the margin (labels are kept within 0–30°), and the orb scale's 2,0 / 0,0 sit on the axis
        // lines, so the band keeps clear of both.
        val clear = fsHouse * 0.6f
        drawRowCaption(canvas, context.getString(R.string.strip_label_transit), (leadT + lineY_t - clear) / 2, lm, fsHouse)
        drawRowCaption(canvas, context.getString(R.string.strip_label_natal), (lineY_n + clear + leadN) / 2, lm, fsHouse)

        natalHitBoxes.clear()
        glyphPnt.textSize = fs
        for (e in natalEntries) {
            val x = xForDeg(e.shown, w)
            glyphPnt.color = e.elemColor
            canvas.drawText(e.planet.glyph, x, natalY, glyphPnt)
            drawHouse(canvas, x, houseBotN, box, e.house)
            ZodiacGlyphs.drawBadge(canvas, ZodiacSign.fromId(e.signId), x, signNatY - fsSign * 0.38f, fsSign * BADGE_R)
            drawLead(canvas, e.elemColor, x, leadN, xForDeg(e.pos, w), lineY_n + tickH, lineY_n)
            // Hit box
            natalHitBoxes += android.graphics.RectF(x - fs*0.8f, natalY - fs, x + fs*0.8f, natalY + fs*0.4f) to e.planet
        }
    }

    /** Row caption centred on [yMid], shrunk if it would not fit the left margin [lm]. */
    private fun drawRowCaption(canvas: Canvas, text: String, yMid: Float, lm: Float, fsHouse: Float) {
        captionPnt.textSize = fsHouse * 0.85f
        val room = lm - 10f
        val width = captionPnt.measureText(text)
        if (width > room) captionPnt.textSize *= room / width
        canvas.drawText(text, 4f, yMid + captionPnt.textSize * 0.36f, captionPnt)
    }

    /**
     * The house number inside a small house outline: a square of side [box] whose bottom edge is at
     * [bottom], with a roof on the same base, half as high. Plain numbers here read as degrees.
     */
    private fun drawHouse(canvas: Canvas, x: Float, bottom: Float, box: Float, house: Int) {
        val half = box / 2
        housePath.reset()
        housePath.moveTo(x - half, bottom)
        housePath.lineTo(x - half, bottom - box)
        housePath.lineTo(x, bottom - box - half)
        housePath.lineTo(x + half, bottom - box)
        housePath.lineTo(x + half, bottom)
        housePath.close()
        canvas.drawPath(housePath, houseFillPnt)
        canvas.drawPath(housePath, houseLinePnt)
        canvas.drawText("$house", x, bottom - half + housePnt.textSize * 0.36f, housePnt)
    }

    /**
     * Joins a label at [x] to its exact degree [xe]: a thin connector from the label edge [yLabel]
     * to the tick end [yTick], then the tick itself down (or up) to the axis at [yAxis].
     */
    private fun drawLead(canvas: Canvas, color: Int, x: Float, yLabel: Float, xe: Float, yTick: Float, yAxis: Float) {
        leadPnt.color = color; canvas.drawLine(x, yLabel, xe, yTick, leadPnt)
        tickPnt.color = color; canvas.drawLine(xe, yTick, xe, yAxis, tickPnt)
        dotPnt.color = color;  canvas.drawCircle(xe, yAxis, 3.5f, dotPnt)
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

/**
 * Spreads labels along a line so that neighbours do not overlap, moving each as little as possible.
 * [pos] are the true centres and [half] the half-widths, in one unit; centres stay within [lo]..[hi].
 * Returns the drawn centre of each label in input order. The order along the line never changes.
 *
 * A crowded run is laid shoulder to shoulder and centred on its members' true positions; runs that
 * then touch are merged and centred again. Pushing a run one way only — what this replaced — shoved
 * the last label of a five-planet pile-up more than 4° off; centring halves that and leaves a label
 * with room around it exactly where it is.
 */
internal fun spreadAround(pos: List<Double>, half: List<Double>, lo: Double, hi: Double): DoubleArray {
    class Run(val members: MutableList<Int>) {
        var start = 0.0   // drawn centre of the first member
        val offsets: DoubleArray get() {
            val o = DoubleArray(members.size)
            for (k in 1 until members.size) o[k] = o[k - 1] + half[members[k - 1]] + half[members[k]]
            return o
        }
        fun place() {
            val o = offsets
            val centred = members.indices.sumOf { pos[members[it]] - o[it] } / members.size
            start = centred.coerceAtMost(hi - o.last()).coerceAtLeast(lo)
        }
        val left get() = start - half[members.first()]
        val right get() = start + offsets.last() + half[members.last()]
    }

    val runs = ArrayList<Run>()
    for (i in pos.indices.sortedBy { pos[it] }) {
        runs += Run(mutableListOf(i)).apply { place() }
        while (runs.size >= 2 && runs[runs.size - 2].right > runs.last().left + 1e-9) {
            val absorbed = runs.removeAt(runs.size - 1)
            runs.last().members += absorbed.members
            runs.last().place()
        }
    }
    val shown = DoubleArray(pos.size)
    for (r in runs) r.offsets.forEachIndexed { k, o -> shown[r.members[k]] = r.start + o }
    return shown
}
