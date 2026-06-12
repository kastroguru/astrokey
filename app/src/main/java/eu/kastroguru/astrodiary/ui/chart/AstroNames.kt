package eu.kastroguru.astrodiary.ui.chart

import android.content.Context
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign

fun Planet.localizedName(ctx: Context): String = ctx.getString(when (this) {
    Planet.SUN      -> R.string.planet_sun
    Planet.MOON     -> R.string.planet_moon
    Planet.MERCURY  -> R.string.planet_mercury
    Planet.VENUS    -> R.string.planet_venus
    Planet.MARS     -> R.string.planet_mars
    Planet.JUPITER  -> R.string.planet_jupiter
    Planet.SATURN   -> R.string.planet_saturn
    Planet.URANUS   -> R.string.planet_uranus
    Planet.NEPTUNE  -> R.string.planet_neptune
    Planet.PLUTO    -> R.string.planet_pluto
    Planet.CHIRON   -> R.string.planet_chiron
    Planet.RAHU     -> R.string.planet_rahu
    Planet.LILITH   -> R.string.planet_lilith
})

fun ZodiacSign.localizedName(ctx: Context): String = ctx.getString(when (this) {
    ZodiacSign.ARIES       -> R.string.sign_aries
    ZodiacSign.TAURUS      -> R.string.sign_taurus
    ZodiacSign.GEMINI      -> R.string.sign_gemini
    ZodiacSign.CANCER      -> R.string.sign_cancer
    ZodiacSign.LEO         -> R.string.sign_leo
    ZodiacSign.VIRGO       -> R.string.sign_virgo
    ZodiacSign.LIBRA       -> R.string.sign_libra
    ZodiacSign.SCORPIO     -> R.string.sign_scorpio
    ZodiacSign.SAGITTARIUS -> R.string.sign_sagittarius
    ZodiacSign.CAPRICORN   -> R.string.sign_capricorn
    ZodiacSign.AQUARIUS    -> R.string.sign_aquarius
    ZodiacSign.PISCES      -> R.string.sign_pisces
})
