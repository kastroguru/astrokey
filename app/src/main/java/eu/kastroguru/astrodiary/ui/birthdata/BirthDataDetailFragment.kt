package eu.kastroguru.astrodiary.ui.birthdata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentBirthDataDetailBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BirthDataDetailFragment : Fragment() {

    private var _binding: FragmentBirthDataDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BirthDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBirthDataDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong("birthDataId") ?: return

        viewModel.selectItem(id)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedItem.collect { entity ->
                entity ?: return@collect
                binding.textName.text = entity.name
                binding.textDateTime.text = "%04d-%02d-%02d %02d:%02d".format(
                    entity.year, entity.month, entity.day, entity.hour, entity.minutes
                )
                binding.textLocation.text = "${entity.city}, ${entity.country}"
                binding.textTimezone.text = entity.timezone
                binding.textLatLon.text = "%.4f, %.4f".format(entity.latitude, entity.longitude)

                // House cusps
                val cusps = listOf(
                    entity.cusp1, entity.cusp2, entity.cusp3, entity.cusp4,
                    entity.cusp5, entity.cusp6, entity.cusp7, entity.cusp8,
                    entity.cusp9, entity.cusp10, entity.cusp11, entity.cusp12
                )
                val cuspText = cusps.mapIndexed { i, d ->
                    "H${i + 1}: %6.2f°".format(d)
                }.joinToString("\n")
                binding.textCusps.text = cuspText
            }
        }

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_birthDataDetailFragment_to_birthDataFormFragment,
                Bundle().apply { putLong("birthDataId", id) }
            )
        }

        binding.buttonViewChart.setOnClickListener {
            findNavController().navigate(
                R.id.action_birthDataDetailFragment_to_chartFragment,
                Bundle().apply { putLong("birthDataId", id) }
            )
        }

        binding.buttonViewTable.setOnClickListener {
            findNavController().navigate(
                R.id.action_birthDataDetailFragment_to_planetTableFragment,
                Bundle().apply { putLong("birthDataId", id) }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
