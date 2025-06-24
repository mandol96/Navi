package com.cho.navi.ui.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cho.navi.data.Post
import com.cho.navi.databinding.FragmentRecommendationBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecommendationFragment : Fragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibRecommendation.setOnClickListener {
            val action = RecommendationFragmentDirections.actionRecommendationToMyPage()
            findNavController().navigate(action)
        }
        setLayout()
    }

    private fun setLayout() {
        val adapter = RecommendationPostAdapter()
        binding.rvRecommendationPost.adapter = adapter
        fetchPosts(adapter)
    }

    private fun fetchPosts(adapter: RecommendationPostAdapter) {
        db.collection("posts")
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.toObjects(Post::class.java)
                adapter.submitList(posts)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "데이터 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}