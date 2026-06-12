package eu.kastroguru.astrodiary.domain.calculator

import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Placidus semi-arc PRIMARY DIRECTIONS.
 *
 * Faithful port of Morinus (Morinus810) — placidiansapd.py / primdirs.py / planets.py —
 * restricted to the v1 scope:
 *   • Placidus semi-arc method only
 *   • Zodiacal directions (no mundane)
 *   • Direct + converse
 *   • Aspects: conjunction + sextile / square / trine / opposition (Ptolemaic), both sides (sinister/dexter)
 *
 * The core arc-of-direction formula (placidiansapd.py:2492):
 *     arc = getDiff(raProm − ra) + t · (90 + v · adProm) · (md_sig / sa_sig)
 *
 * All angles in degrees. "RA" = right ascension, "AD" = ascensional difference,
 * "MD" = meridian distance, "SA" = semi-arc, ARMC = right ascension of the MC.
 *
 * Convention notes (matched to Morinus default = SZNEITHER, no Bianchini latitudes):
 *   • Conjunction promissor (aspect 0°) uses the planet's REAL equatorial RA/decl (with ecliptic
 *     latitude), taken from [EquatorialChart.planets]. Angles (ASC/MC) and aspect rays have no such
 *     entry and fall back to the latitude-0 zodiacal projection — exactly as Morinus does.
 *   • Aspect rays (60/90/120/180) and ALL significator points use the latitude-0 projection of the
 *     ecliptic longitude onto the equator (Ptolemaic "directions without latitude").
 */
class PrimaryDirectionsCalculator {

    /** Degree-for-a-year keys. coeff = years per degree of arc (primdirs.py staticData). */
    enum class Key(val coeff: Double, val label: String) {
        NAIBOD(1.01456164, "Naibod"),
        PTOLEMY(1.0, "Ptolemy"),
        CARDAN(1.0135135, "Cardan"),
    }

    /** Right ascension + declination of one point (degrees). */
    data class Equatorial(val ra: Double, val decl: Double)

    /** Everything the engine needs about a natal chart, in the equatorial frame. */
    data class EquatorialChart(
        val armc: Double,                       // right ascension of MC (ascmc[2])
        val obliquity: Double,                  // true obliquity of the ecliptic
        val geoLat: Double,                     // geographic latitude of birth place
        val planets: Map<String, Equatorial>,   // REAL ra/decl (incl. latitude) per planet key
    )

    /** One computed primary direction (a perfection). */
    data class PrimaryDirection(
        val promissor: String,
        val significator: String,
        val aspectAngle: Int,      // 0, 60, 90, 120, 180
        val sinister: Boolean,     // true = left/forward ray (+angle), false = dexter (−angle)
        val arc: Double,           // signed arc of direction in degrees of RA (>0 direct, <0 converse)
        val isDirect: Boolean,
        val years: Double,         // age at which the direction perfects
    )

    private val aspects = listOf(0, 60, 90, 120, 180)

    /**
     * @param eq             equatorial data for the natal chart (see [AstroCalculator.computeEquatorial])
     * @param longitudes     ecliptic longitude (0–360) of every promissor/significator point, keyed
     *                       by name. Planets by [eu.kastroguru.astrodiary.domain.model.Planet.key];
     *                       angles as "asc" and "mc".
     * @param promissors     keys to use as promissors (the moving bodies / their rays)
     * @param significators  keys to use as significators (the points reached)
     */
    fun calculate(
        eq: EquatorialChart,
        longitudes: Map<String, Double>,
        promissors: List<String>,
        significators: List<String>,
        key: Key = Key.NAIBOD,
        arcToYears: ((Double) -> Double)? = null,
        includeConverse: Boolean = true,
        maxYears: Double = 100.0,
    ): List<PrimaryDirection> {
        val armc = eq.armc
        val raic = norm(armc + 180.0)
        val out = mutableListOf<PrimaryDirection>()

        for (pKey in promissors) {
            val pLon = longitudes[pKey] ?: continue
            for (aspect in aspects) {
                // Opposition & conjunction have a single ray; the rest have sinister (+) and dexter (−).
                val sides = if (aspect == 0 || aspect == 180) listOf(true) else listOf(true, false)
                for (sinister in sides) {
                    val signed = if (sinister) aspect else -aspect

                    // ── Promissor point: ra & ascensional difference ──────────────
                    // Zodiacal "directions without latitude": the promissor (its body for a
                    // conjunction, or its aspect ray) is projected onto the equator at latitude 0,
                    // matching Morinus's calcInterPlanetary / calcZodPromAspsInterPlanetary
                    // (swe_cotrans(lon, 0.0, ...)). The planet's own ecliptic latitude is NOT used.
                    val (raProm, declProm) = eclToEqu(norm(pLon + signed), eq.obliquity)
                    val adProm = ascDiff(eq.geoLat, declProm) ?: continue

                    for (sKey in significators) {
                        if (sKey == pKey) continue
                        val sLon = longitudes[sKey] ?: continue

                        val z = zodMdSa(sLon, armc, raic, eq.geoLat, eq.obliquity) ?: continue
                        val (t, v, ra) = getVars(z.aboveHorizon, z.eastern, armc, raic)
                        val mdPerSa = z.md / z.sa

                        // ── Core Placidus semi-arc arc-of-direction ───────────────
                        var arc = getDiff(raProm - ra) + t * (90.0 + v * adProm) * mdPerSa

                        // create(): resolve direct/converse, fold to 0–180.
                        var direct = true
                        if (arc < 0.0) { arc = -arc; direct = false }
                        if (arc > 180.0) { arc = 360.0 - arc; direct = !direct }
                        if (!includeConverse && !direct) continue

                        // Convert arc → age. Default app key: True Solar Equatorial Arc (supplied via
                        // arcToYears); falls back to the static [key] (Naibod) when none is given.
                        val years = arcToYears?.invoke(arc) ?: (arc * key.coeff)
                        if (years in 0.0..maxYears) {
                            out += PrimaryDirection(
                                promissor = pKey,
                                significator = sKey,
                                aspectAngle = aspect,
                                sinister = sinister,
                                arc = if (direct) arc else -arc,
                                isDirect = direct,
                                years = years,
                            )
                        }
                    }
                }
            }
        }
        return out.sortedBy { it.years }
    }

