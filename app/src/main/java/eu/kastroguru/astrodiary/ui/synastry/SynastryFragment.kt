package eu.kastroguru.astrodiary.ui.synastry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentSynastryBinding
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.synastry.Resonance
import eu.kastroguru.astrodiary.ui.chart.localizedName
import kotlinx.coroutines.launch

/**
 * What already works between two people, and one number for how much of it there is.
 *
 * Two decisions shape this screen. It shows **only** what matches — a list of everything the
 * partner fails to supply would be a different and much worse thing to hand somebody. And the
 * cards lead with ordinary language: the placement behind each one is printed underneath in small
 * grey type, so the score stays checkable without the reading turning back into astrology.
 */
@AndroidEntryPoint
class SynastryFragment : Fragment() {

    private var _binding: FragmentSynastryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SynastryViewModel by viewModels()

    private var openBody: View? = null
    private var openArrow: TextView? = null
    private var spinnerBoundTo: List<Long> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSynastryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong("birthDataId") ?: return
        viewModel.load(id)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                val person = state.person ?: return@collect
                binding.textPerson.text = person.name
                bindSpinner(state)
                renderHeader(state)
                render(state.cards)
            }
        }
    }

    private fun isBg() = AppCompatDelegate.getApplicationLocales().toLanguageTags().startsWith("bg")

    private fun Pair<String, String>.loc() = if (isBg()) second else first

    /** Rebound only when the list of people actually changes, so picking one does not reset it. */
    private fun bindSpinner(state: SynastryViewModel.State) {
        val ids = state.others.map { it.id }
        if (ids == spinnerBoundTo) return
        spinnerBoundTo = ids

        if (state.others.isEmpty()) {
            binding.labelPartner.visibility = View.GONE
            binding.spinnerPartner.visibility = View.GONE
            binding.textEmpty.visibility = View.VISIBLE
            binding.textEmpty.text = getString(R.string.synastry_needs_two)
            return
        }
        val names = listOf(getString(R.string.synastry_pick_prompt)) + state.others.map { it.name }
        binding.spinnerPartner.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, names
        )
        binding.spinnerPartner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectPartner(state.others.getOrNull(position - 1))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun renderHeader(state: SynastryViewModel.State) {
        val partner = state.partner
        val show = if (partner != null) View.VISIBLE else View.GONE
        binding.textScore.visibility = show
        binding.textScoreDetail.visibility = show
        binding.textBand.visibility = show
        binding.textIntro.visibility = show
        if (partner == null || state.person == null) return

        binding.textScore.text = state.score.toString()
        binding.textScoreDetail.text = getString(
            R.string.synastry_score_detail,
            state.scoreForPerson, state.person.name,
            state.scoreForPartner, partner.name,
        )
        binding.textBand.text = viewModel.bandFor(state.score)?.pick().orEmpty()
        binding.textIntro.text = viewModel.intro?.pick().orEmpty()
    }

    private fun render(cards: List<SynastryViewModel.Card>) {
        binding.container.removeAllViews()
        openBody = null
        openArrow = null
        if (cards.isEmpty()) return

        var group: SynastryViewModel.Group? = null
        for (card in cards) {
            if (card.group != group) {
                group = card.group
                binding.container.addView(
                    groupHeading(
                        getString(
                            when (group) {
                                SynastryViewModel.Group.CUSP_MET -> R.string.synastry_group_cusp
                                SynastryViewModel.Group.POINT_MET -> R.string.synastry_group_point
                                SynastryViewModel.Group.CUSP_MISS -> R.string.synastry_group_cusp_miss
                                SynastryViewModel.Group.POINT_MISS -> R.string.synastry_group_point_miss
                            }
                        )
                    )
                )
            }
            binding.container.addView(
                collapsible(
                    card.card.title.pick(), card.card.body.pick(),
                    detailLine(card.detail), card.met,
                )
            )
        }
    }

    private fun eu.kastroguru.astrodiary.domain.interpretation.Bilingual.pick(): String =
        if (isBg()) bg else en

    private fun bodyName(key: String): String =
        Planet.values().find { it.key == key }?.localizedName(requireContext()) ?: key

    /**
     * The reference line under an open card. Short, dim, and last — it is there so a curious
     * reader can check where the number came from, not so the card can be read as a placement.
     */
    private fun detailLine(detail: SynastryViewModel.Detail): String = when (detail) {
        is SynastryViewModel.Detail.Point -> listOfNotNull(
            bodyName(detail.point),
            bodyName(detail.colour),
            detail.channel?.let {
                getString(
                    when (it) {
                        Resonance.Channel.ASPECT -> R.string.synastry_via_aspect
                        Resonance.Channel.SIGN -> R.string.synastry_via_sign
                        Resonance.Channel.HOUSE -> R.string.synastry_via_house
                        Resonance.Channel.SAME_SIGN -> R.string.synastry_via_same_sign
                    }
                )
            },
        ).joinToString(" · ")

        is SynastryViewModel.Detail.Cusp -> listOfNotNull(
            getString(
                when (detail.cusp) {
                    Resonance.Cusp.DESCENDANT -> R.string.synastry_cusp_descendant
                    Resonance.Cusp.IMUM_COELI -> R.string.synastry_cusp_imum_coeli
                }
            ),
            detail.sign.localizedName(requireContext()),
            detail.indicator?.let {
                getString(
                    when (it) {
                        Resonance.CuspIndicator.ASCENDANT_SIGN -> R.string.synastry_by_ascendant
                        Resonance.CuspIndicator.STELLIUM -> R.string.synastry_by_stellium
                        Resonance.CuspIndicator.SUN_SIGN -> R.string.synastry_by_sun_sign
                        Resonance.CuspIndicator.RULER_ASPECTS_SUN -> R.string.synastry_by_ruler_aspect
                        Resonance.CuspIndicator.RULER_ON_ASCENDANT -> R.string.synastry_by_ruler_angle
                    }
                )
            },
        ).joinToString(" · ")
    }

    private fun groupHeading(text: String) = TextView(requireContext()).apply {
        this.text = text
        setTextColor(requireContext().getColor(R.color.gold))
        textSize = 13f
        isAllCaps = true
        letterSpacing = 0.1f
        val d = resources.displayMetrics.density
        updatePadding(left = (4 * d).toInt(), top = (22 * d).toInt(), bottom = (6 * d).toInt())
    }

    /**
     * A card that opens on a tap, with the chart fact in small grey type at the bottom of the open
     * body — present so the number can be checked, placed so it is the last thing read rather than
     * the first.
     */
    private fun collapsible(heading: String, body: String, detail: String, met: Boolean): View {
        val ctx = requireContext()
        val d = resources.displayMetrics.density

        val arrow = TextView(ctx).apply {
            text = "▾"
            setTextColor(ctx.getColor(R.color.text_secondary))
            textSize = 14f
        }
        // The unanswered cards are dimmed rather than coloured. A red mark would turn a reading
        // into a verdict, and the point of listing them at all is that they are information.
        val title = TextView(ctx).apply {
            text = heading
            alpha = if (met) 1f else 0.72f
            setTextColor(ctx.getColor(R.color.text_primary))
            textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(title)
            addView(arrow)
        }
        val text = TextView(ctx).apply {
            this.text = body
            setTextColor(ctx.getColor(R.color.text_secondary))
            textSize = 15f
            setLineSpacing(0f, 1.15f)
            updatePadding(top = (10 * d).toInt())
        }
        val small = TextView(ctx).apply {
            this.text = detail
            setTextColor(ctx.getColor(R.color.text_secondary))
            textSize = 11f
            alpha = 0.5f
            updatePadding(top = (8 * d).toInt())
        }
        val openPart = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            addView(text)
            addView(small)
        }
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_reading_card)
            updatePadding((14 * d).toInt(), (14 * d).toInt(), (14 * d).toInt(), (14 * d).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (8 * d).toInt() }
            isClickable = true
            addView(row)
            addView(openPart)
            setOnClickListener { toggle(openPart, arrow) }
        }
    }

    /** One card open at a time: the same rule as the chart reading, for the same reason. */
    private fun toggle(body: View, arrow: TextView) {
        val wasOpen = body.visibility == View.VISIBLE
        openBody?.visibility = View.GONE
        openArrow?.text = "▾"
        if (wasOpen) {
            openBody = null
            openArrow = null
            return
        }
        body.visibility = View.VISIBLE
        arrow.text = "▴"
        openBody = body
        openArrow = arrow
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
