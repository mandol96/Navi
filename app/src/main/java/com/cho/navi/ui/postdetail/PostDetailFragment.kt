package com.cho.navi.ui.postdetail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cho.navi.databinding.FragmentPostDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import androidx.core.content.edit
import com.cho.navi.data.Post

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()
    private val db = FirebaseFirestore.getInstance()
    private var isLiked = false
    private lateinit var userId: String

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

        binding.ibFavorite.setOnClickListener {
            toggleLike(post)
        }
        loadLikeState(post)
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

    private fun toggleLike(post: Post) {
        val likeRef = db.collection("posts")
            .document(post.id)
            .collection("likes")
            .document(userId)

        if (isLiked) {
            likeRef.delete().addOnSuccessListener {
                isLiked = false
                updateLikeButtonUI(isLiked)
            }
        } else {
            likeRef.set(mapOf("liked" to true)).addOnSuccessListener {
                isLiked = true
                updateLikeButtonUI(isLiked)
            }
        }
    }

    private fun updateLikeButtonUI(isLiked: Boolean) {
        binding.ibFavorite.isSelected = isLiked
    }

    private fun loadLikeState(post: Post) {
        val postId = post.id
        val likeRef = db.collection("posts")
            .document(postId)
            .collection("likes")
            .document(userId)
        likeRef.get().addOnSuccessListener { document ->
            isLiked = document.exists()
            updateLikeButtonUI(isLiked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}