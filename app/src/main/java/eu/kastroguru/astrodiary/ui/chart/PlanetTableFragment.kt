package eu.kastroguru.astrodiary.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.databinding.FragmentPlanetTableBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanetRow(
    val planetName: String,
    val glyph: String,
    val signName: String,
    val signGlyph: String,
    val degreeInSign: Int,
    val minutes: Int,
    val house: Int,
    val dignity: String? = null,
    val retroStatus: String? = null  // "R", "S", or null
)

@AndroidEntryPoint
class PlanetTableFragment : Fragment() {

    private var _binding: FragmentPlanetTableBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChartViewModel by viewModels()
    private lateinit var adapter: PlanetRowAdapter

    @Inject lateinit var chartDisplayPrefs: ChartDisplayPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanetTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = setupPlanetTable(binding.recyclerView, binding.headerDignity, chartDisplayPrefs)

        val id = arguments?.getLong("birthDataId") ?: return
        viewModel.load(id)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.astroData.collect { data ->
                data ?: return@collect
                populatePlanetTable(adapter, binding.cardAspectGrid, binding.containerAspectGrid, data, chartDisplayPrefs, requireContext())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
