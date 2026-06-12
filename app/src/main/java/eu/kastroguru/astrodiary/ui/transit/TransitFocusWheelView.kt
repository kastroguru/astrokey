package eu.kastroguru.astrodiary.ui.transit

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import eu.kastroguru.astrodiary.domain.model.Element
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlin.math.*

/**
 * Single-ring focused chart. Same layout as AstroChartView but showing two sets
 * of planets at their ecliptic positions on one shared ring:
 *
 *   Dark gold  (#C09500) — natal planets involved in the selected aspect
 *   Dark blue  (#1144CC) — transit planets involved in the selected aspect
 *
 * If the same planet appears in both sets (e.g. natal Mars + transit Mars),
 * it is drawn twice at its respective natal / transit position.
 *
 * Aspect lines:
 *   Gold  — focused natal planet ↔ natal planets it aspects (natal orbs)
 *   Blue  — focused transit planet ↔ transit planets it aspects (2° orb)
 *   Black — the cross-aspect between the two focused planets
 */
class TransitFocusWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    // ── Input ─────────────────────────────────────────────────────────────────
    var focusNatalKey:   String = ""
    var focusTransitKey: String = ""
    var natalAbs:   Map<String, Double> = emptyMap(); set(v) { field = v; invalidate() }
    var transitAbs: Map<String, Double> = emptyMap(); set(v) { field = v; invalidate() }
    var natalCusps: List<Double> = emptyList();        set(v) { field = v; invalidate() }
    var natalRulerKey:   String = ""
    var transitRulerKey: String = ""

    // ── Zoom / pan (identical to AstroChartView) ─────────────────────────────
    private var scale = 1f; private var tx = 0f; private var ty = 0f
    private var dragEnabled = false
    private var lx = 0f; private var ly = 0f; private var pid = MotionEvent.INVALID_POINTER_ID

    private val scaleD = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(d: ScaleGestureDetector): Boolean { dragEnabled = true; return true }
        override fun onScale(d: ScaleGestureDetector): Boolean { scale = (scale * d.scaleFactor).coerceIn(0.5f, 5f); invalidate(); return true }
    })
    private val gestD = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean { scale = 1f; tx = 0f; ty = 0f; dragEnabled = false; invalidate(); return true }
    })

    override fun onDetachedFromWindow() { super.onDetachedFromWindow(); scale = 1f; tx = 0f; ty = 0f; dragEnabled = false }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        scaleD.onTouchEvent(e); gestD.onTouchEvent(e)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN          -> { lx = e.x; ly = e.y; pid = e.getPointerId(0) }
            MotionEvent.ACTION_POINTER_DOWN  -> parent?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {
                if (e.pointerCount > 1) parent?.requestDisallowInterceptTouchEvent(true)
                else if (dragEnabled)  parent?.requestDisallowInterceptTouchEvent(true)
                if (!scaleD.isInProgress && dragEnabled) {
                    val i = e.findPointerIndex(pid)
                    if (i >= 0) { tx += e.getX(i) - lx; ty += e.getY(i) - ly; lx = e.getX(i); ly = e.getY(i); invalidate() }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> pid = MotionEvent.INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val i = e.actionIndex; if (e.getPointerId(i) == pid) {
                    val ni = if (i == 0) 1 else 0; lx = e.getX(ni); ly = e.getY(ni); pid = e.getPointerId(ni)
                }
            }
        }
        return true
    }

    // ── Ring radii (identical to AstroChartView) ──────────────────────────────
    private val R_SIGN_OUT = 0.98f
    private val R_SIGN_IN  = 0.88f
    private val R_PLANET   = 0.75f
    private val R_DEG      = 0.67f
    private val R_HOUSE    = 0.53f
    private val R_HNUM     = 0.505f
    private val R_ASPECT   = 0.48f

    // ── Colours ───────────────────────────────────────────────────────────────
    private val C_BG         = Color.WHITE
    private val C_SIGN_LINE  = Color.parseColor("#888888")
    private val C_AXIS       = Color.parseColor("#222222")
    private val C_HOUSE_LINE = Color.parseColor("#777777")
    private val C_HOUSE_NUM  = Color.parseColor("#444444")
    private val C_CIRCLE     = Color.parseColor("#999999")
    private val C_CONN       = Color.parseColor("#888888")
    private val C_NATAL      = Color.parseColor("#C09500")   // dark gold — all natal planets
    private val C_TRANSIT    = Color.parseColor("#1144CC")   // dark blue — all transit planets
    private val C_CROSS      = Color.parseColor("#1A1A1A")   // cross-aspect line

    // ── Aspect orbs ───────────────────────────────────────────────────────────
    private val NATAL_ASPECTS = listOf(0 to 8.0, 60 to 6.0, 90 to 7.0, 120 to 8.0, 150 to 5.0, 180 to 8.0)
    private val TRANSIT_ORB   = 2.0

    private fun mk(b: Paint.() -> Unit) = Paint(Paint.ANTI_ALIAS_FLAG).apply(b)

    private fun elemColorForSign(signId: Int): Int {
        return when (ZodiacSign.fromId(signId.coerceIn(1, 12)).element) {
            Element.FIRE  -> Color.parseColor("#CC3300")
            Element.EARTH -> Color.parseColor("#2A1506")
            Element.WATER -> Color.parseColor("#1144CC")
            Element.AIR   -> Color.parseColor("#C09500")
        }
    }

    // ── Geometry helpers (exact copy from AstroChartView) ─────────────────────
    private fun chartAngle(ecliptic: Double, asc: Double): Double {
        var v = (180.0 - (ecliptic - asc)) % 360.0; if (v < 0) v += 360.0; return v
    }
    private fun pt(cx: Float, cy: Float, r: Float, deg: Double): Pair<Float, Float> {
        val a = Math.toRadians(deg); return cx + r * cos(a).toFloat() to cy + r * sin(a).toFloat()
    }
    private fun spoke(canvas: Canvas, cx: Float, cy: Float, r1: Float, r2: Float, deg: Double, pnt: Paint) {
        val a = Math.toRadians(deg)
        canvas.drawLine(cx+r1*cos(a).toFloat(), cy+r1*sin(a).toFloat(),
                        cx+r2*cos(a).toFloat(), cy+r2*sin(a).toFloat(), pnt)
    }
    private fun angleDiff(a: Double, b: Double): Double { val r = abs(a-b)%360.0; return if(r>180) 360-r else r }
    private fun angDist(a: Double, b: Double): Double { val d=((b-a+360.0)%360.0); return if(d>180.0) 360.0-d else d }
    private fun midBetween(a: Double, b: Double) = (a + ((b-a+360.0)%360.0)/2.0 + 360.0) % 360.0

    override fun onMeasure(w: Int, h: Int) = super.onMeasure(w, w)

    // ── Read aspect-body exclusions from settings (same prefs as every other chart) ──
    private fun buildExcluded(): Set<String> {
        val prefs = context.getSharedPreferences("aspect_settings", android.content.Context.MODE_PRIVATE)
        return buildSet {
            if (!prefs.getBoolean("chiron", true)) add("chiron")
            if (!prefs.getBoolean("lilith", true)) add("lilith")
            if (!prefs.getBoolean("rahu",   true)) add("rahu")
        }
    }
    private fun buildPersonalExcluded(): Set<String> {
        val prefs = context.getSharedPreferences("aspect_settings", android.content.Context.MODE_PRIVATE)
        return if (prefs.getBoolean("hide_personal_transits", false))
            setOf("sun","moon","mercury","venus","mars") else emptySet()
    }

    // ── Relevant planet sets ──────────────────────────────────────────────────
    private fun relevantNatalKeys(): Set<String> {
        val excluded = buildExcluded()
        val focusDeg = natalAbs[focusNatalKey] ?: return setOf(focusNatalKey)
        val result = mutableSetOf(focusNatalKey)
        if (natalRulerKey.isNotBlank() && natalRulerKey !in excluded) result += natalRulerKey
        for ((key, deg) in natalAbs) {
            if (key == focusNatalKey || key in excluded) continue
            val diff = angleDiff(focusDeg, deg)
            for ((aspDeg, orb) in NATAL_ASPECTS)
                if (min(abs(diff - aspDeg), abs(360.0 - diff - aspDeg)) <= orb) { result += key; break }
        }
        return result
    }

    private fun relevantTransitKeys(): Set<String> {
        val excluded = buildExcluded()
        val focusDeg = transitAbs[focusTransitKey] ?: return setOf(focusTransitKey)
        val result = mutableSetOf(focusTransitKey)
        if (transitRulerKey.isNotBlank() && transitRulerKey !in excluded) result += transitRulerKey
        for ((key, deg) in transitAbs) {
            if (key == focusTransitKey || key in excluded) continue
            val diff = angleDiff(focusDeg, deg)
            for ((aspDeg, _) in NATAL_ASPECTS)
                if (min(abs(diff - aspDeg), abs(360.0 - diff - aspDeg)) <= TRANSIT_ORB) { result += key; break }
        }
        return result
    }

    // ── onDraw ────────────────────────────────────────────────────────────────
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (natalAbs.isEmpty()) return
        val cx = width / 2f; val cy = height / 2f
        val R  = min(cx, cy) * 0.95f
        val asc = natalCusps.getOrElse(0) { 0.0 }

        canvas.save()
        canvas.translate(tx, ty)
        canvas.scale(scale, scale, cx, cy)

        canvas.drawCircle(cx, cy, R * R_SIGN_OUT, mk { color = C_BG; style = Paint.Style.FILL })
        drawZodiacRing(canvas, cx, cy, R, asc)
        drawHouseDivisions(canvas, cx, cy, R, asc)

        // Dashed secondary aspects drawn first (behind primary lines)
        drawSecondaryNatalAspects(canvas, cx, cy, R, asc)
        drawSecondaryTransitAspects(canvas, cx, cy, R, asc)
        // Primary aspect lines with symbol badges
        drawNatalAspects(canvas, cx, cy, R, asc)
        drawTransitAspects(canvas, cx, cy, R, asc)
        drawCrossAspect(canvas, cx, cy, R, asc)

        // Planets drawn on top
        drawPlanets(canvas, cx, cy, R, asc)
        drawRulerCircles(canvas, cx, cy, R, asc)

        // Circle borders (identical to AstroChartView)
        val cp = mk { style = Paint.Style.STROKE; color = C_CIRCLE }
        cp.strokeWidth = 2.0f; canvas.drawCircle(cx, cy, R * R_SIGN_OUT, cp)
        cp.strokeWidth = 1.5f
        canvas.drawCircle(cx, cy, R * R_SIGN_IN,  cp)
        canvas.drawCircle(cx, cy, R * R_HOUSE,    cp)
        canvas.drawCircle(cx, cy, R * R_ASPECT,   cp)

        canvas.restore()
    }

    // ── Zodiac ring (annular Path sectors + custom-path glyphs) ──────────────
    private fun drawZodiacRing(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val outerOval = RectF(cx-R*R_SIGN_OUT, cy-R*R_SIGN_OUT, cx+R*R_SIGN_OUT, cy+R*R_SIGN_OUT)
        val innerOval = RectF(cx-R*R_SIGN_IN,  cy-R*R_SIGN_IN,  cx+R*R_SIGN_IN,  cy+R*R_SIGN_IN)
        val midR    = R * (R_SIGN_OUT + R_SIGN_IN) / 2f
        val glyphS  = R * (R_SIGN_OUT - R_SIGN_IN) * 0.50f
        val fill    = mk { style = Paint.Style.FILL }
        val linePnt = mk { style = Paint.Style.STROKE; strokeWidth = 1.5f; color = C_SIGN_LINE }
        for (i in 0 until 12) {
            val startA = chartAngle(i * 30.0, asc).toFloat()
            fill.color = elemColorForSign(i + 1)
            val path = Path()
            path.arcTo(outerOval, startA, -30f); path.arcTo(innerOval, startA - 30f, 30f); path.close()
            canvas.drawPath(path, fill)
            spoke(canvas, cx, cy, R*R_SIGN_OUT, R*R_SIGN_IN, chartAngle(i*30.0, asc), linePnt)
            val (gx, gy) = pt(cx, cy, midR, chartAngle(i*30.0 + 15.0, asc))
            canvas.save(); canvas.translate(gx, gy)
            drawSignGlyph(canvas, ZodiacSign.fromId(i + 1), glyphS)
            canvas.restore()
        }
    }

    // ── House divisions ───────────────────────────────────────────────────────
    private fun drawHouseDivisions(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        if (natalCusps.size < 12) return
        val axisPnt  = mk { style = Paint.Style.STROKE; strokeWidth = 2.0f; color = C_AXIS }
        val housePnt = mk { style = Paint.Style.STROKE; strokeWidth = 1.0f; color = C_HOUSE_LINE }
        val numPnt   = mk { textAlign = Paint.Align.CENTER; color = C_HOUSE_NUM; textSize = R * 0.044f }
        for (i in 0 until 12) {
            spoke(canvas, cx, cy, R*R_SIGN_IN, R*R_HOUSE, chartAngle(natalCusps[i], asc), if (i%3==0) axisPnt else housePnt)
            val mid = midBetween(natalCusps[i], natalCusps[(i+1)%12])
            val (hx, hy) = pt(cx, cy, R*R_HNUM, chartAngle(mid, asc))
            canvas.drawText("${i+1}", hx, hy + numPnt.textSize*0.36f, numPnt)
        }
    }

    // ── Aspect symbol at line midpoint ───────────────────────────────────────
    private fun aspSym(aspDeg: Int) = when(aspDeg){0->"☌";60->"⚹";90->"□";120->"△";150->"⚻";180->"☍";else->""}

    private fun drawBadge(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float,
                          aspDeg: Int, textColor: Int, R: Float, scale: Float = 1f) {
        val sym = aspSym(aspDeg); if (sym.isEmpty()) return
        val mx = (x1+x2)/2f; val my = (y1+y2)/2f; val fs = R * 0.072f * scale
        canvas.drawCircle(mx, my, fs*0.70f, mk { color=C_BG; style=Paint.Style.FILL })
        canvas.drawText(sym, mx, my+fs*0.36f, mk { setColor(textColor); textAlign=Paint.Align.CENTER; textSize=fs })
    }

    // ── Secondary dashed lines (between aspected planets that also aspect each other)
    private fun drawSecondaryNatalAspects(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val secondaries = relevantNatalKeys().filter { it != focusNatalKey }
        val ar = R * R_ASPECT
        val dash = DashPathEffect(floatArrayOf(8f, 5f), 0f)
        for (i in secondaries.indices) {
            for (j in i+1 until secondaries.size) {
                val degA = natalAbs[secondaries[i]] ?: continue
                val degB = natalAbs[secondaries[j]] ?: continue
                val diff = angleDiff(degA, degB)
                for ((aspDeg, orb) in NATAL_ASPECTS) {
                    val actualOrb = min(abs(diff - aspDeg), abs(360.0 - diff - aspDeg))
                    if (actualOrb <= orb) {
                        val frac  = (actualOrb/orb).toFloat()
                        val alpha = ((1f-frac)*110f+55f).toInt().coerceIn(55,165)
                        val (x1,y1) = pt(cx,cy,ar,chartAngle(degA,asc))
                        val (x2,y2) = pt(cx,cy,ar,chartAngle(degB,asc))
                        canvas.drawLine(x1,y1,x2,y2,
                            mk { setColor(C_NATAL);this.alpha=alpha;style=Paint.Style.STROKE;strokeWidth=1.5f;pathEffect=dash })
                        break
                    }
                }
            }
        }
    }

    private fun drawSecondaryTransitAspects(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val secondaries = relevantTransitKeys().filter { it != focusTransitKey }
        val ar = R * R_ASPECT
        val dash = DashPathEffect(floatArrayOf(8f, 5f), 0f)
        for (i in secondaries.indices) {
            for (j in i+1 until secondaries.size) {
                val degA = transitAbs[secondaries[i]] ?: continue
                val degB = transitAbs[secondaries[j]] ?: continue
                val diff = angleDiff(degA, degB)
                for ((aspDeg, _) in NATAL_ASPECTS) {
                    val actualOrb = min(abs(diff - aspDeg), abs(360.0 - diff - aspDeg))
                    if (actualOrb <= TRANSIT_ORB) {
                        val (x1,y1) = pt(cx,cy,ar,chartAngle(degA,asc))
                        val (x2,y2) = pt(cx,cy,ar,chartAngle(degB,asc))
                        canvas.drawLine(x1,y1,x2,y2,
                            mk { setColor(C_TRANSIT);alpha=100;style=Paint.Style.STROKE;strokeWidth=1.5f;pathEffect=dash })
                        break
                    }
                }
            }
        }
    }

    // ── Natal aspect lines (gold) ─────────────────────────────────────────────
    private fun drawNatalAspects(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val excluded = buildExcluded()
        val focusDeg = natalAbs[focusNatalKey] ?: return
        val ar = R * R_ASPECT
        for ((key, deg) in natalAbs) {
            if (key == focusNatalKey || key in excluded) continue
            val diff = angleDiff(focusDeg, deg)
            for ((aspDeg, orb) in NATAL_ASPECTS) {
                val actualOrb = min(abs(diff - aspDeg), abs(360.0 - diff - aspDeg))
                if (actualOrb <= orb) {
                    val frac  = (actualOrb / orb).toFloat()
                    val alpha = ((1f - frac) * 155f + 100f).toInt().coerceIn(100, 255)
                    val width = (2.2f * (1f + (1f - frac) * 1.5f)).coerceAtMost(4f)
                    val (x1,y1) = pt(cx, cy, ar, chartAngle(focusDeg, asc))
                    val (x2,y2) = pt(cx, cy, ar, chartAngle(deg, asc))
                    canvas.drawLine(x1, y1, x2, y2,
                        mk { color=C_NATAL; this.alpha=alpha; style=Paint.Style.STROKE; strokeWidth=width })
                    drawBadge(canvas, x1, y1, x2, y2, aspDeg, C_NATAL, R)
                    break
                }
            }
        }
    }

    // ── Transit aspect lines (blue) ───────────────────────────────────────────
    private fun drawTransitAspects(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val excluded = buildExcluded()
        val focusDeg = transitAbs[focusTransitKey] ?: return
        val ar = R * R_ASPECT
        for ((key, deg) in transitAbs) {
            if (key == focusTransitKey || key in excluded) continue
            val diff = angleDiff(focusDeg, deg)
            for ((aspDeg, _) in NATAL_ASPECTS) {
                val actualOrb = min(abs(diff - aspDeg), abs(360.0 - diff - aspDeg))
                if (actualOrb <= TRANSIT_ORB) {
                    val frac  = (actualOrb / TRANSIT_ORB).toFloat()
                    val alpha = ((1f - frac) * 155f + 100f).toInt().coerceIn(100, 255)
                    val width = (2.2f * (1f + (1f - frac) * 1.5f)).coerceAtMost(4f)
                    val (x1,y1) = pt(cx, cy, ar, chartAngle(focusDeg, asc))
                    val (x2,y2) = pt(cx, cy, ar, chartAngle(deg, asc))
                    canvas.drawLine(x1, y1, x2, y2,
                        mk { color=C_TRANSIT; this.alpha=alpha; style=Paint.Style.STROKE; strokeWidth=width })
                    drawBadge(canvas, x1, y1, x2, y2, aspDeg, C_TRANSIT, R)
                    break
                }
            }
        }
    }

    // ── Cross-aspect line (black) ─────────────────────────────────────────────
    private fun drawCrossAspect(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val tDeg = transitAbs[focusTransitKey] ?: return
        val nDeg = natalAbs[focusNatalKey]     ?: return
        val diff = angleDiff(tDeg, nDeg)
        val ar   = R * R_ASPECT
        for ((aspDeg, _) in NATAL_ASPECTS) {
            if (min(abs(diff - aspDeg), abs(360.0 - diff - aspDeg)) <= 2.0) {
                val (x1,y1) = pt(cx, cy, ar, chartAngle(tDeg, asc))
                val (x2,y2) = pt(cx, cy, ar, chartAngle(nDeg, asc))
                canvas.drawLine(x1, y1, x2, y2, mk { color=C_CROSS; style=Paint.Style.STROKE; strokeWidth=3.5f })
                drawBadge(canvas, x1, y1, x2, y2, aspDeg, C_CROSS, R, scale=1.25f)
                break
            }
        }
    }

    // ── All planets on single ring ────────────────────────────────────────────
    private fun drawPlanets(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val relNatal   = relevantNatalKeys()
        val relTransit = relevantTransitKeys()

        // Build combined entry list: (planet, absD, color, isFocus, isNatal)
        data class Entry(val planet: Planet, val absD: Double, val clr: Int, val isFocus: Boolean, val isNatal: Boolean)
        val entries = mutableListOf<Entry>()

        for (p in Planet.values()) {
            natalAbs[p.key]?.let   { if (p.key in relNatal)   entries += Entry(p, it, C_NATAL,   p.key == focusNatalKey,   true)  }
            transitAbs[p.key]?.let { if (p.key in relTransit) entries += Entry(p, it, C_TRANSIT, p.key == focusTransitKey, false) }
        }

        val angles   = entries.map { chartAngle(it.absD, asc) }
        val resolved = resolveCollisions(angles)
        val fsBase   = R * 0.082f; val fsLg = R * 0.097f
        val connPnt  = mk { color=C_CONN; style=Paint.Style.STROKE; strokeWidth=0.8f; alpha=130 }
        val degPnt   = mk { textAlign=Paint.Align.CENTER; textSize=R*0.038f }

        entries.forEachIndexed { i, e ->
            val dispAngle = resolved[i]; val trueAngle = angles[i]
            val fs = if (e.isFocus) fsLg else fsBase
            val (px, py) = pt(cx, cy, R * R_PLANET, dispAngle)

            // Connector tick to true zodiac position
            if (angDist(dispAngle, trueAngle) > 2.0) {
                val (hx, hy) = pt(cx, cy, R * (R_SIGN_IN - 0.04f), trueAngle)
                canvas.drawLine(px, py, hx, hy, connPnt)
            }
            // Focus halo
            if (e.isFocus) {
                canvas.drawCircle(px, py, fs*0.78f, mk { setColor(e.clr); alpha=40; style=Paint.Style.FILL })
            }
            // Glyph
            canvas.drawText(e.planet.glyph, px, py + fs*0.36f,
                mk { setColor(e.clr); textAlign=Paint.Align.CENTER; textSize=fs; isFakeBoldText=e.isFocus })

            // Degree label at R_DEG
            val (dx, dy) = pt(cx, cy, R * R_DEG, dispAngle)
            degPnt.setColor(e.clr)
            canvas.drawText("${(e.absD % 30.0).toInt()}°", dx, dy + degPnt.textSize*0.36f, degPnt)

            // Tick mark at true ecliptic position on inner zodiac ring
            spoke(canvas, cx, cy, R*R_SIGN_IN, R*(R_SIGN_IN - 0.04f), trueAngle,
                mk { setColor(e.clr); style=Paint.Style.STROKE; strokeWidth=1.2f; alpha=160 })
        }
    }

    // ── Ruler dashed circles ──────────────────────────────────────────────────
    private fun drawRulerCircles(canvas: Canvas, cx: Float, cy: Float, R: Float, asc: Double) {
        val dash = DashPathEffect(floatArrayOf(9f, 5f), 0f)
        val cr   = R * 0.054f
        // Natal ruler at its natal position (gold)
        natalAbs[natalRulerKey]?.let { deg ->
            val (rx, ry) = pt(cx, cy, R * R_PLANET, chartAngle(deg, asc))
            canvas.drawCircle(rx, ry, cr, mk { style=Paint.Style.STROKE; strokeWidth=2.4f; setColor(C_NATAL); pathEffect=dash })
        }
        // Transit ruler at its transit position (blue)
        transitAbs[transitRulerKey]?.let { deg ->
            val (rx, ry) = pt(cx, cy, R * R_PLANET, chartAngle(deg, asc))
            canvas.drawCircle(rx, ry, cr, mk { style=Paint.Style.STROKE; strokeWidth=2.4f; setColor(C_TRANSIT); pathEffect=dash })
        }
    }

    // ── Collision resolution (same algorithm as AstroChartView) ──────────────
    private fun resolveCollisions(angles: List<Double>, minSep: Double = 9.0): List<Double> {
        val n = angles.size; if (n <= 1) return angles
        val indexed = angles.mapIndexed { i, a -> i to a }.sortedBy { it.second }
        val disp = indexed.map { it.second }.toMutableList()
        repeat(50) {
            var i = 0
            while (i < n) {
                val cl = mutableListOf(i)
                while (cl.last() < n-1 && ((disp[cl.last()+1]-disp[cl.last()]+360.0)%360.0) < minSep)
                    cl.add(cl.last()+1)
                if (cl.size > 1) {
                    val cen = cl.map { disp[it] }.average(); val hs = minSep*(cl.size-1)/2.0
                    cl.forEachIndexed { idx, pidx -> disp[pidx] = (cen-hs+idx*minSep+360.0)%360.0 }
                }
                i += cl.size
            }
        }
        val result = DoubleArray(n); indexed.forEachIndexed { si, (oi, _) -> result[oi] = disp[si] }
        return result.toList()
    }

    // ── Custom-path sign glyphs (identical to AstroChartView) ─────────────────
    private fun drawSignGlyph(canvas: Canvas, sign: ZodiacSign, s: Float) {
        val sw = maxOf(3f, s * 0.17f)
        val p  = mk { color=Color.WHITE; style=Paint.Style.STROKE; strokeWidth=sw; strokeCap=Paint.Cap.ROUND; strokeJoin=Paint.Join.ROUND }
        val path = Path(); val oval = RectF()
        when (sign) {
            ZodiacSign.ARIES -> {
                canvas.drawLine(0f,0f,0f,s*0.45f,p)
                oval.set(-s*0.5f,-s*0.4f,0f,s*0.4f);  canvas.drawArc(oval,0f,-180f,false,p)
                oval.set(0f,-s*0.4f,s*0.5f,s*0.4f);   canvas.drawArc(oval,180f,180f,false,p)
            }
            ZodiacSign.TAURUS -> {
                canvas.drawCircle(0f,s*0.15f,s*0.4f,p)
                path.moveTo(-s*0.28f,-s*0.22f); path.quadTo(-s*0.55f,-s*0.15f,-s*0.45f,-s*0.5f)
                path.moveTo( s*0.28f,-s*0.22f); path.quadTo( s*0.55f,-s*0.15f, s*0.45f,-s*0.5f)
            }
            ZodiacSign.GEMINI -> {
                canvas.drawLine(-s*0.22f,-s*0.45f,-s*0.22f,s*0.45f,p); canvas.drawLine(s*0.22f,-s*0.45f,s*0.22f,s*0.45f,p)
                canvas.drawLine(-s*0.44f,-s*0.45f,s*0.44f,-s*0.45f,p); canvas.drawLine(-s*0.44f,s*0.45f,s*0.44f,s*0.45f,p)
            }
            ZodiacSign.CANCER -> {
                val cr=s*0.18f; val ar=cr*2f; val ucy=-s*0.26f; val lcy=s*0.26f
                canvas.drawCircle(-cr,ucy,cr,p); oval.set(-ar,ucy-ar,ar,ucy+ar); canvas.drawArc(oval,180f,180f,false,p)
                canvas.drawCircle(cr,lcy,cr,p);  oval.set(-ar,lcy-ar,ar,lcy+ar); canvas.drawArc(oval,180f,-180f,false,p)
            }
            ZodiacSign.LEO -> {
                canvas.drawCircle(-s*0.06f,s*0.22f,s*0.22f,p)
                path.moveTo(-s*0.06f+s*0.22f,s*0.22f); path.cubicTo(s*0.52f,-s*0.62f,s*0.66f,s*0.15f,s*0.44f,s*0.5f)
            }
            ZodiacSign.VIRGO -> {
                val yT=-s*0.28f; val yM=-s*0.7f; val x1=-s*0.42f; val x2=0f; val x3=s*0.42f
                canvas.drawLine(x1,yT,x1,s*0.12f,p)
                path.moveTo(x1,yT); path.cubicTo(x1,yM,x2,yM,x2,yT); path.lineTo(x2,s*0.12f)
                path.moveTo(x2,yT); path.cubicTo(x2,yM,x3,yM,x3,yT); path.lineTo(x3,s*0.4f)
                path.cubicTo(x3-s*0.12f,s*0.56f,x3+s*0.35f,s*0.56f,x3+s*0.35f,s*0.3f)
                path.cubicTo(x3+s*0.35f,s*0.1f,x3,s*0.1f,x3,s*0.12f)
            }
            ZodiacSign.LIBRA -> {
                path.moveTo(-s*0.42f,0f); path.cubicTo(-s*0.42f,-s*0.55f,s*0.42f,-s*0.55f,s*0.42f,0f)
                canvas.drawPath(path,p); path.reset()
                canvas.drawLine(-s*0.62f,0f,s*0.62f,0f,p); canvas.drawLine(-s*0.58f,s*0.32f,s*0.58f,s*0.32f,p)
            }
            ZodiacSign.SCORPIO -> {
                val yT=-s*0.28f; val yM=-s*0.7f; val yB=s*0.1f; val x1=-s*0.42f; val x2=0f; val x3=s*0.42f
                canvas.drawLine(x1,yT,x1,yB,p)
                path.moveTo(x1,yT); path.cubicTo(x1,yM,x2,yM,x2,yT); path.lineTo(x2,yB)
                path.moveTo(x2,yT); path.cubicTo(x2,yM,x3,yM,x3,yT); path.lineTo(x3,s*0.35f)
                val ae=x3+s*0.42f; path.lineTo(ae,s*0.35f)
                path.moveTo(ae-s*0.2f,s*0.18f); path.lineTo(ae,s*0.35f); path.lineTo(ae-s*0.2f,s*0.52f)
            }
            ZodiacSign.SAGITTARIUS -> {
                canvas.drawLine(-s*0.38f,s*0.38f,s*0.42f,-s*0.42f,p)
                canvas.drawLine(s*0.42f,-s*0.42f,s*0.15f,-s*0.42f,p); canvas.drawLine(s*0.42f,-s*0.42f,s*0.42f,-s*0.15f,p)
                canvas.drawLine(-s*0.18f,-s*0.18f,s*0.18f,s*0.18f,p)
            }
            ZodiacSign.CAPRICORN -> {
                val yT=-s*0.28f; val yM=-s*0.7f; val x1=-s*0.42f; val x2=0f; val x3=s*0.42f
                path.moveTo(x1,yT); path.cubicTo(x1,yM,x2,yM,x2,yT); path.lineTo(x2,s*0.12f)
                path.moveTo(x2,yT); path.cubicTo(x2,yM,x3,yM,x3,yT); path.lineTo(x3,s*0.4f)
                path.cubicTo(x3-s*0.12f,s*0.56f,x3+s*0.35f,s*0.56f,x3+s*0.35f,s*0.3f)
                path.cubicTo(x3+s*0.35f,s*0.1f,x3,s*0.1f,x3,s*0.12f)
            }
            ZodiacSign.AQUARIUS -> {
                for (y in listOf(-s*0.18f, s*0.18f)) {
                    path.moveTo(-s*0.5f,y); path.quadTo(-s*0.25f,y-s*0.24f,0f,y); path.quadTo(s*0.25f,y+s*0.24f,s*0.5f,y)
                }
            }
            ZodiacSign.PISCES -> {
                val r2=s*0.38f; val xO=s*0.48f
                oval.set(-xO-r2,-r2,-xO+r2,r2); canvas.drawArc(oval,270f,180f,false,p)
                oval.set(xO-r2,-r2,xO+r2,r2);   canvas.drawArc(oval,270f,-180f,false,p)
                canvas.drawLine(-s*0.58f,0f,s*0.58f,0f,p)
            }
        }
        canvas.drawPath(path, p)
    }
}
