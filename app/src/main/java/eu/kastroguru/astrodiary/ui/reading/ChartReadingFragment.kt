package eu.kastroguru.astrodiary.ui.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentChartReadingBinding
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.ui.chart.localizedName
import kotlinx.coroutines.launch

/**
 * The chart as a piece of writing rather than a diagram: who the person is, then each body in turn —
 * what it does, where it plays out, and where that part of life is actually decided.
 *
 * The wheel is not here on purpose. It is one tap away from the toolbar for anyone who wants it,
 * which is the whole shape of the plain-language mode.
 */
@AndroidEntryPoint
class ChartReadingFragment : Fragment() {

    private var _binding: FragmentChartReadingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChartReadingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChartReadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong("birthDataId") ?: return
        viewModel.load(id)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                val entity = state.entity ?: return@collect
                binding.textName.text = entity.name
                binding.textBorn.text = getString(
                    R.string.reading_born_line,
                    "%02d.%02d.%04d".format(entity.day, entity.month, entity.year),
                    "%02d:%02d".format(entity.hour, entity.minutes),
                    entity.city,
                )
                render(state.sections)
            }
        }
    }

    private fun render(sections: List<ChartReadingViewModel.Section>) {
        val box = binding.container
        box.removeAllViews()
        val bulgarian = AppCompatDelegate.getApplicationLocales().toLanguageTags().startsWith("bg")

        var lastKind: ChartReadingViewModel.Section.Kind? = null
        for (section in sections) {
            if (section.kind != lastKind) {
                box.addView(groupHeading(
                    if (section.kind == ChartReadingViewModel.Section.Kind.WHO_YOU_ARE)
                        getString(R.string.reading_group_who) else getString(R.string.reading_group_world)
                ))
                lastKind = section.kind
            }
            box.addView(card(headingFor(section), if (bulgarian) section.text.bg else section.text.en))
        }
    }

    /** "Слънце в Телец · 5 дом · владетелят е в 9 дом" — the names live here, not in the texts. */
    private fun headingFor(s: ChartReadingViewModel.Section): String {
        val ctx = requireContext()
        val subject = when (s.planetKey) {
            "asc" -> getString(R.string.point_asc)
            else -> Planet.values().find { it.key == s.planetKey }?.localizedName(ctx) ?: ""
        }
        val parts = mutableListOf<String>()
        s.sign?.let { parts += getString(R.string.reading_in_sign, subject, it.localizedName(ctx)) }
        s.house?.let { parts += getString(R.string.reading_in_house, it) }
        if (s.rulerHouse != null && s.rulerKey != null) {
            val rulerName = Planet.values().find { it.key == s.rulerKey }?.localizedName(ctx) ?: ""
            parts += getString(R.string.reading_ruler_in_house, rulerName, s.rulerHouse)
        }
        return parts.joinToString("  ·  ")
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

    private fun card(heading: String, body: String): View {
        val ctx = requireContext()
        val d = resources.displayMetrics.density
        val box = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_reading_card)
            updatePadding((14 * d).toInt(), (14 * d).toInt(), (14 * d).toInt(), (14 * d).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (8 * d).toInt() }
        }
        box.addView(TextView(ctx).apply {
            text = heading
            setTextColor(ctx.getColor(R.color.text_primary))
            textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        })
        box.addView(TextView(ctx).apply {
            text = body
            setTextColor(ctx.getColor(R.color.text_secondary))
            textSize = 15f
            setLineSpacing(0f, 1.15f)
            updatePadding(top = (8 * d).toInt())
        })
        return box
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
