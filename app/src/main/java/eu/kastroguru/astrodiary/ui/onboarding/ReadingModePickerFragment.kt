package eu.kastroguru.astrodiary.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.ReadingMode
import eu.kastroguru.astrodiary.data.ReadingModeStore
import eu.kastroguru.astrodiary.databinding.FragmentReadingModePickerBinding
import javax.inject.Inject

/**
 * Second question of the first run, right after the language: does this person read charts?
 *
 * Asked once, changeable later in settings. The wording avoids calling anyone a non-astrologer —
 * the choice is about what you want to see first, not about what you are.
 */
@AndroidEntryPoint
class ReadingModePickerFragment : Fragment() {

    private var _binding: FragmentReadingModePickerBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var readingModeStore: ReadingModeStore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReadingModePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardPlain.setOnClickListener { choose(ReadingMode.PLAIN) }
        binding.cardAstrologer.setOnClickListener { choose(ReadingMode.ASTROLOGER) }
    }

    private fun choose(mode: ReadingMode) {
        readingModeStore.choose(mode)
        findNavController().navigate(R.id.action_readingModePickerFragment_to_birthDataListFragment)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
