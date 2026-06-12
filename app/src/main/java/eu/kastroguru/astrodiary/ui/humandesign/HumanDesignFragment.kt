package eu.kastroguru.astrodiary.ui.humandesign

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentHumanDesignBinding
import eu.kastroguru.astrodiary.domain.humandesign.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HumanDesignFragment : Fragment() {

    private var _binding: FragmentHumanDesignBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HumanDesignViewModel by viewModels()

    private val C_DESIGN = Color.parseColor("#1144CC")       // dark blue — 88-day design
    private val C_PERSONALITY_CLR = Color.parseColor("#C09500")  // dark gold — natal

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHumanDesignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { updateUI(it) }
            }
        }
    }

    private fun updateUI(state: HumanDesignUiState) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.tvEmpty.visibility = if (state.allBirthData.isEmpty()) View.VISIBLE else View.GONE

        // Chart spinner
        if (state.allBirthData.isNotEmpty()) {
            val names = state.allBirthData.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerChart.adapter = adapter
            val idx = state.allBirthData.indexOfFirst { it.id == state.selected?.id }
            if (idx >= 0) binding.spinnerChart.setSelection(idx)
            binding.spinnerChart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    if (state.allBirthData[pos].id != state.selected?.id) viewModel.select(state.allBirthData[pos])
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }

        state.selected?.let { binding.tvChartInfo.text = "${it.name} · ${it.day}.${it.month}.${it.year} · ${it.city}" }

        val chart = state.chart ?: return

        // Bodygraph
        binding.bodygraph.apply {
            definedCenters   = chart.definedCenters
            personalityGates = chart.personality.map { it.gate }.toSet()
            designGates      = chart.design.map { it.gate }.toSet()
        }

        // Summary
        binding.tvType.text      = getString(typeRes(chart.type))
        binding.tvStrategy.text  = getString(strategyRes(chart.type))
        binding.tvAuthority.text = getString(R.string.hd_authority) + ": " + getString(authorityRes(chart.authority))
        binding.tvProfile.text   = getString(R.string.hd_profile) + ": " +
                "${chart.profilePersonalityLine}/${chart.profileDesignLine}"
        binding.tvDefinition.text = getString(R.string.hd_definition) + ": " + getString(definitionRes(chart.definition))

        // Activation table
        buildActivations(chart)

        // Detailed descriptions
        buildDescriptions(chart)
    }

    private fun buildActivations(chart: HumanDesignChart) {
        val container = binding.activationsContainer
        container.removeAllViews()
        val persByBody = chart.personality.associateBy { it.body }
        val desByBody  = chart.design.associateBy { it.body }

        for (body in HdBody.values()) {
            val des  = desByBody[body]
            val pers = persByBody[body]
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = dp(5) }
            }
            // Design (left, dark blue — 88 days before birth)
            row.addView(cell(des?.let { "${it.gate}.${it.line}" } ?: "—", C_DESIGN, Gravity.START, bold = true))
            // Planet glyph + name (center)
            row.addView(cell("${body.glyph}  ${body.display}", resolveColor(R.color.text_secondary), Gravity.CENTER, bold = false))
            // Personality (right, dark gold — natal chart)
            row.addView(cell(pers?.let { "${it.gate}.${it.line}" } ?: "—", C_PERSONALITY_CLR, Gravity.END, bold = true))
            container.addView(row)
        }
    }

    private fun cell(text: String, color: Int, gravity: Int, bold: Boolean): TextView =
        TextView(requireContext()).apply {
            this.text = text
            setTextColor(color)
            textSize = 14f
            this.gravity = gravity
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

    private fun resolveColor(res: Int) = androidx.core.content.ContextCompat.getColor(requireContext(), res)
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    // ── Enum → string resource mappings ──────────────────────────────────────
    private fun typeRes(t: HdType) = when (t) {
        HdType.MANIFESTOR -> R.string.hd_type_manifestor
        HdType.GENERATOR -> R.string.hd_type_generator
        HdType.MANIFESTING_GENERATOR -> R.string.hd_type_mg
        HdType.PROJECTOR -> R.string.hd_type_projector
        HdType.REFLECTOR -> R.string.hd_type_reflector
    }
    private fun strategyRes(t: HdType) = when (t) {
        HdType.MANIFESTOR -> R.string.hd_strategy_manifestor
        HdType.GENERATOR -> R.string.hd_strategy_generator
        HdType.MANIFESTING_GENERATOR -> R.string.hd_strategy_mg
        HdType.PROJECTOR -> R.string.hd_strategy_projector
        HdType.REFLECTOR -> R.string.hd_strategy_reflector
    }
    private fun authorityRes(a: HdAuthority) = when (a) {
        HdAuthority.EMOTIONAL -> R.string.hd_auth_emotional
        HdAuthority.SACRAL -> R.string.hd_auth_sacral
        HdAuthority.SPLENIC -> R.string.hd_auth_splenic
        HdAuthority.EGO -> R.string.hd_auth_ego
        HdAuthority.SELF_PROJECTED -> R.string.hd_auth_self
        HdAuthority.MENTAL -> R.string.hd_auth_mental
        HdAuthority.LUNAR -> R.string.hd_auth_lunar
    }
    private fun definitionRes(d: HdDefinition) = when (d) {
        HdDefinition.NONE -> R.string.hd_def_none
        HdDefinition.SINGLE -> R.string.hd_def_single
        HdDefinition.SPLIT -> R.string.hd_def_split
        HdDefinition.TRIPLE_SPLIT -> R.string.hd_def_triple
        HdDefinition.QUADRUPLE_SPLIT -> R.string.hd_def_quad
    }

    // ── Locale helper ─────────────────────────────────────────────────────────
    private fun isBg() = AppCompatDelegate.getApplicationLocales().toLanguageTags().startsWith("bg")
    private fun Pair<String, String>.loc() = if (isBg()) second else first

    // ── Description cards ─────────────────────────────────────────────────────
    private fun buildDescriptions(chart: HumanDesignChart) {
        binding.descriptionsContainer.removeAllViews()

        // Type
        HdDescriptions.typeInfo[chart.type]?.let { ti ->
            val pct = "${ti.name.loc()} · ${ti.percent}"
            addDescCard(
                title   = if (isBg()) getString(R.string.hd_type_section) else "Type",
                heading = pct,
                items   = listOf(
                    (if (isBg()) "Аура" else "Aura") to ti.aura.loc(),
                    (if (isBg()) "Описание" else "Description") to ti.description.loc(),
                    (if (isBg()) "Стратегия" else "Strategy") to ti.strategy.loc(),
                    (if (isBg()) "Подпис" else "Signature") to ti.signature.loc(),
                    (if (isBg()) "Не-Аз тема" else "Not-Self Theme") to ti.notSelf.loc()
                )
            )
        }

        // Authority
        HdDescriptions.authorityInfo[chart.authority]?.let { desc ->
            addDescCard(
                title   = if (isBg()) getString(R.string.hd_authority) else "Authority",
                heading = getString(authorityRes(chart.authority)),
                items   = listOf((if (isBg()) "Как работи" else "How it works") to desc.loc())
            )
        }

        // Profile
        val profileKey = HdDescriptions.profileKey(chart.profilePersonalityLine, chart.profileDesignLine)
        HdDescriptions.profileInfo[profileKey]?.let { desc ->
            addDescCard(
                title   = if (isBg()) getString(R.string.hd_profile) else "Profile",
                heading = "$profileKey  —  ${desc.loc().substringBefore(':')}",
                items   = listOf((if (isBg()) "Описание" else "Description") to desc.loc().substringAfter(':').trim())
            )
        }

        // Definition
        HdDescriptions.definitionInfo[chart.definition]?.let { desc ->
            addDescCard(
                title   = if (isBg()) getString(R.string.hd_definition) else "Definition",
                heading = getString(definitionRes(chart.definition)),
                items   = listOf((if (isBg()) "Значение" else "Meaning") to desc.loc())
            )
        }

        // Defined Centers
        if (chart.definedCenters.isNotEmpty()) {
            val centerItems = chart.definedCenters.mapNotNull { center ->
                HdDescriptions.centerInfo[center]?.defined?.loc()?.let {
                    centerName(center) to it
                }
            }
            if (centerItems.isNotEmpty())
                addDescCard(
                    title   = if (isBg()) "Дефинирани центрове" else "Defined Centers",
                    heading = null,
                    items   = centerItems
                )
        }

        // Undefined Centers
        val undefinedCenters = HdCenter.values().filter { it !in chart.definedCenters }
        if (undefinedCenters.isNotEmpty()) {
            val items = undefinedCenters.mapNotNull { center ->
                HdDescriptions.centerInfo[center]?.undefined?.loc()?.let {
                    centerName(center) to it
                }
            }
            if (items.isNotEmpty())
                addDescCard(
                    title   = if (isBg()) "Недефинирани центрове" else "Undefined Centers",
                    heading = null,
                    items   = items
                )
        }

        // Active Channels
        if (chart.definedChannels.isNotEmpty()) {
            val channelItems = chart.definedChannels.mapNotNull { ch ->
                HdDescriptions.channelDescriptionFor(ch)?.loc()?.let { desc ->
                    desc.substringBefore(':') to desc.substringAfter(':').trim()
                }
            }
            if (channelItems.isNotEmpty())
                addDescCard(
                    title   = if (isBg()) "Активни канали" else "Active Channels",
                    heading = null,
                    items   = channelItems
                )
        }
    }

    private fun centerName(c: HdCenter) = when (c) {
        HdCenter.HEAD         -> if (isBg()) "Глава" else "Head"
        HdCenter.AJNA         -> "Ajna"
        HdCenter.THROAT       -> if (isBg()) "Гърло" else "Throat"
        HdCenter.G            -> "G"
        HdCenter.HEART        -> if (isBg()) "Сърце / Его" else "Heart / Ego"
        HdCenter.SACRAL       -> if (isBg()) "Сакрален" else "Sacral"
        HdCenter.SPLEEN       -> if (isBg()) "Слезка" else "Spleen"
        HdCenter.SOLAR_PLEXUS -> if (isBg()) "Слънчев Сплит" else "Solar Plexus"
        HdCenter.ROOT         -> if (isBg()) "Корен" else "Root"
    }

    private fun addDescCard(title: String, heading: String?, items: List<Pair<String, String>>) {
        val ctx = requireContext()
        val card = MaterialCardView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(8) }
            setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.surface_dark))
            strokeColor = ContextCompat.getColor(ctx, R.color.card_stroke)
            strokeWidth = dp(1)
            radius = dp(12).toFloat()
        }
        val inner = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14))
        }

        // Section title
        inner.addView(TextView(ctx).apply {
            text = title.uppercase()
            setTextColor(ContextCompat.getColor(ctx, R.color.gold))
            textSize = 11f
            setTypeface(typeface, Typeface.BOLD)
            letterSpacing = 0.1f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(6) }
        })

        // Optional heading
        if (heading != null) {
            inner.addView(TextView(ctx).apply {
                text = heading
                setTextColor(ContextCompat.getColor(ctx, R.color.text_primary))
                textSize = 16f
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = dp(10) }
            })
        }

        // Collapsible body — tapping header shows/hides content
        val bodyContainer = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }
        val collapseHint = TextView(ctx).apply {
            text = if (isBg()) "▼ покажи детайли" else "▼ show details"
            setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
            textSize = 12f
        }
        inner.addView(collapseHint)

        for ((label, body) in items) {
            // Label
            bodyContainer.addView(TextView(ctx).apply {
                text = label
                setTextColor(ContextCompat.getColor(ctx, R.color.gold))
                textSize = 12f
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = dp(8); it.bottomMargin = dp(2) }
            })
            // Body
            bodyContainer.addView(TextView(ctx).apply {
                text = body
                setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = dp(2) }
            })
        }

        inner.addView(bodyContainer)

        // Toggle expand on tap
        collapseHint.setOnClickListener {
            if (bodyContainer.visibility == View.GONE) {
                bodyContainer.visibility = View.VISIBLE
                collapseHint.text = if (isBg()) "▲ скрий" else "▲ hide"
            } else {
                bodyContainer.visibility = View.GONE
                collapseHint.text = if (isBg()) "▼ покажи детайли" else "▼ show details"
            }
        }

        card.addView(inner)
        binding.descriptionsContainer.addView(card)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
