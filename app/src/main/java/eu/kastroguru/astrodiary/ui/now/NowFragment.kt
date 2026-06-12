package eu.kastroguru.astrodiary.ui.now

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.data.network.NominatimResult
import eu.kastroguru.astrodiary.databinding.FragmentNowBinding
import eu.kastroguru.astrodiary.ui.chart.PlanetRowAdapter
import eu.kastroguru.astrodiary.ui.chart.populatePlanetTable
import eu.kastroguru.astrodiary.ui.chart.setupPlanetTable
import eu.kastroguru.astrodiary.ui.transit.TransitStep
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class NowFragment : Fragment() {

    private var _binding: FragmentNowBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NowViewModel by viewModels()
    private lateinit var adapter: PlanetRowAdapter

    @Inject lateinit var chartDisplayPrefs: ChartDisplayPrefs

    private val chipButtons = mutableListOf<MaterialButton>()
    private var lastShownResultsSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = setupPlanetTable(binding.recyclerView, binding.headerDignity, chartDisplayPrefs)

        // Toggle city search section
        binding.btnEditLocation.setOnClickListener {
            val expanding = binding.layoutCitySearch.visibility != View.VISIBLE
            binding.layoutCitySearch.visibility = if (expanding) View.VISIBLE else View.GONE
            binding.btnEditLocation.setImageResource(
                if (expanding) android.R.drawable.ic_menu_close_clear_cancel else R.drawable.ic_edit
            )
        }

        // Location search
        binding.buttonSearchCity.setOnClickListener {
            val city    = binding.editCity.text.toString().trim()
            val country = binding.editCountry.text.toString().trim()
            if (city.isBlank()) {
                Snackbar.make(binding.root, R.string.enter_city_name, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lastShownResultsSize = 0
            viewModel.searchCity(city, country)
        }
        binding.buttonRefresh.setOnClickListener { viewModel.calculateNow() }

        // Time navigation
        buildStepChips()
        binding.btnBack.setOnClickListener       { viewModel.goBack() }
        binding.btnForward.setOnClickListener    { viewModel.goForward() }
        binding.btnNowRefresh.setOnClickListener { viewModel.goNow() }
        binding.tvNowDate.setOnClickListener     { showDateTimePicker() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.isVisible = state.isLoading

                    state.error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }

                    // Real current time at location
                    val tz = TimeZone.getTimeZone(state.timezoneId)
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply { timeZone = tz }
                    binding.textCurrentTime.text = sdf.format(Date())
                    binding.textLocation.text = "${state.city}, ${state.country}  (%.4f, %.4f)".format(state.latitude, state.longitude)
                    binding.textTimezone.text = state.timezoneId

                    if (binding.editCity.text.isNullOrBlank())    binding.editCity.setText(state.city)
                    if (binding.editCountry.text.isNullOrBlank()) binding.editCountry.setText(state.country)

                    if (state.geocodingResults.size != lastShownResultsSize && state.geocodingResults.isNotEmpty()) {
                        lastShownResultsSize = state.geocodingResults.size
                        showGeocodingPicker(state.geocodingResults)
                    }

                    // Date label for selected chart time
                    binding.tvNowDate.text = state.dateLabel

                    // Highlight selected step chip
                    chipButtons.forEachIndexed { idx, btn ->
                        val isSelected = TransitStep.values()[idx] == state.selectedStep
                        btn.setStrokeColorResource(if (isSelected) R.color.gold else R.color.card_stroke)
                        btn.setTextColor(if (isSelected) ContextCompat.getColor(requireContext(), R.color.gold)
                                         else Color.GRAY)
                    }

                    binding.chartView.astroData = state.astroData

                    val data = state.astroData ?: return@collect
                    populatePlanetTable(adapter, binding.cardAspectGrid, binding.containerAspectGrid, data, chartDisplayPrefs, requireContext())
                }
            }
        }
    }

    private fun buildStepChips() {
        chipButtons.clear()
        val ctx = requireContext()
        val container = binding.chipGroup
        container.removeAllViews()

        TransitStep.values().forEach { step ->
            val btn = MaterialButton(ctx, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = step.label
                textSize = 11f
                setPadding(16, 0, 16, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginEnd = 4 }
                setOnClickListener { viewModel.selectStep(step) }
            }
            container.addView(btn)
            chipButtons += btn
        }

        val defaultIdx = TransitStep.values().indexOf(TransitStep.DAY)
        if (defaultIdx >= 0) {
            binding.chipScrollView.post {
                val chip = chipButtons[defaultIdx]
                val scrollX = (chip.left - binding.chipScrollView.width / 2 + chip.width / 2).coerceAtLeast(0)
                binding.chipScrollView.scrollTo(scrollX, 0)
            }
        }
    }

    private fun showDateTimePicker() {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = viewModel.selectedTimeMs
        }
        DatePickerDialog(requireContext(), { _, y, m, d ->
            TimePickerDialog(requireContext(), { _, h, min ->
                viewModel.setDateTime(y, m + 1, d, h, min)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showGeocodingPicker(results: List<NominatimResult>) {
        if (results.isEmpty()) return
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select location")
            .setItems(results.map { it.displayName }.toTypedArray()) { _, idx ->
                viewModel.selectGeocodingResult(results[idx])
                lastShownResultsSize = 0
                binding.layoutCitySearch.visibility = View.GONE
                binding.btnEditLocation.setImageResource(R.drawable.ic_edit)
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.calculateNow()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
