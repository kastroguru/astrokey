package eu.kastroguru.astrodiary.ui.chart

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ReplacementSpan
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

/*
 * Our sign badges outside a chart canvas — in text, as a view background, as a bitmap. The emoji
 * ♈…♓ are never shown to the user: their colours ignore the element (Pisces comes out red).
 * Everything here draws through ZodiacGlyphs.drawBadge, so a badge looks the same everywhere.
 */

/** Badge diameter as a share of the surrounding text size. */
private const val BADGE_EM = 1.05f
/** Clear space either side of an inline badge, as a share of the text size. */
private const val BADGE_SIDE_EM = 0.06f

/** An inline sign badge, sized to the text it sits in and centred on its middle. */
class SignBadgeSpan(private val sign: ZodiacSign) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm != null) paint.getFontMetricsInt(fm)
        return ceil(paint.textSize * (BADGE_EM + 2 * BADGE_SIDE_EM)).toInt()
    }

    override fun draw(
        canvas: Canvas, text: CharSequence?, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val size = paint.textSize
        val r = size * BADGE_EM / 2
        // Lower-case letters sit around 0.35 em above the baseline; centring there lines the badge
        // up with the words beside it.
        ZodiacGlyphs.drawBadge(canvas, sign, x + size * BADGE_SIDE_EM + r, y - size * 0.35f, r)
    }
}

/** The badge filling its bounds — for a view's background. */
class SignBadgeDrawable(private val sign: ZodiacSign) : Drawable() {
    override fun draw(canvas: Canvas) {
        val b = bounds
        ZodiacGlyphs.drawBadge(canvas, sign, b.exactCenterX(), b.exactCenterY(), min(b.width(), b.height()) / 2f)
    }
    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    @Deprecated("Deprecated in Java")
    override fun getOpacity() = PixelFormat.TRANSLUCENT
}

/** Every ♈…♓ in [text] replaced by our badge; anything else is left as it was. */
fun withSignBadges(text: CharSequence): CharSequence {
    val out = SpannableStringBuilder(text)
    var i = 0
    while (i < out.length) {
        val sign = ZodiacSign.values().firstOrNull { it.symbol[0] == out[i] }
        if (sign == null) { i++; continue }
        // An emoji-presentation selector (U+FE0F) after the sign belongs to it.
        val end = if (i + 1 < out.length && out[i + 1] == '️') i + 2 else i + 1
        out.setSpan(SignBadgeSpan(sign), i, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        i = end
    }
    return out
}

/**
 * [prefix], the badge and [suffix] in one line, rendered to a bitmap — for a home-screen widget,
 * where a RemoteViews text cannot carry a drawing span.
 */
fun signBadgeLineBitmap(prefix: String, sign: ZodiacSign?, suffix: String, textPx: Float, color: Int): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = textPx; this.color = color }
    val badge = if (sign != null) textPx * (BADGE_EM + 2 * BADGE_SIDE_EM) else 0f
    val width = paint.measureText(prefix) + badge + paint.measureText(suffix)
    val fm = paint.fontMetrics
    val height = maxOf(fm.descent - fm.ascent, textPx * BADGE_EM)
    val bmp = Bitmap.createBitmap(ceil(width).toInt().coerceAtLeast(1), ceil(height).roundToInt().coerceAtLeast(1), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val baseline = (height - (fm.descent - fm.ascent)) / 2 - fm.ascent
    var x = 0f
    canvas.drawText(prefix, x, baseline, paint); x += paint.measureText(prefix)
    if (sign != null) {
        val r = textPx * BADGE_EM / 2
        ZodiacGlyphs.drawBadge(canvas, sign, x + textPx * BADGE_SIDE_EM + r, baseline - textPx * 0.35f, r)
        x += badge
    }
    canvas.drawText(suffix, x, baseline, paint)
    return bmp
}
