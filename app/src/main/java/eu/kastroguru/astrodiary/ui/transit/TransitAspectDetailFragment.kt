package eu.kastroguru.astrodiary.ui.transit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentTransitAspectDetailBinding
import eu.kastroguru.astrodiary.domain.humandesign.TransitInterpretations
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.chart.localizedName
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransitAspectDetailFragment : Fragment() {

    private var _binding: FragmentTransitAspectDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransitAspectDetailViewModel by viewModels()

    private var isPd = false
    private var directedLon = Double.NaN   // primary directions: the promissor's directed longitude

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransitAspectDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val natalKey   = arguments?.getString("natalPlanetKey")   ?: return
        val transitKey = arguments?.getString("transitPlanetKey") ?: return
        val natalId    = arguments?.getLong("natalDataId")        ?: return
        val transitMs  = arguments?.getLong("transitMs")          ?: return
        isPd = arguments?.getBoolean("isPd") ?: false
        directedLon = (arguments?.getFloat("directedLon") ?: -1f).toDouble().let { if (it < 0) Double.NaN else it }

        viewModel.load(natalId, transitMs, natalKey, transitKey)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.isLoading || state.natal == null || state.transit == null) return@collect
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: FocusAspectState) {
        val natal   = state.natal!!
        val transit = state.transit!!

        // ── Build natal planet absolute-degree map ────────────────────────────
        val natalAbs = mapOf(
            "sun" to natal.sunD, "moon" to natal.moonD, "mercury" to natal.mercuryD,
            "venus" to natal.venusD, "mars" to natal.marsD, "jupiter" to natal.jupiterD,
            "saturn" to natal.saturnD, "uranus" to natal.uranusD, "neptune" to natal.neptuneD,
            "pluto" to natal.plutoD, "chiron" to natal.chironD, "rahu" to natal.rahuD,
            "lilith" to natal.lilithD
        )
        // In primary-directions mode the outer point is the promissor's DIRECTED position
        // (not its transit position): show only that point so the wheel reflects the direction.
        val transitAbs = if (isPd && !directedLon.isNaN())
            mapOf(state.focusTransitKey to directedLon)
        else transit.planets.mapValues { it.value.absoluteDegree }

        // ── Wire wheel view ───────────────────────────────────────────────────
        binding.focusWheelView.apply {
            focusNatalKey   = state.focusNatalKey
            focusTransitKey = state.focusTransitKey
            natalRulerKey   = state.natalRulerKey
            transitRulerKey = state.transitRulerKey
            natalCusps      = state.natalCusps
            this.natalAbs   = natalAbs
            this.transitAbs = transitAbs
        }

        // ── Title: "♅ Уран → ♂ Марс" ─────────────────────────────────────────
        val nPlanet = Planet.values().find { it.key == state.focusNatalKey }
        val tPlanet = Planet.values().find { it.key == state.focusTransitKey }
        binding.tvPlanetTitle.text = "${tPlanet?.glyph ?: state.focusTransitKey} → ${nPlanet?.glyph ?: state.focusNatalKey}"

        val nNatalPos  = natalAbs[state.focusNatalKey]
        val tTransPos  = transitAbs[state.focusTransitKey]
        val nSignLabel = if (nNatalPos != null) {
            val s = ZodiacSign.fromId(((nNatalPos / 30.0).toInt() % 12) + 1)
            "${s.symbol} ${s.localizedName(requireContext())}"
        } else ""
        val tSignLabel = if (tTransPos != null) {
            val s = ZodiacSign.fromId(((tTransPos / 30.0).toInt() % 12) + 1)
            "${s.symbol} ${s.localizedName(requireContext())}"
        } else ""
        binding.tvAspectDetail.text =
            "${tPlanet?.localizedName(requireContext()) ?: state.focusTransitKey} $tSignLabel  ✦  ${natal.name}: ${nPlanet?.localizedName(requireContext()) ?: state.focusNatalKey} $nSignLabel"

        // ── Legend ────────────────────────────────────────────────────────────
        binding.tvLegendNatal.text   = getString(R.string.legend_natal_aspects, nPlanet?.localizedName(requireContext()) ?: state.focusNatalKey)
        binding.tvLegendTransit.text = getString(
            if (isPd) R.string.legend_directed_aspects else R.string.legend_transit_aspects,
            tPlanet?.localizedName(requireContext()) ?: state.focusTransitKey)
        binding.tvLegendCross.text   = getString(R.string.legend_cross_aspect)

        // ── Interpretation ────────────────────────────────────────────────────
        val isBg = AppCompatDelegate.getApplicationLocales().toLanguageTags().startsWith("bg")
        val interp = TransitInterpretations.getGeneral(state.focusTransitKey, state.focusNatalKey)
        if (interp != null) {
            binding.tvInterpretation.visibility = View.VISIBLE
            binding.tvInterpretation.text = if (isBg) interp.second else interp.first
        } else {
            binding.tvInterpretation.visibility = View.GONE
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
