package com.cho.navi.ui.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cho.navi.data.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadLikeState(postId: String, userId: String) {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading
            repository.getLikeState(postId, userId)
                .onSuccess { liked ->
                    _uiState.value = PostDetailUiState.Success(liked)
                }
                .onFailure {
                    _uiState.value = PostDetailUiState.Error("불러오기 실패")
                }
        }
    }

    fun toggleLike(postId: String, userId: String) {
        val current = _uiState.value
        if (current !is PostDetailUiState.Success) return

        val currentLike = current.isLiked
        val newLike = !currentLike

        _uiState.value = PostDetailUiState.Success(isLiked = newLike)

        viewModelScope.launch {
            val result = if (newLike) {
                repository.likePost(postId, userId)
            } else {
                repository.unlikePost(postId, userId)
            }
            result.onSuccess {
                _uiState.value = PostDetailUiState.Success(isLiked = newLike)
            }.onFailure {
                _uiState.value = PostDetailUiState.Error("좋아요 실패")
            }
        }
    }
}

sealed class PostDetailUiState {
    data object Loading : PostDetailUiState()
    data class Success(val isLiked: Boolean) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}