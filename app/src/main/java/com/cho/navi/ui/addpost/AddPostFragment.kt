package com.cho.navi.ui.addpost

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.R
import com.cho.navi.data.Post
import com.cho.navi.data.PostRepository
import com.cho.navi.databinding.FragmentAddPostBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var isValidPostCategory = false
    private var isValidPostTitle = false
    private var isValidPostDescription = false

    private val viewModel: AddPostViewModel by viewModels()

    private val selectedImageUris = mutableListOf<Uri>()

    private val pickMultiplePhotos =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                selectedImageUris.addAll(uris)
                binding.ibUploadImage.setImageURI(uris.first())
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_unselected_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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

        binding.toolbarAddPost.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.ibUploadImage.setOnClickListener {
            pickMultiplePhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnConfirm.setOnClickListener {
            val post = Post(
                category = binding.autoCompleteTvAddPostCategory.text.toString(),
                title = binding.etPostTitle.text.toString(),
                description = binding.etPostDescription.text.toString(),
                likeCount = 0
            )
            viewModel.addPost(post, selectedImageUris)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        AddPostUiState.Idle -> Unit
                        AddPostUiState.Loading -> showProgress()
                        is AddPostUiState.Success -> completeTask()
                        is AddPostUiState.Error -> showError()
                    }
                }
        }
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

    private fun showProgress() {
        binding.groupAddPost.visibility = View.GONE
        binding.progressCircular.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.groupAddPost.visibility = View.VISIBLE
        binding.progressCircular.visibility = View.GONE
    }

    private fun completeTask() {
        hideProgress()
        Toast.makeText(
            requireContext(),
            getString(R.string.toast_save_post_message), Toast.LENGTH_SHORT
        ).show()
        findNavController().navigateUp()
    }

    private fun showError() {
        hideProgress()
        Toast.makeText(
            requireContext(),
            getString(R.string.toast_error_post_message), Toast.LENGTH_SHORT
        )
            .show()
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