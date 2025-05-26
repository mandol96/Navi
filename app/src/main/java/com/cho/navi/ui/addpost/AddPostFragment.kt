package com.cho.navi.ui.addpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.data.Post
import com.cho.navi.data.PostRepository
import com.cho.navi.databinding.FragmentAddPostBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var isValidPostCategory = false
    private var isValidPostTitle = false
    private var isValidPostDescription = false

    private val viewModel: AddPostViewModel by viewModels {
        AddPostViewModel.provideFactory(PostRepository(Firebase.firestore))
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

        val post = Post(
            id = null,
            imageUrls = null,
            category = binding.autoCompleteTvAddPostCategory.text.toString(),
            title = binding.etPostTitle.text.toString(),
            description = binding.etPostDescription.text.toString(),
            location = null,
        )

        viewModel.addPost(post)

        binding.toolbarAddPost.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnConfirm.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.uiState
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            AddPostUiState.Loading -> showProgress()
                            is AddPostUiState.Success -> addPost(uiState.post)
                            is AddPostUiState.Error -> shoError()
                        }
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

    }

    private fun addPost(post: Post) {
        viewModel.addPost(post)
        Toast.makeText(requireContext(), "게시글이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun shoError() {
        Toast.makeText(requireContext(), "저장 실패", Toast.LENGTH_SHORT)
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