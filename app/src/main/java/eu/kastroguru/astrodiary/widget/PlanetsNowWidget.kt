package eu.kastroguru.astrodiary.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import eu.kastroguru.astrodiary.MainActivity
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
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

                fun planetText(key: String, glyph: String): String {
                    val pos = astro.planets[key] ?: return "$glyph –"
                    val sign = try { ZodiacSign.fromId(pos.sign) } catch (e: Exception) { return "$glyph –" }
                    return "$glyph${sign.symbol}${pos.degreeInSign}°"
                }

                views.setTextViewText(R.id.widget_sun,     planetText("sun",     "☉"))
                views.setTextViewText(R.id.widget_moon,    planetText("moon",    "☽"))
                views.setTextViewText(R.id.widget_mercury, planetText("mercury", "☿"))
                views.setTextViewText(R.id.widget_venus,   planetText("venus",   "♀"))
                views.setTextViewText(R.id.widget_mars,    planetText("mars",    "♂"))
                views.setTextViewText(R.id.widget_jupiter, planetText("jupiter", "♃"))
                views.setTextViewText(R.id.widget_saturn,  planetText("saturn",  "♄"))

                // ASC from cusps (index 0)
                val ascDeg = astro.cusps.getOrElse(0) { 0.0 }
                val ascSign = try { ZodiacSign.fromId((ascDeg / 30).toInt() + 1) } catch (e: Exception) { null }
                views.setTextViewText(R.id.widget_asc, "↑${ascSign?.symbol ?: ""}${(ascDeg % 30).roundToInt()}°")

                // Time label
                val h = cal.get(Calendar.HOUR_OF_DAY)
                val m = cal.get(Calendar.MINUTE)
                views.setTextViewText(R.id.widget_time, "%02d:%02d UTC".format(h, m))

            } catch (e: Exception) {
                views.setTextViewText(R.id.widget_sun, "Error")
            }

            manager.updateAppWidget(widgetId, views)
        }
    }
}
