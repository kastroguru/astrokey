package eu.kastroguru.astrodiary.ui.help

import eu.kastroguru.astrodiary.R

/**
 * One plain-language explanation per screen, shown by the "?" in the toolbar.
 *
 * Every destination that carries a title must appear here — `ScreenHelpCoverageTest` reads
 * `nav_graph.xml` and fails the build if a titled screen is added without an explanation, so a new
 * screen cannot quietly ship with a question mark that has nothing behind it (or none at all).
 */
object ScreenHelp {

    val entries: Map<Int, Int> = mapOf(
        R.id.birthDataListFragment to R.string.help_birth_data_list,
        R.id.birthDataFormFragment to R.string.help_birth_data_form,
        R.id.birthDataDetailFragment to R.string.help_birth_data_detail,
        R.id.chartFragment to R.string.help_chart,
        R.id.chartReadingFragment to R.string.help_chart_reading,
        R.id.planetTableFragment to R.string.help_planet_table,
        R.id.eventListFragment to R.string.help_event_list,
        R.id.eventFormFragment to R.string.help_event_form,
        R.id.eventDetailFragment to R.string.help_event_detail,
        R.id.eventChartFragment to R.string.help_chart,
        R.id.eventPlanetTableFragment to R.string.help_planet_table,
        R.id.nowFragment to R.string.help_now,
        R.id.transitFragment to R.string.help_transits,
        R.id.transitReadingFragment to R.string.help_transit_reading,
        R.id.transitAspectDetailFragment to R.string.help_aspect_detail,
        R.id.humanDesignFragment to R.string.help_human_design,
        R.id.settingsFragment to R.string.help_settings,
    )

    /** The explanation for a destination, or null for screens that carry no title (and no "?"). */
    fun forDestination(destinationId: Int): Int? = entries[destinationId]
}
