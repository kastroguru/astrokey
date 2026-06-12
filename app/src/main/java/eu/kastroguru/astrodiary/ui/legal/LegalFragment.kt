package eu.kastroguru.astrodiary.ui.legal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.FragmentLegalBinding

class LegalFragment : Fragment() {

    private var _binding: FragmentLegalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLegalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getString(ARG_TYPE) ?: TYPE_TERMS
        val textRes = when (type) {
            TYPE_PRIVACY -> R.string.legal_privacy_text
            TYPE_GDPR    -> R.string.legal_gdpr_text
            else         -> R.string.legal_terms_text
        }
        binding.tvLegalContent.text = getString(textRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TYPE    = "legalType"
        const val TYPE_TERMS  = "terms"
        const val TYPE_PRIVACY = "privacy"
        const val TYPE_GDPR   = "gdpr"
    }
}
