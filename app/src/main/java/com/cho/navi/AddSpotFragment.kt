package com.cho.navi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.cho.navi.databinding.FragmentAddSpotBinding

class AddSpotFragment : Fragment() {

    private var _binding: FragmentAddSpotBinding? = null
    private val binding get() = _binding!!

    private var isValidSpotCategory = false
    private var isValidSpotName = false
    private var isValidSpotDescription = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSpotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDropDownMenu()
        setTextField()
    }

    private fun setDropDownMenu() {
        binding.autoCompleteTvSpotCategory.doAfterTextChanged {
            val spotCategory = it.toString()
            isValidSpotCategory = spotCategory.isNotBlank()
            updateButtonEnabledState()
        }
    }

    private fun setTextField() {
        binding.etSpotName.doAfterTextChanged {
            val spotName = it?.toString() ?: ""
            isValidSpotName = spotName.isNotBlank()
            updateButtonEnabledState()
        }
        binding.etSpotDescription.doAfterTextChanged {
            val spotDescription = it?.toString() ?: ""
            isValidSpotDescription = spotDescription.isNotBlank()
            updateButtonEnabledState()
        }
    }

    private fun updateButtonEnabledState() {
        binding.btnConfirm.isEnabled =
            isValidSpotName && isValidSpotDescription && isValidSpotCategory
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}