package com.cho.navi.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cho.navi.data.AuthRepository
import com.cho.navi.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.provideFactory(
            AuthRepository(
                CredentialManager.create(requireContext()),
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginToRegister()
            findNavController().navigate(action)
        }

        binding.ibGoogleLogin.setOnClickListener {
            viewModel.signInWithGoogle(requireActivity())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        LoginUiState.Idle -> {
                            binding.progressCircular.visibility = View.GONE
                            binding.ibGoogleLogin.isEnabled = true
                        }

                        LoginUiState.Loading -> {
                            binding.progressCircular.visibility = View.VISIBLE
                            binding.ibGoogleLogin.isEnabled = false
                        }

                        is LoginUiState.Success -> {
                            binding.progressCircular.visibility = View.GONE
                            binding.ibGoogleLogin.isEnabled = true
                        }

                        is LoginUiState.Error -> {
                            binding.progressCircular.visibility = View.GONE
                            binding.ibGoogleLogin.isEnabled = true
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}