package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Hanging gates — one half of a channel is active, the other half is not.
 *
 * This is the layer that answers "why do I keep attracting these people". A hanging gate is an
 * open request: the person carries one end of a circuit and looks, mostly unconsciously, for
 * whoever carries the other end. Nothing new is calculated — it falls straight out of
 * [HumanDesignChart.activeGates] and [CHANNELS].
 */
data class HdHangingGate(
    val gate: Int,          // the gate the person has
    val seeks: Int,         // the gate they do not have, on the far side of the channel
    val channel: HdChannel,
    /** True when the missing gate sits in a centre the person has no definition in at all. */
    val opensNewCentre: Boolean
)

fun hangingGatesOf(activeGates: Set<Int>, definedCentres: Set<HdCenter>): List<HdHangingGate> =
    CHANNELS.mapNotNull { ch ->
        val hasA = ch.a in activeGates
        val hasB = ch.b in activeGates
        if (hasA == hasB) return@mapNotNull null          // both or neither — not hanging
        val mine = if (hasA) ch.a else ch.b
        val missing = if (hasA) ch.b else ch.a
        val farCentre = centerOfGate(missing)
        HdHangingGate(
            gate = mine,
            seeks = missing,
            channel = ch,
            opensNewCentre = farCentre != null && farCentre !in definedCentres
        )
    }.sortedBy { it.gate }
