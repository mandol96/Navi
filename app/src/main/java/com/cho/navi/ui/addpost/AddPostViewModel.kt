package com.cho.navi.ui.addpost

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cho.navi.data.Post
import com.cho.navi.data.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddPostUiState>(AddPostUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun addPost(post: Post, imageUris: List<Uri>) {
        viewModelScope.launch {
            _uiState.value = AddPostUiState.Loading
            repository.addPost(post, imageUris)
                .onSuccess {
                    _uiState.value = AddPostUiState.Success
                }.onFailure {
                    _uiState.value = AddPostUiState.Error(it)
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