package eu.kastroguru.astrodiary.ui.birthdata

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
import eu.kastroguru.astrodiary.databinding.FragmentBirthDataFormBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take

@AndroidEntryPoint
class BirthDataFormFragment : Fragment() {

    private var _binding: FragmentBirthDataFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BirthDataViewModel by viewModels()

    private var selectedYear   = 1990
    private var selectedMonth  = 1
    private var selectedDay    = 1
    private var selectedHour   = 12
    private var selectedMinute = 0
    private var selectedLat    = Double.NaN
    private var selectedLon    = Double.NaN
    private var detectedTzId   = ""

    /** 0 = create new; >0 = edit existing */
    private var editId = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBirthDataFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editId = arguments?.getLong("birthDataId") ?: 0L
        val isEdit = editId > 0L

        if (isEdit) {
            requireActivity().title = getString(R.string.edit_birth_data)
            binding.buttonSave.text = getString(R.string.recalculate_update)
            // Load existing entity to pre-fill
            viewModel.selectItem(editId)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.selectedItem.filterNotNull().take(1).collect { entity ->
                    selectedYear   = entity.year
                    selectedMonth  = entity.month
                    selectedDay    = entity.day
                    selectedHour   = entity.hour
                    selectedMinute = entity.minutes
                    selectedLat    = entity.latitude
                    selectedLon    = entity.longitude
                    detectedTzId   = entity.timezone

                    binding.editName.setText(entity.name)
                    binding.editCity.setText(entity.city)
                    binding.editCountry.setText(entity.country)
                    updateDateLabel()
                    updateTimeLabel()
                    binding.textLocationSelected.text = getString(R.string.location_selected, selectedLat, selectedLon)
                    binding.textTimezone.text = getString(R.string.timezone_detected, detectedTzId)
                }
            }
        }

        // Date picker
        updateDateLabel()
        binding.buttonPickDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedYear = y; selectedMonth = m + 1; selectedDay = d; updateDateLabel()
            }, selectedYear, selectedMonth - 1, selectedDay).show()
        }

        // Time picker
        updateTimeLabel()
        binding.buttonPickTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                selectedHour = h; selectedMinute = m; updateTimeLabel()
            }, selectedHour, selectedMinute, true).show()
        }

        // City search
        binding.buttonSearchCity.setOnClickListener {
            val city    = binding.editCity.text.toString().trim()
            val country = binding.editCountry.text.toString().trim()
            if (city.isBlank()) { binding.layoutCity.error = getString(R.string.error_city_required); return@setOnClickListener }
            binding.layoutCity.error = null
            viewModel.searchCity(city, country)
        }

        // Save / Update
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
                name       = name,
                year       = selectedYear, month = selectedMonth, day = selectedDay,
                hour       = selectedHour, minutes = selectedMinute,
                city       = binding.editCity.text.toString().trim(),
                country    = binding.editCountry.text.toString().trim(),
                timezoneId = detectedTzId,
                latitude   = selectedLat,
                longitude  = selectedLon,
                editId     = editId
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.formState.collect { state ->
                when (state) {
                    is FormState.Idle    -> binding.progressBar.isVisible = false
                    is FormState.Loading -> binding.progressBar.isVisible = true
                    is FormState.GeocodingResults -> {
                        binding.progressBar.isVisible = false
                        showGeocodingPicker(state.results)
                    }
                    is FormState.Success -> {
                        binding.progressBar.isVisible = false
                        viewModel.resetFormState()
                        findNavController().navigate(
                            R.id.action_birthDataFormFragment_to_chartFragment,
                            Bundle().apply { putLong("birthDataId", state.id) }
                        )
                    }
                    is FormState.Error -> {
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
                // Cache for future searches
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

    private fun updateDateLabel() {
        binding.buttonPickDate.text = "%04d-%02d-%02d".format(selectedYear, selectedMonth, selectedDay)
    }

    private fun updateTimeLabel() {
        binding.buttonPickTime.text = "%02d:%02d".format(selectedHour, selectedMinute)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
