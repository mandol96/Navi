package com.cho.navi.ui.addspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectSpotViewModel(
    private val repository: SpotRepository
) : ViewModel() {

    private val _address = MutableStateFlow<String?>("")
    val address = _address.asStateFlow()

    fun fetchAddress(lat: Double, lng: Double) {
        viewModelScope.launch {
            runCatching {
                repository.getAddressFromCoordinates(lat, lng)
            }.onSuccess { address ->
                _address.value = address?.addressName
            }.onFailure {
                _address.value = null
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