package eu.kastroguru.astrodiary.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.widget.RemoteViews
import eu.kastroguru.astrodiary.MainActivity
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.chart.signBadgeLineBitmap
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.roundToInt

class PlanetsNowWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { widgetId ->
            updateWidget(context, manager, widgetId)
        }
    }

    companion object {
        fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_planets_now)

            // Launch app on tap
            val tapIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(android.R.id.background, pendingIntent)

            // Calculate current positions
            try {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH) + 1
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val hour = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0

                // London as default location for "no ascendant" display
                val astro = AstroCalculator(context.applicationContext).calculate(year, month, day, hour, 51.5, -0.1)

                // Each cell is a bitmap: a widget's text cannot carry our sign badge, and the emoji
                // signs are never shown (their colours ignore the element).
                val textPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, context.resources.displayMetrics)
                fun cell(id: Int, glyph: String, sign: ZodiacSign?, suffix: String, color: String) =
                    views.setImageViewBitmap(id, signBadgeLineBitmap(glyph, sign, suffix, textPx, Color.parseColor(color)))
                fun planetCell(id: Int, key: String, glyph: String, color: String) {
                    val pos = astro.planets[key]
                    val sign = pos?.let { try { ZodiacSign.fromId(it.sign) } catch (e: Exception) { null } }
                    if (pos == null || sign == null) cell(id, "$glyph –", null, "", color)
                    else cell(id, glyph, sign, "${pos.degreeInSign}°", color)
                }

                planetCell(R.id.widget_sun,     "sun",     "☉", "#FFD700")
                planetCell(R.id.widget_moon,    "moon",    "☽", "#BBDDFF")
                planetCell(R.id.widget_mercury, "mercury", "☿", "#CCCCCC")
                planetCell(R.id.widget_venus,   "venus",   "♀", "#FF9EAD")
                planetCell(R.id.widget_mars,    "mars",    "♂", "#E53935")
                planetCell(R.id.widget_jupiter, "jupiter", "♃", "#FF9800")
                planetCell(R.id.widget_saturn,  "saturn",  "♄", "#9090A0")

                // ASC from cusps (index 0)
                val ascDeg = astro.cusps.getOrElse(0) { 0.0 }
                val ascSign = try { ZodiacSign.fromId((ascDeg / 30).toInt() + 1) } catch (e: Exception) { null }
                cell(R.id.widget_asc, "↑", ascSign, "${(ascDeg % 30).roundToInt()}°", "#4CAF50")

                // Time label
                val h = cal.get(Calendar.HOUR_OF_DAY)
                val m = cal.get(Calendar.MINUTE)
                views.setTextViewText(R.id.widget_time, "%02d:%02d UTC".format(h, m))

            } catch (e: Exception) {
                views.setTextViewText(R.id.widget_time, "Error")
            }

            manager.updateAppWidget(widgetId, views)
        }
    }
}
