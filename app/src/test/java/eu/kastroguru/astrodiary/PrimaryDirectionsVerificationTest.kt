package eu.kastroguru.astrodiary

import de.thmac.swisseph.SweConst
import de.thmac.swisseph.SweDate
import de.thmac.swisseph.SwissEph
import eu.kastroguru.astrodiary.domain.calculator.PrimaryDirectionsCalculator
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.math.abs

/**
 * Host-JVM verification of [PrimaryDirectionsCalculator] against Morinus.
 *
 * Produces a Placidus-semi-arc directions table for a FIXED chart, writes it to
 * build/pd_verification.txt, and asserts the analytic anchor:
 *   directing a body to the MC by conjunction ⇒ arc == RA-difference (MD_MC ≈ 0).
 *
 * To validate against Morinus: open the SAME chart (date/time UTC + coordinates below) in
 * Morinus810, set method = Placidus semi-arc, zodiacal, key = Naibod, and diff the perfection
 * ages against the generated table.
 */
class PrimaryDirectionsVerificationTest {

    // ── Fixed test chart ─────────────────────────────────────────────────────
    // 1990-06-15, 09:30 UT, Sofia. Coordinates chosen exact at degree+minute resolution
    // (Morinus enters place as whole degrees + minutes), so both sides use identical input:
    //   Lat 42°42′ N = 42.700000   Lon 23°20′ E = 23.333333
    private val year = 1990; private val month = 6; private val day = 15
    private val hourUtc = 9.5
    private val lat = 42.0 + 42.0 / 60.0   // 42°42′ N
    private val lon = 23.0 + 20.0 / 60.0   // 23°20′ E

    private val planetIds = listOf(
        "sun" to SweConst.SE_SUN, "moon" to SweConst.SE_MOON, "mercury" to SweConst.SE_MERCURY,
        "venus" to SweConst.SE_VENUS, "mars" to SweConst.SE_MARS, "jupiter" to SweConst.SE_JUPITER,
        "saturn" to SweConst.SE_SATURN, "uranus" to SweConst.SE_URANUS, "neptune" to SweConst.SE_NEPTUNE,
        "pluto" to SweConst.SE_PLUTO, "chiron" to SweConst.SE_CHIRON, "rahu" to SweConst.SE_TRUE_NODE,
        "lilith" to SweConst.SE_MEAN_APOG,
    )

