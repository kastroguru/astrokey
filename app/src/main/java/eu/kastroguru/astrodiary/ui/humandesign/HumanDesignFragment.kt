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
        buildDescriptions(state, chart)
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
    private fun buildDescriptions(state: HumanDesignUiState, chart: HumanDesignChart) {
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

        // ── Today (transit gates against this chart) ──────────────────────────
        buildTodayCard(state.transitGates)

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

            // ── How the openness conditions you — the not-self layer ──────────
            val conditioning = undefinedCenters.mapNotNull { center ->
                HdOpenCenterTexts.conditioning[center]?.loc()?.let { centerName(center) to it }
            }
            if (conditioning.isNotEmpty())
                addDescCard(
                    title   = if (isBg()) "Къде не сте себе си" else "Where you are not yourself",
                    heading = if (isBg()) "Фалшивите въпроси на отвореното"
                              else "The false questions of your openness",
                    items   = conditioning
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

        // ── Hanging gates — what you look for in others ───────────────────────
        buildHangingCard(chart)

        // ── Variable — the four arrows ────────────────────────────────────────
        buildVariableCard(chart)

        // ── The child's chart read as parenting advice ────────────────────────
        buildParentingCard(chart)

        // ── Connection chart ──────────────────────────────────────────────────
        buildConnectionCard(state)
    }

    // ── Labels ────────────────────────────────────────────────────────────────
    private fun gateLabel(g: Int) = if (isBg()) "Гейт $g" else "Gate $g"

    /** Localized channel name, taken from the one place it is already written. */
    private fun channelLabel(ch: HdChannel): String =
        HdDescriptions.channelDescriptionFor(ch)?.loc()?.substringBefore(':')?.trim() ?: ch.name

    // ── Today ─────────────────────────────────────────────────────────────────
    private fun buildTodayCard(transits: List<HdTransitGate>) {
        if (transits.isEmpty()) return

        // One line per effect group, strongest first, then the per-gate texts inside it.
        val items = mutableListOf<Pair<String, String>>()
        for (eff in listOf(HdTransitEffect.COMPLETES, HdTransitEffect.AMPLIFIES, HdTransitEffect.OPEN)) {
            val group = transits.filter { it.effect == eff }
            if (group.isEmpty()) continue
            HdGateTransitTexts.effect[eff]?.loc()?.let { items += effectLabel(eff) to it }
            // Two bodies can sit in the same gate today; merge them onto one line instead of
            // printing the gate's text twice.
            for ((gate, sameGate) in group.groupBy { it.gate }) {
                val text = HdGateTransitTexts.gate[gate]?.loc() ?: continue
                val t = sameGate.first()
                val bodies = sameGate.joinToString(" ") { "${it.body.glyph}${it.line}" }
                val head = if (t.effect == HdTransitEffect.COMPLETES && t.completedChannel != null)
                    "${gateLabel(gate)} · $bodies → ${channelLabel(t.completedChannel)}"
                else
                    "${gateLabel(gate)} · $bodies"
                items += head to text
            }
        }
        addDescCard(
            title   = if (isBg()) "Днес" else "Today",
            heading = if (isBg()) "Небето върху вашата карта" else "The sky against your own chart",
            items   = items
        )
    }

    private fun effectLabel(e: HdTransitEffect) = when (e) {
        HdTransitEffect.COMPLETES -> if (isBg()) "Затваря верига у вас" else "Closes a circuit in you"
        HdTransitEffect.AMPLIFIES -> if (isBg()) "Усилва ваше" else "Amplifies what you have"
        HdTransitEffect.OPEN      -> if (isBg()) "Пада в отвореното" else "Lands in your openness"
    }

    // ── Hanging gates ─────────────────────────────────────────────────────────
    private fun buildHangingCard(chart: HumanDesignChart) {
        if (chart.hangingGates.isEmpty()) return
        // A gate can hang in several channels at once (10 hangs towards 20, 34 and 57). The text is
        // keyed by gate, so group first — otherwise the same paragraph prints two or three times.
        val items = chart.hangingGates.groupBy { it.gate }.mapNotNull { (gate, hangs) ->
            val text = HdHangingGateTexts.seeking[gate]?.loc() ?: return@mapNotNull null
            val seeks = hangs.map { it.seeks }.sorted().joinToString(", ")
            val channels = hangs.joinToString(", ") { channelLabel(it.channel) }
            val head = "${gateLabel(gate)} → $seeks  ·  $channels" +
                if (hangs.any { it.opensNewCentre })
                    (if (isBg()) "  ·  отваря нов център" else "  ·  opens a new centre")
                else ""
            head to text
        }
        if (items.isEmpty()) return
        addDescCard(
            title   = if (isBg()) "Какво търсите в другите" else "What you look for in others",
            heading = if (isBg()) "Висящи гейтове (${items.size})" else "Hanging gates (${items.size})",
            items   = items
        )
    }

    // ── Variable ──────────────────────────────────────────────────────────────
    private fun buildVariableCard(chart: HumanDesignChart) {
        val v = chart.variables ?: return
        val items = mutableListOf<Pair<String, String>>()

        // Heading + "what this arrow is", then the person's own value as a continuation paragraph.
        fun add(introKey: String, label: String, value: String, body: String?) {
            if (body == null) return
            val intro = HdVariableTexts.intro[introKey]?.loc()
            items += "$label — $value" to (intro ?: body)
            if (intro != null) items += "" to body
        }

        add("determination",
            if (isBg()) "Храносмилане (Слънце в дизайна)" else "Digestion (Design Sun)",
            determinationName(v.determination),
            HdVariableTexts.determination[v.determination]?.loc())
        add("environment",
            if (isBg()) "Среда (възел в дизайна)" else "Environment (Design Node)",
            environmentName(v.environment),
            HdVariableTexts.environment[v.environment]?.loc())
        add("motivation",
            if (isBg()) "Мотивация (личностно Слънце)" else "Motivation (Personality Sun)",
            motivationName(v.motivation),
            HdVariableTexts.motivation[v.motivation]?.loc())
        add("perspective",
            if (isBg()) "Перспектива (личностен възел)" else "Perspective (Personality Node)",
            perspectiveName(v.perspective),
            HdVariableTexts.perspective[v.perspective]?.loc())

        if (items.isEmpty()) return

        if (chart.birthTimeIsRounded) {
            items += (if (isBg()) "⚠ Точност на часа" else "⚠ Birth-time accuracy") to
                (if (isBg())
                    "Часът на раждане е записан на кръгъл час или половин час, което обикновено значи, че е закръглен. Храносмилането и средата се четат до тон — Слънцето минава един тон за около 38 минути — така че при закръглен час втората половина от тези две (вариантът) може да е грешна. Първата половина, самият вид, търпи няколко часа отклонение и остава надеждна."
                 else
                    "The stored birth time falls on a round hour or half-hour, which usually means it was rounded. Digestion and Environment are read to the tone — the Sun crosses one tone in about 38 minutes — so with a rounded time the second half of those two (the variant) may be wrong. The first half, the kind itself, tolerates a few hours and stays reliable.")
        }

        addDescCard(
            title   = if (isBg()) "Четирите стрелки" else "The four arrows",
            heading = if (isBg()) "Как приемате и какво ви движи" else "How you take things in, and what moves you",
            items   = items
        )
    }

    // ── Parenting ─────────────────────────────────────────────────────────────
    private fun buildParentingCard(chart: HumanDesignChart) {
        val key = HdParentingTexts.keyFor(chart.type, chart.authority)
        val text = HdParentingTexts.parenting[key]?.loc() ?: return
        addDescCard(
            title   = if (isBg()) "Ако това е дете" else "If this is a child",
            heading = "${getString(typeRes(chart.type))} · ${getString(authorityRes(chart.authority))}",
            items   = listOf((if (isBg()) "Как да го гледате" else "How to raise them") to text)
        )
    }

    // ── Connection chart ──────────────────────────────────────────────────────
    private fun buildConnectionCard(state: HumanDesignUiState) {
        val others = state.allBirthData.filter { it.id != state.selected?.id }
        if (others.isEmpty()) return

        val ctx = requireContext()
        val none = if (isBg()) "— изберете втори човек —" else "— pick a second person —"
        val spinner = android.widget.Spinner(ctx).apply {
            val names = listOf(none) + others.map { it.name }
            adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, names).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            val idx = others.indexOfFirst { it.id == state.partner?.id }
            setSelection(if (idx >= 0) idx + 1 else 0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    val wanted = if (pos == 0) null else others[pos - 1]
                    if (wanted?.id != state.partner?.id) viewModel.selectPartner(wanted)
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }

        val conn = state.connection
        val items = mutableListOf<Pair<String, String>>()
        items += "" to HdConnectionTexts.intro.loc()

        if (conn != null) {
            fun group(kind: HdConnectionKind, links: List<HdChannelConnection>, label: String) {
                if (links.isEmpty()) return
                HdConnectionTexts.state[kind]?.loc()?.let { items += "$label (${links.size})" to it }
                for (l in links) {
                    val meaning = HdDescriptions.channelDescriptionFor(l.channel)?.loc()
                        ?.substringAfter(':')?.trim() ?: continue
                    val head = when (l.state) {
                        HdConnectionState.ELECTROMAGNETIC ->
                            "${channelLabel(l.channel)}  ·  ${l.gateFromA} + ${l.gateFromB}"
                        HdConnectionState.DOMINANCE_A, HdConnectionState.COMPROMISE_B ->
                            "${channelLabel(l.channel)}  ·  ${state.selected?.name ?: "A"}"
                        HdConnectionState.DOMINANCE_B, HdConnectionState.COMPROMISE_A ->
                            "${channelLabel(l.channel)}  ·  ${state.partner?.name ?: "B"}"
                        else -> channelLabel(l.channel)
                    }
                    items += head to meaning
                }
            }
            group(HdConnectionKind.ELECTROMAGNETIC, conn.electromagnetic,
                if (isBg()) "Електромагнитни — създават се между вас" else "Electromagnetic — created between you")
            group(HdConnectionKind.COMPROMISE, conn.compromise,
                if (isBg()) "Компромис — кой отстъпва" else "Compromise — who bends")
            group(HdConnectionKind.DOMINANCE, conn.dominance,
                if (isBg()) "Доминиране — кой го носи" else "Dominance — who carries it")
            group(HdConnectionKind.COMPANIONSHIP, conn.companionship,
                if (isBg()) "Другарство — общо и лесно" else "Companionship — shared and easy")

            if (conn.aConditionsB.isNotEmpty() || conn.bConditionsA.isNotEmpty()) {
                items += (if (isBg()) "Обуславяне" else "Conditioning") to HdConnectionTexts.conditioning.loc()
                if (conn.aConditionsB.isNotEmpty()) items +=
                    "${state.selected?.name ?: "A"} → ${state.partner?.name ?: "B"}" to
                        conn.aConditionsB.joinToString(", ") { centerName(it) }
                if (conn.bConditionsA.isNotEmpty()) items +=
                    "${state.partner?.name ?: "B"} → ${state.selected?.name ?: "A"}" to
                        conn.bConditionsA.joinToString(", ") { centerName(it) }
            }
            if (conn.bothOpenCentres.isNotEmpty()) {
                items += (if (isBg()) "Отворено и у двамата" else "Open in both of you") to HdConnectionTexts.bothOpen.loc()
                items += "" to conn.bothOpenCentres.joinToString(", ") { centerName(it) }
            }
        }

        addDescCard(
            title    = if (isBg()) "Свързваща карта" else "Connection chart",
            heading  = state.partner?.let { "${state.selected?.name} + ${it.name}" },
            items    = items,
            extraTop = spinner
        )
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

    // ── Variable value names ──────────────────────────────────────────────────
    private fun determinationName(d: HdDetermination) = when (d) {
        HdDetermination.APPETITE_CONSECUTIVE -> if (isBg()) "Последователен апетит" else "Consecutive Appetite"
        HdDetermination.APPETITE_ALTERNATING -> if (isBg()) "Редуващ се апетит" else "Alternating Appetite"
        HdDetermination.TASTE_OPEN           -> if (isBg()) "Отворен вкус" else "Open Taste"
        HdDetermination.TASTE_CLOSED         -> if (isBg()) "Затворен вкус" else "Closed Taste"
        HdDetermination.THIRST_NERVOUS       -> if (isBg()) "Нервна жажда" else "Nervous Thirst"
        HdDetermination.THIRST_CALM          -> if (isBg()) "Спокойна жажда" else "Calm Thirst"
        HdDetermination.TOUCH_COLD           -> if (isBg()) "Студен допир" else "Cold Touch"
        HdDetermination.TOUCH_HOT            -> if (isBg()) "Топъл допир" else "Hot Touch"
        HdDetermination.SOUND_HIGH           -> if (isBg()) "Висок звук" else "High Sound"
        HdDetermination.SOUND_LOW            -> if (isBg()) "Нисък звук" else "Low Sound"
        HdDetermination.LIGHT_DIRECT         -> if (isBg()) "Пряка светлина" else "Direct Light"
        HdDetermination.LIGHT_INDIRECT       -> if (isBg()) "Непряка светлина" else "Indirect Light"
    }

    private fun environmentName(e: HdEnvironment) = when (e) {
        HdEnvironment.CAVES_SELECTIVE    -> if (isBg()) "Избирателни пещери" else "Selective Caves"
        HdEnvironment.CAVES_WET          -> if (isBg()) "Влажни пещери" else "Wet Caves"
        HdEnvironment.MARKETS_INTERNAL   -> if (isBg()) "Вътрешни пазари" else "Internal Markets"
        HdEnvironment.MARKETS_EXTERNAL   -> if (isBg()) "Външни пазари" else "External Markets"
        HdEnvironment.KITCHENS_WET       -> if (isBg()) "Влажни кухни" else "Wet Kitchens"
        HdEnvironment.KITCHENS_DRY       -> if (isBg()) "Сухи кухни" else "Dry Kitchens"
        HdEnvironment.MOUNTAINS_ACTIVE   -> if (isBg()) "Активни планини" else "Active Mountains"
        HdEnvironment.MOUNTAINS_PASSIVE  -> if (isBg()) "Пасивни планини" else "Passive Mountains"
        HdEnvironment.VALLEYS_NARROW     -> if (isBg()) "Тесни долини" else "Narrow Valleys"
        HdEnvironment.VALLEYS_WIDE       -> if (isBg()) "Широки долини" else "Wide Valleys"
        HdEnvironment.SHORES_NATURAL     -> if (isBg()) "Естествени брегове" else "Natural Shores"
        HdEnvironment.SHORES_ARTIFICIAL  -> if (isBg()) "Изкуствени брегове" else "Artificial Shores"
    }

    private fun motivationName(m: HdMotivation) = when (m) {
        HdMotivation.FEAR      -> if (isBg()) "Страх" else "Fear"
        HdMotivation.HOPE      -> if (isBg()) "Надежда" else "Hope"
        HdMotivation.DESIRE    -> if (isBg()) "Желание" else "Desire"
        HdMotivation.NEED      -> if (isBg()) "Нужда" else "Need"
        HdMotivation.GUILT     -> if (isBg()) "Вина" else "Guilt"
        HdMotivation.INNOCENCE -> if (isBg()) "Невинност" else "Innocence"
    }

    private fun perspectiveName(p: HdPerspective) = when (p) {
        HdPerspective.SURVIVAL    -> if (isBg()) "Оцеляване" else "Survival"
        HdPerspective.POSSIBILITY -> if (isBg()) "Възможност" else "Possibility"
        HdPerspective.POWER       -> if (isBg()) "Сила" else "Power"
        HdPerspective.WANTING     -> if (isBg()) "Искане" else "Wanting"
        HdPerspective.PROBABILITY -> if (isBg()) "Вероятност" else "Probability"
        HdPerspective.PERSONAL    -> if (isBg()) "Лично" else "Personal"
    }

    private fun addDescCard(
        title: String,
        heading: String?,
        items: List<Pair<String, String>>,
        extraTop: View? = null
    ) {
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

        // A control that must stay visible even while the body is collapsed (the partner spinner).
        extraTop?.let {
            inner.addView(it, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { lp -> lp.bottomMargin = dp(8) })
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
            // Label — blank labels are used for continuation paragraphs
            if (label.isNotEmpty()) bodyContainer.addView(TextView(ctx).apply {
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
