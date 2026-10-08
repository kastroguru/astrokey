package eu.kastroguru.astrodiary.ui.birthdata

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
            // Swipe a row left for these two, in either reading mode.
            onEdit = { entity ->
                findNavController().navigate(
                    R.id.action_birthDataListFragment_to_birthDataFormFragment,
                    Bundle().apply { putLong("birthDataId", entity.id) }
                )
            },
            onDelete = { entity ->
                viewModel.delete(entity)
                showUndoSnackbar(entity)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) adapter.closeOpenRow()
            }
        })

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

    private fun showUndoSnackbar(entity: BirthDataEntity) {
        Snackbar.make(binding.root, getString(R.string.birth_data_deleted, entity.name), Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { viewModel.restore(entity) }
            .setActionTextColor(Color.parseColor("#8E8CEB"))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
