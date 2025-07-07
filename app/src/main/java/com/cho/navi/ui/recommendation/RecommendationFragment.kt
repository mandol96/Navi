package com.cho.navi.ui.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.R
import com.cho.navi.data.RecommendationRepository
import com.cho.navi.databinding.FragmentRecommendationBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RecommendationFragment : Fragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecommendationViewModel by viewModels {
        RecommendationViewModel.provideFactory(RecommendationRepository(Firebase.firestore))
    }

    private val adapter = RecommendationPostAdapter()

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
        collectUiState()
        viewModel.loadPosts()
    }

    private fun setLayout() {
        binding.rvRecommendationPost.adapter = adapter
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        RecommendationUiState.Loading ->
                            binding.progressCircular.isVisible = true

                        is RecommendationUiState.Success -> {
                            binding.progressCircular.isVisible = false
                            adapter.submitList(uiState.posts)
                        }

                        is RecommendationUiState.Error ->
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_error_load),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}