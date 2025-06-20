package com.cho.navi.ui.post

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cho.navi.PostClickListener
import com.cho.navi.data.Category
import com.cho.navi.data.Post
import com.cho.navi.databinding.FragmentPostCategoryBinding
import com.cho.navi.util.Constants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostCategoryFragment : Fragment(), PostClickListener {

    private var _binding: FragmentPostCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var category: Category
    private val adapter = PostCategoryAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCategory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
        loadCategoryPosts()
    }

    private fun setCategory() {
        category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(Constants.KEY_CATEGORY, Category::class.java)
        } else {
            arguments?.getSerializable(Constants.KEY_CATEGORY) as? Category
        } ?: Category.VIEW
    }

    private fun setLayout() {
        binding.rvPost.adapter = adapter
    }

    private fun loadCategoryPosts() {
        Firebase.firestore.collection("posts")
            .whereEqualTo("category", category.displayName)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.documents.mapNotNull { it.toObject(Post::class.java) }
                adapter.submitList(posts)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "불러오기 실패", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(category: Category): PostCategoryFragment {
            return PostCategoryFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(Constants.KEY_CATEGORY, category)
                }
            }
        }
    }

    override fun onPostClick(post: Post) {
        val action = PostFragmentDirections.actionGlobalPostDetail(post)
        findNavController().navigate(action)
    }
}