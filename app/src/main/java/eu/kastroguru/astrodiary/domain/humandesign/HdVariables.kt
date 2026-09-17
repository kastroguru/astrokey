package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Variable — the four sub-line values ("the four arrows"): the PHS pair (Determination,
 * Environment) and the Rave-Psychology pair (Motivation, Perspective).
 *
 * Resolution below the line:
 *   gate   5.625°   (GATE_ARC)
 *   line   0.9375°  (LINE_ARC)      = gate / 6
 *   colour 0.15625° (COLOUR_ARC)    = line / 6
 *   tone   0.026°   (TONE_ARC)      = colour / 6
 *   base   0.0043°                  — NOT computed: it needs a birth time good to ~6 minutes,
 *                                     which is more than stored charts can honestly carry.
 *
 * Birth-time tolerance that follows (the Sun moves ~0.9856°/day):
 *   colour ≈ 3.8 h,  tone ≈ 38 min.  The Nodes crawl (~0.053°/day), so node-derived values
 *   tolerate several days. Determination and Environment are read to the tone, so they are the
 *   two that need a birth time accurate to about half an hour — see [needsExactBirthTime].
 *
 * Sources (fixed by the system, not by us):
 *   Determination ← colour+tone of the DESIGN Sun
 *   Environment   ← colour+tone of the DESIGN North Node
 *   Motivation    ← colour of the PERSONALITY Sun
 *   Perspective   ← colour of the PERSONALITY North Node
 */

const val COLOUR_ARC = LINE_ARC / 6.0     // 0.15625°
const val TONE_ARC = COLOUR_ARC / 6.0     // 0.0260416°

/** Gate, line, colour and tone of one longitude. Colour and tone are 1–6 like the line. */
data class HdSubLine(val gate: Int, val line: Int, val colour: Int, val tone: Int)

fun subLineFor(longitude: Double): HdSubLine {
    val off = ((longitude - WHEEL_START_DEG) % 360.0 + 360.0) % 360.0
    val idx = (off / GATE_ARC).toInt().coerceIn(0, 63)
    val gate = GATE_WHEEL[idx]
    val withinGate = off - idx * GATE_ARC
    val line = (withinGate / LINE_ARC).toInt().coerceIn(0, 5)
    val withinLine = withinGate - line * LINE_ARC
    val colour = (withinLine / COLOUR_ARC).toInt().coerceIn(0, 5)
    val withinColour = withinLine - colour * COLOUR_ARC
    val tone = (withinColour / TONE_ARC).toInt().coerceIn(0, 5)
    return HdSubLine(gate, line + 1, colour + 1, tone + 1)
}

/** Tones 1–3 read as strategic (left), 4–6 as receptive (right) — the arrow's direction. */
enum class HdArrow { LEFT, RIGHT }

fun arrowOf(tone: Int): HdArrow = if (tone <= 3) HdArrow.LEFT else HdArrow.RIGHT

// ── Determination (digestion) — colour of the Design Sun, split by tone ──────────
enum class HdDetermination {
    APPETITE_CONSECUTIVE, APPETITE_ALTERNATING,
    TASTE_OPEN, TASTE_CLOSED,
    THIRST_NERVOUS, THIRST_CALM,
    TOUCH_COLD, TOUCH_HOT,
    SOUND_HIGH, SOUND_LOW,
    LIGHT_DIRECT, LIGHT_INDIRECT
}

// ── Environment — colour of the Design North Node, split by tone ─────────────────
enum class HdEnvironment {
    CAVES_SELECTIVE, CAVES_WET,
    MARKETS_INTERNAL, MARKETS_EXTERNAL,
    KITCHENS_WET, KITCHENS_DRY,
    MOUNTAINS_ACTIVE, MOUNTAINS_PASSIVE,
    VALLEYS_NARROW, VALLEYS_WIDE,
    SHORES_NATURAL, SHORES_ARTIFICIAL
}

// ── Motivation — colour of the Personality Sun ───────────────────────────────────
enum class HdMotivation { FEAR, HOPE, DESIRE, NEED, GUILT, INNOCENCE }

