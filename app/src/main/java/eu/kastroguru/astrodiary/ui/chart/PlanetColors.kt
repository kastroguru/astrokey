package eu.kastroguru.astrodiary.ui.chart

import android.graphics.Color

/**
 * Per-planet colors for the primary-directions wheel (directed points & lines).
 * Chosen by the user; tuned slightly for visibility on the light surface.
 * (Natal planets keep their element colors, as in the other charts.)
 */
object PlanetColors {
    private val map = mapOf(
        "sun"     to "#E0A800", // yellow/gold (pure yellow is invisible on the light background)
        "moon"    to "#2E7D32", // green
        "mercury" to "#1565C0", // blue
        "venus"   to "#EF6C00", // orange
        "mars"    to "#D32F2F", // red
        "jupiter" to "#8B0000", // dark red
        "saturn"  to "#616161", // gray
        "uranus"  to "#29B6F6", // light blue
        "neptune" to "#1A237E", // dark blue
        "pluto"   to "#000000", // black
        "chiron"  to "#00897B", // teal (my pick)
        "rahu"    to "#7E57C2", // violet (my pick)
        "lilith"  to "#AD1457", // dark magenta (my pick)
        // angles
        "asc"     to "#455A64",
        "desc"    to "#455A64",
        "mc"      to "#455A64",
        "ic"      to "#455A64",
    )

    fun of(key: String): Int = Color.parseColor(map[key] ?: "#444444")
}
