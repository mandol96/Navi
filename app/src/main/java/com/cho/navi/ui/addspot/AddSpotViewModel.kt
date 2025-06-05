package com.cho.navi.ui.addspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cho.navi.data.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddSpotViewModel(
    private val repository: SpotRepository
) : ViewModel() {

    private val _coordinates = MutableStateFlow<List<Pair<Double, Double>>>(emptyList())
    val coordinate = _coordinates.asStateFlow()

    fun loadSpots() {
        viewModelScope.launch {
            val items = mutableListOf<Pair<Double, Double>>()
            repository.fetchSpots { lat, lng ->
                items.add(lat to lng)
                _coordinates.value = items.toList()
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