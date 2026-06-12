package eu.kastroguru.astrodiary.ui.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import eu.kastroguru.astrodiary.domain.model.Element
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.transit.TransitAspect
import kotlin.math.*

class BiWheelChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var natalCusps: List<Double> = emptyList()
        set(value) { field = value; invalidate() }
    var natalPlanets: Map<String, Double> = emptyMap()
        set(value) { field = value; invalidate() }
    var transitData: AstroData? = null
        set(value) { field = value; invalidate() }
    var aspects: List<TransitAspect> = emptyList()
        set(value) { field = value; invalidate() }

    // ── Ring radii ─────────────────────────────────────────────────────────────
    private val R_SIGN_OUTER  = 0.98f   // outer zodiac ring
    private val R_SIGN_INNER  = 0.87f   // inner zodiac ring
    private val R_TRANSIT     = 0.80f   // transit planets — close to sign ring (short connector ticks)
    private val R_TRANSIT_DEG = 0.72f   // transit degree numbers
    private val R_NATAL_PLANET= 0.64f   // natal planets — own ring OUTSIDE house area
    private val R_NATAL_OUTER = 0.57f   // house outer boundary
    private val R_HNUM        = 0.47f   // house numbers midpoint between R_NATAL_OUTER and R_ASPECT
    private val R_ASPECT      = 0.38f   // aspect lines / inner wall

    // ── Colours ────────────────────────────────────────────────────────────────
    private val C_BG       = Color.WHITE
    private val C_SIGN_BND = Color.parseColor("#EEEDF4")
    private val C_SIGN_LN  = Color.parseColor("#888888")
    private val C_AXIS     = Color.parseColor("#222222")
    private val C_HOUSE    = Color.parseColor("#777777")
    private val C_HNUM     = Color.parseColor("#555555")
    private val C_DEG      = Color.parseColor("#888888")
    private val C_CONN     = Color.parseColor("#888888")
    private val C_TICK     = Color.parseColor("#999999")
    private val C_CIRCLE   = Color.parseColor("#999999")

    // Element colours (same as AstroChartView)
    private val C_FIRE  = Color.parseColor("#CC3300")
    private val C_EARTH = Color.parseColor("#2A1506")
    private val C_WATER = Color.parseColor("#1144CC")
    private val C_AIR   = Color.parseColor("#C09500")

    // Aspect colours + definitions (same as AstroChartView)
    private data class AspectDef(val angle: Int, val orb: Float, val color: Int, val width: Float)
    private val ASPECTS = listOf(
        AspectDef(  0, 8f, Color.parseColor("#1A1ABB"), 2.0f),
        AspectDef( 60, 6f, Color.parseColor("#0A6644"), 1.6f),
        AspectDef( 90, 8f, Color.parseColor("#CC0000"), 2.0f),
        AspectDef(120, 8f, Color.parseColor("#0055BB"), 2.0f),
        AspectDef(150, 5f, Color.parseColor("#886600"), 1.4f),
        AspectDef(180, 8f, Color.parseColor("#111111"), 2.0f)
    )

    private fun mk(b: Paint.() -> Unit) = Paint(Paint.ANTI_ALIAS_FLAG).apply(b)

    private val bgPaint    = mk { color = C_BG;       style = Paint.Style.FILL }
    private val signLinePnt= mk { color = C_SIGN_LN;  style = Paint.Style.STROKE; strokeWidth = 1.5f }
    private val signTxtPnt = mk { color = Color.WHITE; textAlign = Paint.Align.CENTER }
    private val axisPnt    = mk { color = C_AXIS;      style = Paint.Style.STROKE; strokeWidth = 2.5f }
    private val housePnt   = mk { color = C_HOUSE;     style = Paint.Style.STROKE; strokeWidth = 1.2f }
    private val houseNumPnt= mk { color = C_HNUM;      textAlign = Paint.Align.CENTER }
    private val transitPnt = mk { textAlign = Paint.Align.CENTER }
    private val natalPnt   = mk { textAlign = Paint.Align.CENTER }
    private val degPnt     = mk { color = C_DEG;       textAlign = Paint.Align.CENTER }
    private val connPnt    = mk { color = C_CONN;      style = Paint.Style.STROKE; strokeWidth = 0.8f; alpha = 140 }
    private val tickPnt    = mk { color = C_TICK;      style = Paint.Style.STROKE; strokeWidth = 1.5f }
    private val circlePnt  = mk { color = C_CIRCLE;    style = Paint.Style.STROKE; strokeWidth = 1.5f }
    private val sectorFill = mk { style = Paint.Style.FILL }

    // ── Zoom / pan ─────────────────────────────────────────────────────────────
    private var scaleFactor = 1f; private var translateX = 0f; private var translateY = 0f
    private var dragEnabled = false
    private var lastX = 0f; private var lastY = 0f; private var activePtr = MotionEvent.INVALID_POINTER_ID

    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(d: ScaleGestureDetector): Boolean { dragEnabled = true; return true }
            override fun onScale(d: ScaleGestureDetector): Boolean {
                scaleFactor = (scaleFactor * d.scaleFactor).coerceIn(0.5f, 5f); invalidate(); return true
            }
        })
    private val gestureDetector = GestureDetector(context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                scaleFactor = 1f; translateX = 0f; translateY = 0f; dragEnabled = false; invalidate(); return true
            }
        })

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scaleFactor = 1f; translateX = 0f; translateY = 0f; dragEnabled = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event); gestureDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { lastX = event.x; lastY = event.y; activePtr = event.getPointerId(0) }
            MotionEvent.ACTION_POINTER_DOWN -> parent?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount > 1) parent?.requestDisallowInterceptTouchEvent(true)
                else if (dragEnabled) parent?.requestDisallowInterceptTouchEvent(true)
                if (!scaleDetector.isInProgress && dragEnabled) {
                    val idx = event.findPointerIndex(activePtr)
                    if (idx >= 0) {
                        translateX += event.getX(idx) - lastX; translateY += event.getY(idx) - lastY
                        lastX = event.getX(idx); lastY = event.getY(idx); invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> activePtr = MotionEvent.INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val idx = event.actionIndex
                if (event.getPointerId(idx) == activePtr) {
                    val ni = if (idx == 0) 1 else 0
                    lastX = event.getX(ni); lastY = event.getY(ni); activePtr = event.getPointerId(ni)
                }
            }
        }
        return true
    }

    override fun onMeasure(w: Int, h: Int) = super.onMeasure(w, w)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f; val cy = height / 2f; val r = min(cx, cy) * 0.97f

        canvas.save()
        canvas.translate(translateX, translateY)
        canvas.scale(scaleFactor, scaleFactor, cx, cy)

        val asc = natalCusps.getOrElse(0) { 0.0 }

        canvas.drawCircle(cx, cy, r * R_SIGN_OUTER, bgPaint)

        drawZodiacRing(canvas, cx, cy, r, asc)
        drawNatalHouses(canvas, cx, cy, r, asc)
        drawAspectLines(canvas, cx, cy, r, asc)
        drawTransitPlanets(canvas, cx, cy, r, asc)
        drawNatalPlanets(canvas, cx, cy, r, asc)

        // Circle borders
        circlePnt.strokeWidth = 2f
        canvas.drawCircle(cx, cy, r * R_SIGN_OUTER, circlePnt)
        circlePnt.strokeWidth = 1.5f
        canvas.drawCircle(cx, cy, r * R_SIGN_INNER,   circlePnt)
        canvas.drawCircle(cx, cy, r * R_NATAL_PLANET, circlePnt)  // natal planet ring border
        circlePnt.strokeWidth = 0.8f                               // thin for house boundary
        canvas.drawCircle(cx, cy, r * R_NATAL_OUTER,  circlePnt)
        circlePnt.strokeWidth = 1.5f
        canvas.drawCircle(cx, cy, r * R_ASPECT,       circlePnt)

        canvas.restore()
    }

    // ── Zodiac ring — same approach as AstroChartView ─────────────────────────
    private fun drawZodiacRing(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        val outerOval = RectF(cx - r*R_SIGN_OUTER, cy - r*R_SIGN_OUTER, cx + r*R_SIGN_OUTER, cy + r*R_SIGN_OUTER)
        val innerOval = RectF(cx - r*R_SIGN_INNER, cy - r*R_SIGN_INNER, cx + r*R_SIGN_INNER, cy + r*R_SIGN_INNER)
        val midR = r * (R_SIGN_OUTER + R_SIGN_INNER) / 2f
        val glyphSize = r * (R_SIGN_OUTER - R_SIGN_INNER) * 0.50f

        for (i in 0 until 12) {
            val startCanvas = chartAngle(i * 30.0, asc).toFloat()
            sectorFill.color = elementColorFor(ZodiacSign.values()[i].element)
            val path = android.graphics.Path()
            path.arcTo(outerOval, startCanvas, -30f)
            path.arcTo(innerOval, startCanvas - 30f, 30f)
            path.close()
            canvas.drawPath(path, sectorFill)
            drawSpoke(canvas, cx, cy, r * R_SIGN_OUTER, r * R_SIGN_INNER, chartAngle(i * 30.0, asc), signLinePnt)
            val (gx, gy) = pt(cx, cy, midR, chartAngle(i * 30.0 + 15.0, asc))
            canvas.save(); canvas.translate(gx, gy)
            drawSignGlyph(canvas, ZodiacSign.values()[i], glyphSize)
            canvas.restore()
        }
    }

    // ── Natal houses + house numbers ──────────────────────────────────────────
    private fun drawNatalHouses(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        if (natalCusps.isEmpty()) return
        houseNumPnt.textSize = r * 0.040f
        for (i in 0 until 12) {
            val angle = chartAngle(natalCusps[i], asc)
            drawSpoke(canvas, cx, cy, r * R_SIGN_INNER, r * R_NATAL_OUTER,
                angle, if (i % 3 == 0) axisPnt else housePnt)
            // House number at midpoint between R_NATAL_OUTER and R_ASPECT
            val next = natalCusps[(i + 1) % 12]
            val mid  = midBetween(natalCusps[i], next)
            val (hx, hy) = pt(cx, cy, r * R_HNUM, chartAngle(mid, asc))
            canvas.drawText("${i + 1}", hx, hy + houseNumPnt.textSize * 0.36f, houseNumPnt)
        }
    }

    // ── Aspect lines — same colours and dynamic thickness as natal chart ───────
    private fun drawAspectLines(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        val ar = r * R_ASPECT * 0.97f  // reaches very close to the inner circle
        for (aspect in aspects.take(25)) {
            val nDeg = natalPlanets[aspect.natalPlanet] ?: continue
            val tDeg = transitData?.planets?.get(aspect.transitPlanet)?.absoluteDegree ?: continue
            val diff = abs(((tDeg - nDeg + 360.0) % 360.0).let { if (it > 180) 360 - it else it })
            val asp = ASPECTS.firstOrNull { abs(diff - it.angle) <= it.orb } ?: continue
            val orbFraction = abs(diff - asp.angle) / asp.orb
            val alpha = ((1.0 - orbFraction) * 155 + 100).toInt().coerceIn(100, 255)
            val dynamicWidth = asp.width * (1.0f + (1.0f - orbFraction.toFloat()) * 1.5f)
            val p = mk {
                color = asp.color; this.alpha = alpha; style = Paint.Style.STROKE
                strokeWidth = dynamicWidth
                if (asp.angle == 90 || asp.angle == 150)
                    pathEffect = DashPathEffect(floatArrayOf(8f, 4f), 0f)
            }
            val nA = Math.toRadians(chartAngle(nDeg, asc))
            val tA = Math.toRadians(chartAngle(tDeg, asc))
            canvas.drawLine(cx + ar*cos(nA).toFloat(), cy + ar*sin(nA).toFloat(),
                            cx + ar*cos(tA).toFloat(), cy + ar*sin(tA).toFloat(), p)
        }
    }

    // ── Transit planets — element colours, collision resolution, degree ring ───
    private fun drawTransitPlanets(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        val data = transitData ?: return
        transitPnt.textSize = r * 0.070f   // larger — more readable on small screens
        degPnt.textSize     = r * 0.038f

        data class Entry(val planet: Planet, val trueAngle: Double, val deg: Int, val elemColor: Int)
        val entries = Planet.values().mapNotNull { planet ->
            val pos = data.planets[planet.key] ?: return@mapNotNull null
            val color = elementColorFor(ZodiacSign.fromId(pos.sign).element)
            Entry(planet, chartAngle(pos.absoluteDegree, asc), pos.degreeInSign, color)
        }

        val displayAngles = resolveCollisions(entries.map { it.trueAngle })

        entries.forEachIndexed { i, entry ->
            val trueAngle = entry.trueAngle
            val dispAngle = displayAngles[i]

            // Connector line when displaced
            if (angDist(dispAngle, trueAngle) > 2.0) {
                val (hx, hy) = pt(cx, cy, r * (R_SIGN_IN_LOCAL - 0.04f), trueAngle)
                val (px, py) = pt(cx, cy, r * R_TRANSIT, dispAngle)
                canvas.drawLine(px, py, hx, hy, connPnt)
            }

            // Glyph at display angle
            val (px, py) = pt(cx, cy, r * R_TRANSIT, dispAngle)
            transitPnt.color = entry.elemColor
            canvas.drawText(entry.planet.glyph, px, py + transitPnt.textSize * 0.36f, transitPnt)

            // Degree at R_TRANSIT_DEG (dedicated ring)
            val (dx, dy) = pt(cx, cy, r * R_TRANSIT_DEG, dispAngle)
            degPnt.color = entry.elemColor
            canvas.drawText("${entry.deg}°", dx, dy + degPnt.textSize * 0.36f, degPnt)

            // Tick at true ecliptic position on sign ring
            drawSpoke(canvas, cx, cy, r * R_SIGN_INNER, r * (R_SIGN_INNER - 0.04f), trueAngle, tickPnt)
        }
    }

    // ── Natal planets — element colours, collision resolution ─────────────────
    private fun drawNatalPlanets(canvas: Canvas, cx: Float, cy: Float, r: Float, asc: Double) {
        natalPnt.textSize = r * 0.062f   // larger

        data class NEntry(val planet: Planet, val trueAngle: Double, val elemColor: Int)
        val asc0 = natalCusps.getOrElse(0) { 0.0 }
        val entries = natalPlanets.mapNotNull { (key, degree) ->
            val planet = Planet.values().find { it.key == key } ?: return@mapNotNull null
            NEntry(planet, chartAngle(degree, asc0), elementColorFor(ZodiacSign.fromDegree(degree).element))
        }

        val displayAngles = resolveCollisions(entries.map { it.trueAngle })

        entries.forEachIndexed { i, entry ->
            val trueAngle = entry.trueAngle
            val dispAngle = displayAngles[i]
            val (px, py) = pt(cx, cy, r * R_NATAL_PLANET, dispAngle)
            natalPnt.color = entry.elemColor
            canvas.drawText(entry.planet.glyph, px, py + natalPnt.textSize * 0.36f, natalPnt)
            // Connector when displaced
            if (angDist(dispAngle, trueAngle) > 2.0) {
                val (hx, hy) = pt(cx, cy, r * (R_NATAL_PLANET - 0.04f), trueAngle)
                canvas.drawLine(px, py, hx, hy, connPnt)
            }
            // Tick at true position on the natal ring
            drawSpoke(canvas, cx, cy, r * R_NATAL_PLANET, r * (R_NATAL_PLANET - 0.04f), trueAngle, tickPnt)
        }
    }

    // ── Collision resolution (same algorithm as AstroChartView) ───────────────
    private fun resolveCollisions(angles: List<Double>, minSep: Double = 9.0): List<Double> {
        val n = angles.size; if (n <= 1) return angles
        val indexed = angles.mapIndexed { i, a -> i to a }.sortedBy { it.second }
        val disp = indexed.map { it.second }.toMutableList()
        repeat(50) {
            var i = 0
            while (i < n) {
                val cluster = mutableListOf(i)
                while (cluster.last() < n - 1) {
                    val next = cluster.last() + 1
                    if (((disp[next] - disp[cluster.last()] + 360.0) % 360.0) < minSep)
                        cluster.add(next) else break
                }
                if (cluster.size > 1) {
                    val centroid = cluster.map { disp[it] }.average()
                    val halfSpan = minSep * (cluster.size - 1) / 2.0
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

    private fun angDist(a: Double, b: Double): Double {
        val d = ((b - a + 360.0) % 360.0); return if (d > 180.0) 360.0 - d else d
    }

    // ── Sign glyphs (identical to AstroChartView) ─────────────────────────────
    private val R_SIGN_IN_LOCAL = R_SIGN_INNER  // alias for connector endpoint

    private fun drawSignGlyph(canvas: Canvas, sign: ZodiacSign, s: Float) {
        val sw = maxOf(3f, s * 0.17f)
        val p  = mk { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = sw; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
        val fp = mk { color = Color.WHITE; style = Paint.Style.FILL }
        val path = android.graphics.Path(); val oval = RectF()
        when (sign) {
            ZodiacSign.ARIES -> {
                canvas.drawLine(0f,0f,0f,s*0.45f,p)
                oval.set(-s*0.5f,-s*0.4f,0f,s*0.4f); canvas.drawArc(oval,0f,-180f,false,p)
                oval.set(0f,-s*0.4f,s*0.5f,s*0.4f);  canvas.drawArc(oval,180f,180f,false,p)
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
                val cr=s*0.22f; canvas.drawCircle(-s*0.06f,s*0.22f,cr,p)
                path.moveTo(-s*0.06f+cr,s*0.22f); path.cubicTo(s*0.52f,-s*0.62f,s*0.66f,s*0.15f,s*0.44f,s*0.5f)
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
                val yT=-s*0.28f; val yM=-s*0.7f; val x1=-s*0.42f; val x2=0f; val x3=s*0.42f
                canvas.drawLine(x1,yT,x1,s*0.1f,p)
                path.moveTo(x1,yT); path.cubicTo(x1,yM,x2,yM,x2,yT); path.lineTo(x2,s*0.1f)
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
                for (y in listOf(-s*0.18f,s*0.18f)) {
                    path.moveTo(-s*0.5f,y); path.quadTo(-s*0.25f,y-s*0.24f,0f,y); path.quadTo(s*0.25f,y+s*0.24f,s*0.5f,y)
                }
            }
            ZodiacSign.PISCES -> {
                val rr=s*0.38f; val xO=s*0.36f  // use different name to avoid shadowing outer r
                oval.set(-xO-rr,-rr,-xO+rr,rr); canvas.drawArc(oval,270f, 180f,false,p)
                oval.set( xO-rr,-rr, xO+rr,rr); canvas.drawArc(oval,270f,-180f,false,p)
                canvas.drawLine(-s*0.58f,0f,s*0.58f,0f,p)
            }
        }
        canvas.drawPath(path, p)
    }

    // ── Element colours ────────────────────────────────────────────────────────
    private fun elementColorFor(element: Element): Int = when (element) {
        Element.FIRE  -> C_FIRE
        Element.EARTH -> C_EARTH
        Element.WATER -> C_WATER
        Element.AIR   -> C_AIR
    }

    // ── Coordinate helpers ────────────────────────────────────────────────────
    private fun chartAngle(eclipticDeg: Double, ascDeg: Double): Double {
        var v = (180.0 - (eclipticDeg - ascDeg)) % 360.0
        if (v < 0) v += 360.0; return v
    }

    private fun pt(cx: Float, cy: Float, r: Float, canvasDeg: Double): Pair<Float, Float> {
        val a = Math.toRadians(canvasDeg)
        return cx + r * cos(a).toFloat() to cy + r * sin(a).toFloat()
    }

    private fun drawSpoke(canvas: Canvas, cx: Float, cy: Float, r1: Float, r2: Float, deg: Double, paint: Paint) {
        val a = Math.toRadians(deg); val c = cos(a).toFloat(); val s = sin(a).toFloat()
        canvas.drawLine(cx + r1*c, cy + r1*s, cx + r2*c, cy + r2*s, paint)
    }

    private fun midBetween(a: Double, b: Double): Double {
        val d = ((b - a) % 360.0 + 360.0) % 360.0; return (a + d / 2.0) % 360.0
    }
}
