package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Today's transit read against one person's own gates.
 *
 * The daily HD format everyone sells is not "the Sun is in gate 41" on its own — it is what that
 * gate does to *your* chart. Three cases, in descending order of how much a person actually
 * notices them:
 *
 *   COMPLETES — the transit gate is the far half of one of your hanging channels. For as long as
 *               it lasts you are temporarily defined there, and it usually feels like relief or
 *               like suddenly being able to do something you normally cannot.
 *   AMPLIFIES — you already carry that gate. Familiar, turned up louder.
 *   OPEN      — the gate lands where you have nothing. It arrives as other people's pressure or
 *               as a mood you mistake for your own.
 */
enum class HdTransitEffect { COMPLETES, AMPLIFIES, OPEN }

data class HdTransitGate(
    val body: HdBody,
    val gate: Int,
    val line: Int,
    val effect: HdTransitEffect,
    /** For COMPLETES: the channel that closes, and your own gate in it. */
    val completedChannel: HdChannel? = null,
    val yourGate: Int? = null
)

/**
 * @param longitudes current ecliptic longitude per [HdBody.key]; Earth and South Node are derived
 *                   by the caller exactly as in the natal calculation.
 */
fun transitGatesFor(chart: HumanDesignChart, longitudes: Map<String, Double>): List<HdTransitGate> {
    val hanging = hangingGatesOf(chart.activeGates, chart.definedCenters).associateBy { it.seeks }

    return HdBody.values().mapNotNull { body ->
        val lon = longitudes[body.key] ?: return@mapNotNull null
        val (gate, line) = gateLineFor(lon)
        val hang = hanging[gate]
        when {
            hang != null -> HdTransitGate(
                body, gate, line, HdTransitEffect.COMPLETES,
                completedChannel = hang.channel, yourGate = hang.gate
            )
            gate in chart.activeGates -> HdTransitGate(body, gate, line, HdTransitEffect.AMPLIFIES)
            else -> HdTransitGate(body, gate, line, HdTransitEffect.OPEN)
        }
    }
}
