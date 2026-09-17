package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Connection chart — two people's gates laid over the same 36 channels.
 *
 * Per channel there are four classical states, and they are the whole point of the feature: they
 * say *how* a channel is shared, which is what decides whether it feels like attraction, like
 * pressure, or like nothing at all.
 *
 *   ELECTROMAGNETIC — each person holds a different half. The channel only exists when they are
 *                     together: the strongest pull, and the classic source of "I can't explain it".
 *   COMPANIONSHIP   — both hold the whole channel. Easy, familiar, and mutually reinforcing;
 *                     also where neither of them grows.
 *   DOMINANCE       — one holds the whole channel, the other holds neither gate. The one without
 *                     it simply receives that energy; there is nothing to negotiate.
 *   COMPROMISE      — one holds the whole channel, the other holds exactly one gate of it. The
 *                     one with the single gate is the one who bends.
 */
enum class HdConnectionState { ELECTROMAGNETIC, COMPANIONSHIP, DOMINANCE_A, DOMINANCE_B, COMPROMISE_A, COMPROMISE_B }

/** The four states as text keys — A/B only says who, not what it feels like. */
enum class HdConnectionKind { ELECTROMAGNETIC, COMPANIONSHIP, DOMINANCE, COMPROMISE }

val HdConnectionState.kind: HdConnectionKind
    get() = when (this) {
        HdConnectionState.ELECTROMAGNETIC -> HdConnectionKind.ELECTROMAGNETIC
        HdConnectionState.COMPANIONSHIP -> HdConnectionKind.COMPANIONSHIP
        HdConnectionState.DOMINANCE_A, HdConnectionState.DOMINANCE_B -> HdConnectionKind.DOMINANCE
        HdConnectionState.COMPROMISE_A, HdConnectionState.COMPROMISE_B -> HdConnectionKind.COMPROMISE
    }

/** [byWhom] is the person the state is named after: A or B. Null for the symmetrical states. */
data class HdChannelConnection(
    val channel: HdChannel,
    val state: HdConnectionState,
    /** For ELECTROMAGNETIC: the gate A brings. Null for the other states. */
    val gateFromA: Int? = null,
    val gateFromB: Int? = null
)

data class HdConnectionAnalysis(
    val electromagnetic: List<HdChannelConnection>,
    val companionship: List<HdChannelConnection>,
    val dominance: List<HdChannelConnection>,
    val compromise: List<HdChannelConnection>,
    /** Centres defined once both charts are laid on top of each other. */
    val compositeCentres: Set<HdCenter>,
    /** Centres neither of them defines — the pair's shared blind spot. */
    val bothOpenCentres: Set<HdCenter>,
    /** Centres A defines and B does not: A conditions B here (and vice versa). */
    val aConditionsB: Set<HdCenter>,
    val bConditionsA: Set<HdCenter>
) {
    val all: List<HdChannelConnection>
        get() = electromagnetic + companionship + dominance + compromise
}

fun analyseConnection(a: HumanDesignChart, b: HumanDesignChart): HdConnectionAnalysis {
    val links = CHANNELS.mapNotNull { ch -> classify(ch, a.activeGates, b.activeGates) }

    val compositeGates = a.activeGates + b.activeGates
    val compositeChannels = CHANNELS.filter { it.a in compositeGates && it.b in compositeGates }
    val compositeCentres = compositeChannels
        .flatMap { listOfNotNull(centerOfGate(it.a), centerOfGate(it.b)) }
        .toSet()

    return HdConnectionAnalysis(
        electromagnetic = links.filter { it.state == HdConnectionState.ELECTROMAGNETIC },
        companionship = links.filter { it.state == HdConnectionState.COMPANIONSHIP },
        dominance = links.filter { it.state == HdConnectionState.DOMINANCE_A || it.state == HdConnectionState.DOMINANCE_B },
        compromise = links.filter { it.state == HdConnectionState.COMPROMISE_A || it.state == HdConnectionState.COMPROMISE_B },
        compositeCentres = compositeCentres,
        bothOpenCentres = HdCenter.values().toSet() - compositeCentres,
        aConditionsB = a.definedCenters - b.definedCenters,
        bConditionsA = b.definedCenters - a.definedCenters
    )
}

private fun classify(ch: HdChannel, ga: Set<Int>, gb: Set<Int>): HdChannelConnection? {
    val aCount = listOf(ch.a in ga, ch.b in ga).count { it }
    val bCount = listOf(ch.a in gb, ch.b in gb).count { it }

    return when {
        aCount == 2 && bCount == 2 -> HdChannelConnection(ch, HdConnectionState.COMPANIONSHIP)
        aCount == 2 && bCount == 0 -> HdChannelConnection(ch, HdConnectionState.DOMINANCE_A)
        bCount == 2 && aCount == 0 -> HdChannelConnection(ch, HdConnectionState.DOMINANCE_B)
        // The one holding a single gate is the one who compromises.
        aCount == 2 && bCount == 1 -> HdChannelConnection(ch, HdConnectionState.COMPROMISE_B)
        bCount == 2 && aCount == 1 -> HdChannelConnection(ch, HdConnectionState.COMPROMISE_A)
        // One gate each, and they are different gates: the channel is created between them.
        aCount == 1 && bCount == 1 -> {
            val fromA = if (ch.a in ga) ch.a else ch.b
            val fromB = if (ch.a in gb) ch.a else ch.b
            if (fromA == fromB) null    // the same single gate twice — nothing is bridged
            else HdChannelConnection(ch, HdConnectionState.ELECTROMAGNETIC, gateFromA = fromA, gateFromB = fromB)
        }
        else -> null
    }
}
