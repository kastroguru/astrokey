package eu.kastroguru.astrodiary.ui.birthdata

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.ReadingMode
import eu.kastroguru.astrodiary.data.ReadingModeStore
import javax.inject.Inject
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.databinding.FragmentBirthDataListBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BirthDataListFragment : Fragment() {

    @Inject lateinit var readingModeStore: ReadingModeStore

    private var _binding: FragmentBirthDataListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BirthDataViewModel by viewModels()
    private lateinit var adapter: BirthDataAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBirthDataListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BirthDataAdapter(
            onClick = { entity ->
                // Plain mode opens the reading; astrologer mode opens the chart data, as before.
                val args = Bundle().apply { putLong("birthDataId", entity.id) }
                if (readingModeStore.current == ReadingMode.PLAIN) {
                    findNavController().navigate(R.id.chartReadingFragment, args)
                } else {
                    findNavController().navigate(
                        R.id.action_birthDataListFragment_to_birthDataDetailFragment, args
                    )
                }
            },
            onLongClick = { /* handled by swipe */ }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        attachSwipeToDelete()

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_birthDataListFragment_to_birthDataFormFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is BirthDataUiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.emptyText.isVisible = false
                    }
                    is BirthDataUiState.Success -> {
                        binding.progressBar.isVisible = false
                        adapter.submitList(state.items)
                        binding.emptyText.isVisible = state.items.isEmpty()
                    }
                    is BirthDataUiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.emptyText.isVisible = true
                        binding.emptyText.text = state.message
                    }
                }
            }
        }
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
                    // Red background
                    val bgPaint = Paint().apply { color = deleteColor }
                    c.drawRect(
                        itemView.right + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), bgPaint
                    )
                    // Trash icon
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

    private fun showUndoSnackbar(entity: BirthDataEntity) {
        var undone = false
        Snackbar.make(binding.root, "\"${entity.name}\" deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                undone = true
                viewModel.restore(entity)
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(snackbar: Snackbar, event: Int) {
                    // if not undone — already deleted by ViewModel
                }
            })
            .setActionTextColor(Color.parseColor("#8E8CEB"))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
