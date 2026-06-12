package eu.kastroguru.astrodiary.ui.transit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.AspectPrefs
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.calculator.PrimaryDirectionsCalculator
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.Planet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.math.abs

data class TransitAspect(
    val transitPlanet: String,
    val natalPlanet: String,
    val aspectName: String,
    val orb: Double,
    val isApplying: Boolean,
    val exactDegree: Int,
    /** Primary-directions only: the promissor's directed longitude (NaN for transit aspects). */
    val directedLon: Double = Double.NaN,
)

enum class TransitStep(val label: String, val millis: Long) {
    MINUTE("1 мин",     60_000L),
    MIN5  ("5 мин",   5*60_000L),
    MIN10 ("10 мин", 10*60_000L),
    MIN15 ("15 мин", 15*60_000L),
    HOUR  ("Час",    60*60_000L),
    DAY   ("Ден",  24*60*60_000L),
    WEEK  ("Седмица",  7*24*60*60_000L),
    MONTH ("Месец",   30*24*60*60_000L),
    MONTH3("3 м-ца",  91*24*60*60_000L),
    YEAR  ("Година", 365*24*60*60_000L)
}

enum class TransitMode { TRANSITS, PRIMARY_DIRECTIONS }

data class TransitUiState(
    val natalData: BirthDataEntity? = null,
    val transitAstro: AstroData? = null,
    val aspects: List<TransitAspect> = emptyList(),
    val allBirthData: List<BirthDataEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val dateLabel: String = "",
    val selectedStep: TransitStep = TransitStep.DAY,
    val isLive: Boolean = true,   // true = follow real time; false = manual navigation
    // ── Primary-directions mode ──
    val mode: TransitMode = TransitMode.TRANSITS,
    val pdAscendant: Double = 0.0,
    val pdNatalLongitudes: Map<String, Double> = emptyMap(),
    val pdDirected: List<PrimaryDirectionsCalculator.DirectedPosition> = emptyList(),
    val pdDirections: List<PrimaryDirectionsCalculator.PrimaryDirection> = emptyList(),
    val pdAgeYears: Double = 0.0,
    val pdCusps: List<Double> = emptyList(),
    val pdCurrentArc: Double = 0.0,
)

