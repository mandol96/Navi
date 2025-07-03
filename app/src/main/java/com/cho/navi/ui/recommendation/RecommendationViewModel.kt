package com.cho.navi.ui.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.Post
import com.cho.navi.data.RecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecommendationViewModel(
    private val repository: RecommendationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecommendationUiState>(RecommendationUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = RecommendationUiState.Loading
            repository.fetchPosts()
                .onSuccess { posts ->
                    _uiState.value = RecommendationUiState.Success(posts)
                }.onFailure {
                    _uiState.value = RecommendationUiState.Error("불러오기 실패")
                }
        }
    }

    companion object {
        fun provideFactory(repository: RecommendationRepository) = viewModelFactory {
            initializer {
                RecommendationViewModel(repository)
            }
        }
    }
}

sealed class RecommendationUiState {
    data object Loading : RecommendationUiState()
    data class Success(val posts: List<Post>) : RecommendationUiState()
    data class Error(val message: String) : RecommendationUiState()
}