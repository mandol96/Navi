package com.cho.navi.ui.postdetail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cho.navi.R
import com.cho.navi.data.PostRepository
import com.cho.navi.databinding.FragmentPostDetailBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()
    private lateinit var userId: String

    private val viewModel: PostDetailViewModel by viewModels()

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
            tvPostDetailNickName.text = post.nickName
            tvPostDetailTitle.text = post.title
            tvPostDetailDescription.text = post.description
            rvPostDetailImage.adapter = adapter
            toolbarPostDetail.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        post.imageUrls.let { adapter.addImages(it) }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is PostDetailUiState.Loading -> {
                            binding.progressCircular.isVisible = true
                            binding.ibFavorite.isEnabled = false
                        }

                        is PostDetailUiState.Success -> {
                            binding.progressCircular.isVisible = false
                            binding.ibFavorite.isEnabled = true
                            binding.ibFavorite.isSelected = uiState.isLiked
                            binding.ibFavorite.setOnClickListener {
                                viewModel.toggleLike(args.post.id, userId)
                            }
                        }

                        is PostDetailUiState.Error -> {
                            binding.progressCircular.isVisible = false
                            binding.ibFavorite.isEnabled = false
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_error_load), Toast.LENGTH_SHORT
                            ).show()
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