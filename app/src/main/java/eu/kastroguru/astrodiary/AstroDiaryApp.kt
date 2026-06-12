package eu.kastroguru.astrodiary

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AstroDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val current = AppCompatDelegate.getApplicationLocales()
        val hasExplicitLocale = !current.isEmpty &&
                current.toLanguageTags().isNotEmpty() &&
                current.toLanguageTags() != "und"

        // Existing users who already have a locale set skip the picker on upgrade.
        if (hasExplicitLocale && !prefs.getBoolean("language_picked", false)) {
            prefs.edit().putBoolean("language_picked", true).apply()
        }
    }
}
