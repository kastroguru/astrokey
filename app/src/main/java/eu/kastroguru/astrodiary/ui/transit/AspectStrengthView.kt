package eu.kastroguru.astrodiary.ui.transit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.domain.calculator.TransitTimeline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The aspect's strength over time: 1.0 where it is exact, 0.0 where it is out of orb.
 *
 * The dates alone do not show the shape of a transit. A slow planet can leave orb and come back
 * months later when it turns retrograde over the same degree — as separate humps on this curve,
 * which is the thing that is hard to picture from three dates in a row.
 */
class AspectStrengthView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    data class Data(
        val curve: List<TransitTimeline.Sample>,
        val exactMs: List<Long>,
        val nowMs: Long,
    )

    var data: Data? = null
        set(value) { field = value; invalidate() }

    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = dp(2.5f); strokeCap = Paint.Cap.ROUND
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val grid = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = dp(1f) }
    private val marker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = dp(1.5f)
        pathEffect = DashPathEffect(floatArrayOf(dp(4f), dp(4f)), 0f)
    }
    private val dot = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = dp(11f) }

    private val dayFmt = SimpleDateFormat("d MMM", Locale.getDefault())
    private val monthFmt = SimpleDateFormat("LLL yy", Locale.getDefault())
    private val path = Path()

    private fun dp(v: Float) = v * resources.displayMetrics.density

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, dp(150f).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        val d = data ?: return
        if (d.curve.size < 2) return

        val accent = context.getColor(R.color.gold)
        val secondary = context.getColor(R.color.text_secondary)
        val stroke = context.getColor(R.color.card_stroke)

        val padL = dp(26f); val padR = dp(10f); val padT = dp(10f); val padB = dp(26f)
        val w = width.toFloat(); val h = height.toFloat()
        val plotW = w - padL - padR; val plotH = h - padT - padB

        // Zoom to where anything actually happens: a Pluto scan spans years but the aspect may be
        // in orb for two months of it, and a flat line either side says nothing.
        val active = d.curve.filter { it.strength > 0.0 }
        val fromMs: Long
        val toMs: Long
        if (active.isEmpty()) {
            fromMs = d.curve.first().ms; toMs = d.curve.last().ms
        } else {
            val span = (active.last().ms - active.first().ms).coerceAtLeast(86_400_000L)
            val pad = (span * 0.25).toLong()
            fromMs = maxOf(d.curve.first().ms, active.first().ms - pad)
            toMs = minOf(d.curve.last().ms, active.last().ms + pad)
        }
        val range = (toMs - fromMs).toFloat().coerceAtLeast(1f)
        fun x(ms: Long) = padL + plotW * ((ms - fromMs).toFloat() / range)
        fun y(strength: Double) = padT + plotH * (1f - strength.toFloat())

        // Grid: 0, ½ and 1 — "1" is exact, "0" is out of range.
        grid.color = stroke
        label.color = secondary
        for ((value, text) in listOf(1.0 to "1", 0.5 to "", 0.0 to "0")) {
            val yy = y(value)
            canvas.drawLine(padL, yy, w - padR, yy, grid)
            if (text.isNotEmpty()) canvas.drawText(text, dp(6f), yy + dp(4f), label)
        }

        // The curve, filled underneath so the active stretches read as blocks of time.
        path.reset()
        path.moveTo(x(d.curve.first().ms), y(0.0))
        d.curve.filter { it.ms in fromMs..toMs }.forEach { path.lineTo(x(it.ms), y(it.strength)) }
        path.lineTo(x(d.curve.last().ms.coerceAtMost(toMs)), y(0.0))
        path.close()
        fill.color = (accent and 0x00FFFFFF) or 0x33000000
        canvas.drawPath(path, fill)
        line.color = accent
        canvas.drawPath(path, line)

        // "Now", and every moment the aspect is exact.
        marker.color = secondary
        val nowX = x(d.nowMs)
        if (d.nowMs in fromMs..toMs) {
            canvas.drawLine(nowX, padT, nowX, padT + plotH, marker)
            label.color = secondary
            canvas.drawText(context.getString(R.string.aspect_graph_now), nowX + dp(3f), padT + dp(10f), label)
        }
        dot.color = accent
        d.exactMs.filter { it in fromMs..toMs }.forEach { hit ->
            canvas.drawCircle(x(hit), y(1.0), dp(4f), dot)
        }

        // Dates along the bottom: the window edges plus each exact hit, dropped when they would
        // run into the label before them — three dates that overlap read as one long word.
        label.color = secondary
        val spanDays = (toMs - fromMs) / 86_400_000.0
        val fmt = if (spanDays > 200) monthFmt else dayFmt
        val gap = dp(8f)
        val baseline = h - dp(8f)

        val first = fmt.format(Date(fromMs))
        canvas.drawText(first, padL, baseline, label)
        var lastRight = padL + label.measureText(first) + gap

        for (hit in d.exactMs.filter { it in fromMs..toMs }.sorted()) {
            val text = fmt.format(Date(hit))
            val tw = label.measureText(text)
            val tx = (x(hit) - tw / 2).coerceIn(padL, w - padR - tw)
            if (tx < lastRight) continue
            label.color = accent
            canvas.drawText(text, tx, baseline, label)
            label.color = secondary
            lastRight = tx + tw + gap
        }

        val last = fmt.format(Date(toMs))
        val lastX = w - padR - label.measureText(last)
        if (lastX >= lastRight) canvas.drawText(last, lastX, baseline, label)
    }
}
