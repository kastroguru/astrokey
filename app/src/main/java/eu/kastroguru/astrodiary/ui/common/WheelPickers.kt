package eu.kastroguru.astrodiary.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.TimePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.kastroguru.astrodiary.R
import java.util.Calendar

/**
 * Date and time as wheels — day / month / year, hour / minute — for the forms whose date lies in
 * the past and is already known: a birth, an event. The platform calendar turned a year decades
 * back into a scroll through a list of years one per row; on a wheel it is one fling, and tapping
 * the centre number lets it be typed. Wheel order and month names follow the app's locale.
 *
 * "Now" and the transit screens keep the calendar: their dates sit close to today.
 */
object WheelPickers {

    /** Earliest year offered: the start of the main ephemeris files (sepl_18 / semo_18). */
    private const val MIN_YEAR = 1800

    /** [month] is 1..12 both ways. */
    fun pickDate(
        context: Context, year: Int, month: Int, day: Int,
        onPicked: (year: Int, month: Int, day: Int) -> Unit,
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_date_wheels, null)
        val picker = view.findViewById<DatePicker>(R.id.datePicker)
        // A stored date older than the floor lowers it, so merely opening the dialog never
        // clamps — and silently changes — a chart imported from elsewhere.
        val shown = Calendar.getInstance().apply { clear(); set(year, month - 1, day) }
        val floor = Calendar.getInstance().apply { clear(); set(MIN_YEAR, Calendar.JANUARY, 1) }
        picker.minDate = minOf(shown.timeInMillis, floor.timeInMillis)
        picker.updateDate(year, month - 1, day)

        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ ->
                picker.clearFocus()   // commits a value typed into a wheel
                onPicked(picker.year, picker.month + 1, picker.dayOfMonth)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun pickTime(
        context: Context, hour: Int, minute: Int,
        onPicked: (hour: Int, minute: Int) -> Unit,
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_time_wheels, null)
        val picker = view.findViewById<TimePicker>(R.id.timePicker)
        picker.setIs24HourView(true)
        picker.hourCompat = hour
        picker.minuteCompat = minute

        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ ->
                picker.clearFocus()
                onPicked(picker.hourCompat, picker.minuteCompat)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    // TimePicker.hour / .minute arrived in API 23; minSdk is 21.
    @Suppress("DEPRECATION")
    private var TimePicker.hourCompat: Int
        get() = currentHour
        set(value) { currentHour = value }

    @Suppress("DEPRECATION")
    private var TimePicker.minuteCompat: Int
        get() = currentMinute
        set(value) { currentMinute = value }
}
