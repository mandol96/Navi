package com.cho.navi.ui.addspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.SpotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectSpotViewModel @Inject constructor(
    private val repository: SpotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SelectSpotUiState>(SelectSpotUiState.Success(null))
    val uiState = _uiState.asStateFlow()

    fun fetchAddress(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.value = SelectSpotUiState.Loading
            runCatching {
                repository.getAddressFromCoordinates(lat, lng)
            }.onSuccess { address ->
                _uiState.value = SelectSpotUiState.Success(address?.addressName)
            }.onFailure { error ->
                _uiState.value = SelectSpotUiState.Error(error)
            }
        }
    }

    companion object {
        fun provideFactory(repository: SpotRepository) = viewModelFactory {
            initializer {
                SelectSpotViewModel(repository)
            }
        }
    }
}

sealed interface SelectSpotUiState {
    data object Loading : SelectSpotUiState
    data class Success(val address: String?) : SelectSpotUiState
    data class Error(val exception: Throwable) : SelectSpotUiState
}