    // ── Ported helpers ──────────────────────────────────────────────────────────

    /** Quadrant coefficients (placidiansapd.py:getvars). Returns (t, v, ra). */
    private fun getVars(aboveHorizon: Boolean, eastern: Boolean, armc: Double, raic: Double): Triple<Double, Double, Double> {
        val t = if ((eastern && !aboveHorizon) || (!eastern && aboveHorizon)) 1.0 else -1.0
        val v: Double
        val ra: Double
        if (!aboveHorizon) { v = -1.0; ra = raic } else { v = 1.0; ra = armc }
        return Triple(t, v, ra)
    }

    /** Normalises an RA difference to a signed shortest arc (primdirs.py:getDiff). */
    private fun getDiff(diffIn: Double): Double {
        var diff = diffIn
        var direct = true
        if (diff < 0.0) { diff = -diff; direct = false }
        if (diff > 180.0) { diff = 360.0 - diff; direct = !direct }
        if (!direct) diff = -diff
        return diff
    }

    private data class ZodMdSa(val md: Double, val sa: Double, val aboveHorizon: Boolean, val eastern: Boolean)

    /** md / sa of a zodiacal point at latitude 0 (placidiansapd.py:getZodMDSA). Null if circumpolar. */
    private fun zodMdSa(lon: Double, armc: Double, raic: Double, geoLat: Double, obliquity: Double): ZodMdSa? {
        val (ra, decl) = eclToEqu(lon, obliquity)

        val eastern = isEastern(ra, armc, raic)

        var med = abs(armc - ra); if (med > 180.0) med = 360.0 - med
        var icd = abs(raic - ra); if (icd > 180.0) icd = 360.0 - icd

        val ad = ascDiff(geoLat, decl) ?: return null
        val dsa = 90.0 + ad
        val nsa = 90.0 - ad

        val aboveHorizon = med <= dsa
        val md: Double
        val sa: Double
        if (aboveHorizon) { md = med; sa = dsa } else { md = icd; sa = nsa }
        if (sa == 0.0) return null
        return ZodMdSa(md, sa, aboveHorizon, eastern)
    }

    /** Eastern half (rising) vs western half, by RA quadrant vs ARMC/RAIC (planets.py). */
    private fun isEastern(ra: Double, armc: Double, raic: Double): Boolean {
        return if (armc > raic) {
            !(ra > raic && ra < armc)
        } else {
            !((ra > raic && ra < 360.0) || (ra < armc && ra > 0.0))
        }
    }

    /** Ascensional difference AD = asin(tan(lat)·tan(decl)); null when the point never rises. */
    private fun ascDiff(geoLat: Double, decl: Double): Double? {
        val v = tan(rad(geoLat)) * tan(rad(decl))
        if (abs(v) > 1.0) return null
        return deg(asin(v))
    }

    /** Ecliptic longitude (latitude 0) → equatorial (RA, decl). */
    private fun eclToEqu(lon: Double, obliquity: Double): Pair<Double, Double> {
        val l = rad(lon); val e = rad(obliquity)
        val ra = norm(deg(atan2(sin(l) * cos(e), cos(l))))
        val decl = deg(asin(sin(e) * sin(l)))
        return ra to decl
    }

    /** Inverse of [eclToEqu] for a point taken on the ecliptic: right ascension → ecliptic longitude. */
    fun equToEcl(ra: Double, obliquity: Double): Double {
        val r = rad(ra); val e = rad(obliquity)
        return norm(deg(atan2(sin(r) / cos(e), cos(r))))
    }

    /** A natal point's directed positions on the zodiac at a given arc. */
    data class DirectedPosition(
        val key: String,
        val natalLon: Double,
        val directLon: Double,    // carried forward by [arc] in RA, projected to the ecliptic
        val converseLon: Double,  // carried backward by [arc]
    )

    /**
     * Where each point's directed markers sit on the wheel for a given arc of direction.
     * The body is carried ±[arc] in right ascension (primary motion) and re-projected to the
     * ecliptic. Matches the rigorous engine exactly for the angles; a faithful visual for the rest.
     */
    fun directedPositions(longitudes: Map<String, Double>, obliquity: Double, arc: Double): List<DirectedPosition> =
        longitudes.map { (key, lon) ->
            val ra = eclToEqu(lon, obliquity).first
            DirectedPosition(
                key = key,
                natalLon = norm(lon),
                // Direct = carried WITH the diurnal motion (ASC→MC→DESC, clockwise on the wheel),
                // which is decreasing RA/longitude. Converse = against it. Verified by the to-MC
                // conjunction landing exactly on the MC at its arc.
                directLon = equToEcl(ra - arc, obliquity),
                converseLon = equToEcl(ra + arc, obliquity),
            )
        }

    private fun norm(a: Double): Double { var x = a % 360.0; if (x < 0) x += 360.0; return x }
    private fun rad(d: Double) = d * Math.PI / 180.0
    private fun deg(r: Double) = r * 180.0 / Math.PI
}
