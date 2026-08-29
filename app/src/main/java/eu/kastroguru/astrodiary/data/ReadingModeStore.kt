package eu.kastroguru.astrodiary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * How much astrology the person in front of the app wants to see.
 *
 * Not two apps and not a stripped-down version: the same data, with a different first layer. In
 * [ReadingMode.PLAIN] a screen opens on what the placement means in ordinary words and the chart is
 * one tap away; in [ReadingMode.ASTROLOGER] it opens on the chart and the same readings are one tap
 * away. Nothing is removed in either direction.
 */
enum class ReadingMode { PLAIN, ASTROLOGER }

@Singleton
class ReadingModeStore @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _mode = MutableStateFlow(
        if (prefs.getString(KEY, null) == ReadingMode.ASTROLOGER.name) ReadingMode.ASTROLOGER
        else ReadingMode.PLAIN
    )
    val mode: StateFlow<ReadingMode> = _mode.asStateFlow()

    /** False until the question has been answered once, which is what the onboarding screen checks. */
    val isChosen: Boolean get() = prefs.getString(KEY, null) != null

    val current: ReadingMode get() = _mode.value

    fun choose(mode: ReadingMode) {
        prefs.edit().putString(KEY, mode.name).apply()
        _mode.value = mode
    }

    private companion object { const val KEY = "reading_mode" }
}
