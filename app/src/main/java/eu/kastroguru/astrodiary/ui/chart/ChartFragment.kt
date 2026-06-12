package eu.kastroguru.astrodiary.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.databinding.FragmentChartBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChartViewModel by viewModels()
    private lateinit var adapter: PlanetRowAdapter

    @Inject lateinit var chartDisplayPrefs: ChartDisplayPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentChartBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = setupPlanetTable(binding.recyclerView, binding.headerDignity, chartDisplayPrefs)

        val id = arguments?.getLong("birthDataId") ?: return
        viewModel.load(id)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.astroData.collect { data ->
                data ?: return@collect
                binding.chartView.astroData = data
                populatePlanetTable(adapter, binding.cardAspectGrid, binding.containerAspectGrid, data, chartDisplayPrefs, requireContext())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.entity.collect { entity ->
                entity ?: return@collect
                requireActivity().title = entity.name
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
