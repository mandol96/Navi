package com.cho.navi.ui.postdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cho.navi.databinding.FragmentPostDetailBinding

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}