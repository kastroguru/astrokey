package eu.kastroguru.astrodiary.domain.humandesign

import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.AstroData
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Builds a complete Human Design chart from a stored natal chart.
 *
 *  - Personality (conscious) activations come straight from the stored BirthDataEntity.
 *  - Design (unconscious) activations are computed at the moment the Sun was exactly
 *    88° earlier in the zodiac (~88 days before birth), using the existing AstroCalculator
 *    and the stored birth date/time/location.
 *  - Earth = Sun + 180°,  South Node = North Node + 180° (both derived, not stored).
 */
@Singleton
class HumanDesignCalculator @Inject constructor(
    private val calculator: AstroCalculator
) {

    private val SUN_DEG_PER_DAY = 0.985647

    fun compute(birth: BirthDataEntity): HumanDesignChart {
        // ── Personality (birth moment) — from stored data ────────────────────
        val personalityLong = mapOf(
            "sun" to birth.sunD, "moon" to birth.moonD, "mercury" to birth.mercuryD,
            "venus" to birth.venusD, "mars" to birth.marsD, "jupiter" to birth.jupiterD,
            "saturn" to birth.saturnD, "uranus" to birth.uranusD, "neptune" to birth.neptuneD,
            "pluto" to birth.plutoD,
            "north_node" to birth.rahuD,
            "south_node" to wrap(birth.rahuD + 180.0),
            "earth" to wrap(birth.sunD + 180.0)
        )
        val personality = toActivations(personalityLong)

        // ── Design (88° of solar arc before birth) — computed ─────────────────
        val designData = findDesignMoment(birth, birth.sunD)
        val p = designData.planets
        fun d(key: String) = p[key]?.absoluteDegree ?: 0.0
        val designLong = mapOf(
            "sun" to d("sun"), "moon" to d("moon"), "mercury" to d("mercury"),
            "venus" to d("venus"), "mars" to d("mars"), "jupiter" to d("jupiter"),
            "saturn" to d("saturn"), "uranus" to d("uranus"), "neptune" to d("neptune"),
            "pluto" to d("pluto"),
            "north_node" to d("rahu"),
            "south_node" to wrap(d("rahu") + 180.0),
            "earth" to wrap(d("sun") + 180.0)
        )
        val design = toActivations(designLong)

        // ── Active gates / channels / centers ─────────────────────────────────
        val activeGates = (personality + design).map { it.gate }.toSet()
        val definedChannels = CHANNELS.filter { it.a in activeGates && it.b in activeGates }
        val definedCenters = definedChannels
            .flatMap { listOfNotNull(centerOfGate(it.a), centerOfGate(it.b)) }
            .toSet()

        // ── Derived attributes ────────────────────────────────────────────────
        val type       = determineType(definedCenters, definedChannels)
        val authority  = determineAuthority(definedCenters, definedChannels, type)
        val definition = determineDefinition(definedCenters, definedChannels)
        val persSunLine = personality.first { it.body == HdBody.SUN }.line
        val desSunLine  = design.first { it.body == HdBody.SUN }.line

        return HumanDesignChart(
            personality = personality,
            design = design,
            activeGates = activeGates,
            definedChannels = definedChannels,
            definedCenters = definedCenters,
            type = type,
            authority = authority,
            profilePersonalityLine = persSunLine,
            profileDesignLine = desSunLine,
            definition = definition
        )
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────
    private fun wrap(d: Double) = (d % 360.0 + 360.0) % 360.0

    private fun toActivations(longByKey: Map<String, Double>): List<HdActivation> =
        HdBody.values().map { body ->
            val lon = longByKey[body.key] ?: 0.0
            val (gate, line) = gateLineFor(lon)
            HdActivation(body, lon, gate, line)
        }

    /** Iterate to the UTC moment when the Sun was 88° earlier than at birth. */
    private fun findDesignMoment(birth: BirthDataEntity, birthSunLong: Double): AstroData {
        val target = wrap(birthSunLong - 88.0)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(birth.yearUtc, birth.monthUtc - 1, birth.dayUtc, birth.hourUtc, birth.minutesUtc, 0)
        }
        cal.add(Calendar.DAY_OF_MONTH, -88)   // close starting guess

        var last = calcAt(cal, birth.latitude, birth.longitude)
        repeat(12) {
            val curr = last.planets["sun"]?.absoluteDegree ?: return last
            val diff = ((curr - target + 540.0) % 360.0) - 180.0  // signed shortest, −180..180
            if (abs(diff) < 0.0008) return last
            val deltaSeconds = (-diff / SUN_DEG_PER_DAY * 86400.0).toInt()
            cal.add(Calendar.SECOND, deltaSeconds)
            last = calcAt(cal, birth.latitude, birth.longitude)
        }
        return last
    }

    private fun calcAt(cal: Calendar, lat: Double, lon: Double): AstroData {
        val hourUtc = cal.get(Calendar.HOUR_OF_DAY) +
                cal.get(Calendar.MINUTE) / 60.0 +
                cal.get(Calendar.SECOND) / 3600.0
        return calculator.calculate(
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
            hourUtc, lat, lon
        )
    }

    // ── Type / Authority / Definition logic ──────────────────────────────────────

    private fun determineType(defined: Set<HdCenter>, channels: List<HdChannel>): HdType {
        if (defined.isEmpty()) return HdType.REFLECTOR
        val sacralDefined = HdCenter.SACRAL in defined
        val throatConnected = centersConnectedToThroat(defined, channels)

        return if (sacralDefined) {
            if (HdCenter.SACRAL in throatConnected) HdType.MANIFESTING_GENERATOR
            else HdType.GENERATOR
        } else {
            val motorToThroat = MOTOR_CENTERS.any { it != HdCenter.SACRAL && it in throatConnected }
            if (HdCenter.THROAT in defined && motorToThroat) HdType.MANIFESTOR
            else HdType.PROJECTOR
        }
    }

    private fun determineAuthority(defined: Set<HdCenter>, channels: List<HdChannel>, type: HdType): HdAuthority {
        if (type == HdType.REFLECTOR) return HdAuthority.LUNAR
        return when {
            HdCenter.SOLAR_PLEXUS in defined -> HdAuthority.EMOTIONAL
            HdCenter.SACRAL in defined       -> HdAuthority.SACRAL
            HdCenter.SPLEEN in defined       -> HdAuthority.SPLENIC
            HdCenter.HEART in defined        -> HdAuthority.EGO
            HdCenter.G in defined            -> HdAuthority.SELF_PROJECTED
            else                             -> HdAuthority.MENTAL
        }
    }

    private fun determineDefinition(defined: Set<HdCenter>, channels: List<HdChannel>): HdDefinition {
        if (defined.isEmpty()) return HdDefinition.NONE
        return when (countComponents(defined, channels)) {
            1 -> HdDefinition.SINGLE
            2 -> HdDefinition.SPLIT
            3 -> HdDefinition.TRIPLE_SPLIT
            else -> HdDefinition.QUADRUPLE_SPLIT
        }
    }

    // ── Graph helpers over defined centers ────────────────────────────────────

    private fun adjacency(defined: Set<HdCenter>, channels: List<HdChannel>): Map<HdCenter, MutableSet<HdCenter>> {
        val adj = defined.associateWith { mutableSetOf<HdCenter>() }
        for (ch in channels) {
            val ca = centerOfGate(ch.a); val cb = centerOfGate(ch.b)
            if (ca != null && cb != null && ca != cb && ca in defined && cb in defined) {
                adj[ca]?.add(cb); adj[cb]?.add(ca)
            }
        }
        return adj
    }

    private fun centersConnectedToThroat(defined: Set<HdCenter>, channels: List<HdChannel>): Set<HdCenter> {
        if (HdCenter.THROAT !in defined) return emptySet()
        val adj = adjacency(defined, channels)
        val seen = mutableSetOf(HdCenter.THROAT)
        val stack = ArrayDeque<HdCenter>().apply { add(HdCenter.THROAT) }
        while (stack.isNotEmpty()) {
            val c = stack.removeLast()
            for (n in adj[c] ?: emptySet()) if (seen.add(n)) stack.add(n)
        }
        return seen
    }

    private fun countComponents(defined: Set<HdCenter>, channels: List<HdChannel>): Int {
        val adj = adjacency(defined, channels)
        val seen = mutableSetOf<HdCenter>()
        var comps = 0
        for (start in defined) {
            if (start in seen) continue
            comps++
            val stack = ArrayDeque<HdCenter>().apply { add(start) }
            seen.add(start)
            while (stack.isNotEmpty()) {
                val c = stack.removeLast()
                for (n in adj[c] ?: emptySet()) if (seen.add(n)) stack.add(n)
            }
        }
        return comps
    }
}
