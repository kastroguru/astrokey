package eu.kastroguru.astrodiary.ui.events

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import eu.kastroguru.astrodiary.databinding.FragmentEventListBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventListFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: EventAdapter

    private val SPAN = 3
    private var tags: List<String> = emptyList()
    private var persons: List<BirthDataEntity> = emptyList()
    private var selectedTags: Set<String> = emptySet()
    private var bindingPersonSpinner = false   // guard so programmatic selection doesn't re-filter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventAdapter(
            onClick = { entity ->
                findNavController().navigate(
                    R.id.action_eventListFragment_to_eventDetailFragment,
                    Bundle().apply { putLong("eventId", entity.id) }
                )
            },
            onLongClick = { entity -> confirmDelete(entity) }
        )

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), SPAN)
        binding.recyclerView.adapter = adapter
        val gap = (resources.displayMetrics.density * 6).toInt()
        binding.recyclerView.addItemDecoration(GridSpacingDecoration(SPAN, gap))
        binding.recyclerView.setPadding(gap, gap, gap, (resources.displayMetrics.density * 80).toInt())
        binding.recyclerView.clipToPadding = false

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_eventListFragment_to_eventFormFragment)
        }

        setupPersonSpinner()
        binding.buttonTags.setOnClickListener { showTagDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableTags.collect { tags = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availablePersons.collect { persons = it; rebuildPersonSpinner() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filter.collect { f ->
                selectedTags = f.tags
                binding.buttonTags.text =
                    if (f.tags.isEmpty()) getString(R.string.filter_tags)
                    else getString(R.string.filter_tags_count, f.tags.size)
                // The person can also change from another screen (app-wide selection) — keep the
                // dropdown in step without re-firing onItemSelected.
                syncPersonSpinner(f.personId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EventUiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.emptyText.isVisible = false
                    }
                    is EventUiState.Success -> {
                        binding.progressBar.isVisible = false
                        adapter.submitList(state.items)
                        binding.emptyText.isVisible = state.items.isEmpty()
                    }
                    is EventUiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.emptyText.isVisible = true
                        binding.emptyText.text = state.message
                    }
                }
            }
        }
    }

    private fun setupPersonSpinner() {
        binding.spinnerPerson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (bindingPersonSpinner) return
                viewModel.setPersonFilter(if (position == 0) null else persons.getOrNull(position - 1)?.id)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /** Moves the spinner to [personId] without treating it as a user selection. */
    private fun syncPersonSpinner(personId: Long?) {
        val idx = if (personId == null) 0
                  else persons.indexOfFirst { it.id == personId }.let { if (it < 0) 0 else it + 1 }
        if (binding.spinnerPerson.selectedItemPosition == idx) return
        bindingPersonSpinner = true
        binding.spinnerPerson.setSelection(idx)
        binding.spinnerPerson.post { bindingPersonSpinner = false }
    }

    private fun rebuildPersonSpinner() {
        val labels = listOf(getString(R.string.filter_all_persons)) + persons.map { it.name }
        bindingPersonSpinner = true
        binding.spinnerPerson.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, labels
        )
        val current = viewModel.filter.value.personId
        val idx = if (current == null) 0 else persons.indexOfFirst { it.id == current }.let { if (it < 0) 0 else it + 1 }
        binding.spinnerPerson.setSelection(idx)
        binding.spinnerPerson.post { bindingPersonSpinner = false }
    }

    private fun showTagDialog() {
        val all = tags.toTypedArray()
        if (all.isEmpty()) {
            Snackbar.make(binding.root, R.string.filter_no_tags_available, Snackbar.LENGTH_SHORT).show()
            return
        }
        val checked = BooleanArray(all.size) { all[it] in selectedTags }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.filter_tags)
            .setMultiChoiceItems(all, checked) { _, which, isChecked -> checked[which] = isChecked }
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.setTags(all.filterIndexed { i, _ -> checked[i] }.toSet())
            }
            // "Без тагове" clears the tag selection → back to all events.
            .setNeutralButton(R.string.filter_no_tags) { _, _ -> viewModel.setTags(emptySet()) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmDelete(entity: HistoryEventEntity) {
        viewModel.delete(entity)
        Snackbar.make(binding.root, getString(R.string.event_deleted, entity.name), Snackbar.LENGTH_LONG)
            .setActionTextColor(Color.parseColor("#8E8CEB"))
            .setAction(R.string.undo) { viewModel.restore(entity) }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(sb: Snackbar, event: Int) {
                    if (event != DISMISS_EVENT_ACTION) viewModel.deleteImageFile(entity.imagePath)
                }
            })
            .show()
    }

    private class GridSpacingDecoration(private val span: Int, private val gap: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val col = parent.getChildAdapterPosition(view) % span
            outRect.left = gap - col * gap / span
            outRect.right = (col + 1) * gap / span
            outRect.top = gap
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
