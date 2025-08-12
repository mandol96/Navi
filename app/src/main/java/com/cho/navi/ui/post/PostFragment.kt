package com.cho.navi.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cho.navi.data.Category
import com.cho.navi.databinding.FragmentPostBinding
import com.cho.navi.util.AuthManager
import com.cho.navi.util.DialogUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val postCategories = Category.entries


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    private fun setLayout() {
        binding.viewpagerPost.adapter = TabAdapter(this, postCategories)
        TabLayoutMediator(binding.tabPost, binding.viewpagerPost) { tab, position ->
            tab.text = postCategories[position].displayName
        }.attach()

        binding.fabAddPost.setOnClickListener {
            if (AuthManager.isLoggedIn()) {
                val action = PostFragmentDirections.actionPostToAddPost()
                findNavController().navigate(action)
            } else {
                DialogUtil.showLoginRequiredDialog(requireContext()) {
                    val action = PostFragmentDirections.actionGlobalLogin()
                    findNavController().navigate(action)
                }
            }
        }
        binding.ibMyPage.setOnClickListener {
            if (AuthManager.isLoggedIn()) {
                val action = PostFragmentDirections.actionPostToMyPage()
                findNavController().navigate(action)
            } else {
                DialogUtil.showLoginRequiredDialog(requireContext()) {
                    val action = PostFragmentDirections.actionGlobalLogin()
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}