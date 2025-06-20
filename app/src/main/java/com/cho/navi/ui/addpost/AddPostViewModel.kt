package com.cho.navi.ui.addpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.Post
import com.cho.navi.data.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AddPostViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddPostUiState>(AddPostUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun addPost(post:Post) {
        viewModelScope.launch {
            _uiState.value = AddPostUiState.Loading
            repository.addPost(post)
                .onSuccess {
                    _uiState.value = AddPostUiState.Success
                }.onFailure {
                    _uiState.value = AddPostUiState.Error(it)
                }
        }
    }

    companion object {
        fun provideFactory(repository: PostRepository) = viewModelFactory {
            initializer {
                AddPostViewModel(repository)
            }
        }
    }
}

sealed interface AddPostUiState {
    data object Idle : AddPostUiState
    data object Loading : AddPostUiState
    data object Success : AddPostUiState
    data class Error(val exception: Throwable) : AddPostUiState
}