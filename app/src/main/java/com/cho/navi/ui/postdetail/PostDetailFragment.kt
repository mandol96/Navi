package com.cho.navi.ui.postdetail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cho.navi.data.PostRepository
import com.cho.navi.databinding.FragmentPostDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.UUID

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()
    private lateinit var userId: String

    private val viewModel: PostDetailViewModel by viewModels {
        PostDetailViewModel.provideFactory(PostRepository(Firebase.firestore))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = getDeviceUserId(requireContext())
        setLayout()
        collectUiState()
        viewModel.loadLikeState(args.post.id, userId)
    }

    private fun setLayout() {
        val adapter = PostDetailAdapter()
        val post = args.post

        with(binding) {
            tvPostDetailLocation.text = post.location
            tvPostDetailTitle.text = post.title
            tvPostDetailDescription.text = post.description
            rvPostDetailImage.adapter = adapter
            toolbarPostDetail.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        post.imageUrls?.let { adapter.addImages(it) }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is PostDetailUiState.Loading -> {

                        }

                        is PostDetailUiState.Success -> {
                            binding.ibFavorite.isSelected = uiState.isLiked
                            binding.ibFavorite.setOnClickListener {
                                viewModel.toggleLike(args.post.id, userId)
                            }
                        }

                        is PostDetailUiState.Error -> {

                        }
                    }
                }
            }
        }
    }

    private fun getDeviceUserId(context: Context): String {
        val preferences = context.getSharedPreferences("like_prefs", Context.MODE_PRIVATE)
        var userId = preferences.getString("user_id", null)

        if (userId == null) {
            userId = UUID.randomUUID().toString()
            preferences.edit() { putString("user_id", userId) }
        }

        return userId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}