    @Test
    fun generateAndVerifyDirectionsTable() {
        val swe = SwissEph()
        val epheDir = File("src/main/assets/ephe").absoluteFile
        if (epheDir.isDirectory) swe.swe_set_ephe_path(epheDir.absolutePath)

        val jd = SweDate(year, month, day, hourUtc, SweDate.SE_GREG_CAL).julDay
        val xx = DoubleArray(6); val err = StringBuffer()

        val obliquity = run {
            val rc = swe.swe_calc_ut(jd, SweConst.SE_ECL_NUT, SweConst.SEFLG_SWIEPH, xx, err)
            if (rc >= 0) xx[0] else 23.4393 - 0.013004 * ((jd - 2451545.0) / 36525.0)
        }

        val equ = mutableMapOf<String, PrimaryDirectionsCalculator.Equatorial>()
        val lons = mutableMapOf<String, Double>()
        for ((key, id) in planetIds) {
            // Ecliptic longitude
            var rc = swe.swe_calc_ut(jd, id, SweConst.SEFLG_SWIEPH or SweConst.SEFLG_SPEED, xx, err)
            if (rc < 0) rc = swe.swe_calc_ut(jd, id, SweConst.SEFLG_MOSEPH or SweConst.SEFLG_SPEED, xx, err)
            if (rc >= 0) lons[key] = xx[0]
            // Equatorial RA/decl
            rc = swe.swe_calc_ut(jd, id, SweConst.SEFLG_SWIEPH or SweConst.SEFLG_EQUATORIAL, xx, err)
            if (rc < 0) rc = swe.swe_calc_ut(jd, id, SweConst.SEFLG_MOSEPH or SweConst.SEFLG_EQUATORIAL, xx, err)
            if (rc >= 0) equ[key] = PrimaryDirectionsCalculator.Equatorial(xx[0], xx[1])
        }

        val cusps = DoubleArray(13); val ascmc = DoubleArray(10)
        swe.swe_houses(jd, 0, lat, lon, 'P'.code, cusps, ascmc)
        val armc = ascmc[2]
        lons["asc"] = ascmc[0]
        lons["mc"] = ascmc[1]

        val eq = PrimaryDirectionsCalculator.EquatorialChart(armc, obliquity, lat, equ)
        val points = planetIds.map { it.first } + listOf("asc", "mc")

        // True Solar Equatorial Arc key (the app's only exposed key): days for the real Sun to
        // advance `arc` degrees of RA from birth = age in years. Mirrors AstroCalculator.trueSolarArcYears.
        fun sunRa(jdv: Double): Pair<Double, Double> {
            var rc = swe.swe_calc_ut(jdv, SweConst.SE_SUN, SweConst.SEFLG_SWIEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED, xx, err)
            if (rc < 0) rc = swe.swe_calc_ut(jdv, SweConst.SE_SUN, SweConst.SEFLG_MOSEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED, xx, err)
            return xx[0] to xx[3]
        }
        val sunRa0 = sunRa(jd).first
        fun trueSolarArcYears(arcDeg: Double): Double {
            if (arcDeg <= 0.0) return 0.0
            var t = arcDeg / 0.98565
            repeat(10) {
                val (raT, sp) = sunRa(jd + t)
                var adv = (raT - sunRa0) % 360.0; if (adv < 0) adv += 360.0
                val rate = if (sp > 0.05) sp else 0.9856
                val next = t - (adv - arcDeg) / rate
                t = if (next < 0) 0.0 else next
            }
            return t
        }

        val calc = PrimaryDirectionsCalculator()
        val dirs = calc.calculate(
            eq = eq,
            longitudes = lons,
            promissors = points,
            significators = points,
            arcToYears = { trueSolarArcYears(it) },
            includeConverse = true,
            maxYears = 100.0,
        )

        // ── Write the table ────────────────────────────────────────────────────
        val aspectGlyph = mapOf(0 to "☌", 60 to "⚹", 90 to "□", 120 to "△", 180 to "☍")
        val sb = StringBuilder()
        sb.appendLine("Primary Directions (Placidus semi-arc, zodiacal, True Solar Equatorial Arc key)")
        sb.appendLine("Chart: $year-$month-$day ${hourUtc}h UTC  lat=$lat lon=$lon")
        sb.appendLine("ARMC=%.4f  obliquity=%.4f".format(armc, obliquity))
        sb.appendLine("-".repeat(64))
        sb.appendLine("%-9s %-4s %-9s %8s %8s  %s".format("Promissor", "Asp", "Signif.", "Arc°", "Age", "Dir/Conv"))
        for (d in dirs) {
            sb.appendLine(
                "%-9s %-4s %-9s %8.3f %8.2f  %s".format(
                    d.promissor, aspectGlyph[d.aspectAngle] ?: "${d.aspectAngle}", d.significator,
                    d.arc, d.years, if (d.isDirect) "direct" else "converse",
                )
            )
        }
        sb.appendLine("-".repeat(64))
        sb.appendLine("Total directions (0–100y): ${dirs.size}")

        // ── DEBUG dump: armc, mc projection, per-planet lat-0 RA and conj-to-MC arc ──
        val obl0 = Math.toRadians(obliquity)
        fun ra0(lonDeg: Double): Double {
            val l = Math.toRadians(lonDeg)
            return Math.toDegrees(Math.atan2(Math.cos(obl0) * Math.sin(l), Math.cos(l))).let { (it % 360 + 360) % 360 }
        }
        fun decl0(lonDeg: Double) = Math.toDegrees(Math.asin(Math.sin(obl0) * Math.sin(Math.toRadians(lonDeg))))
        fun gdiff(d0: Double): Double { var d = d0; if (d < 0) d += 360; if (d > 180) d -= 360; return d }
        sb.appendLine(); sb.appendLine("=== DEBUG ===")
        sb.appendLine("armc=%.4f  eclToEqu(mc_lon=%.4f).ra=%.4f  (should equal armc)".format(armc, lons["mc"], ra0(lons["mc"]!!)))
        sb.appendLine("eclToEqu(asc_lon=%.4f).ra=%.4f".format(lons["asc"], ra0(lons["asc"]!!)))
        sb.appendLine("%-9s %10s %10s %10s %12s".format("body","lon","ra(lat0)","decl(lat0)","conj→MC arc"))
        for ((k, lonDeg) in lons) {
            if (k == "asc" || k == "mc") continue
            sb.appendLine("%-9s %10.4f %10.4f %10.4f %12.4f".format(k, lonDeg, ra0(lonDeg), decl0(lonDeg), gdiff(ra0(lonDeg) - armc)))
        }

        val outFile = File("build/pd_verification.txt")
        outFile.parentFile?.mkdirs()
        outFile.writeText(sb.toString())
        println(sb.toString())

        // ── Analytic anchor: promissor → MC by conjunction == RA difference ─────
        // For significator = MC, meridian distance ≈ 0, so arc == getDiff(RA_prom − ARMC).
        // RA_prom is the latitude-0 projection of the promissor's longitude (directions without
        // latitude), matching the engine.
        val obl = Math.toRadians(obliquity)
        var anchorsChecked = 0
        for ((key, lonDeg) in lons) {
            if (key == "asc" || key == "mc") continue
            val toMc = dirs.firstOrNull { it.promissor == key && it.significator == "mc" && it.aspectAngle == 0 }
                ?: continue
            val l = Math.toRadians(lonDeg)
            val raProm = Math.toDegrees(Math.atan2(Math.cos(obl) * Math.sin(l), Math.cos(l))).let { (it % 360 + 360) % 360 }
            var expected = raProm - armc
            // fold to signed shortest arc (same as getDiff)
            if (expected < 0) expected += 360.0
            if (expected > 180.0) expected -= 360.0
            assertTrue(
                "MC-direction anchor failed for $key: arc=${toMc.arc} expected≈$expected",
                abs(abs(toMc.arc) - abs(expected)) < 0.05,
            )
            anchorsChecked++
        }
        assertTrue("No MC-conjunction anchors were found to verify", anchorsChecked > 0)
        println("Analytic MC anchor verified for $anchorsChecked bodies.")
    }

