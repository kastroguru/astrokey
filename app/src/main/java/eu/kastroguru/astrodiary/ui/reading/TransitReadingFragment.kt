package eu.kastroguru.astrodiary.ui.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentTransitReadingBinding
import eu.kastroguru.astrodiary.domain.humandesign.TransitInterpretations
import eu.kastroguru.astrodiary.domain.interpretation.PlanetGrammar
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.ui.chart.localizedName
import eu.kastroguru.astrodiary.ui.events.EventAspectPhrase
import eu.kastroguru.astrodiary.ui.transit.TransitAspect
import eu.kastroguru.astrodiary.ui.transit.TransitUiState
import eu.kastroguru.astrodiary.ui.transit.TransitViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * What is affecting the selected person right now, in words — and nothing else.
 *
 * Deliberately not the transit screen with its date stepper: someone who does not read charts has no
 * idea what moving the date means, so there is nothing to move here. Just today's active contacts,
 * strongest first, each explained. The full screen with the aspectarian is behind the wheel button
 * in the toolbar for anyone who wants it.
 */
@AndroidEntryPoint
class TransitReadingFragment : Fragment() {

    private var _binding: FragmentTransitReadingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransitViewModel by viewModels()

    private var spinnerNames: List<String> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransitReadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { render(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startLiveRefresh()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopLiveRefresh()
    }

    private fun render(state: TransitUiState) {
        bindPersonSpinner(state)
        binding.tvToday.text = getString(
            R.string.transit_reading_today,
            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date())
        )

        // Primary directions are an astrologer's technique; this screen stays on transits only.
        val aspects = state.aspects.filter { it.directedLon.isNaN() }.take(MAX_SHOWN)
        binding.tvEmpty.visibility = if (aspects.isEmpty() && !state.isLoading) View.VISIBLE else View.GONE

        val box = binding.container
        box.removeAllViews()
        openBody = null
        openArrow = null
        val bulgarian = AppCompatDelegate.getApplicationLocales().toLanguageTags().startsWith("bg")
        for (aspect in aspects) {
            box.addView(card(aspect, bulgarian, state))
        }
    }

    private fun bindPersonSpinner(state: TransitUiState) {
        if (state.allBirthData.isEmpty()) return
        val names = state.allBirthData.map { it.name }
        if (names != spinnerNames) {
            spinnerNames = names
            binding.spinnerNatal.onItemSelectedListener = null
            binding.spinnerNatal.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item, names
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            binding.spinnerNatal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    val list = viewModel.state.value.allBirthData
                    if (position in list.indices) viewModel.selectNatal(list[position])
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }
        val index = state.allBirthData.indexOfFirst { it.id == state.natalData?.id }
        if (index >= 0 && binding.spinnerNatal.selectedItemPosition != index) {
            binding.spinnerNatal.setSelection(index)
        }
    }

    /** The one open body, so eight long readings cannot stack into a wall of text. */
    private var openBody: View? = null
    private var openArrow: TextView? = null

    /**
     * A heading that opens its reading when tapped: "Сатурн квадрат вашата Луна · набира сила".
     * The dates line inside the opened text is what navigates on — tapping the heading only opens
     * and closes, so a tap never takes you somewhere by surprise.
     */
    private fun card(aspect: TransitAspect, bulgarian: Boolean, state: TransitUiState): View {
        val ctx = requireContext()
        val d = resources.displayMetrics.density
        val transitName = Planet.values().find { it.key == aspect.transitPlanet }?.localizedName(ctx) ?: ""
        val natalName = Planet.values().find { it.key == aspect.natalPlanet }?.localizedName(ctx) ?: ""
        val aspectName = EventAspectPhrase.aspectName(ctx, aspect.exactDegree).lowercase()
        // "вашата Луна", "вашия Марс", "вашето Слънце" — the possessive follows the body's name.
        val headingRes = when (PlanetGrammar.of(aspect.natalPlanet)) {
            PlanetGrammar.Gender.FEMININE -> R.string.transit_reading_heading_f
            PlanetGrammar.Gender.NEUTER -> R.string.transit_reading_heading_n
            else -> R.string.transit_reading_heading_m
        }
        val heading = getString(headingRes, transitName, aspectName, natalName)
        val phase = getString(
            if (aspect.isApplying) R.string.transit_reading_building else R.string.transit_reading_fading
        )
        val interpretation = TransitInterpretations.getGeneral(aspect.transitPlanet, aspect.natalPlanet)
        // The stored texts open by naming the pair, which the heading already says. Drop that lead-in.
        val bodyText = interpretation?.let { if (bulgarian) it.second else it.first }
            ?.replace(Regex("^(Транзит\\w*|Transit)[^:]{0,60}:\\s*"), "")
            ?: getString(R.string.transit_reading_no_text)

        val arrow = TextView(ctx).apply {
            text = "▾"
            setTextColor(ctx.getColor(R.color.text_secondary))
            textSize = 14f
        }
        val title = TextView(ctx).apply {
            text = heading
            setTextColor(ctx.getColor(R.color.text_primary))
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val body = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            addView(TextView(ctx).apply {
                text = bodyText
                setTextColor(ctx.getColor(R.color.text_secondary))
                textSize = 15f
                setLineSpacing(0f, 1.15f)
                updatePadding(top = (10 * d).toInt())
            })
            addView(TextView(ctx).apply {
                text = getString(R.string.transit_reading_tap_for_dates)
                setTextColor(ctx.getColor(R.color.gold))
                textSize = 13f
                updatePadding(top = (10 * d).toInt())
                isClickable = true
                setOnClickListener { openDetail(aspect, state) }
            })
        }
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_reading_card)
            updatePadding((14 * d).toInt(), (14 * d).toInt(), (14 * d).toInt(), (14 * d).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (10 * d).toInt() }
            isClickable = true
            addView(LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(title)
                addView(arrow)
            })
            addView(TextView(ctx).apply {
                text = phase
                setTextColor(ctx.getColor(R.color.gold))
                textSize = 13f
                updatePadding(top = (2 * d).toInt())
            })
            addView(body)
            setOnClickListener {
                val wasOpen = body.visibility == View.VISIBLE
                openBody?.visibility = View.GONE
                openArrow?.text = "▾"
                if (wasOpen) { openBody = null; openArrow = null } else {
                    body.visibility = View.VISIBLE
                    arrow.text = "▴"
                    openBody = body
                    openArrow = arrow
                }
            }
        }
    }

    private fun openDetail(aspect: TransitAspect, state: TransitUiState) {
        findNavController().navigate(
            R.id.transitAspectDetailFragment,
            bundleOf(
                "natalPlanetKey" to aspect.natalPlanet,
                "transitPlanetKey" to aspect.transitPlanet,
                "natalDataId" to (state.natalData?.id ?: 0L),
                "transitMs" to viewModel.transitMs,
                "isPd" to false,
                "directedLon" to -1f,
                "aspectDeg" to aspect.exactDegree,
            )
        )
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    private companion object { const val MAX_SHOWN = 8 }
}
