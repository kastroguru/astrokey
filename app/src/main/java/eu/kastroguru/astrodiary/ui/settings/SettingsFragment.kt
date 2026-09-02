package eu.kastroguru.astrodiary.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.BuildConfig
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.ReadingMode
import eu.kastroguru.astrodiary.data.ReadingModeStore
import eu.kastroguru.astrodiary.data.AspectPrefs
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.databinding.FragmentSettingsBinding
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject lateinit var readingModeStore: ReadingModeStore

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var aspectPrefs: AspectPrefs
    @Inject lateinit var chartDisplayPrefs: ChartDisplayPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The version is here because a user reporting a problem has no other way to say which
        // build they are on, and the store listing lags behind what is installed.
        binding.tvAppVersion.text =
            getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        // ── Chart display settings ────────────────────────────────────────────
        val houseSystemNames  = arrayOf("Placidus", "Whole Sign", "Koch", "Equal", "Regiomontanus", "Porphyry")
        val houseSystemValues = arrayOf("P", "W", "K", "E", "R", "O")

        fun updateHouseSystemLabel() {
            val idx = houseSystemValues.indexOf(chartDisplayPrefs.houseSystem).coerceAtLeast(0)
            binding.tvHouseSystemValue.text = houseSystemNames[idx]
        }
        updateHouseSystemLabel()

        binding.rowHouseSystem.setOnClickListener {
            val current = houseSystemValues.indexOf(chartDisplayPrefs.houseSystem).coerceAtLeast(0)
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.house_system))
                .setSingleChoiceItems(houseSystemNames, current) { dialog, which ->
                    chartDisplayPrefs.houseSystem = houseSystemValues[which]
                    updateHouseSystemLabel()
                    dialog.dismiss()
                }
                .show()
        }

        // Reading mode — the same question asked on first run, changeable at any time.
        fun renderMode() {
            binding.tvReadingModeValue.text = getString(
                if (readingModeStore.current == ReadingMode.PLAIN) R.string.mode_plain_short
                else R.string.mode_astrologer_short
            )
        }
        renderMode()
        binding.rowReadingMode.setOnClickListener {
            val labels = arrayOf(
                getString(R.string.mode_plain_short) + " — " + getString(R.string.mode_plain_answer),
                getString(R.string.mode_astrologer_short) + " — " + getString(R.string.mode_astrologer_answer),
            )
            val current = if (readingModeStore.current == ReadingMode.PLAIN) 0 else 1
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.mode_setting_title)
                .setSingleChoiceItems(labels, current) { dialog, which ->
                    readingModeStore.choose(if (which == 0) ReadingMode.PLAIN else ReadingMode.ASTROLOGER)
                    renderMode()
                    dialog.dismiss()
                }
                .show()
        }

        binding.switchShowDignities.isChecked = chartDisplayPrefs.showDignities
        binding.switchShowPartOfFortune.isChecked = chartDisplayPrefs.showPartOfFortune
        binding.switchShowAspectGrid.isChecked = chartDisplayPrefs.showAspectGrid

        binding.switchShowDignities.setOnCheckedChangeListener { _, v -> chartDisplayPrefs.showDignities = v }
        binding.switchShowPartOfFortune.setOnCheckedChangeListener { _, v -> chartDisplayPrefs.showPartOfFortune = v }
        binding.switchShowAspectGrid.setOnCheckedChangeListener { _, v -> chartDisplayPrefs.showAspectGrid = v }

        // ── Aspect body switches ──────────────────────────────────────────────
        binding.switchChiron.isChecked = aspectPrefs.includeChiron
        binding.switchLilith.isChecked = aspectPrefs.includeLilith
        binding.switchRahu.isChecked   = aspectPrefs.includeRahu
        binding.switchAsc.isChecked    = aspectPrefs.includeAsc
        binding.switchDsc.isChecked    = aspectPrefs.includeDsc
        binding.switchMc.isChecked     = aspectPrefs.includeMc
        binding.switchIc.isChecked     = aspectPrefs.includeIc

        binding.switchChiron.setOnCheckedChangeListener { _, v -> aspectPrefs.includeChiron = v }
        binding.switchLilith.setOnCheckedChangeListener { _, v -> aspectPrefs.includeLilith = v }
        binding.switchRahu.setOnCheckedChangeListener   { _, v -> aspectPrefs.includeRahu   = v }
        binding.switchAsc.setOnCheckedChangeListener    { _, v -> aspectPrefs.includeAsc    = v }
        binding.switchDsc.setOnCheckedChangeListener    { _, v -> aspectPrefs.includeDsc    = v }
        binding.switchMc.setOnCheckedChangeListener     { _, v -> aspectPrefs.includeMc     = v }
        binding.switchIc.setOnCheckedChangeListener     { _, v -> aspectPrefs.includeIc     = v }

        binding.switchHidePersonalTransits.isChecked = aspectPrefs.hidePersonalTransits
        binding.switchHidePersonalTransits.setOnCheckedChangeListener { _, v ->
            aspectPrefs.hidePersonalTransits = v
        }

        // ── Language selection ────────────────────────────────────────────────
        val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (currentTag.startsWith("bg")) {
            binding.radioLangBg.isChecked = true
        } else {
            binding.radioLangEn.isChecked = true
        }

        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val tag = if (checkedId == binding.radioLangBg.id) "bg" else "en"
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
            // AppCompat automatically recreates the activity after locale change
        }

        // ── Import from Astro.com ─────────────────────────────────────────────
        binding.btnImportAstrocom.setOnClickListener {
            AstroComImportDialog().show(childFragmentManager, "import_astrocom")
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
