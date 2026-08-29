package eu.kastroguru.astrodiary.domain.calculator

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import de.thmac.swisseph.SweConst
import de.thmac.swisseph.SweDate
import de.thmac.swisseph.SwissEph
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.PlanetPosition
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class AstroCalculator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val TAG = "AstroCalculator"

    // Swiss Ephemeris file-based calculation (accurate, covers 1200–2400 CE).
    // Ephemeris files bundled in assets/ephe/ are extracted to filesDir/ephe/ on first launch.
    // Falls back to Moshier (built-in) for dates outside the bundled file range.
    // Chiron is now accurate from the sepl files; Keplerian fallback retained only for
    // dates outside 1200–2400 where even Moshier doesn't have Chiron data.
    private val swe = SwissEph()

    init {
        try {
            val epheDir = extractEphemerisFiles()
            swe.swe_set_ephe_path(epheDir)
            Log.i(TAG, "Swiss Ephemeris initialised from $epheDir")
        } catch (e: Exception) {
            Log.e(TAG, "Ephemeris init failed, falling back to Moshier: ${e.message}")
        }
    }

    private val PLANET_IDS = listOf(
        "sun"     to SweConst.SE_SUN,
        "moon"    to SweConst.SE_MOON,
        "mercury" to SweConst.SE_MERCURY,
        "venus"   to SweConst.SE_VENUS,
        "mars"    to SweConst.SE_MARS,
        "jupiter" to SweConst.SE_JUPITER,
        "saturn"  to SweConst.SE_SATURN,
        "uranus"  to SweConst.SE_URANUS,
        "neptune" to SweConst.SE_NEPTUNE,
        "pluto"   to SweConst.SE_PLUTO,
        "chiron"  to SweConst.SE_CHIRON,
        "rahu"    to SweConst.SE_TRUE_NODE,
        "lilith"  to SweConst.SE_MEAN_APOG
    )

    fun calculate(
        year: Int, month: Int, day: Int,
        hourUtc: Double,
        latitude: Double, longitude: Double,
        houseSystem: Char = 'P'
    ): AstroData {
        val jd = julianDay(year, month, day, hourUtc)
        val T  = (jd - 2451545.0) / 36525.0
        Log.d(TAG, "JD=$jd  T=$T  lat=$latitude  lon=$longitude")

        val xx  = DoubleArray(6)
        val err = StringBuffer()
        val planets = mutableMapOf<String, PlanetPosition>()

        for ((key, id) in PLANET_IDS) {
            try {
                // Primary: file-based Swiss Ephemeris (accurate, 1200–2400 CE)
                val flags = SweConst.SEFLG_SWIEPH or SweConst.SEFLG_SPEED
                var rc = swe.swe_calc_ut(jd, id, flags, xx, err)
                if (rc < 0) {
                    // Outside bundled file range → retry with Moshier built-in
                    Log.w(TAG, "  $key SEFLG_SWIEPH rc=$rc → Moshier retry")
                    rc = swe.swe_calc_ut(jd, id, SweConst.SEFLG_MOSEPH or SweConst.SEFLG_SPEED, xx, err)
                }
                if (rc >= 0) {
                    planets[key] = toPlanetPosition(xx[0], xx[3])
                    Log.d(TAG, "  $key = ${xx[0]}°  speed=${xx[3]}")
                } else {
                    Log.w(TAG, "  $key Moshier rc=$rc → Keplerian fallback")
                    planets[key] = fallback(key, T)
                }
            } catch (e: Exception) {
                Log.e(TAG, "  $key EXC ${e.javaClass.simpleName}: ${e.message} → fallback")
                planets[key] = fallback(key, T)
            }
        }

        val cuspArr = DoubleArray(13)
        val ascmc   = DoubleArray(10)
        swe.swe_houses(jd, 0, latitude, longitude, houseSystem.code, cuspArr, ascmc)
        val cusps = (1..12).map { cuspArr[it] }
        Log.d(TAG, "  ASC=${cuspArr[1]}°  MC=${cuspArr[10]}°")

        val obliquity = 23.4393 - 0.013004 * T

        return AstroData(
            julianDay  = jd,
            obliquity  = obliquity,
            planets    = planets.mapValues { (_, p) -> p.copy(house = planetHouse(p.absoluteDegree, cusps)) },
            cusps      = cusps
        )
    }

    /**
     * Computes the equatorial-frame data a primary-directions calculation needs for a stored chart:
     * the right ascension of the MC (ARMC), the true obliquity, the geographic latitude, and the
     * REAL right ascension / declination of every planet (with ecliptic latitude included).
     *
     * Uses the chart's UTC birth moment. House system is irrelevant here — ARMC depends only on
     * sidereal time + longitude — so Placidus is used arbitrarily.
     */
    fun computeEquatorial(
        yearUtc: Int, monthUtc: Int, dayUtc: Int, hourUtc: Int, minutesUtc: Int,
        latitude: Double, longitude: Double,
    ): PrimaryDirectionsCalculator.EquatorialChart {
        val jd = julianDay(yearUtc, monthUtc, dayUtc, hourUtc + minutesUtc / 60.0)

        // True obliquity of the ecliptic (SE_ECL_NUT returns it in xx[0]).
        val xx  = DoubleArray(6)
        val err = StringBuffer()
        val obliquity = run {
            val rc = swe.swe_calc_ut(jd, SweConst.SE_ECL_NUT, SweConst.SEFLG_SWIEPH, xx, err)
            if (rc >= 0) xx[0] else 23.4393 - 0.013004 * ((jd - 2451545.0) / 36525.0)
        }

        val planets = mutableMapOf<String, PrimaryDirectionsCalculator.Equatorial>()
        for ((key, id) in PLANET_IDS) {
            try {
                val flags = SweConst.SEFLG_SWIEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED
                var rc = swe.swe_calc_ut(jd, id, flags, xx, err)
                if (rc < 0) {
                    rc = swe.swe_calc_ut(jd, id,
                        SweConst.SEFLG_MOSEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED, xx, err)
                }
                if (rc >= 0) planets[key] = PrimaryDirectionsCalculator.Equatorial(ra = xx[0], decl = xx[1])
                else Log.w(TAG, "  equ $key rc=$rc → skipped (no RA/decl)")
            } catch (e: Exception) {
                Log.e(TAG, "  equ $key EXC ${e.message} → skipped")
            }
        }

        val cuspArr = DoubleArray(13)
        val ascmc   = DoubleArray(10)
        swe.swe_houses(jd, 0, latitude, longitude, 'P'.code, cuspArr, ascmc)
        val armc = ascmc[2]   // right ascension of the MC

        return PrimaryDirectionsCalculator.EquatorialChart(
            armc = armc, obliquity = obliquity, geoLat = latitude, planets = planets,
        )
    }

    /**
     * **True Solar Equatorial Arc** key for primary directions.
     *
     * Converts an arc of direction ([arcDeg], in degrees of right ascension) into age in years by
     * the day-for-a-year measure using the Sun's REAL motion: the number of (fractional) days the
     * true Sun takes to advance [arcDeg] degrees in RA from its birth position. Because the Sun's
     * RA speed varies through the year (~0.95–1.02°/day), this is non-linear and chart-specific —
     * the astronomically rigorous "true measure of time" (vs Naibod's fixed mean rate).
     *
     * Faithful to Morinus's calcTrueSolarArc (forward direction; useregressive=False, the default).
     */
    fun trueSolarArcYears(birthJd: Double, arcDeg: Double): Double {
        if (arcDeg <= 0.0) return 0.0
        val xx = DoubleArray(6)
        val err = StringBuffer()
        fun sunRa(jd: Double): Pair<Double, Double> {
            val flags = SweConst.SEFLG_SWIEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED
            var rc = swe.swe_calc_ut(jd, SweConst.SE_SUN, flags, xx, err)
            if (rc < 0) rc = swe.swe_calc_ut(jd, SweConst.SE_SUN,
                SweConst.SEFLG_MOSEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED, xx, err)
            return xx[0] to xx[3]   // RA, RA-speed (deg/day)
        }
        val ra0 = sunRa(birthJd).first
        var t = arcDeg / 0.98565   // Naibod-rate days, a close initial guess
        repeat(10) {
            val (raT, speed) = sunRa(birthJd + t)
            var adv = (raT - ra0) % 360.0
            if (adv < 0) adv += 360.0           // RA advanced since birth (arc ≤ 180 ⇒ no full-turn ambiguity)
            val rate = if (speed > 0.05) speed else 0.9856
            val next = t - (adv - arcDeg) / rate // Newton step; f is near-linear so it converges in a few iters
            t = if (next < 0) 0.0 else next
        }
        return t   // day-count == age in years (day-for-a-year)
    }

    /** The Sun's right ascension (degrees) at a Julian day (file Swiss Eph, Moshier fallback). */
    private fun sunRa(jd: Double): Double {
        val xx = DoubleArray(6); val err = StringBuffer()
        val rc = swe.swe_calc_ut(jd, SweConst.SE_SUN, SweConst.SEFLG_SWIEPH or SweConst.SEFLG_EQUATORIAL, xx, err)
        if (rc < 0) swe.swe_calc_ut(jd, SweConst.SE_SUN, SweConst.SEFLG_MOSEPH or SweConst.SEFLG_EQUATORIAL, xx, err)
        return xx[0]
    }

    /**
     * Builds a fast arc→age converter for the True Solar Equatorial Arc, sampling the Sun's RA once
     * per day up to [maxYears] (≈120 Swiss-Eph calls total) and interpolating. Equivalent to calling
     * [trueSolarArcYears] per arc, but ~thousands of times cheaper — essential when converting many
     * directions at once (otherwise the per-direction Newton solver causes an ANR).
     */
    fun trueSolarArcConverter(birthJd: Double, maxYears: Int = 120): (Double) -> Double {
        val ra0 = sunRa(birthJd)
        val n = maxYears + 2
        val arcs = DoubleArray(n)   // cumulative RA advance at day i (monotonic — Sun's RA always increases)
        for (i in 0 until n) {
            var d = (sunRa(birthJd + i) - ra0) % 360.0
            if (d < 0) d += 360.0
            arcs[i] = d
        }
        return fn@{ arc ->
            if (arc <= 0.0) return@fn 0.0
            var i = 0
            while (i < n - 1 && arcs[i + 1] < arc) i++
            if (i >= n - 1) return@fn (n - 1).toDouble()
            val span = arcs[i + 1] - arcs[i]
            i + if (span > 1e-9) (arc - arcs[i]) / span else 0.0
        }
    }

    /**
     * The arc of direction (degrees of RA) reached at a given [ageYears], under the True Solar
     * Equatorial Arc — the inverse of [trueSolarArcYears]. By the day-for-a-year measure this is
     * simply the Sun's RA advance over [ageYears] DAYS after birth. Drives where the directed
     * points sit on the wheel for the selected date.
     */
    fun directedArc(birthJd: Double, ageYears: Double): Double {
        if (ageYears <= 0.0) return 0.0
        var d = (sunRa(birthJd + ageYears) - sunRa(birthJd)) % 360.0
        if (d < 0) d += 360.0
        return d
    }

    // ── Ephemeris file extraction ─────────────────────────────────────────────

    // Copies bundled .se1 files from assets/ephe/ to filesDir/ephe/ once per version.
    // Returns the directory path for swe_set_ephe_path().
    private fun extractEphemerisFiles(): String {
        val dir = File(context.filesDir, "ephe")
        dir.mkdirs()
        val prefs = context.getSharedPreferences("ephe_prefs", Context.MODE_PRIVATE)
        val currentVersion = 1  // bump here when adding new .se1 files to assets/ephe/
        if (prefs.getInt("ephe_version", 0) < currentVersion) {
            val files = listOf("sepl_12.se1", "sepl_18.se1", "semo_12.se1", "semo_18.se1")
            for (name in files) {
                try {
                    context.assets.open("ephe/$name").use { inp ->
                        File(dir, name).outputStream().use { out -> inp.copyTo(out) }
                    }
                    Log.d(TAG, "Extracted $name")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to extract $name: ${e.message}")
                }
            }
            prefs.edit { putInt("ephe_version", currentVersion) }
        }
        return dir.absolutePath
    }

    // ── Fallback: Keplerian mechanics for bodies outside Swiss Eph range ──────
    // Chiron reaches this only for dates before 1200 CE or after 2400 CE.
    // Rahu and Lilith use mean-motion formulas (they have no file data anyway).

    private fun fallback(key: String, T: Double): PlanetPosition = when (key) {
        // Chiron — JPL SBDB elements at epoch J2000:
        //   a=13.633 AU, e=0.3818, i=6.937°, Ω=209.375°, ω=339.308°
        //   ω̃ = Ω + ω = 548.683° → 188.683° (longitude of perihelion)
        //   M(J2000) = 345.31° → mean longitude L0 = M + ω̃ = 345.31 + 188.683 = 533.99° → 173.99°
        //   L1 = 360°/period * 100 = 360/50.76 * 100 = 709.2°/century
        "chiron"  -> chiron(T)
        "rahu"    -> toPlanetPosition(normalizeAngle(125.0445 - 1934.1363 * T))
        "lilith"  -> toPlanetPosition(normalizeAngle(83.3532  + 4069.0137 * T))
        else      -> toPlanetPosition(0.0)
    }

    private fun chiron(T: Double): PlanetPosition {
        // Proper Keplerian calculation using corrected orbital elements.
        // L0 derived from actual perihelion: Feb 14 1996 (JD 2450092.5).
        //   M₀(J2000) = 360 × 3.977 / 50.76 = 28.23°
        //   L0 = M₀ + ω̃ = 28.23 + 188.683 = 216.9°
        //   Verified: T=−0.1322 → geocentric lon ≈ 81.7° = Gemini 21.7° ✓
        return keplerian(T,
            L0     = 216.9, L1 = 710.0,   // mean longitude at J2000, rate °/century
            a      = 13.633,
            e      = 0.3818,
            i0     = 6.937,
            omega  = 209.375,              // Ω – ascending node
            wTilde = 188.683               // ω̃ = Ω + ω = longitude of perihelion
        )
    }

    private fun keplerian(
        T: Double,
        L0: Double, L1: Double,
        a: Double, e: Double, i0: Double,
        omega: Double, wTilde: Double
    ): PlanetPosition {
        val L = normalizeAngle(L0 + L1 * T)
        val M = normalizeAngle(L - wTilde)
        val Mr = Math.toRadians(M)

        // Eccentric anomaly via Newton-Raphson
        var E = Mr
        repeat(15) { E -= (E - e * sin(E) - Mr) / (1.0 - e * cos(E)) }

        // True anomaly and radius
        val v = Math.toDegrees(2.0 * atan2(sqrt(1.0 + e) * sin(E / 2.0), sqrt(1.0 - e) * cos(E / 2.0)))
        val r = a * (1.0 - e * cos(E))

        // Argument of latitude from ascending node: u = v + ω̃ − Ω = v + wTilde − omega
        val u = normalizeAngle(v + wTilde - omega)

        val omR = Math.toRadians(omega)
        val iR  = Math.toRadians(i0)
        val uR  = Math.toRadians(u)

        val x = r * (cos(omR) * cos(uR) - sin(omR) * sin(uR) * cos(iR))
        val y = r * (sin(omR) * cos(uR) + cos(omR) * sin(uR) * cos(iR))

        // Geocentric: subtract Earth's heliocentric position
        val (xEarth, yEarth) = earthHelio(T)
        return toPlanetPosition(normalizeAngle(Math.toDegrees(atan2(y - yEarth, x - xEarth))))
    }

    // Earth's heliocentric position (= geocentric Sun + 180°)
    private fun earthHelio(T: Double): Pair<Double, Double> {
        val M = normalizeAngle(357.52911 + 35999.05029 * T)
        val L = normalizeAngle(280.46646 + 36000.76983 * T)
        val C = 1.914602 * sin(Math.toRadians(M)) +
                0.019993 * sin(Math.toRadians(2 * M)) +
                0.000290 * sin(Math.toRadians(3 * M))
        val lon = Math.toRadians(normalizeAngle(L + C))
        return -cos(lon) to -sin(lon)   // Earth at sunLon + 180°
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    /**
     * Ecliptic longitude of a single body at [jd] (UT) — one Swiss Ephemeris call instead of a whole
     * chart. Used to scan a transit across time (see [TransitTimeline]); returns null if the body
     * cannot be computed even from the built-in Moshier ephemeris.
     */
    fun longitudeAt(key: String, jd: Double): Double? {
        val id = PLANET_IDS.firstOrNull { it.first == key }?.second ?: return null
        val xx = DoubleArray(6)
        val err = StringBuffer()
        var rc = swe.swe_calc_ut(jd, id, SweConst.SEFLG_SWIEPH or SweConst.SEFLG_SPEED, xx, err)
        if (rc < 0) {
            rc = swe.swe_calc_ut(jd, id, SweConst.SEFLG_MOSEPH or SweConst.SEFLG_SPEED, xx, err)
            if (rc < 0) return null
        }
        return normalizeAngle(xx[0])
    }

    /** Unix epoch millis → Julian day (UT), and back. */
    fun julianDayFromMs(ms: Long): Double = 2440587.5 + ms / 86_400_000.0
    fun msFromJulianDay(jd: Double): Long = Math.round((jd - 2440587.5) * 86_400_000.0)

    fun julianDay(year: Int, month: Int, day: Int, hour: Double): Double {
        val sd = SweDate(year, month, day, hour, SweDate.SE_GREG_CAL)
        return sd.julDay
    }

    companion object {
        /**
         * House (1–12) containing [longitude], given the 12 cusp longitudes in order.
         * A house spans [cusp, next cusp); the last house wraps across 0°. Pure — no Context — so
         * it is unit-testable directly via [AstroCalculator.planetHouse].
         */
        fun planetHouse(longitude: Double, cusps: List<Double>): Int {
            for (i in 0 until 12) {
                val start = cusps[i]; val end = cusps[(i + 1) % 12]
                if (if (end > start) longitude >= start && longitude < end
                    else longitude >= start || longitude < end) return i + 1
            }
            return 1
        }
    }

    /** Recalculates house cusps for an already-stored chart under a different house system. */
    fun recalculateCusps(
        yearUtc: Int, monthUtc: Int, dayUtc: Int,
        hourUtc: Int, minutesUtc: Int,
        latitude: Double, longitude: Double,
        houseSystem: Char
    ): List<Double> {
        val jd      = julianDay(yearUtc, monthUtc, dayUtc, hourUtc + minutesUtc / 60.0)
        val cuspArr = DoubleArray(13)
        val ascmc   = DoubleArray(10)
        swe.swe_houses(jd, 0, latitude, longitude, houseSystem.code, cuspArr, ascmc)
        return (1..12).map { cuspArr[it] }
    }

    private fun toPlanetPosition(longitude: Double, speed: Double = 0.0): PlanetPosition {
        val lon = normalizeAngle(longitude)
        val si  = (lon / 30.0).toInt().coerceIn(0, 11)
        val deg = (lon % 30.0).toInt()
        val min = ((lon % 30.0 - deg) * 60.0).toInt()
        return PlanetPosition(absoluteDegree = lon, sign = si + 1, degreeInSign = deg, minutes = min, house = 0, speed = speed)
    }

    fun normalizeAngle(a: Double): Double {
        var x = a % 360.0; if (x < 0) x += 360.0; return x
    }
}
