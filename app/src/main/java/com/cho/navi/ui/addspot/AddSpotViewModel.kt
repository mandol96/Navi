package com.cho.navi.ui.addspot

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.SpotRepository
import com.cho.navi.data.model.Position
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSpotViewModel @Inject constructor(
    private val repository: SpotRepository
) : ViewModel() {

    private val _coordinates = MutableStateFlow<List<Position>>(emptyList())
    val coordinate = _coordinates.asStateFlow()

    private val _uiState = MutableStateFlow<AddSpotUiState>(AddSpotUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadSpots() {
        viewModelScope.launch {
            val items = mutableListOf<Position>()
            repository.fetchSpots { lat, lng ->
                items.add(Position(lat, lng))
                _coordinates.value = items.toList()
            }
        }
    }

    fun addSpot(
        address: String,
        name: String,
        description: String,
        selectedImageUris: List<Uri>
    ) {
        viewModelScope.launch {
            _uiState.value = AddSpotUiState.Loading
            repository.addSpot(address, name, description, selectedImageUris)
                .onSuccess {
                    _uiState.value = AddSpotUiState.Success
                }.onFailure {
                    _uiState.value = AddSpotUiState.Error(it)
                }
        }
    }

    companion object {
        fun provideFactory(repository: SpotRepository) = viewModelFactory {
            initializer {
                AddSpotViewModel(repository)
            }
        }
    }
}

sealed interface AddSpotUiState {
    data object Idle : AddSpotUiState
    data object Loading : AddSpotUiState
    data object Success : AddSpotUiState
    data class Error(val exception: Throwable) : AddSpotUiState
}