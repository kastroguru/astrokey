package eu.kastroguru.astrodiary.ui.transit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentTransitBinding
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.chart.localizedName
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransitFragment : Fragment() {

    private var _binding: FragmentTransitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransitViewModel by viewModels()
    private lateinit var aspectAdapter: AspectAdapter

    // Step chip buttons (built programmatically)
    private val chipButtons = mutableListOf<MaterialButton>()
    private var natalSpinnerNames: List<String> = emptyList()
    private var lastErrorShown: String? = null
    private val pdDateFmt = java.text.SimpleDateFormat("MM.yyyy", java.util.Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aspectAdapter = AspectAdapter()
        binding.rvAspects.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(requireContext(), androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
        )
        aspectAdapter.onItemClick = { aspect ->
            val state = viewModel.state.value
            findNavController().navigate(
                R.id.action_transitFragment_to_aspectDetail,
                bundleOf(
                    "natalPlanetKey"   to aspect.natalPlanet,
                    "transitPlanetKey" to aspect.transitPlanet,
                    "natalDataId"      to (state.natalData?.id ?: 0L),
                    "transitMs"        to viewModel.transitMs,
                    "isPd"             to !aspect.directedLon.isNaN(),
                    "directedLon"      to if (aspect.directedLon.isNaN()) -1f else aspect.directedLon.toFloat(),
                    "aspectDeg"        to aspect.exactDegree
                )
            )
        }
        binding.rvAspects.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = aspectAdapter
        }

        // ── Build step chips ──────────────────────────────────────────────────
        buildStepChips()

        // ── Method selector: Transits ↔ Primary directions ─────────────────────
        val methodAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            listOf(getString(R.string.pd_method_transits), getString(R.string.pd_method_directions))
        )
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMethod.adapter = methodAdapter
        binding.spinnerMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setMode(if (position == 1) TransitMode.PRIMARY_DIRECTIONS else TransitMode.TRANSITS)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // ── Navigation buttons ────────────────────────────────────────────────
        binding.btnBack.setOnClickListener    { viewModel.goBack() }
        binding.btnForward.setOnClickListener { viewModel.goForward() }
        binding.btnRefresh.setOnClickListener { viewModel.goNow() }

        // Tap the date label → open date + time picker
        binding.tvTransitDate.setOnClickListener { showDateTimePicker() }

        // ── Natal planet click ────────────────────────────────────────────────
        binding.aspectsChart.onNatalPlanetClick = { planet ->
            Snackbar.make(binding.root, "${planet.glyph} ${planet.localizedName(requireContext())}", Snackbar.LENGTH_SHORT).show()
        }

        // ── State collection ──────────────────────────────────────────────────
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun buildStepChips() {
        chipButtons.clear()
        val ctx = requireContext()
        val container = binding.chipGroup
        container.removeAllViews()

        TransitStep.values().forEach { step ->
            val btn = MaterialButton(ctx, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = getString(step.labelRes)
                textSize = 11f
                setPadding(16, 0, 16, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginEnd = 4 }
                setOnClickListener { viewModel.selectStep(step) }
            }
            container.addView(btn)
            chipButtons += btn
        }

        // Scroll to show the default selected chip (DAY) after layout is complete
        val defaultIdx = TransitStep.values().indexOf(TransitStep.DAY)
        if (defaultIdx >= 0 && defaultIdx < chipButtons.size) {
            binding.chipScrollView.post {
                val chip = chipButtons[defaultIdx]
                val scrollX = (chip.left - binding.chipScrollView.width / 2 + chip.width / 2)
                    .coerceAtLeast(0)
                binding.chipScrollView.scrollTo(scrollX, 0)
            }
        }
    }

    private fun updateUI(state: TransitUiState) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        // Surface calculation errors (once per distinct message) instead of failing silently.
        val err = state.errorMessage
        if (err != null && err != lastErrorShown) {
            Snackbar.make(binding.root, err, Snackbar.LENGTH_LONG).show()
            lastErrorShown = err
        } else if (err == null) {
            lastErrorShown = null
        }

        // Transit date label
        binding.tvTransitDate.text = state.dateLabel

        // ── Mode: Transits (linear chart) vs Primary directions (biwheel) ──────
        val pd = state.mode == TransitMode.PRIMARY_DIRECTIONS
        binding.aspectsChart.visibility   = if (pd) View.GONE else View.VISIBLE
        binding.tvTransitSummary.visibility = View.VISIBLE
        binding.pdWheel.visibility         = if (pd) View.VISIBLE else View.GONE
        var pdAspectItems = emptyList<TransitAspect>()
        if (pd) {
            binding.pdWheel.ascendant       = state.pdAscendant
            binding.pdWheel.natalLongitudes = state.pdNatalLongitudes
            binding.pdWheel.directed        = state.pdDirected
            binding.pdWheel.houseCusps      = state.pdCusps
            // Readout below the wheel: current age + directed arc, plus the marker legend.
            binding.tvTransitSummary.text = getString(
                R.string.pd_age_arc,
                String.format("%.1f", state.pdAgeYears),
                String.format("%.1f", state.pdCurrentArc),
            ) + "\n" + getString(R.string.pd_legend)
            // Active = within a 1° orb of arc: |direction's arc − arc reached at the current age|.
            val nowArc = state.pdCurrentArc
            val byKey = state.pdDirected.associateBy { it.key }
            val active = state.pdDirections.filter { kotlin.math.abs(kotlin.math.abs(it.arc) - nowArc) < 1.0 }
            binding.pdWheel.activeLinks = active.mapNotNull { d ->
                val dp = byKey[d.promissor] ?: return@mapNotNull null
                val lon = if (d.isDirect) dp.directLon else dp.converseLon
                eu.kastroguru.astrodiary.ui.chart.PrimaryDirectionsWheelView.ActiveLink(
                    d.promissor, d.significator, lon, d.aspectAngle, d.isDirect
                )
            }
            // Active-aspects list (same adapter/screen as transits): promissor → significator,
            // deduped per pair+aspect, tightest orb first; shows the perfection date.
            val natalBirth = state.natalData
            pdAspectItems = active
                .groupBy { Triple(it.promissor, it.aspectAngle, it.significator) }
                .map { (_, g) -> g.minByOrNull { kotlin.math.abs(kotlin.math.abs(it.arc) - nowArc) }!! }
                .map { d ->
                    val orb = kotlin.math.abs(kotlin.math.abs(d.arc) - nowArc)
                    val dp = byKey[d.promissor]
                    val dirLon = when {
                        dp == null   -> Double.NaN
                        d.isDirect   -> dp.directLon
                        else         -> dp.converseLon
                    }
                    val perfLabel = natalBirth?.let { nb ->
                        val cal = java.util.Calendar.getInstance().apply {
                            set(nb.year, nb.month - 1, nb.day, 0, 0, 0)
                            add(java.util.Calendar.DAY_OF_YEAR, (d.years * 365.2422).toInt())
                        }
                        getString(R.string.pd_perfects, pdDateFmt.format(cal.time))
                    }
                    TransitAspect(
                        transitPlanet = d.promissor,
                        natalPlanet   = d.significator,
                        aspectName    = pdAspectName(d.aspectAngle),
                        orb           = Math.round(orb * 10.0) / 10.0,
                        isApplying    = nowArc < kotlin.math.abs(d.arc),  // not yet perfected = applying
                        exactDegree   = d.aspectAngle,
                        directedLon   = dirLon,
                        perfectionLabel = perfLabel,
                    )
                }
                .sortedBy { it.orb }
        }
        binding.tvNoAspects.visibility = if (pd && pdAspectItems.isEmpty()) View.VISIBLE else View.GONE

        // Highlight selected step chip
        chipButtons.forEachIndexed { idx, btn ->
            val isSelected = TransitStep.values()[idx] == state.selectedStep
            btn.setStrokeColorResource(if (isSelected) R.color.gold else R.color.card_stroke)
            btn.setTextColor(if (isSelected) ContextCompat.getColor(requireContext(), R.color.gold)
                             else Color.GRAY)
        }

        // Natal spinner — build the adapter ONCE per data set (rebuilding it every state
        // emission re-fires onItemSelected → selectNatal → recompute → new state → … a loop
        // that makes the chart jump). Only update the selection when it actually differs.
        if (state.allBirthData.isNotEmpty()) {
            val names = state.allBirthData.map { it.name }
            if (names != natalSpinnerNames) {
                natalSpinnerNames = names
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerNatal.onItemSelectedListener = null
                binding.spinnerNatal.adapter = adapter
                binding.spinnerNatal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val list = viewModel.state.value.allBirthData
                        if (position in list.indices) viewModel.selectNatal(list[position])
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            val selectedIndex = state.allBirthData.indexOfFirst { it.id == state.natalData?.id }
            if (selectedIndex >= 0 && binding.spinnerNatal.selectedItemPosition != selectedIndex) {
                binding.spinnerNatal.setSelection(selectedIndex)
            }
        }

        // Wire AspectsChartView
        val natal = state.natalData
        val transit = state.transitAstro
        if (natal != null && transit != null) {
            binding.aspectsChart.natalCusps = listOf(
                natal.cusp1, natal.cusp2, natal.cusp3, natal.cusp4,
                natal.cusp5, natal.cusp6, natal.cusp7, natal.cusp8,
                natal.cusp9, natal.cusp10, natal.cusp11, natal.cusp12
            )
            binding.aspectsChart.natalPlanets = mapOf(
                "sun" to natal.sunD, "moon" to natal.moonD,
                "mercury" to natal.mercuryD, "venus" to natal.venusD,
                "mars" to natal.marsD, "jupiter" to natal.jupiterD,
                "saturn" to natal.saturnD, "uranus" to natal.uranusD,
                "neptune" to natal.neptuneD, "pluto" to natal.plutoD,
                "chiron" to natal.chironD, "rahu" to natal.rahuD,
                "lilith" to natal.lilithD
            )
            binding.aspectsChart.transitData = transit
            binding.tvNatalInfo.text = "${natal.name} · ${natal.day}.${natal.month}.${natal.year} · ${natal.city}"
        }

        // Transit planet summary
        if (transit != null) {
            val sb = StringBuilder()
            Planet.values().forEach { planet ->
                val pos = transit.planets[planet.key] ?: return@forEach
                val sign = try { ZodiacSign.fromId(pos.sign) } catch (e: Exception) { return@forEach }
                sb.append("${planet.glyph}${sign.symbol}${pos.degreeInSign}°${pos.minutes}'  ")
            }
            binding.tvTransitSummary.text = sb.toString()
        }

        aspectAdapter.submitList(if (pd) pdAspectItems else state.aspects)
    }

    private fun pdAspectName(angle: Int): String = when (angle) {
        0 -> "Conjunction"; 60 -> "Sextile"; 90 -> "Square"; 120 -> "Trine"; 180 -> "Opposition"; else -> "$angle"
    }

    private fun showDateTimePicker() {
        val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = viewModel.transitMs
        }
        DatePickerDialog(requireContext(), { _, y, m, d ->
            TimePickerDialog(requireContext(), { _, h, min ->
                viewModel.setTransitDateTime(y, m + 1, d, h, min)
            }, cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE), true).show()
        }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH),
           cal.get(java.util.Calendar.DAY_OF_MONTH)).show()
    }

    override fun onResume() {
        super.onResume()
        // Re-apply settings in case the user changed them in SettingsFragment.
        // MutableStateFlow won't re-emit if AstroData is structurally equal,
        // so we force a rebuild here directly.
        binding.aspectsChart.refreshSettings()
        viewModel.startLiveRefresh()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopLiveRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
