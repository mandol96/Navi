package com.cho.navi.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val currentUser = repository.getCurrentUser()

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            repository.signInWithGoogle(context)
                .onSuccess { user ->
                    _loginState.value = LoginUiState.Success(user)
                }
                .onFailure {
                    _loginState.value = LoginUiState.Error("로그인 실패")
                }
        }
    }

    companion object {
        fun provideFactory(repository: AuthRepository) = viewModelFactory {
            initializer {
                LoginViewModel(repository)
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