package eu.kastroguru.astrodiary.ui.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skedgo.converter.TimezoneMapper
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
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

    private var persons: List<BirthDataEntity> = emptyList()
    private var pendingPersonId: Long? = null   // person to preselect once the chart list loads

    private var originalImagePath: String? = null   // image saved on the event when editing (null on create)
    private var currentImagePath: String? = null    // working value, possibly a freshly picked temp
    private var savedSuccessfully = false

    // System photo picker — no storage permission needed.
    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@registerForActivityResult
        viewLifecycleOwner.lifecycleScope.launch {
            val newPath = viewModel.saveImage(uri)
            if (newPath == null) {
                Snackbar.make(binding.root, R.string.event_image_failed, Snackbar.LENGTH_SHORT).show()
                return@launch
            }
            // Drop any earlier freshly-picked temp from this session (avoid orphaned files).
            if (currentImagePath != null && currentImagePath != originalImagePath) viewModel.deleteImageFile(currentImagePath)
            currentImagePath = newPath
            updateImageUi()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editId = arguments?.getLong("eventId") ?: 0L
        val isEdit = editId > 0L

        // Populate the person dropdown from the natal charts; keep it in sync if charts change.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availablePersons.collect { list ->
                persons = list
                val labels = listOf(getString(R.string.person_none)) + list.map { it.name }
                binding.spinnerPerson.adapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_dropdown_item, labels
                )
                applyPersonSelection()
            }
        }

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
                    pendingPersonId = e.personId
                    applyPersonSelection()
                    originalImagePath = e.imagePath
                    currentImagePath = e.imagePath
                    updateImageUi()
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

        binding.buttonPickImage.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.buttonDeleteImage.setOnClickListener {
            // Drop a freshly-picked temp now; an already-saved original is removed on save.
            if (currentImagePath != null && currentImagePath != originalImagePath) viewModel.deleteImageFile(currentImagePath)
            currentImagePath = null
            updateImageUi()
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
                personId = selectedPersonId(),
                imagePath = currentImagePath,
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
                        // The save committed currentImagePath; a replaced original is now orphaned.
                        if (originalImagePath != null && originalImagePath != currentImagePath) {
                            viewModel.deleteImageFile(originalImagePath)
                        }
                        savedSuccessfully = true
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

    /** Spinner position 0 = "None"; positions 1..n map to [persons]. */
    private fun selectedPersonId(): Long? {
        val pos = binding.spinnerPerson.selectedItemPosition
        return if (pos <= 0) null else persons.getOrNull(pos - 1)?.id
    }

    private fun applyPersonSelection() {
        val id = pendingPersonId
        val idx = if (id == null) 0 else persons.indexOfFirst { it.id == id }.let { if (it < 0) 0 else it + 1 }
        if (binding.spinnerPerson.adapter != null && idx < binding.spinnerPerson.adapter.count) {
            binding.spinnerPerson.setSelection(idx)
        }
    }

    private fun fallbackTimezone(lon: Double): String {
        val h = Math.round(lon / 15.0).toInt()
        return if (h >= 0) "GMT+$h" else "GMT$h"
    }

    private fun updateDateLabel() { binding.buttonPickDate.text = "%04d-%02d-%02d".format(selectedYear, selectedMonth, selectedDay) }
    private fun updateTimeLabel() { binding.buttonPickTime.text = "%02d:%02d".format(selectedHour, selectedMinute) }

    private fun updateImageUi() {
        val path = currentImagePath
        val file = path?.let { java.io.File(it) }
        if (file != null && file.exists()) {
            val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
            binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath, opts))
            binding.imagePreview.isVisible = true
            binding.textNoImage.isVisible = false
            binding.buttonDeleteImage.isVisible = true
            binding.buttonPickImage.text = getString(R.string.event_image_replace)
        } else {
            binding.imagePreview.setImageDrawable(null)
            binding.imagePreview.isVisible = false
            binding.textNoImage.isVisible = true
            binding.buttonDeleteImage.isVisible = false
            binding.buttonPickImage.text = getString(R.string.event_image_upload)
        }
    }

    override fun onDestroyView() {
        // If we picked an image but never saved (user backed out), drop the orphaned temp file.
        if (!savedSuccessfully && currentImagePath != null && currentImagePath != originalImagePath) {
            viewModel.deleteImageFile(currentImagePath)
        }
        super.onDestroyView(); _binding = null
    }
}
