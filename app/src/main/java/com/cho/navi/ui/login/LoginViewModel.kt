package com.cho.navi.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cho.navi.R
import com.cho.navi.data.auth.AuthRepository
import com.cho.navi.data.auth.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val signInClient: GoogleSignInClient,
) : ViewModel() {

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
                            _loginState.value = LoginUiState.Error(R.string.error_login_failed)
                        }
                }
                .onFailure {
                    _loginState.value = LoginUiState.Error(R.string.error_get_token_failed)
                }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val currentUser: FirebaseUser?) : LoginUiState()
    data class Error(@StringRes val message: Int) : LoginUiState()
}