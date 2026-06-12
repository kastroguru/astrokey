package eu.kastroguru.astrodiary.ui.events

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
import eu.kastroguru.astrodiary.databinding.FragmentEventDetailBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()
    private var eventId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventId = arguments?.getLong("eventId") ?: return
        viewModel.selectItem(eventId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedItem.collect { entity ->
                entity ?: return@collect
                binding.textName.text = entity.name
                binding.textDateTime.text = "%04d-%02d-%02d %02d:%02d".format(
                    entity.year, entity.month, entity.day, entity.hour, entity.minutes
                )
                binding.textLocation.text = "${entity.city}, ${entity.country}"
                binding.textTimezone.text = entity.timezone
                binding.textDescription.text = entity.description.ifBlank { getString(R.string.no_description) }
                binding.textTags.text = entity.tags.ifBlank { getString(R.string.no_tags) }
                binding.textGlobal.text = if (entity.isGlobal) getString(R.string.global_event) else getString(R.string.personal_event)
            }
        }

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_eventDetailFragment_to_eventFormFragment,
                Bundle().apply { putLong("eventId", eventId) }
            )
        }

        binding.buttonViewChart.setOnClickListener {
            findNavController().navigate(
                R.id.action_eventDetailFragment_to_eventChartFragment,
                Bundle().apply { putLong("eventId", eventId) }
            )
        }

        binding.buttonViewTable.setOnClickListener {
            findNavController().navigate(
                R.id.action_eventDetailFragment_to_eventPlanetTableFragment,
                Bundle().apply { putLong("eventId", eventId) }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
