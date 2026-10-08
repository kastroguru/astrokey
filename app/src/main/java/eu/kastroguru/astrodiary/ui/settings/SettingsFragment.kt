package eu.kastroguru.astrodiary.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.BuildConfig
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.backup.AstroKeyArchive
import eu.kastroguru.astrodiary.data.backup.NewerAstroKeyFile
import eu.kastroguru.astrodiary.data.backup.NotAnAstroKeyFile
import eu.kastroguru.astrodiary.data.ReadingMode
import eu.kastroguru.astrodiary.data.ReadingModeStore
import eu.kastroguru.astrodiary.data.AspectPrefs
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject lateinit var readingModeStore: ReadingModeStore

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var aspectPrefs: AspectPrefs
    @Inject lateinit var chartDisplayPrefs: ChartDisplayPrefs

    // Activity-scoped: MainActivity resets navigation to the start screen whenever it is recreated
    // (rotation on a tablet, dark mode, font size), which would otherwise kill an export or import
    // halfway. The work finishes, and the result waits for the next visit to Settings.
    private val backupViewModel: BackupViewModel by activityViewModels()
    private var confirmDialog: AlertDialog? = null

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument(AstroKeyArchive.MIME)
    ) { uri -> if (uri != null) backupViewModel.export(uri) }

    // "*/*": a custom extension has no registered type, so any narrower filter hides the file in
    // some pickers. The content is checked instead.
    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) backupViewModel.inspect(uri) }

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

        // ── Backup: export to / import from an .astrokey file ─────────────────
        binding.btnBackupExport.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (backupViewModel.hasData()) {
                    val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    exportLauncher.launch("astrokey-$day.${AstroKeyArchive.EXTENSION}")
                } else {
                    backupViewModel.nothingToExport()
                }
            }
        }
        binding.btnBackupImport.setOnClickListener { importLauncher.launch(arrayOf("*/*")) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                backupViewModel.state.collect(::renderBackup)
            }
        }
    }

    private fun renderBackup(state: BackupState) {
        val working = state is BackupState.Working
        binding.btnBackupExport.isEnabled = !working
        binding.btnBackupImport.isEnabled = !working
        binding.layoutBackupProgress.visibility = if (working) View.VISIBLE else View.GONE
        if (state is BackupState.Working) {
            binding.tvBackupProgress.setText(if (state.importing) R.string.backup_importing else R.string.backup_exporting)
        }

        val status: String? = when (state) {
            is BackupState.Exported -> with(state.result) {
                getString(R.string.backup_export_done, charts, events, photos)
            }
            is BackupState.Imported -> with(state.result) {
                getString(R.string.backup_import_done, addedCharts, addedEvents, skippedCharts, skippedEvents)
            }
            BackupState.NothingToExport -> getString(R.string.backup_nothing_to_export)
            is BackupState.Failed -> when (val e = state.error) {
                is NotAnAstroKeyFile -> getString(R.string.backup_error_not_astrokey)
                is NewerAstroKeyFile -> getString(R.string.backup_error_newer)
                else -> getString(R.string.backup_error_failed, e.message ?: e.javaClass.simpleName)
            }
            else -> null
        }
        binding.tvBackupStatus.text = status
        binding.tvBackupStatus.visibility = if (status == null) View.GONE else View.VISIBLE

        if (state is BackupState.Confirm) showImportConfirm(state)
    }

    private fun showImportConfirm(state: BackupState.Confirm) {
        if (confirmDialog?.isShowing == true) return
        val exported = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
            .format(Date(state.exportedAt))
        // Cancel through the button or the back key only: a dismiss also happens on rotation, and
        // the question has to survive that.
        confirmDialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.backup_import_confirm_title)
            .setMessage(getString(R.string.backup_import_confirm_msg, exported, state.charts, state.events))
            .setPositiveButton(R.string.import_btn) { _, _ -> backupViewModel.confirmImport() }
            .setNegativeButton(R.string.cancel) { _, _ -> backupViewModel.cancel() }
            .setOnCancelListener { backupViewModel.cancel() }
            .show()
    }

    override fun onDestroyView() {
        confirmDialog?.dismiss()
        confirmDialog = null
        backupViewModel.clearResult()   // shown once; an unanswered question or running work stays
        super.onDestroyView()
        _binding = null
    }
}
