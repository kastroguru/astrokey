package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Fixed Human Design data: the Rave Mandala gate wheel, the 9 centers, the 36 channels,
 * and the gate → center mapping. None of this is calculated — it is the invariant
 * structure of the bodygraph.
 */

// ── The 9 centers ───────────────────────────────────────────────────────────
enum class HdCenter { HEAD, AJNA, THROAT, G, HEART, SACRAL, SPLEEN, SOLAR_PLEXUS, ROOT }

// Motors push energy; relevant for Type determination.
val MOTOR_CENTERS = setOf(HdCenter.SACRAL, HdCenter.HEART, HdCenter.SOLAR_PLEXUS, HdCenter.ROOT)

// ── Gate → center map (all 64 gates) ──────────────────────────────────────────
val GATE_CENTER: Map<Int, HdCenter> = buildMap {
    // Head
    listOf(64, 61, 63).forEach { put(it, HdCenter.HEAD) }
    // Ajna
    listOf(47, 24, 4, 17, 11, 43).forEach { put(it, HdCenter.AJNA) }
    // Throat
    listOf(62, 23, 56, 35, 12, 45, 33, 8, 31, 20, 16).forEach { put(it, HdCenter.THROAT) }
    // G (Self / Identity)
    listOf(7, 1, 13, 25, 46, 2, 15, 10).forEach { put(it, HdCenter.G) }
    // Heart / Ego / Will
    listOf(21, 40, 26, 51).forEach { put(it, HdCenter.HEART) }
    // Sacral
    listOf(34, 5, 14, 29, 59, 9, 3, 42, 27).forEach { put(it, HdCenter.SACRAL) }
    // Spleen
    listOf(48, 57, 44, 50, 32, 28, 18).forEach { put(it, HdCenter.SPLEEN) }
    // Solar Plexus (Emotional)
    listOf(36, 22, 37, 6, 49, 55, 30).forEach { put(it, HdCenter.SOLAR_PLEXUS) }
    // Root
    listOf(58, 38, 54, 53, 60, 52, 19, 39, 41).forEach { put(it, HdCenter.ROOT) }
}

fun centerOfGate(gate: Int): HdCenter? = GATE_CENTER[gate]

// ── The 36 channels (gate pairs) ──────────────────────────────────────────────
data class HdChannel(val a: Int, val b: Int, val name: String)

val CHANNELS: List<HdChannel> = listOf(
    HdChannel(1, 8,   "Inspiration"),
    HdChannel(2, 14,  "The Beat"),
    HdChannel(3, 60,  "Mutation"),
    HdChannel(4, 63,  "Logic"),
    HdChannel(5, 15,  "Rhythm"),
    HdChannel(6, 59,  "Mating"),
    HdChannel(7, 31,  "The Alpha"),
    HdChannel(9, 52,  "Concentration"),
    HdChannel(10, 20, "Awakening"),
    HdChannel(10, 34, "Exploration"),
    HdChannel(10, 57, "Perfected Form"),
    HdChannel(11, 56, "Curiosity"),
    HdChannel(12, 22, "Openness"),
    HdChannel(13, 33, "The Prodigal"),
    HdChannel(16, 48, "The Wavelength"),
    HdChannel(17, 62, "Acceptance"),
    HdChannel(18, 58, "Judgment"),
    HdChannel(19, 49, "Synthesis"),
    HdChannel(20, 34, "Charisma"),
    HdChannel(20, 57, "The Brainwave"),
    HdChannel(21, 45, "Money"),
    HdChannel(23, 43, "Structuring"),
    HdChannel(24, 61, "Awareness"),
    HdChannel(25, 51, "Initiation"),
    HdChannel(26, 44, "Surrender"),
    HdChannel(27, 50, "Preservation"),
    HdChannel(28, 38, "Struggle"),
    HdChannel(29, 46, "Discovery"),
    HdChannel(30, 41, "Recognition"),
    HdChannel(32, 54, "Transformation"),
    HdChannel(34, 57, "Power"),
    HdChannel(35, 36, "Transitoriness"),
    HdChannel(37, 40, "Community"),
    HdChannel(39, 55, "Emoting"),
    HdChannel(42, 53, "Maturation"),
    HdChannel(47, 64, "Abstraction")
)

