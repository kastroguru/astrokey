package eu.kastroguru.astrodiary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartDisplayPrefs @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences("chart_display_settings", Context.MODE_PRIVATE)

    /** One of "P", "W", "K", "E", "R", "O" — Placidus is the default. */
    var houseSystem: String
        get() = prefs.getString("house_system", "P") ?: "P"
        set(v) { prefs.edit().putString("house_system", v).apply() }

    val houseSystemChar: Char get() = houseSystem.first()

    var showDignities: Boolean
        get() = prefs.getBoolean("show_dignities", true)
        set(v) { prefs.edit().putBoolean("show_dignities", v).apply() }

    var showAspectGrid: Boolean
        get() = prefs.getBoolean("show_aspect_grid", true)
        set(v) { prefs.edit().putBoolean("show_aspect_grid", v).apply() }

    var showPartOfFortune: Boolean
        get() = prefs.getBoolean("show_part_of_fortune", true)
        set(v) { prefs.edit().putBoolean("show_part_of_fortune", v).apply() }
}
