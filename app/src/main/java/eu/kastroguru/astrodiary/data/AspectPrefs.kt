package eu.kastroguru.astrodiary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistent settings controlling which bodies are included in aspect calculations.
 * Defaults: all bodies ON; house cusps (ASC/DSC/MC/IC) OFF.
 */
@Singleton
class AspectPrefs @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences("aspect_settings", Context.MODE_PRIVATE)

    var includeChiron: Boolean
        get() = prefs.getBoolean("chiron", true)
        set(v) { prefs.edit().putBoolean("chiron", v).apply() }

    var includeLilith: Boolean
        get() = prefs.getBoolean("lilith", true)
        set(v) { prefs.edit().putBoolean("lilith", v).apply() }

    var includeRahu: Boolean
        get() = prefs.getBoolean("rahu", true)
        set(v) { prefs.edit().putBoolean("rahu", v).apply() }

    var includeAsc: Boolean
        get() = prefs.getBoolean("asc", false)
        set(v) { prefs.edit().putBoolean("asc", v).apply() }

    var includeDsc: Boolean
        get() = prefs.getBoolean("dsc", false)
        set(v) { prefs.edit().putBoolean("dsc", v).apply() }

    var includeMc: Boolean
        get() = prefs.getBoolean("mc", false)
        set(v) { prefs.edit().putBoolean("mc", v).apply() }

    var includeIc: Boolean
        get() = prefs.getBoolean("ic", false)
        set(v) { prefs.edit().putBoolean("ic", v).apply() }

    /** When true, Sun/Moon/Mercury/Venus/Mars are excluded from the transit row only. */
    var hidePersonalTransits: Boolean
        get() = prefs.getBoolean("hide_personal_transits", false)
        set(v) { prefs.edit().putBoolean("hide_personal_transits", v).apply() }
}
