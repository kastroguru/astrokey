package eu.kastroguru.astrodiary.ui.events

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import eu.kastroguru.astrodiary.databinding.FragmentEventListBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventListFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: EventAdapter

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
            onLongClick = { /* handled by swipe */ }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        attachSwipeToDelete()

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_eventListFragment_to_eventFormFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableTags.collect { tags -> updateTagChips(tags) }
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

    private fun updateTagChips(tags: List<String>) {
        binding.chipGroupTags.removeAllViews()

        val allChip = Chip(requireContext()).apply {
            text = getString(R.string.all)
            isCheckable = true
            isChecked = true
            chipBackgroundColor = null
            setChipBackgroundColorResource(R.color.chip_bg_selector)
            setOnClickListener {
                viewModel.clearFilter()
                isChecked = true
            }
        }
        binding.chipGroupTags.addView(allChip)

        tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag
                isCheckable = true
                chipBackgroundColor = null
                setChipBackgroundColorResource(R.color.chip_bg_selector)
                setOnClickListener {
                    viewModel.setTagFilter(tag)
                    allChip.isChecked = false
                    isChecked = true
                }
                setOnLongClickListener {
                    showDeleteTagDialog(tag)
                    true
                }
            }
            binding.chipGroupTags.addView(chip)
        }
    }

    private fun showDeleteTagDialog(tag: String) {
        val dp = resources.displayMetrics.density.toInt()
        val checkBox = CheckBox(requireContext()).apply {
            setText(R.string.also_delete_events_with_tag)
            isChecked = false
            setPadding(dp * 24, dp * 8, dp * 16, dp * 8)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_tag_title, tag))
            .setView(checkBox)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteTag(tag, checkBox.isChecked)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun attachSwipeToDelete() {
        val deleteColor = Color.parseColor("#B71C1C")
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_swipe)

        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val entity = adapter.currentList[position]
                viewModel.delete(entity)
                showUndoSnackbar(entity)
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                if (dX < 0) {
                    val bgPaint = Paint().apply { color = deleteColor }
                    c.drawRect(
                        itemView.right + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), bgPaint
                    )
                    deleteIcon?.let { icon ->
                        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                        icon.setBounds(iconLeft, iconTop, iconLeft + icon.intrinsicWidth, iconTop + icon.intrinsicHeight)
                        icon.draw(c)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)
    }

    private fun showUndoSnackbar(entity: HistoryEventEntity) {
        Snackbar.make(binding.root, "\"${entity.name}\" deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") { viewModel.restore(entity) }
            .setActionTextColor(Color.parseColor("#8E8CEB"))
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshTags()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
