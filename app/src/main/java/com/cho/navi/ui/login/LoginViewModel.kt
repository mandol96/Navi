package com.cho.navi.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.auth.AuthRepository
import com.cho.navi.data.auth.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val signInClient: GoogleSignInClient,
) : ViewModel() {

    private val currentUser = repository.getCurrentUser()

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun signInWithGoogle() {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            val googleIdToken = signInClient.getGoogleIdToken()
            googleIdToken
                .onSuccess { token ->
                    repository.signInWithGoogle(token)
                        .onSuccess { user ->
                            _loginState.value = LoginUiState.Success(user)
                        }
                        .onFailure {
                            _loginState.value = LoginUiState.Error("로그인 실패")
                        }
                }
                .onFailure {
                    _loginState.value = LoginUiState.Error("토큰 가져오기 실패")
                }
        }
    }

    companion object {
        fun provideFactory(repository: AuthRepository, signInClient: GoogleSignInClient) =
            viewModelFactory {
                initializer {
                    LoginViewModel(repository, signInClient)
                }
            }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val currentUser: FirebaseUser?) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}