// ── Perspective — colour of the Personality North Node ───────────────────────────
enum class HdPerspective { SURVIVAL, POSSIBILITY, POWER, WANTING, PROBABILITY, PERSONAL }

data class HdVariables(
    val determination: HdDetermination,
    val environment: HdEnvironment,
    val motivation: HdMotivation,
    val perspective: HdPerspective,
    val designSun: HdSubLine,
    val designNode: HdSubLine,
    val personalitySun: HdSubLine,
    val personalityNode: HdSubLine
) {
    val determinationArrow: HdArrow get() = arrowOf(designSun.tone)
    val environmentArrow: HdArrow get() = arrowOf(designNode.tone)
    val motivationArrow: HdArrow get() = arrowOf(personalitySun.tone)
    val perspectiveArrow: HdArrow get() = arrowOf(personalityNode.tone)
}

/**
 * A tone sits 0.026° wide; the Sun crosses that in ~38 minutes. When the stored birth time is a
 * round hour (or worse, unknown and stored as 12:00), the tone half of Determination and
 * Environment is a coin flip and the screen says so instead of pretending.
 */
fun needsExactBirthTime(hour: Int, minute: Int): Boolean = minute == 0 || minute == 30

private fun pick(colour: Int, tone: Int, first: HdDetermination, second: HdDetermination) =
    if (arrowOf(tone) == HdArrow.LEFT) first else second

fun determinationFor(sub: HdSubLine): HdDetermination = when (sub.colour) {
    1 -> pick(sub.colour, sub.tone, HdDetermination.APPETITE_CONSECUTIVE, HdDetermination.APPETITE_ALTERNATING)
    2 -> pick(sub.colour, sub.tone, HdDetermination.TASTE_OPEN, HdDetermination.TASTE_CLOSED)
    3 -> pick(sub.colour, sub.tone, HdDetermination.THIRST_NERVOUS, HdDetermination.THIRST_CALM)
    4 -> pick(sub.colour, sub.tone, HdDetermination.TOUCH_COLD, HdDetermination.TOUCH_HOT)
    5 -> pick(sub.colour, sub.tone, HdDetermination.SOUND_HIGH, HdDetermination.SOUND_LOW)
    else -> pick(sub.colour, sub.tone, HdDetermination.LIGHT_DIRECT, HdDetermination.LIGHT_INDIRECT)
}

fun environmentFor(sub: HdSubLine): HdEnvironment {
    val left = arrowOf(sub.tone) == HdArrow.LEFT
    return when (sub.colour) {
        1 -> if (left) HdEnvironment.CAVES_SELECTIVE else HdEnvironment.CAVES_WET
        2 -> if (left) HdEnvironment.MARKETS_INTERNAL else HdEnvironment.MARKETS_EXTERNAL
        3 -> if (left) HdEnvironment.KITCHENS_WET else HdEnvironment.KITCHENS_DRY
        4 -> if (left) HdEnvironment.MOUNTAINS_ACTIVE else HdEnvironment.MOUNTAINS_PASSIVE
        5 -> if (left) HdEnvironment.VALLEYS_NARROW else HdEnvironment.VALLEYS_WIDE
        else -> if (left) HdEnvironment.SHORES_NATURAL else HdEnvironment.SHORES_ARTIFICIAL
    }
}

fun motivationFor(sub: HdSubLine): HdMotivation = HdMotivation.values()[(sub.colour - 1).coerceIn(0, 5)]

fun perspectiveFor(sub: HdSubLine): HdPerspective = HdPerspective.values()[(sub.colour - 1).coerceIn(0, 5)]

fun variablesFor(
    designSunLong: Double,
    designNodeLong: Double,
    personalitySunLong: Double,
    personalityNodeLong: Double
): HdVariables {
    val ds = subLineFor(designSunLong)
    val dn = subLineFor(designNodeLong)
    val ps = subLineFor(personalitySunLong)
    val pn = subLineFor(personalityNodeLong)
    return HdVariables(
        determination = determinationFor(ds),
        environment = environmentFor(dn),
        motivation = motivationFor(ps),
        perspective = perspectiveFor(pn),
        designSun = ds, designNode = dn, personalitySun = ps, personalityNode = pn
    )
}
