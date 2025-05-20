package com.cho.navi.ui.addpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.cho.navi.databinding.FragmentAddPostBinding

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var isValidPostCategory = false
    private var isValidPostTitle = false
    private var isValidPostDescription = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    private fun setLayout() {
        setDropDownMenu()
        setTextField()
    }

    private fun setDropDownMenu() {
        binding.autoCompleteTvAddPostCategory.doAfterTextChanged {
            val postCategory = it.toString()
            isValidPostCategory = postCategory.isNotBlank()
            updateButtonEnabledState()
        }
    }

    private fun setTextField() {
        with(binding) {
            etPostTitle.doAfterTextChanged {
                val postTitle = it?.toString() ?: ""
                isValidPostTitle = postTitle.isNotBlank()
                updateButtonEnabledState()
            }
            etPostDescription.doAfterTextChanged {
                val postDescription = it?.toString() ?: ""
                isValidPostDescription = postDescription.isNotBlank()
                updateButtonEnabledState()
            }
        }
    }

    private fun updateButtonEnabledState() {
        binding.btnConfirm.isEnabled =
            isValidPostCategory && isValidPostTitle && isValidPostDescription
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}