package eu.kastroguru.astrodiary.ui.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import eu.kastroguru.astrodiary.domain.model.ZodiacSign

/**
 * The traditional astrological zodiac-sign glyphs, drawn as canvas paths (no font/emoji).
 * Shared by every chart so all wheels render the signs identically.
 *
 * Draws [sign] centred at (0,0) with half-size [s]; the caller translates the canvas to the
 * target position first.
 */
object ZodiacGlyphs {

    fun draw(canvas: Canvas, sign: ZodiacSign, s: Float, color: Int = Color.WHITE) {
        val sw = maxOf(3f, s * 0.17f)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color; style = Paint.Style.STROKE; strokeWidth = sw
            strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
        }
        val path = Path()
        val oval = RectF()

        when (sign) {
            // ── Aries ♈ ──
            ZodiacSign.ARIES -> {
                canvas.drawLine(0f, 0f, 0f, s * 0.45f, p)
                oval.set(-s * 0.5f, -s * 0.4f, 0f, s * 0.4f)
                canvas.drawArc(oval, 0f, -180f, false, p)
                oval.set(0f, -s * 0.4f, s * 0.5f, s * 0.4f)
                canvas.drawArc(oval, 180f, 180f, false, p)
            }
            // ── Taurus ♉ ──
            ZodiacSign.TAURUS -> {
                canvas.drawCircle(0f, s * 0.15f, s * 0.4f, p)
                path.moveTo(-s * 0.28f, -s * 0.22f); path.quadTo(-s * 0.55f, -s * 0.15f, -s * 0.45f, -s * 0.5f)
                path.moveTo(s * 0.28f, -s * 0.22f); path.quadTo(s * 0.55f, -s * 0.15f, s * 0.45f, -s * 0.5f)
            }
            // ── Gemini ♊ ──
            ZodiacSign.GEMINI -> {
                canvas.drawLine(-s * 0.22f, -s * 0.45f, -s * 0.22f, s * 0.45f, p)
                canvas.drawLine(s * 0.22f, -s * 0.45f, s * 0.22f, s * 0.45f, p)
                canvas.drawLine(-s * 0.44f, -s * 0.45f, s * 0.44f, -s * 0.45f, p)
                canvas.drawLine(-s * 0.44f, s * 0.45f, s * 0.44f, s * 0.45f, p)
            }
            // ── Cancer ♋ ──
            ZodiacSign.CANCER -> {
                val cr = s * 0.18f; val ar = cr * 2f
                val ucy = -s * 0.26f
                canvas.drawCircle(-cr, ucy, cr, p)
                oval.set(-ar, ucy - ar, ar, ucy + ar)
                canvas.drawArc(oval, 180f, 180f, false, p)
                val lcy = s * 0.26f
                canvas.drawCircle(cr, lcy, cr, p)
                oval.set(-ar, lcy - ar, ar, lcy + ar)
                canvas.drawArc(oval, 180f, -180f, false, p)
            }
            // ── Leo ♌ ──
            ZodiacSign.LEO -> {
                val cr = s * 0.22f
                canvas.drawCircle(-s * 0.06f, s * 0.22f, cr, p)
                path.moveTo(-s * 0.06f + cr, s * 0.22f)
                path.cubicTo(s * 0.52f, -s * 0.62f, s * 0.66f, s * 0.15f, s * 0.44f, s * 0.5f)
            }
            // ── Virgo ♍ ──
            ZodiacSign.VIRGO -> {
                val yT = -s * 0.28f; val yM = -s * 0.7f
                val x1 = -s * 0.42f; val x2 = 0f; val x3 = s * 0.42f
                canvas.drawLine(x1, yT, x1, s * 0.12f, p)
                path.moveTo(x1, yT); path.cubicTo(x1, yM, x2, yM, x2, yT); path.lineTo(x2, s * 0.12f)
                path.moveTo(x2, yT); path.cubicTo(x2, yM, x3, yM, x3, yT); path.lineTo(x3, s * 0.4f)
                path.cubicTo(x3 - s * 0.12f, s * 0.56f, x3 + s * 0.35f, s * 0.56f, x3 + s * 0.35f, s * 0.3f)
                path.cubicTo(x3 + s * 0.35f, s * 0.1f, x3, s * 0.1f, x3, s * 0.12f)
            }
            // ── Libra ♎ ──
            ZodiacSign.LIBRA -> {
                path.moveTo(-s * 0.42f, 0f)
                path.cubicTo(-s * 0.42f, -s * 0.55f, s * 0.42f, -s * 0.55f, s * 0.42f, 0f)
                canvas.drawPath(path, p); path.reset()
                canvas.drawLine(-s * 0.62f, 0f, s * 0.62f, 0f, p)
                canvas.drawLine(-s * 0.58f, s * 0.32f, s * 0.58f, s * 0.32f, p)
            }
            // ── Scorpio ♏ ──
            ZodiacSign.SCORPIO -> {
                val yT = -s * 0.28f; val yM = -s * 0.7f; val yB = s * 0.1f
                val x1 = -s * 0.42f; val x2 = 0f; val x3 = s * 0.42f
                canvas.drawLine(x1, yT, x1, yB, p)
                path.moveTo(x1, yT); path.cubicTo(x1, yM, x2, yM, x2, yT); path.lineTo(x2, yB)
                path.moveTo(x2, yT); path.cubicTo(x2, yM, x3, yM, x3, yT); path.lineTo(x3, s * 0.35f)
                val ae = x3 + s * 0.42f
                path.lineTo(ae, s * 0.35f)
                path.moveTo(ae - s * 0.2f, s * 0.18f)
                path.lineTo(ae, s * 0.35f)
                path.lineTo(ae - s * 0.2f, s * 0.52f)
            }
            // ── Sagittarius ♐ ──
            ZodiacSign.SAGITTARIUS -> {
                canvas.drawLine(-s * 0.38f, s * 0.38f, s * 0.42f, -s * 0.42f, p)
                canvas.drawLine(s * 0.42f, -s * 0.42f, s * 0.15f, -s * 0.42f, p)
                canvas.drawLine(s * 0.42f, -s * 0.42f, s * 0.42f, -s * 0.15f, p)
                canvas.drawLine(-s * 0.18f, -s * 0.18f, s * 0.18f, s * 0.18f, p)
            }
            // ── Capricorn ♑ ──
            ZodiacSign.CAPRICORN -> {
                val yT = -s * 0.28f; val yM = -s * 0.7f
                val x2 = 0f; val x3 = s * 0.42f; val x1 = -s * 0.42f
                path.moveTo(x1, yT); path.cubicTo(x1, yM, x2, yM, x2, yT); path.lineTo(x2, s * 0.12f)
                path.moveTo(x2, yT); path.cubicTo(x2, yM, x3, yM, x3, yT); path.lineTo(x3, s * 0.4f)
                path.cubicTo(x3 - s * 0.12f, s * 0.56f, x3 + s * 0.35f, s * 0.56f, x3 + s * 0.35f, s * 0.3f)
                path.cubicTo(x3 + s * 0.35f, s * 0.1f, x3, s * 0.1f, x3, s * 0.12f)
            }
            // ── Aquarius ♒ ──
            ZodiacSign.AQUARIUS -> {
                for (y in listOf(-s * 0.18f, s * 0.18f)) {
                    path.moveTo(-s * 0.5f, y)
                    path.quadTo(-s * 0.25f, y - s * 0.24f, 0f, y)
                    path.quadTo(s * 0.25f, y + s * 0.24f, s * 0.5f, y)
                }
            }
            // ── Pisces ♓ ──
            ZodiacSign.PISCES -> {
                val rr = s * 0.38f; val xO = s * 0.48f
                oval.set(-xO - rr, -rr, -xO + rr, rr)
                canvas.drawArc(oval, 270f, 180f, false, p)
                oval.set(xO - rr, -rr, xO + rr, rr)
                canvas.drawArc(oval, 270f, -180f, false, p)
                canvas.drawLine(-s * 0.58f, 0f, s * 0.58f, 0f, p)
            }
        }
        canvas.drawPath(path, p)
    }
}