// ── The gate wheel (Rave Mandala) ─────────────────────────────────────────────
//
// The 64 gates are arranged around the ecliptic, each spanning 5.625° (5°37'30").
// GATE_WHEEL lists them in zodiacal order; GATE_WHEEL[0] begins at WHEEL_START_DEG.
//
// VERIFY: WHEEL_START_DEG is the wheel's anchor offset relative to 0° Aries. If a
// known chart's Sun gate comes out wrong, this single constant (and only this) is
// what to adjust — nudging it by ±5.625° shifts by one whole gate; ±0.9375° shifts
// by one line. The ORDER below is the standard mandala sequence.
const val GATE_ARC = 5.625      // degrees per gate (360 / 64)
const val LINE_ARC = 0.9375     // degrees per line (GATE_ARC / 6)
const val WHEEL_START_DEG = 358.25  // longitude where GATE_WHEEL[0] (gate 25) begins

val GATE_WHEEL: IntArray = intArrayOf(
    25, 17, 21, 51, 42, 3, 27, 24, 2, 23, 8, 20, 16, 35, 45, 12,
    15, 52, 39, 53, 62, 56, 31, 33, 7, 4, 29, 59, 40, 64, 47, 6,
    46, 18, 48, 57, 32, 50, 28, 44, 1, 43, 14, 34, 9, 5, 26, 11,
    10, 58, 38, 54, 61, 60, 41, 19, 13, 49, 30, 55, 37, 63, 22, 36
)

/** Maps an ecliptic longitude (0–360°) to its Human Design gate (1–64) and line (1–6). */
fun gateLineFor(longitude: Double): Pair<Int, Int> {
    val off = ((longitude - WHEEL_START_DEG) % 360.0 + 360.0) % 360.0
    val idx = (off / GATE_ARC).toInt().coerceIn(0, 63)
    val gate = GATE_WHEEL[idx]
    val within = off - idx * GATE_ARC
    val line = (within / LINE_ARC).toInt().coerceIn(0, 5) + 1
    return gate to line
}

// ── HD bodies (13 activations per side) ───────────────────────────────────────
// key, glyph, display name, display order (top → bottom in the standard chart)
enum class HdBody(val key: String, val glyph: String, val display: String) {
    SUN("sun", "☉", "Sun"),
    EARTH("earth", "⊕", "Earth"),
    NORTH_NODE("north_node", "☊", "North Node"),
    SOUTH_NODE("south_node", "☋", "South Node"),
    MOON("moon", "☽", "Moon"),
    MERCURY("mercury", "☿", "Mercury"),
    VENUS("venus", "♀", "Venus"),
    MARS("mars", "♂", "Mars"),
    JUPITER("jupiter", "♃", "Jupiter"),
    SATURN("saturn", "♄", "Saturn"),
    URANUS("uranus", "♅", "Uranus"),
    NEPTUNE("neptune", "♆", "Neptune"),
    PLUTO("pluto", "♇", "Pluto")
}

// ── Result model ──────────────────────────────────────────────────────────────
enum class HdType { MANIFESTOR, GENERATOR, MANIFESTING_GENERATOR, PROJECTOR, REFLECTOR }
enum class HdAuthority { EMOTIONAL, SACRAL, SPLENIC, EGO, SELF_PROJECTED, MENTAL, LUNAR }
enum class HdDefinition { NONE, SINGLE, SPLIT, TRIPLE_SPLIT, QUADRUPLE_SPLIT }

/** One planetary activation: which body, its longitude, and the resulting gate.line. */
data class HdActivation(
    val body: HdBody,
    val longitude: Double,
    val gate: Int,
    val line: Int
)

data class HumanDesignChart(
    val personality: List<HdActivation>,   // conscious (birth)
    val design: List<HdActivation>,         // unconscious (~88° earlier)
    val activeGates: Set<Int>,
    val definedChannels: List<HdChannel>,
    val definedCenters: Set<HdCenter>,
    val type: HdType,
    val authority: HdAuthority,
    val profilePersonalityLine: Int,        // line of Personality Sun
    val profileDesignLine: Int,             // line of Design Sun
    val definition: HdDefinition
)
