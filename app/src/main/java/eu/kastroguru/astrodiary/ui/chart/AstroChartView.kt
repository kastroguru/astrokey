package eu.kastroguru.astrodiary.ui.chart

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.ChartUtil
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlin.math.*

class AstroChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var astroData: AstroData? = null
        set(value) { field = value; invalidate() }

    // ── Ring radii ────────────────────────────────────────────────────────────
    private val R_SIGN_OUT = 0.98f   // outer zodiac ring edge
    private val R_SIGN_IN  = 0.88f   // inner zodiac ring edge — wider band = more room for glyphs
    private val R_PLANET   = 0.75f   // planet glyph ring
    private val R_DEG      = 0.67f   // dedicated degree-number ring (between planets and house nums)
    private val R_HOUSE    = 0.53f   // outer boundary of house number band (reduced toward R_ASPECT)
    private val R_HNUM     = 0.505f  // house numbers — midpoint between R_HOUSE (0.53) and R_ASPECT (0.48)
    private val R_ASPECT   = 0.48f   // aspect lines drawn at this radius

    // ── Aspect definitions ────────────────────────────────────────────────────
    private data class AspectDef(val angle: Int, val orb: Float, val color: Int, val width: Float, val symbol: String)
    private val ASPECTS = listOf(
        AspectDef(  0, 8f, Color.parseColor("#1A1ABB"), 2.2f, "☌"),   // conjunction – deep blue
        AspectDef( 60, 6f, Color.parseColor("#0A6644"), 1.8f, "⚹"),   // sextile – dark teal
        AspectDef( 90, 8f, Color.parseColor("#CC0000"), 2.2f, "□"),   // square – dark red
        AspectDef(120, 8f, Color.parseColor("#0055BB"), 2.2f, "△"),   // trine – strong blue
        AspectDef(150, 5f, Color.parseColor("#886600"), 1.5f, "⚻"),   // quincunx – dark amber
        AspectDef(180, 8f, Color.parseColor("#111111"), 2.2f, "☍")    // opposition – black
    )

    // ── Colours ───────────────────────────────────────────────────────────────
    private val C_BG        = Color.WHITE
    private val C_SIGN_BAND = Color.parseColor("#EEEDF4")
    private val C_SIGN_TXT  = Color.parseColor("#1A1A3A")
    private val C_SIGN_LINE = Color.parseColor("#888888")
    private val C_AXIS      = Color.parseColor("#222222")
    private val C_HOUSE     = Color.parseColor("#777777")
    private val C_HOUSE_NUM = Color.parseColor("#444444")
    private val C_PLANET    = Color.parseColor("#1A1A3A")
    private val C_DEG       = Color.parseColor("#888888")
    private val C_TICK      = Color.parseColor("#999999")
    private val C_CIRCLE    = Color.parseColor("#999999")

    private fun mk(b: Paint.() -> Unit) = Paint(Paint.ANTI_ALIAS_FLAG).apply(b)
    private val bgPaint     = mk { color = C_BG;        style = Paint.Style.FILL }
    private val bandFill    = mk { color = C_SIGN_BAND; style = Paint.Style.FILL }
    private val bandMask    = mk { color = C_BG;        style = Paint.Style.FILL }
    private val signLinePnt = mk { color = C_SIGN_LINE; style = Paint.Style.STROKE; strokeWidth = 1.5f }
    private val signTxtPnt  = mk { color = C_SIGN_TXT;  textAlign = Paint.Align.CENTER }
    private val axisPnt     = mk { color = C_AXIS;      style = Paint.Style.STROKE; strokeWidth = 4.5f }
    private val housePnt    = mk { color = C_HOUSE;     style = Paint.Style.STROKE; strokeWidth = 3f }
    private val houseNumPnt = mk { color = C_HOUSE_NUM; textAlign = Paint.Align.CENTER }
    private val planetPnt      = mk { color = C_PLANET;    textAlign = Paint.Align.CENTER }
    private val degPnt         = mk { color = C_DEG;       textAlign = Paint.Align.CENTER }
    private val retrogradePnt  = mk { textAlign = Paint.Align.LEFT; textSize = 10f }
    private val tickPnt     = mk { color = C_TICK;      style = Paint.Style.STROKE; strokeWidth = 1.5f }
    private val circlePnt   = mk { color = C_CIRCLE;    style = Paint.Style.STROKE; strokeWidth = 1.5f }

    // ── Zoom / pan ────────────────────────────────────────────────────────────
    // dragEnabled: single-finger pan is locked until the user has pinched once.
    // Resets when the view is detached (tab change / fragment destroy).
    private var scale = 1f; private var tx = 0f; private var ty = 0f
    private var dragEnabled = false
    private var lx = 0f; private var ly = 0f; private var pid = MotionEvent.INVALID_POINTER_ID

    private val scaleD = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(d: ScaleGestureDetector): Boolean { dragEnabled = true; return true }
        override fun onScale(d: ScaleGestureDetector): Boolean { scale = (scale * d.scaleFactor).coerceIn(0.5f, 5f); invalidate(); return true }
    })
    private val gestD = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            scale = 1f; tx = 0f; ty = 0f; dragEnabled = false; invalidate(); return true
        }
    })

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scale = 1f; tx = 0f; ty = 0f; dragEnabled = false
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        scaleD.onTouchEvent(e); gestD.onTouchEvent(e)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> { lx = e.x; ly = e.y; pid = e.getPointerId(0) }
            MotionEvent.ACTION_POINTER_DOWN -> parent?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {
                if (e.pointerCount > 1) parent?.requestDisallowInterceptTouchEvent(true)
                else if (dragEnabled) parent?.requestDisallowInterceptTouchEvent(true)
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

    override fun onMeasure(w: Int, h: Int) {
        val width = MeasureSpec.getSize(w)
        setMeasuredDimension(width, wheelHeight(width, resources))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f; val cy = height / 2f; val r = min(cx, cy) * 0.95f
        canvas.save()
        canvas.translate(tx, ty)
        canvas.scale(scale, scale, cx, cy)

        val asc = astroData?.cusps?.getOrNull(0) ?: 0.0
        canvas.drawCircle(cx, cy, r * R_SIGN_OUT, bgPaint)

        drawZodiacRing(canvas, cx, cy, r, asc)
        drawHouseDivisions(canvas, cx, cy, r, asc)
        drawAspects(canvas, cx, cy, r, asc)
        drawPlanets(canvas, cx, cy, r, asc)

        circlePnt.strokeWidth = 2f;  canvas.drawCircle(cx, cy, r * R_SIGN_OUT, circlePnt)
        circlePnt.strokeWidth = 1.5f; canvas.drawCircle(cx, cy, r * R_SIGN_IN,  circlePnt)
        canvas.drawCircle(cx, cy, r * R_HOUSE,  circlePnt)
        canvas.drawCircle(cx, cy, r * R_ASPECT, circlePnt)

        canvas.restore()
    }

    private fun drawZodiacRing(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        val outerOval = android.graphics.RectF(cx - r*R_SIGN_OUT, cy - r*R_SIGN_OUT, cx + r*R_SIGN_OUT, cy + r*R_SIGN_OUT)
        val innerOval = android.graphics.RectF(cx - r*R_SIGN_IN,  cy - r*R_SIGN_IN,  cx + r*R_SIGN_IN,  cy + r*R_SIGN_IN)
        val midR = r * (R_SIGN_OUT + R_SIGN_IN) / 2f
        val glyphSize = r * (R_SIGN_OUT - R_SIGN_IN) * 0.50f   // glyph uses ~half the ring band height
        val sectorFill = mk { style = Paint.Style.FILL }

        for (i in 0 until 12) {
            val startCanvas = chartAngle(i * 30.0, asc).toFloat()

            // ── Solid element-colour annular sector ─────────────────────────
            sectorFill.color = elementColor(ZodiacSign.values()[i].id)
            val path = android.graphics.Path()
            path.arcTo(outerOval, startCanvas, -30f)       // outer arc CCW
            path.arcTo(innerOval, startCanvas - 30f, 30f)  // inner arc CW (reverse)
            path.close()
            canvas.drawPath(path, sectorFill)

            // Thin dividing line at each cusp
            spoke(canvas, cx, cy, r * R_SIGN_OUT, r * R_SIGN_IN, chartAngle(i * 30.0, asc), signLinePnt)

            // ── Pure-white custom-path glyph (never an emoji icon) ──────────
            val (gx, gy) = pt(cx, cy, midR, chartAngle(i * 30.0 + 15.0, asc))
            canvas.save()
            canvas.translate(gx, gy)
            ZodiacGlyphs.draw(canvas, ZodiacSign.values()[i], glyphSize)
            canvas.restore()
        }
    }

    private fun drawHouseDivisions(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        val cusps = astroData?.cusps ?: return
        houseNumPnt.textSize = r * 0.044f
        for (i in 0 until 12) {
            val angle = chartAngle(cusps[i], asc)
            // A cusp takes the colour of its sign's element; the four angles stay thicker
            val pnt = if (i % 3 == 0) axisPnt else housePnt
            pnt.color = elementColor(ZodiacSign.fromDegree(cusps[i].mod(360.0)).element)
            spoke(canvas, cx, cy, r * R_SIGN_IN, r * R_HOUSE, angle, pnt)
            val mid = midBetween(cusps[i], cusps[(i + 1) % 12])
            val (hx, hy) = pt(cx, cy, r * R_HNUM, chartAngle(mid, asc))
            canvas.drawText("${i + 1}", hx, hy + houseNumPnt.textSize * 0.36f, houseNumPnt)
        }
    }

    private fun drawAspects(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        val data = astroData ?: return
        val prefs: SharedPreferences = context.getSharedPreferences("aspect_settings", Context.MODE_PRIVATE)

        // Build planet list filtered by settings
        val excluded = buildSet<String> {
            if (!prefs.getBoolean("chiron", true)) add("chiron")
            if (!prefs.getBoolean("lilith", true)) add("lilith")
            if (!prefs.getBoolean("rahu",   true)) add("rahu")
        }
        val basePlanets = Planet.values()
            .filter { it.key !in excluded }
            .mapNotNull { p -> data.planets[p.key]?.let { pos -> p.localizedName(context) to pos.absoluteDegree } }

        // Optionally add house cusps
        val cusps = data.cusps
        val extraPoints = mutableListOf<Pair<String, Double>>()
        if (prefs.getBoolean("asc", false) && cusps.size > 0)  extraPoints += "ASC" to cusps[0]
        if (prefs.getBoolean("dsc", false) && cusps.size > 6)  extraPoints += "DSC" to cusps[6]
        if (prefs.getBoolean("mc",  false) && cusps.size > 9)  extraPoints += "MC"  to cusps[9]
        if (prefs.getBoolean("ic",  false) && cusps.size > 3)  extraPoints += "IC"  to cusps[3]

        val pointList = basePlanets + extraPoints
        val ar = r * R_ASPECT
        for (i in pointList.indices) {
            for (j in i + 1 until pointList.size) {
                val (_, degA) = pointList[i]; val (_, degB) = pointList[j]
                val diff = abs(((degA - degB + 360.0) % 360.0).let { if (it > 180.0) 360.0 - it else it })
                val asp = ASPECTS.firstOrNull { abs(diff - it.angle) <= it.orb } ?: continue
                val orbFraction = abs(diff - asp.angle) / asp.orb  // 0 = exact, 1 = max orb
                val alpha = ((1.0 - orbFraction) * 155 + 100).toInt().coerceIn(100, 255)
                // Exact aspects (orbFraction near 0) are drawn much thicker
                val dynamicWidth = asp.width * (1.0f + (1.0f - orbFraction.toFloat()) * 1.5f)
                val p = mk {
                    color = asp.color; this.alpha = alpha; style = Paint.Style.STROKE
                    strokeWidth = dynamicWidth
                }
                val (ax, ay) = pt(cx, cy, ar, chartAngle(degA, asc))
                val (bx, by) = pt(cx, cy, ar, chartAngle(degB, asc))
                canvas.drawLine(ax, ay, bx, by, p)

                // Symbol at midpoint
                val mx = (ax + bx) / 2f
                val my = (ay + by) / 2f
                val symSize = r * 0.058f
                val bgP = mk { color = Color.WHITE; style = Paint.Style.FILL; this.alpha = 210 }
                canvas.drawCircle(mx, my, symSize * 0.72f, bgP)
                val symP = mk {
                    color = asp.color; this.alpha = alpha
                    textAlign = Paint.Align.CENTER; textSize = symSize
                }
                canvas.drawText(asp.symbol, mx, my + symSize * 0.36f, symP)
            }
        }
    }

    private fun drawPlanets(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        val data = astroData ?: return
        planetPnt.textSize    = r * 0.082f
        degPnt.textSize       = r * 0.038f
        retrogradePnt.textSize = r * 0.042f

        data class Entry(val planet: Planet, val trueAngle: Double, val deg: Int, val signId: Int, val retroStatus: String?)
        val entries = Planet.values().mapNotNull { p ->
            data.planets[p.key]?.let { pos ->
                Entry(p, chartAngle(pos.absoluteDegree, asc), pos.degreeInSign, pos.sign,
                    ChartUtil.retrogradeStatus(p.key, pos.speed))
            }
        }

        // Resolve collisions so glyphs don't overlap
        val displayAngles = resolveCollisions(entries.map { it.trueAngle })

        val connPaint = mk { color = C_SIGN_LINE; style = Paint.Style.STROKE; strokeWidth = 0.8f; alpha = 140 }

        entries.forEachIndexed { i, entry ->
            val trueAngle = entry.trueAngle
            val dispAngle = displayAngles[i]
            val elemColor = elementColor(entry.signId)

            // ── Planet glyph at R_PLANET ──────────────────────────────────────────
            val (px, py) = pt(cx, cy, r * R_PLANET, dispAngle)
            if (angDist(dispAngle, trueAngle) > 2.0) {
                val (hx, hy) = pt(cx, cy, r * (R_SIGN_IN - 0.04f), trueAngle)
                canvas.drawLine(px, py, hx, hy, connPaint)
            }
            planetPnt.color = elemColor
            canvas.drawText(entry.planet.glyph, px, py + planetPnt.textSize * 0.36f, planetPnt)

            if (entry.retroStatus != null) {
                retrogradePnt.color = if (entry.retroStatus == "R") Color.parseColor("#EF5350") else Color.parseColor("#FFA726")
                val rx = px + planetPnt.textSize * 0.38f
                val ry = py - planetPnt.textSize * 0.18f
                canvas.drawText(entry.retroStatus, rx, ry, retrogradePnt)
            }

            // ── Degree in dedicated R_DEG ring (same display angle as glyph) ──────
            val (dx, dy) = pt(cx, cy, r * R_DEG, dispAngle)
            degPnt.color = elemColor
            canvas.drawText("${entry.deg}°", dx, dy + degPnt.textSize * 0.36f, degPnt)

            // Tick on zodiac ring inner edge at TRUE ecliptic position
            spoke(canvas, cx, cy, r * R_SIGN_IN, r * (R_SIGN_IN - 0.04f), trueAngle, tickPnt)
        }
    }

    /**
     * Spread planet canvas angles so no two are closer than MIN_SEP.
     * Uses a cluster-expansion sweep: each pass finds groups of planets that are
     * too close, spreads them evenly around their centroid, then re-centres.
     * Handles any number of planets in the same sign correctly.
     */
    private fun resolveCollisions(angles: List<Double>, minSep: Double = 9.0): List<Double> {
        val n = angles.size
        if (n <= 1) return angles

        val indexed = angles.mapIndexed { i, a -> i to a }.sortedBy { it.second }
        val disp = indexed.map { it.second }.toMutableList()

        // Multiple passes: find clusters and expand them evenly
        repeat(50) {
            var i = 0
            while (i < n) {
                // Grow a cluster: consecutive planets within minSep of each other
                val cluster = mutableListOf(i)
                while (cluster.last() < n - 1) {
                    val next = cluster.last() + 1
                    if (((disp[next] - disp[cluster.last()] + 360.0) % 360.0) < minSep)
                        cluster.add(next)
                    else break
                }
                if (cluster.size > 1) {
                    // Spread evenly around cluster centroid
                    val centroid = cluster.map { disp[it] }.average()
                    val halfSpan  = minSep * (cluster.size - 1) / 2.0
                    cluster.forEachIndexed { idx, pidx ->
                        disp[pidx] = (centroid - halfSpan + idx * minSep + 360.0) % 360.0
                    }
                }
                i += cluster.size
            }
        }

        val result = DoubleArray(n)
        indexed.forEachIndexed { si, (oi, _) -> result[oi] = disp[si] }
        return result.toList()
    }

    /** Shortest angular distance between two canvas angles (0–180). */
    private fun angDist(a: Double, b: Double): Double {
        val d = ((b - a + 360.0) % 360.0)
        return if (d > 180.0) 360.0 - d else d
    }

    /** Map a sign ID (1–12) to its element colour. */
    private fun elementColor(signId: Int): Int {
        val element = try { ZodiacSign.fromId(signId).element } catch (_: Exception) { return C_PLANET }
        return elementColor(element)
    }

    private fun elementColor(element: eu.kastroguru.astrodiary.domain.model.Element): Int =
        ZodiacGlyphs.elementColor(element)

    // ── Coordinate helpers ────────────────────────────────────────────────────

    /**
     * chartAngle: ecliptic → canvas angle.
     * Canvas: 0=East, 90=South(bottom), 180=West(ASC-left), 270=North(top).
     * Use pt() / spoke() which apply +sin (canvas Y-down convention).
     * Signs go counter-clockwise: from ASC downward → bottom → right → top → back to ASC.
     */
    private fun chartAngle(ecliptic: Double, asc: Double): Double {
        var v = (180.0 - (ecliptic - asc)) % 360.0
        if (v < 0) v += 360.0
        return v
    }

    private fun pt(cx: Float, cy: Float, r: Float, deg: Double): Pair<Float, Float> {
        val a = Math.toRadians(deg)
        return cx + r * cos(a).toFloat() to cy + r * sin(a).toFloat()
    }

    private fun spoke(canvas: Canvas, cx: Float, cy: Float, r1: Float, r2: Float, deg: Double, paint: Paint) {
        val a = Math.toRadians(deg)
        val c = cos(a).toFloat(); val s = sin(a).toFloat()
        canvas.drawLine(cx + r1 * c, cy + r1 * s, cx + r2 * c, cy + r2 * s, paint)
    }

    private fun midBetween(a: Double, b: Double): Double {
        val d = ((b - a) % 360.0 + 360.0) % 360.0
        return (a + d / 2.0) % 360.0
    }
}

/**
 * Height of a round chart [width] wide: square, but never more than 80 % of the screen height.
 * Squaring by width alone made the wheel taller than the viewport on a tablet in landscape — Android
 * 16 ignores the portrait lock there — so only its top half showed. Phones in portrait are
 * unaffected: their width is always the smaller side. The wheel is drawn centred in the box.
 */
internal fun wheelHeight(width: Int, res: android.content.res.Resources): Int =
    min(width, (res.displayMetrics.heightPixels * 0.8f).toInt())
