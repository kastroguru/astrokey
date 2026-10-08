package eu.kastroguru.astrodiary.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * A list row whose front slides left to uncover action buttons behind it, and stays open
 * until it is tapped, slid back, or another row opens.
 *
 * Children, in this order: the action layer (aligned to the end, as wide as its buttons),
 * then the front. A tap on an open front only closes it, so it never opens the row by mistake.
 */
class SwipeRevealLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    /** Called when this row has opened, so the list can close the one that was open before. */
    var onOpened: ((SwipeRevealLayout) -> Unit)? = null

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var downX = 0f
    private var downY = 0f
    private var startTranslation = 0f
    private var dragging = false
    private var closingTap = false

    private val actions: View get() = getChildAt(0)
    private val front: View get() = getChildAt(1)
    private val revealWidth: Float get() = actions.width.toFloat()

    val isOpen: Boolean get() = front.translationX < 0f

    fun open() = settleTo(-revealWidth)

    fun close() = settleTo(0f)

    /** Back to closed without animating — for a row being rebound to another item. */
    fun closeNow() {
        front.animate().cancel()
        front.translationX = 0f
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                down(ev)
                // The front of an open row is not clickable: a tap there closes the row.
                closingTap = isOpen && ev.x < width + front.translationX
                return closingTap
            }
            MotionEvent.ACTION_MOVE -> startDragIfHorizontal(ev)
        }
        return dragging
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> down(ev)
            MotionEvent.ACTION_MOVE -> {
                startDragIfHorizontal(ev)
                if (dragging) {
                    front.translationX = (startTranslation + ev.x - downX).coerceIn(-revealWidth, 0f)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                when {
                    dragging -> settleTo(if (front.translationX < -revealWidth / 2) -revealWidth else 0f)
                    closingTap -> close()
                }
                dragging = false
                closingTap = false
            }
        }
        return true
    }

    private fun down(ev: MotionEvent) {
        downX = ev.x
        downY = ev.y
        front.animate().cancel()
        startTranslation = front.translationX
        dragging = false
    }

    private fun startDragIfHorizontal(ev: MotionEvent) {
        if (dragging) return
        val dx = ev.x - downX
        val dy = ev.y - downY
        if (abs(dx) > touchSlop && abs(dx) > abs(dy)) {
            dragging = true
            parent?.requestDisallowInterceptTouchEvent(true)
        }
    }

    private fun settleTo(target: Float) {
        front.animate().translationX(target).setDuration(180).start()
        if (target < 0f) onOpened?.invoke(this)
    }
}
