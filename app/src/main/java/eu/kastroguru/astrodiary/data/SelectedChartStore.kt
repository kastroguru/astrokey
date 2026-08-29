package eu.kastroguru.astrodiary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The one natal chart the whole app is currently "on".
 *
 * Transits and Human Design used to remember their own person, each in its own SharedPreferences
 * file ("transit_prefs"/last_natal_id and "human_design_prefs"/last_hd_id), and the gallery filter
 * was a third, independent state — so picking a person on one screen left the others on the
 * previous chart. Every screen with a person selector now reads and writes this single store.
 */
@Singleton
class SelectedChartStore @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences("selected_chart", Context.MODE_PRIVATE)

    private val _selectedId = MutableStateFlow(restore(context))

    /** Id of the app-wide selected natal chart, or null while nothing has been chosen yet. */
    val selectedId: StateFlow<Long?> = _selectedId.asStateFlow()

    fun select(id: Long) {
        if (_selectedId.value == id) return
        prefs.edit().putLong(KEY, id).apply()
        _selectedId.value = id
    }

    /** Called when the selected chart is gone (deleted) so screens can fall back to the first one. */
    fun clear() {
        prefs.edit().remove(KEY).apply()
        _selectedId.value = null
    }

    private fun restore(context: Context): Long? {
        prefs.getLong(KEY, NONE).let { if (it != NONE) return it }
        // First run after the update: adopt whichever per-screen choice existed, so nobody's
        // selection is reset by the migration.
        for ((file, key) in LEGACY) {
            val old = context.getSharedPreferences(file, Context.MODE_PRIVATE).getLong(key, NONE)
            if (old != NONE) {
                prefs.edit().putLong(KEY, old).apply()
                return old
            }
        }
        return null
    }

    private companion object {
        const val KEY = "selected_birth_id"
        const val NONE = -1L
        val LEGACY = listOf(
            "transit_prefs" to "last_natal_id",
            "human_design_prefs" to "last_hd_id",
        )
    }
}