    /**
     * The True Solar Equatorial Arc key (the app's only exposed key) maps an arc of direction to an
     * age. This pins two properties of that map: it is strictly increasing (a larger arc is always
     * reached later in life), and inverting it — bisecting for the arc that yields a target age —
     * round-trips back to the original arc.
     */
    @Test
    fun trueSolarArcYearsIsMonotonicAndRoundTrips() {
        val swe = SwissEph()
        val epheDir = File("src/main/assets/ephe").absoluteFile
        if (epheDir.isDirectory) swe.swe_set_ephe_path(epheDir.absolutePath)

        val jd = SweDate(year, month, day, hourUtc, SweDate.SE_GREG_CAL).julDay
        val xx = DoubleArray(6); val err = StringBuffer()

        fun sunRa(jdv: Double): Pair<Double, Double> {
            var rc = swe.swe_calc_ut(jdv, SweConst.SE_SUN, SweConst.SEFLG_SWIEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED, xx, err)
            if (rc < 0) rc = swe.swe_calc_ut(jdv, SweConst.SE_SUN, SweConst.SEFLG_MOSEPH or SweConst.SEFLG_EQUATORIAL or SweConst.SEFLG_SPEED, xx, err)
            return xx[0] to xx[3]
        }
        val sunRa0 = sunRa(jd).first
        fun trueSolarArcYears(arcDeg: Double): Double {
            if (arcDeg <= 0.0) return 0.0
            var t = arcDeg / 0.98565
            repeat(10) {
                val (raT, sp) = sunRa(jd + t)
                var adv = (raT - sunRa0) % 360.0; if (adv < 0) adv += 360.0
                val rate = if (sp > 0.05) sp else 0.9856
                val next = t - (adv - arcDeg) / rate
                t = if (next < 0) 0.0 else next
            }
            return t
        }

        // Strictly increasing across the lifetime range.
        var prev = -1.0
        var arc = 1.0
        while (arc <= 100.0) {
            val years = trueSolarArcYears(arc)
            assertTrue("years should be positive for arc=$arc, got $years", years > 0.0)
            assertTrue("years not increasing at arc=$arc: $prev -> $years", years > prev)
            prev = years
            arc += 1.0
        }

        // Round-trip: pick an arc, get its age, bisect for the arc that reproduces that age.
        for (trueArc in listOf(7.5, 23.0, 41.3, 66.0, 88.8)) {
            val targetYears = trueSolarArcYears(trueArc)
            var lo = 0.0; var hi = 120.0
            repeat(60) {
                val mid = (lo + hi) / 2.0
                if (trueSolarArcYears(mid) < targetYears) lo = mid else hi = mid
            }
            val recovered = (lo + hi) / 2.0
            assertTrue(
                "arc↔years round-trip failed: $trueArc → ${targetYears}y → $recovered",
                abs(recovered - trueArc) < 1e-3,
            )
        }
    }
}
