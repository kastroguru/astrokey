package eu.kastroguru.astrodiary.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.aaf.AafParser
import eu.kastroguru.astrodiary.data.repository.BirthDataRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AstroComImportDialog : DialogFragment() {

    @Inject lateinit var repository: BirthDataRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_astrocom_import, null)
        val etAafData = view.findViewById<EditText>(R.id.etAafData)
        val layoutProgress = view.findViewById<View>(R.id.layoutProgress)
        val tvProgress = view.findViewById<TextView>(R.id.tvProgress)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.import_astrocom_title)
            .setView(view)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.import_btn, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val text = etAafData.text.toString()
                val entries = AafParser.parse(text)
                if (entries.isEmpty()) {
                    etAafData.error = getString(R.string.import_no_data)
                    return@setOnClickListener
                }
                etAafData.error = null
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
                layoutProgress.visibility = View.VISIBLE

                val appContext = requireContext().applicationContext
                lifecycleScope.launch {
                    var success = 0
                    var failed = 0
                    entries.forEachIndexed { i, entry ->
                        tvProgress.text = getString(R.string.import_progress, i + 1, entries.size)
                        val result = repository.calculateAndSave(
                            name = entry.name,
                            year = entry.year, month = entry.month, day = entry.day,
                            hour = entry.hour, minutes = entry.minute,
                            city = entry.city, country = entry.country,
                            timezoneId = entry.timezoneId,
                            latitude = entry.latitude, longitude = entry.longitude
                        )
                        if (result.isSuccess) success++ else failed++
                    }

                    dismiss()
                    val msg = if (failed == 0)
                        appContext.getString(R.string.import_success, success)
                    else
                        appContext.getString(R.string.import_partial, success, failed)
                    Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show()
                }
            }
        }

        return dialog
    }
}