@HiltViewModel
class TransitViewModel @Inject constructor(
    private val repository: BirthDataRepository,
    private val calculator: AstroCalculator,
    private val aspectPrefs: AspectPrefs,
    private val chartDisplayPrefs: ChartDisplayPrefs,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("transit_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(TransitUiState())
    val state: StateFlow<TransitUiState> = _state.asStateFlow()

    // The transit date/time being displayed (UTC)
    var transitMs: Long = System.currentTimeMillis()
        private set
    private val dateFmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private var refreshJob: Job? = null
    private val pdCalc = PrimaryDirectionsCalculator()
    private val calcMutex = Mutex()   // serialize shared SwissEph access

    init {
        viewModelScope.launch {
            repository.getAll().collect { list ->
                _state.value = _state.value.copy(allBirthData = list)
                if (_state.value.natalData == null && list.isNotEmpty()) {
                    // Restore last-used natal chart; fall back to first if not found
                    val lastId = prefs.getLong("last_natal_id", -1L)
                    val toSelect = if (lastId >= 0) list.find { it.id == lastId } else null
                    selectNatal(toSelect ?: list[0])
                }
            }
        }
    }

    fun selectNatal(entity: BirthDataEntity) {
        prefs.edit().putLong("last_natal_id", entity.id).apply()  // persist choice
        _state.value = _state.value.copy(natalData = entity)
        calculate()
    }

    fun selectStep(step: TransitStep) {
        _state.value = _state.value.copy(selectedStep = step)
    }

    fun setMode(mode: TransitMode) {
        if (_state.value.mode == mode) return
        _state.value = _state.value.copy(mode = mode)
        calculate()
    }

    fun goBack() {
        transitMs -= _state.value.selectedStep.millis
        _state.value = _state.value.copy(isLive = false)
        calculate()
    }

    fun goForward() {
        transitMs += _state.value.selectedStep.millis
        _state.value = _state.value.copy(isLive = false)
        calculate()
    }

    fun goNow() {
        transitMs = System.currentTimeMillis()
        _state.value = _state.value.copy(isLive = true)
        calculate()
    }

    /** Set an explicit UTC date/time for the transit calculation. */
    fun setTransitDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(year, month - 1, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }
        transitMs = cal.timeInMillis
        _state.value = _state.value.copy(isLive = false)
        calculate()
    }

    fun calculateNow() {
        if (_state.value.isLive) {
            transitMs = System.currentTimeMillis()
        }
        calculate()
    }

    private fun calculate() {
        val natal = _state.value.natalData ?: return
        val mode  = _state.value.mode
        val live  = _state.value.isLive
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // All Swiss-Ephemeris work runs OFF the main thread (PD computes thousands of
                // positions) and is serialized — the shared SwissEph instance is not thread-safe.
                val next = withContext(Dispatchers.Default) {
                    calcMutex.withLock {
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = transitMs }
                        val year  = cal.get(Calendar.YEAR)
                        val month = cal.get(Calendar.MONTH) + 1
                        val day   = cal.get(Calendar.DAY_OF_MONTH)
                        val hour  = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0
                        val label = dateFmt.format(cal.time) + if (live) context.getString(R.string.now_live_label) else ""

                        if (mode == TransitMode.PRIMARY_DIRECTIONS) {
                            val pd = computePrimaryDirections(natal, year, month, day, hour)
                            _state.value.copy(
                                isLoading = false, dateLabel = label, errorMessage = null,
                                pdAscendant = pd.asc, pdNatalLongitudes = pd.natal,
                                pdDirected = pd.directed, pdDirections = pd.directions, pdAgeYears = pd.age,
                                pdCusps = pd.cusps, pdCurrentArc = pd.currentArc,
                            )
                        } else {
                            val transitAstro = calculator.calculate(year, month, day, hour, natal.latitude, natal.longitude)
                            val aspects = calculateAspects(transitAstro, natal)
                            _state.value.copy(
                                transitAstro = transitAstro, aspects = aspects,
                                isLoading = false, dateLabel = label, errorMessage = null,
                            )
                        }
                    }
                }
                _state.value = next
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    // ── Primary directions ────────────────────────────────────────────────────
    private data class PdResult(
        val asc: Double,
        val natal: Map<String, Double>,
        val directed: List<PrimaryDirectionsCalculator.DirectedPosition>,
        val directions: List<PrimaryDirectionsCalculator.PrimaryDirection>,
        val age: Double,
        val cusps: List<Double>,
        val currentArc: Double,
    )

    private fun computePrimaryDirections(natal: BirthDataEntity, year: Int, month: Int, day: Int, hour: Double): PdResult {
        val birthJd = calculator.julianDay(natal.yearUtc, natal.monthUtc, natal.dayUtc, natal.hourUtc + natal.minutesUtc / 60.0)
        val selJd   = calculator.julianDay(year, month, day, hour)
        val age     = ((selJd - birthJd) / 365.2421904).coerceAtLeast(0.0)

        val eq  = calculator.computeEquatorial(natal.yearUtc, natal.monthUtc, natal.dayUtc, natal.hourUtc, natal.minutesUtc, natal.latitude, natal.longitude)
        val arc = calculator.directedArc(birthJd, age)

        // House cusps for the configured house system (the natal cusps stored on the entity are Placidus).
        val cusps = calculator.recalculateCusps(
            natal.yearUtc, natal.monthUtc, natal.dayUtc, natal.hourUtc, natal.minutesUtc,
            natal.latitude, natal.longitude, chartDisplayPrefs.houseSystemChar,
        )
        val asc = cusps[0]; val mc = cusps[9]

        // Bodies included per the "displayed aspects of objects" settings.
        // Sun..Pluto are always on; Chiron/Lilith/Rahu and the angles are toggleable.
        val planetLon = LinkedHashMap<String, Double>().apply {
            put("sun", natal.sunD); put("moon", natal.moonD); put("mercury", natal.mercuryD)
            put("venus", natal.venusD); put("mars", natal.marsD); put("jupiter", natal.jupiterD)
            put("saturn", natal.saturnD); put("uranus", natal.uranusD); put("neptune", natal.neptuneD)
            put("pluto", natal.plutoD)
            if (aspectPrefs.includeChiron) put("chiron", natal.chironD)
            if (aspectPrefs.includeRahu)   put("rahu", natal.rahuD)
            if (aspectPrefs.includeLilith) put("lilith", natal.lilithD)
        }
        val angles = LinkedHashMap<String, Double>().apply {
            if (aspectPrefs.includeAsc) put("asc", asc)
            if (aspectPrefs.includeMc)  put("mc", mc)
            if (aspectPrefs.includeDsc) put("desc", (asc + 180.0) % 360.0)
            if (aspectPrefs.includeIc)  put("ic", (mc + 180.0) % 360.0)
        }
        val allLon = LinkedHashMap(planetLon).apply { putAll(angles) }
        val directed = pdCalc.directedPositions(planetLon, eq.obliquity, arc)
        val toYears = calculator.trueSolarArcConverter(birthJd, maxYears = 120)  // built once, then O(1) per direction
        val directions = pdCalc.calculate(
            eq = eq, longitudes = allLon,
            promissors = planetLon.keys.toList(),
            significators = allLon.keys.toList(),
            arcToYears = toYears,
            includeConverse = true, maxYears = 120.0,
        )
        return PdResult(asc, allLon, directed, directions, age, cusps, arc)
    }

    fun startLiveRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                if (_state.value.isLive) {
                    transitMs = System.currentTimeMillis()
                    calculate()
                }
                delay(60_000)
            }
        }
    }

    fun stopLiveRefresh() { refreshJob?.cancel() }

    // ── Aspect calculation ────────────────────────────────────────────────────
    private fun calculateAspects(transit: AstroData, natal: BirthDataEntity): List<TransitAspect> {
        val natalPlanets = extractNatalPlanets(natal)
        val aspectDefs = listOf(
            Triple(0, "Conjunction", 2.0), Triple(60, "Sextile", 2.0),
            Triple(90, "Square", 2.0),    Triple(120, "Trine", 2.0),
            Triple(150, "Quincunx", 2.0), Triple(180, "Opposition", 2.0)
        )
        // Mirror all filters from the chart view so the list stays in sync
        val aspectPrefs = context.getSharedPreferences("aspect_settings", Context.MODE_PRIVATE)
        val excluded = buildSet<String> {
            if (!aspectPrefs.getBoolean("chiron", true)) add("chiron")
            if (!aspectPrefs.getBoolean("lilith", true)) add("lilith")
            if (!aspectPrefs.getBoolean("rahu",   true)) add("rahu")
        }
        val hidePersonal = aspectPrefs.getBoolean("hide_personal_transits", false)
        val personalKeys = setOf("sun", "moon", "mercury", "venus", "mars")
        val filteredNatalPlanets = natalPlanets.filterKeys { it !in excluded }

        val result = mutableListOf<TransitAspect>()
        for (tPlanet in Planet.values()) {
            if (tPlanet.key in excluded) continue
            if (hidePersonal && tPlanet.key in personalKeys) continue
            val tPos = transit.planets[tPlanet.key] ?: continue
            for ((nKey, nDeg) in filteredNatalPlanets) {
                for ((aspectDeg, aspectName, maxOrb) in aspectDefs) {
                    val diff = angleDiff(tPos.absoluteDegree, nDeg)
                    val orb  = abs(diff - aspectDeg).let { minOf(it, abs(diff - (360 - aspectDeg))) }
                    if (orb <= maxOrb) {
                        result += TransitAspect(tPlanet.key, nKey, aspectName,
                            Math.round(orb * 10.0) / 10.0,
                            diff < aspectDeg, aspectDeg)
                    }
                }
            }
        }
        return result.sortedBy { it.orb }
    }

    private fun angleDiff(a: Double, b: Double): Double {
        val raw = abs(a - b) % 360.0; return if (raw > 180) 360 - raw else raw
    }

    private fun extractNatalPlanets(natal: BirthDataEntity): Map<String, Double> = mapOf(
        "sun" to natal.sunD, "moon" to natal.moonD, "mercury" to natal.mercuryD,
        "venus" to natal.venusD, "mars" to natal.marsD, "jupiter" to natal.jupiterD,
        "saturn" to natal.saturnD, "uranus" to natal.uranusD, "neptune" to natal.neptuneD,
        "pluto" to natal.plutoD, "chiron" to natal.chironD, "rahu" to natal.rahuD,
        "lilith" to natal.lilithD
    )
}
