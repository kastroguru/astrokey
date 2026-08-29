package eu.kastroguru.astrodiary.ui.events

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

/**
 * Gallery thumbnail for an event. The base is either the uploaded photo (center-cropped) or — when
 * there's no photo — generated art of the event's most-exact aspect. Over the base, a compact bottom
 * band (≤ 1/3 of the cell, drawn on a scrim so it reads over any photo) shows ☉+sun sign, ☽+moon
 * sign, the city and the date-time, all at roughly the same size.
 */
class EventThumbnailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    data class Data(
        val glyphA: String, val colorA: Int,
        val glyphB: String, val colorB: Int,
        val aspectSymbol: String, val aspectColor: Int,
        /** The aspect in words ("тригон"), so the cell reads without knowing the glyphs. */
        val aspectLabel: String,
        val sunSign: String, val sunColor: Int,
        val moonSign: String, val moonColor: Int,
        val city: String, val datetime: String,
    )

    var data: Data? = null
        set(value) { field = value; invalidate() }

    /** Uploaded photo, or null to draw the generated aspect art instead. */
    var bitmap: Bitmap? = null
        set(value) { field = value; invalidate() }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glyphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val scrimPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dst = RectF()

    override fun onDraw(canvas: Canvas) {
        val d = data ?: return
        val w = width.toFloat(); val h = height.toFloat()
        val bmp = bitmap

        if (bmp != null) {
            // Center-crop the photo to fill the cell.
            val scale = max(w / bmp.width, h / bmp.height)
            val dw = bmp.width * scale; val dh = bmp.height * scale
            val left = (w - dw) / 2f; val top = (h - dh) / 2f
            dst.set(left, top, left + dw, top + dh)
            canvas.drawBitmap(bmp, null, dst, paint)
        } else {
            // Generated art: faint wash of the aspect color + the aspect hero in the upper area.
            bgPaint.color = Color.parseColor("#ECEFF6")
            canvas.drawRect(0f, 0f, w, h, bgPaint)
            bgPaint.color = (d.aspectColor and 0x00FFFFFF) or 0x14000000
            canvas.drawRect(0f, 0f, w, h, bgPaint)

            // A side "glyph" can be the word "Asc"/"MC" instead of a single symbol, which is ~3x
            // wider and used to be clipped at the cell edge — shrink the row until both sides fit.
            glyphPaint.textSize = w * 0.26f
            val heroBudget = w * 0.40f
            val widest = max(glyphPaint.measureText(d.glyphA), glyphPaint.measureText(d.glyphB))
            if (widest > heroBudget) glyphPaint.textSize = w * 0.26f * (heroBudget / widest)
            val heroY = h * 0.30f - (glyphPaint.descent() + glyphPaint.ascent()) / 2f
            val gap = w * 0.28f
            glyphPaint.color = d.colorA
            canvas.drawText(d.glyphA, w / 2f - gap, heroY, glyphPaint)
            glyphPaint.color = d.colorB
            canvas.drawText(d.glyphB, w / 2f + gap, heroY, glyphPaint)
            glyphPaint.color = d.aspectColor
            glyphPaint.textSize = w * 0.17f
            canvas.drawText(d.aspectSymbol, w / 2f, h * 0.30f - (glyphPaint.descent() + glyphPaint.ascent()) / 2f, glyphPaint)

            // …and the same thing in words underneath, for everyone who does not read glyphs.
            glyphPaint.textSize = w * 0.10f
            canvas.drawText(ellipsize(d.aspectLabel, glyphPaint, w * 0.92f), w / 2f, h * 0.50f, glyphPaint)
        }

        // ── Bottom info band (≤ 1/3 of the cell), on a dark scrim for legibility over photos ──
        val bandTop = h * 0.67f
        scrimPaint.shader = LinearGradient(0f, bandTop, 0f, h, 0x00000000, 0xCC000000.toInt(), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, bandTop, w, h, scrimPaint)

        val sz = w * 0.105f
        glyphPaint.textSize = sz
        // Signs line: ☉<sign> on the left half, ☽<sign> on the right half.
        glyphPaint.color = d.sunColor
        canvas.drawText("☉ ${d.sunSign}", w * 0.30f, h * 0.78f, glyphPaint)
        glyphPaint.color = d.moonColor
        canvas.drawText("☽ ${d.moonSign}", w * 0.70f, h * 0.78f, glyphPaint)
        // City + date-time, white.
        glyphPaint.color = Color.WHITE
        canvas.drawText(ellipsize(d.city, glyphPaint, w * 0.94f), w / 2f, h * 0.89f, glyphPaint)
        glyphPaint.textSize = w * 0.092f
        canvas.drawText(ellipsize(d.datetime, glyphPaint, w * 0.94f), w / 2f, h * 0.985f, glyphPaint)
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var end = text.length
        while (end > 0 && paint.measureText(text.substring(0, end) + "…") > maxWidth) end--
        return text.substring(0, end) + "…"
    }
}
