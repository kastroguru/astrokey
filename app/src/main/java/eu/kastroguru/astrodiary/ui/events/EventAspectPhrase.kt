package eu.kastroguru.astrodiary.ui.events

import android.content.Context
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.domain.EventAspects
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.ui.chart.localizedName

/**
 * The diary in words. Every event already knows its tightest aspect ([EventAspects]); the gallery
 * renders it as glyphs, which reads as code to anyone who is not an astrologer. This turns the same
 * fact into a sentence — "Юпитер в тригон с Нептун" — plus one line on what that aspect does.
 */
object EventAspectPhrase {

    fun pointName(ctx: Context, key: String): String = when (key) {
        "asc" -> ctx.getString(R.string.point_asc)
        "mc" -> ctx.getString(R.string.point_mc)
        else -> Planet.values().find { it.key == key }?.localizedName(ctx) ?: key
    }

    fun aspectName(ctx: Context, angle: Int): String = ctx.getString(
        when (angle) {
            0 -> R.string.aspect_conjunction
            60 -> R.string.aspect_sextile
            90 -> R.string.aspect_square
            120 -> R.string.aspect_trine
            150 -> R.string.aspect_quincunx
            else -> R.string.aspect_opposition
        }
    )

    fun meaning(ctx: Context, angle: Int): String = ctx.getString(
        when (angle) {
            0 -> R.string.aspect_meaning_0
            60 -> R.string.aspect_meaning_60
            90 -> R.string.aspect_meaning_90
            120 -> R.string.aspect_meaning_120
            150 -> R.string.aspect_meaning_150
            else -> R.string.aspect_meaning_180
        }
    )

    /** e.g. "Юпитер в тригон с Нептун" / "Jupiter trine Neptune". */
    fun sentence(ctx: Context, aspect: EventAspects.TightAspect): String = ctx.getString(
        R.string.event_sky_line,
        pointName(ctx, aspect.pointA),
        aspectName(ctx, aspect.angle).lowercase(),
        pointName(ctx, aspect.pointB),
    )

    /** "точност 0.4°" — how exact the aspect was, without the word "orb". */
    fun exactness(ctx: Context, aspect: EventAspects.TightAspect): String =
        ctx.getString(R.string.event_sky_exactness, "%.1f".format(aspect.orb))
}
