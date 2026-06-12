package eu.kastroguru.astrodiary.ui.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skedgo.converter.TimezoneMapper
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.network.NominatimResult
import eu.kastroguru.astrodiary.databinding.FragmentEventFormBinding
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class EventFormFragment : Fragment() {

    private var _binding: FragmentEventFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()

    private var selectedYear   = 0
    private var selectedMonth  = 0
    private var selectedDay    = 0
    private var selectedHour   = 0
    private var selectedMinute = 0
    private var selectedLat    = Double.NaN
    private var selectedLon    = Double.NaN
    private var detectedTzId   = ""
    private var editId         = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editId = arguments?.getLong("eventId") ?: 0L
        val isEdit = editId > 0L

        if (isEdit) {
            requireActivity().title = getString(R.string.edit_birth_data)
            binding.buttonSave.text = getString(R.string.recalculate_update)
            viewModel.selectItem(editId)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.selectedItem.filterNotNull().take(1).collect { e ->
                    selectedYear = e.year; selectedMonth = e.month; selectedDay = e.day
                    selectedHour = e.hour; selectedMinute = e.minutes
                    selectedLat = e.latitude; selectedLon = e.longitude
                    detectedTzId = e.timezone
                    binding.editName.setText(e.name)
                    binding.editCity.setText(e.city)
                    binding.editCountry.setText(e.country)
                    binding.editDescription.setText(e.description)
                    binding.editTags.setText(e.tags)
                    binding.switchGlobal.isChecked = e.isGlobal
                    updateDateLabel(); updateTimeLabel()
                    binding.textLocationSelected.text = getString(R.string.location_selected, selectedLat, selectedLon)
                    binding.textTimezone.text = getString(R.string.timezone_detected, detectedTzId)
                }
            }
        } else {
            // Default to current date/time
            val now = Calendar.getInstance()
            selectedYear   = now.get(Calendar.YEAR)
            selectedMonth  = now.get(Calendar.MONTH) + 1
            selectedDay    = now.get(Calendar.DAY_OF_MONTH)
            selectedHour   = now.get(Calendar.HOUR_OF_DAY)
            selectedMinute = now.get(Calendar.MINUTE)
        }

        updateDateLabel(); updateTimeLabel()

        binding.buttonPickDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedYear = y; selectedMonth = m + 1; selectedDay = d; updateDateLabel()
            }, selectedYear, selectedMonth - 1, selectedDay).show()
        }

        binding.buttonPickTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                selectedHour = h; selectedMinute = m; updateTimeLabel()
            }, selectedHour, selectedMinute, true).show()
        }

        binding.buttonSearchCity.setOnClickListener {
            val city = binding.editCity.text.toString().trim()
            val country = binding.editCountry.text.toString().trim()
            if (city.isBlank()) { binding.layoutCity.error = getString(R.string.error_city_required); return@setOnClickListener }
            binding.layoutCity.error = null
            viewModel.searchCity(city, country)
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.editName.text.toString().trim()
            if (name.isBlank()) { binding.layoutName.error = getString(R.string.error_name_required); return@setOnClickListener }
            binding.layoutName.error = null
            if (selectedLat.isNaN() || selectedLon.isNaN()) {
                Snackbar.make(binding.root, R.string.error_location_required, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (detectedTzId.isBlank()) {
                Snackbar.make(binding.root, R.string.timezone_not_detected, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.calculateAndSave(
                name = name,
                year = selectedYear, month = selectedMonth, day = selectedDay,
                hour = selectedHour, minutes = selectedMinute,
                city = binding.editCity.text.toString().trim(),
                country = binding.editCountry.text.toString().trim(),
                timezoneId = detectedTzId,
                latitude = selectedLat, longitude = selectedLon,
                description = binding.editDescription.text.toString(),
                tags = binding.editTags.text.toString(),
                isGlobal = binding.switchGlobal.isChecked,
                editId = editId
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.formState.collect { state ->
                when (state) {
                    is EventFormState.Idle    -> binding.progressBar.isVisible = false
                    is EventFormState.Loading -> binding.progressBar.isVisible = true
                    is EventFormState.GeocodingResults -> {
                        binding.progressBar.isVisible = false
                        showGeocodingPicker(state.results)
                    }
                    is EventFormState.Success -> {
                        binding.progressBar.isVisible = false
                        Snackbar.make(binding.root, R.string.saved_successfully, Snackbar.LENGTH_SHORT).show()
                        viewModel.resetFormState()
                        findNavController().navigateUp()
                    }
                    is EventFormState.Error -> {
                        binding.progressBar.isVisible = false
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        viewModel.resetFormState()
                    }
                }
            }
        }
    }

    private fun showGeocodingPicker(results: List<NominatimResult>) {
        if (results.isEmpty()) { Snackbar.make(binding.root, R.string.no_results_found, Snackbar.LENGTH_SHORT).show(); return }
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_location)
            .setItems(results.map { it.displayName }.toTypedArray()) { _, idx ->
                val r = results[idx]
                selectedLat = r.lat.toDouble()
                selectedLon = r.lon.toDouble()
                detectedTzId = TimezoneMapper.latLngToTimezoneString(selectedLat, selectedLon)
                    .takeIf { it.isNotBlank() } ?: fallbackTimezone(selectedLon)
                binding.textLocationSelected.text = getString(R.string.location_selected, selectedLat, selectedLon)
                binding.textTimezone.text = getString(R.string.timezone_auto, detectedTzId)
                // Cache so repeat searches skip the API
                viewModel.cacheLocation(
                    binding.editCity.text.toString().trim(),
                    binding.editCountry.text.toString().trim(),
                    selectedLat, selectedLon, detectedTzId
                )
            }
            .show()
    }

    private fun fallbackTimezone(lon: Double): String {
        val h = Math.round(lon / 15.0).toInt()
        return if (h >= 0) "GMT+$h" else "GMT$h"
    }

    private fun updateDateLabel() { binding.buttonPickDate.text = "%04d-%02d-%02d".format(selectedYear, selectedMonth, selectedDay) }
    private fun updateTimeLabel() { binding.buttonPickTime.text = "%02d:%02d".format(selectedHour, selectedMinute) }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
