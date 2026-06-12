package eu.kastroguru.astrodiary.ui.humandesign

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import eu.kastroguru.astrodiary.domain.humandesign.*

/**
 * Human Design bodygraph. Every gate has a fixed PORT on its center's perimeter;
 * channels connect port-to-port (gate.a → gate.b), so lines always reach the real
 * edges of each shape and gates sit where they belong.
 *
 *  - Defined centers  → gold fill, gold border. Undefined → white, gray border.
 *  - Channel half      → black = activated by Personality, red = by Design,
 *                        light gray = inactive ("blueprint").
 *  - Gate numbers      → drawn at every port, colored by activation.
 */
class BodygraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var definedCenters: Set<HdCenter> = emptySet();   set(v) { field = v; invalidate() }
    var personalityGates: Set<Int> = emptySet();      set(v) { field = v; invalidate() }
    var designGates: Set<Int> = emptySet();           set(v) { field = v; invalidate() }

    // ── Colours ───────────────────────────────────────────────────────────────
    private val C_BG          = Color.WHITE
    private val C_DEF_FILL     = Color.parseColor("#FFF5CC")  // light cream-yellow
    private val C_DEF_BORDER    = Color.parseColor("#C09500")
    private val C_UNDEF_FILL    = Color.parseColor("#F5F3FF")
    private val C_UNDEF_BORDER   = Color.parseColor("#BBBBBB")
    private val C_BLUEPRINT      = Color.parseColor("#DBDBDB")
    private val C_PERSONALITY     = Color.parseColor("#C09500")   // natal chart — dark gold
    private val C_DESIGN          = Color.parseColor("#1144CC")   // 88-day design — dark blue
    private val C_NUM_INACTIVE     = Color.parseColor("#B8B8B8")
    private val C_GATE_ACTIVE_BG   = Color.parseColor("#43A047")  // green circle for activated gates
    private val C_LABEL_DEF        = Color.parseColor("#5A4500")
    private val C_LABEL_UNDEF      = Color.parseColor("#9A9A9A")

    private fun mk(b: Paint.() -> Unit) = Paint(Paint.ANTI_ALIAS_FLAG).apply(b)

    // ── Zoom / pan ───────────────────────────────────────────────────────────
    private var scale = 1f; private var tx = 0f; private var ty = 0f
    private var dragEnabled = false
    private var lx = 0f; private var ly = 0f; private var pid = MotionEvent.INVALID_POINTER_ID
    private val scaleD = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(d: ScaleGestureDetector): Boolean { dragEnabled = true; return true }
        override fun onScale(d: ScaleGestureDetector): Boolean { scale = (scale * d.scaleFactor).coerceIn(0.5f, 6f); invalidate(); return true }
    })
    private val gestD = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean { scale = 1f; tx = 0f; ty = 0f; dragEnabled = false; invalidate(); return true }
    })
    override fun onDetachedFromWindow() { super.onDetachedFromWindow(); scale = 1f; tx = 0f; ty = 0f; dragEnabled = false }
    override fun onTouchEvent(e: MotionEvent): Boolean {
        scaleD.onTouchEvent(e); gestD.onTouchEvent(e)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN         -> { lx = e.x; ly = e.y; pid = e.getPointerId(0) }
            MotionEvent.ACTION_POINTER_DOWN -> parent?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {
                if (e.pointerCount > 1 || dragEnabled) parent?.requestDisallowInterceptTouchEvent(true)
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(w, (w * 1.55f).toInt())
    }

    // ── Center geometry ───────────────────────────────────────────────────────
    private enum class Shape { TRI_UP, TRI_DOWN, TRI_LEFT, TRI_RIGHT, SQUARE, DIAMOND }
    private data class Geom(val cx: Float, val cy: Float, val r: Float, val shape: Shape)

    private fun layout(W: Float, H: Float): Map<HdCenter, Geom> {
        fun g(nx: Float, ny: Float, hs: Float, s: Shape) = Geom(nx * W, ny * H, hs * W, s)
        return mapOf(
            HdCenter.HEAD         to g(0.50f, 0.045f, 0.075f, Shape.TRI_UP),
            HdCenter.AJNA         to g(0.50f, 0.165f, 0.085f, Shape.TRI_DOWN),
            HdCenter.THROAT       to g(0.50f, 0.305f, 0.095f, Shape.SQUARE),
            HdCenter.G            to g(0.50f, 0.450f, 0.105f, Shape.DIAMOND),
            HdCenter.HEART        to g(0.695f, 0.490f, 0.068f, Shape.TRI_UP),   // ← UP, not LEFT
            HdCenter.SPLEEN       to g(0.140f, 0.610f, 0.095f, Shape.TRI_RIGHT),
            HdCenter.SACRAL       to g(0.50f, 0.660f, 0.095f, Shape.SQUARE),
            HdCenter.SOLAR_PLEXUS to g(0.860f, 0.610f, 0.095f, Shape.TRI_LEFT),
            HdCenter.ROOT         to g(0.50f, 0.840f, 0.095f, Shape.SQUARE)
        )
    }

    // Gate → local port offset (units of the center's half-size r; x right, y down)
    // G diamond ports sit exactly on the perimeter (|x|+|y|=1.0) so channel lines
    // start at the shape boundary and never pass through the interior.
    private val PORTS: Map<Int, Pair<Float, Float>> = mapOf(
        // HEAD (TRI_UP — apex at top, base at bottom toward Ajna)
        64 to (-0.5f to 0.60f), 61 to (0.0f to 0.60f), 63 to (0.5f to 0.60f),
        // AJNA — top row 47/24/4 all same y; bottom 17(moved up)/43(moved down)/11(moved up)
        47 to (-0.50f to -0.65f), 24 to (0.0f to -0.65f), 4 to (0.50f to -0.65f),
        17 to (-0.40f to 0.22f), 43 to (0.0f to 0.75f), 11 to (0.40f to 0.22f),
        // THROAT — top 62/23/56, left 16(above mid)/20(below mid), right 35/12/45, bottom 31/8/33
        62 to (-0.6f to -1f), 23 to (0.0f to -1f), 56 to (0.6f to -1f),
        16 to (-1f to -0.25f), 20 to (-1f to  0.25f),
        35 to (1f to -0.5f), 12 to (1f to 0.0f), 45 to (1f to 0.5f),
        31 to (-0.5f to 1f), 8 to (0.0f to 1f), 33 to (0.5f to 1f),
        // G (DIAMOND) — ports ON the diamond perimeter (|x|+|y|=1.0)
        // so channel lines exit from the visible boundary, not through the interior
        1  to ( 0.0f  to -1.00f),
        7  to (-0.40f to -0.60f),
        13 to ( 0.40f to -0.60f),
        10 to (-0.96f to  0.0f),
        25 to ( 0.96f to  0.0f),
        15 to (-0.40f to  0.60f),
        46 to ( 0.40f to  0.60f),
        2  to ( 0.0f  to  1.00f),
        // HEART (TRI_UP — apex on top, base horizontal)
        // 21 at top (moved down a little), 26 bottom-left, 40 bottom-right, 51 mid left-edge
        21 to ( 0.0f  to -0.78f),
        51 to (-0.48f to -0.07f),
        26 to (-0.88f to  0.72f),
        40 to ( 0.88f to  0.72f),
        // SPLEEN (TRI_RIGHT — right apex = gate 50, top-left = 48, bottom-left = 18)
        // top edge:    48 → 57 → 44 → 50
        // bottom edge: 18 → 28 → 32 → 50
        48 to (-0.72f to -0.88f),
        57 to (-0.20f to -0.59f), 44 to (0.33f to -0.29f),
        50 to ( 0.85f to  0.0f),
        18 to (-0.72f to  0.88f),
        28 to (-0.20f to  0.59f), 32 to (0.33f to  0.29f),
        // SACRAL — 34 ABOVE 27 on left edge; top 5/14/29; right 59; bottom 42/3/9
        34 to (-1f to -0.45f),
        27 to (-1f to  0.10f),
        5  to (-0.45f to -1f), 14 to (0.0f to -1f), 29 to (0.45f to -1f),
        59 to (1f to 0.0f),
        42 to (-0.45f to 1f), 3 to (0.0f to 1f), 9 to (0.45f to 1f),
        // SOLAR PLEXUS (TRI_LEFT — left apex = 6, top-right = 36, bottom-right = 30)
        // top edge 6→37→22→36; bottom edge 6→49→55→30
        6  to (-0.85f to  0.0f),
        37 to (-0.40f to -0.32f), 22 to (0.22f to -0.63f), 36 to (0.75f to -0.90f),
        49 to (-0.40f to  0.32f), 55 to (0.22f to  0.63f), 30 to (0.75f to  0.90f),
        // ROOT (SQUARE) — top row 53/60/52; left column 54/38/58; right column 19/39/41
        53 to (-0.50f to -0.88f), 60 to (0.0f to -0.88f), 52 to (0.50f to -0.88f),
        54 to (-0.82f to -0.33f), 38 to (-0.82f to  0.20f), 58 to (-0.82f to  0.72f),
        19 to ( 0.82f to -0.33f), 39 to ( 0.82f to  0.20f), 41 to ( 0.82f to  0.72f)
    )

    private val shortName = mapOf(
        HdCenter.HEAD to "Head", HdCenter.AJNA to "Ajna", HdCenter.THROAT to "Throat",
        HdCenter.G to "G", HdCenter.HEART to "Heart", HdCenter.SACRAL to "Sacral",
        HdCenter.SPLEEN to "Spleen", HdCenter.SOLAR_PLEXUS to "Solar", HdCenter.ROOT to "Root"
    )

    // Per-center label nudge (fraction of r, x right / y down)
    private val LABEL_NUDGE: Map<HdCenter, Pair<Float, Float>> = mapOf(
        HdCenter.AJNA         to ( 0.0f to -0.28f),  // up (clear of 17/11)
        HdCenter.SPLEEN       to (-0.28f to  0.0f),  // left
        HdCenter.HEART        to ( 0.0f to  0.28f),  // down (with 21 lower)
    )

    // Absolute port position in pixels
    private fun port(gate: Int, geom: Map<HdCenter, Geom>): Pair<Float, Float>? {
        val center = centerOfGate(gate) ?: return null
        val g = geom[center] ?: return null
        val (lpx, lpy) = PORTS[gate] ?: return null
        return (g.cx + lpx * g.r) to (g.cy + lpy * g.r)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val W = width.toFloat(); val H = height.toFloat()
        if (W == 0f) return
        canvas.drawColor(C_BG)
        canvas.save()
        canvas.translate(tx, ty)
        canvas.scale(scale, scale, W / 2f, H / 2f)

        val geom = layout(W, H)
        drawHumanSilhouette(canvas, W, H)
        drawChannels(canvas, geom, W)
        drawIntegrationExtras(canvas, geom, W)
        drawCenters(canvas, geom, W)
        drawGateNumbers(canvas, geom, W)

        canvas.restore()
    }

    // ── Channels: port → port, each half colored by its gate's activation ─────
    private fun drawChannels(canvas: Canvas, geom: Map<HdCenter, Geom>, W: Float) {
        for (ch in CHANNELS) {
            // Skip all integration channels between gates 10 / 20 / 34 / 57
            val aIsInt = ch.a == 10 || ch.a == 20 || ch.a == 34 || ch.a == 57
            val bIsInt = ch.b == 10 || ch.b == 20 || ch.b == 34 || ch.b == 57
            if (aIsInt && bIsInt) continue
            val pa = port(ch.a, geom) ?: continue
            val pb = port(ch.b, geom) ?: continue
            val mx = (pa.first + pb.first) / 2f; val my = (pa.second + pb.second) / 2f
            drawHalf(canvas, pa.first, pa.second, mx, my, ch.a, W)
            drawHalf(canvas, pb.first, pb.second, mx, my, ch.b, W)
        }
    }

    /**
     * Integration routing for channels 57 / 20 / 10 / 34.
     *
     * J1 = midpoint(port57, port20)  ← where gate 10 connects
     * J2 = midpoint(port57, J1)      ← where gate 34 connects
     *
     * Lines drawn:
     *   port(57) → J1          (Spleen-side trunk — the "57-20 line")
     *   port(10)  → J1         (gate 10 taps the midpoint)
     *   port(34)  → J2         (gate 34 taps between 57 and J1)
     *
     * The gate-20 arm (port(20)→J1) is intentionally NOT drawn
     * to avoid the confusing diagonal line from Throat gate 20.
     */
    private fun drawIntegrationExtras(canvas: Canvas, geom: Map<HdCenter, Geom>, W: Float) {
        val p57 = port(57, geom) ?: return
        val p20 = port(20, geom) ?: return
        val j1x = (p57.first  + p20.first)  / 2f
        val j1y = (p57.second + p20.second) / 2f
        val j2x = (p57.first  + j1x) / 2f
        val j2y = (p57.second + j1y) / 2f
        // Full 57-20 trunk, split at J1 so each half is coloured by its own gate
        drawHalf(canvas, p57.first, p57.second, j1x, j1y, 57, W)
        drawHalf(canvas, p20.first, p20.second, j1x, j1y, 20, W)
        // Gate 10 → J1  (taps the midpoint of 57-20)
        port(10, geom)?.let { (x, y) -> drawActivationLine(canvas, x, y, j1x, j1y, 10, W) }
        // Gate 34 → J2  (taps the midpoint between 57 and J1)
        port(34, geom)?.let { (x, y) -> drawActivationLine(canvas, x, y, j2x, j2y, 34, W) }
    }

    private fun drawActivationLine(canvas: Canvas, x1: Float, y1: Float,
                                   x2: Float, y2: Float, gate: Int, W: Float) {
        val inP = gate in personalityGates; val inD = gate in designGates
        if (!inP && !inD) {
            canvas.drawLine(x1, y1, x2, y2,
                mk { color = C_BLUEPRINT; strokeWidth = W * 0.008f; strokeCap = Paint.Cap.ROUND })
            return
        }
        if (inD) canvas.drawLine(x1, y1, x2, y2,
            mk { color = C_DESIGN; strokeWidth = W * 0.015f; strokeCap = Paint.Cap.ROUND })
        if (inP) canvas.drawLine(x1, y1, x2, y2,
            mk { color = C_PERSONALITY; strokeWidth = if (inD) W * 0.008f else W * 0.015f; strokeCap = Paint.Cap.ROUND })
    }

    private fun drawHalf(canvas: Canvas, ex: Float, ey: Float, mx: Float, my: Float, gate: Int, W: Float) {
        val inP = gate in personalityGates
        val inD = gate in designGates
        if (!inP && !inD) {
            canvas.drawLine(ex, ey, mx, my, mk { color = C_BLUEPRINT; strokeWidth = W * 0.008f; strokeCap = Paint.Cap.ROUND })
            return
        }
        // Red underlay if Design; black on top if Personality (both → layered)
        if (inD) canvas.drawLine(ex, ey, mx, my, mk { color = C_DESIGN; strokeWidth = W * 0.015f; strokeCap = Paint.Cap.ROUND })
        if (inP) canvas.drawLine(ex, ey, mx, my, mk { color = C_PERSONALITY; strokeWidth = if (inD) W * 0.008f else W * 0.015f; strokeCap = Paint.Cap.ROUND })
    }

    // ── Centers ────────────────────────────────────────────────────────────────
    private fun drawCenters(canvas: Canvas, geom: Map<HdCenter, Geom>, W: Float) {
        val namePnt = mk { textAlign = Paint.Align.CENTER; textSize = W * 0.028f }
        for ((center, g) in geom) {
            val defined = center in definedCenters
            val fill   = mk { style = Paint.Style.FILL;   color = if (defined) C_DEF_FILL else C_UNDEF_FILL }
            val border = mk { style = Paint.Style.STROKE; color = if (defined) C_DEF_BORDER else C_UNDEF_BORDER; strokeWidth = W * 0.006f }
            val path = shapePath(g)
            canvas.drawPath(path, fill)
            canvas.drawPath(path, border)
            namePnt.color = if (defined) C_LABEL_DEF else C_LABEL_UNDEF
            namePnt.isFakeBoldText = defined
            val nudge = LABEL_NUDGE[center] ?: (0.0f to 0.0f)
            canvas.drawText(
                shortName[center] ?: "",
                g.cx + nudge.first * g.r,
                g.cy + nudge.second * g.r + namePnt.textSize * 0.36f,
                namePnt
            )
        }
    }

    // ── Gate numbers: at every port, nudged inward, colored by activation ─────
    private fun drawGateNumbers(canvas: Canvas, geom: Map<HdCenter, Geom>, W: Float) {
        val pnt = mk { textAlign = Paint.Align.CENTER; textSize = W * 0.0235f }
        val circlePnt = mk { style = Paint.Style.FILL; setColor(C_GATE_ACTIVE_BG) }
        for ((gate, local) in PORTS) {
            val center = centerOfGate(gate) ?: continue
            val g = geom[center] ?: continue
            val px = g.cx + local.first  * g.r * 0.74f
            val py = g.cy + local.second * g.r * 0.74f
            val inP = gate in personalityGates; val inD = gate in designGates
            if (inP || inD) {
                // Green circle background so activated gates stand out
                canvas.drawCircle(px, py, pnt.textSize * 0.65f, circlePnt)
                pnt.color = Color.WHITE
            } else {
                pnt.color = C_NUM_INACTIVE
            }
            pnt.isFakeBoldText = inP || inD
            canvas.drawText("$gate", px, py + pnt.textSize * 0.36f, pnt)
        }
    }

    // ── Human body silhouette (drawn first, behind everything) ─────────────────
    private fun drawHumanSilhouette(canvas: Canvas, W: Float, H: Float) {
        val pnt = mk {
            style = Paint.Style.STROKE; color = Color.parseColor("#C8C8C8")
            strokeWidth = W * 0.007f; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
        }
        val p = Path()
        // Start at the very top-centre (above HEAD center)
        p.moveTo(W * 0.50f, 0f)
        // ── Left side of head ─────────────────────────────────────────────────
        p.cubicTo(W * 0.30f, 0f,        W * 0.28f, H * 0.22f, W * 0.38f, H * 0.24f)
        // ── Left neck → left shoulder ─────────────────────────────────────────
        p.cubicTo(W * 0.28f, H * 0.26f, W * 0.12f, H * 0.28f, W * 0.06f, H * 0.32f)
        // ── Left shoulder → left body (outside Spleen) ───────────────────────
        p.cubicTo(W * 0.02f, H * 0.42f, W * 0.01f, H * 0.55f, W * 0.02f, H * 0.65f)
        // ── Left body at hip level ────────────────────────────────────────────
        p.cubicTo(W * 0.01f, H * 0.72f, W * 0.02f, H * 0.78f, W * 0.06f, H * 0.85f)
        // ── Left lower body → bottom ──────────────────────────────────────────
        p.cubicTo(W * 0.10f, H * 0.92f, W * 0.20f, H * 0.96f, W * 0.30f, H * 0.97f)
        // ── Bottom ───────────────────────────────────────────────────────────
        p.lineTo(W * 0.50f, H * 0.975f)
        p.lineTo(W * 0.70f, H * 0.97f)
        // ── Right lower body ─────────────────────────────────────────────────
        p.cubicTo(W * 0.80f, H * 0.96f, W * 0.90f, H * 0.92f, W * 0.94f, H * 0.85f)
        // ── Right body at hip level ───────────────────────────────────────────
        p.cubicTo(W * 0.98f, H * 0.78f, W * 0.99f, H * 0.72f, W * 0.98f, H * 0.65f)
        // ── Right body → right shoulder ───────────────────────────────────────
        p.cubicTo(W * 0.99f, H * 0.55f, W * 0.98f, H * 0.42f, W * 0.94f, H * 0.32f)
        // ── Right shoulder → right neck ──────────────────────────────────────
        p.cubicTo(W * 0.88f, H * 0.28f, W * 0.72f, H * 0.26f, W * 0.62f, H * 0.24f)
        // ── Right side of head ────────────────────────────────────────────────
        p.cubicTo(W * 0.72f, H * 0.22f, W * 0.70f, 0f,        W * 0.50f, 0f)
        p.close()
        canvas.drawPath(p, pnt)
    }

    private fun shapePath(g: Geom): Path {
        val p = Path(); val r = g.r
        when (g.shape) {
            Shape.SQUARE -> p.addRect(g.cx - r, g.cy - r, g.cx + r, g.cy + r, Path.Direction.CW)
            Shape.DIAMOND -> {
                p.moveTo(g.cx, g.cy - r); p.lineTo(g.cx + r, g.cy)
                p.lineTo(g.cx, g.cy + r); p.lineTo(g.cx - r, g.cy); p.close()
            }
            Shape.TRI_UP -> {
                p.moveTo(g.cx, g.cy - r); p.lineTo(g.cx + r, g.cy + r * 0.8f)
                p.lineTo(g.cx - r, g.cy + r * 0.8f); p.close()
            }
            Shape.TRI_DOWN -> {
                p.moveTo(g.cx, g.cy + r); p.lineTo(g.cx + r, g.cy - r * 0.8f)
                p.lineTo(g.cx - r, g.cy - r * 0.8f); p.close()
            }
            Shape.TRI_LEFT -> {
                p.moveTo(g.cx - r, g.cy); p.lineTo(g.cx + r * 0.8f, g.cy - r)
                p.lineTo(g.cx + r * 0.8f, g.cy + r); p.close()
            }
            Shape.TRI_RIGHT -> {
                p.moveTo(g.cx + r, g.cy); p.lineTo(g.cx - r * 0.8f, g.cy - r)
                p.lineTo(g.cx - r * 0.8f, g.cy + r); p.close()
            }
        }
        return p
    }
}
