package eu.kastroguru.astrodiary.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentLanguagePickerBinding

class LanguagePickerFragment : Fragment() {

    private var _binding: FragmentLanguagePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguagePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardBg.setOnClickListener { pickLanguage("bg") }
        binding.cardEn.setOnClickListener { pickLanguage("en") }
    }

    private fun pickLanguage(tag: String) {
        requireContext()
            .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("language_picked", true)
            .apply()

        val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (currentTag.startsWith(tag)) {
            // Locale unchanged — no activity recreate will happen, navigate manually.
            findNavController().navigate(R.id.action_languagePickerFragment_to_readingModePickerFragment)
        } else {
            // Locale changed — activity recreates; MainActivity routes to birthDataListFragment.
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
