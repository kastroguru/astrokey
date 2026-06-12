package eu.kastroguru.astrodiary.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.databinding.FragmentPlanetTableBinding
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.ui.chart.PlanetRowAdapter
import eu.kastroguru.astrodiary.ui.chart.populatePlanetTable
import eu.kastroguru.astrodiary.ui.chart.setupPlanetTable
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EventPlanetTableFragment : Fragment() {

    private var _binding: FragmentPlanetTableBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: PlanetRowAdapter

    @Inject lateinit var astroCalculator: AstroCalculator
    @Inject lateinit var chartDisplayPrefs: ChartDisplayPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentPlanetTableBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = setupPlanetTable(binding.recyclerView, binding.headerDignity, chartDisplayPrefs)

        val id = arguments?.getLong("eventId") ?: return
        viewModel.selectItem(id)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedItem.collect { entity ->
                entity ?: return@collect
                val astroData = astroCalculator.calculate(
                    entity.yearUtc, entity.monthUtc, entity.dayUtc,
                    entity.hourUtc + entity.minutesUtc / 60.0,
                    entity.latitude, entity.longitude,
                    chartDisplayPrefs.houseSystemChar
                )
                populatePlanetTable(adapter, binding.cardAspectGrid, binding.containerAspectGrid, astroData, chartDisplayPrefs, requireContext())
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
