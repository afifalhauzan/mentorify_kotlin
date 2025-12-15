package com.example.cobasupabase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobasupabase.data.repositories.PlaceRepository
import com.example.cobasupabase.domain.model.Place
import com.example.cobasupabase.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceViewModel(
    private val repo: PlaceRepository = PlaceRepository()
) : ViewModel() {

    private val _state =
        MutableStateFlow<UiResult<List<Place>>>(UiResult.Idle)
    val state = _state.asStateFlow()

    fun loadPlaces() {
        viewModelScope.launch {
            _state.value = UiResult.Loading
            try {
                val data = repo.getPlaces()
                _state.value = UiResult.Success(data)
            } catch (e: Exception) {
                _state.value =
                    UiResult.Error(e.message ?: "Gagal memuat data tempat")
            }
        }
    }

    fun addPlace(
        name: String,
        address: String,
        rating: Double,
        imageBytes: ByteArray?
    ) {
        viewModelScope.launch {
            try {
                repo.createPlace(
                    name = name,
                    address = address,
                    rating = rating,
                    imageBytes = imageBytes
                )
                loadPlaces()
            } catch (e: Exception) {
                _state.value =
                    UiResult.Error(e.message ?: "Gagal menambah tempat")
            }
        }
    }

    fun deletePlace(id: Int) {
        viewModelScope.launch {
            try {
                repo.deletePlace(id)
                loadPlaces()
            } catch (e: Exception) {
                _state.value =
                    UiResult.Error(e.message ?: "Gagal menghapus tempat")
            }
        }
    }

    fun resetState() {
        _state.value = UiResult.Idle
    }
}
