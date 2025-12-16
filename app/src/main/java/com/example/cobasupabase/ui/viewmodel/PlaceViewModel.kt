package com.example.cobasupabase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobasupabase.data.repositories.PlaceRepository
import com.example.cobasupabase.domain.model.Place
import com.example.cobasupabase.data.remote.SupabaseHolder
import com.example.cobasupabase.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import io.github.jan.supabase.gotrue.auth

class PlaceViewModel(
    private val repo: PlaceRepository = PlaceRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<UiResult<List<Place>>>(UiResult.Idle)
    val state: StateFlow<UiResult<List<Place>>> = _state.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    init {
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        _currentUserId.value = SupabaseHolder.client.auth.currentUserOrNull()?.id
    }

    fun loadPlaces() {
        viewModelScope.launch {
            _state.value = UiResult.Loading
            val userId = currentUserId.value
            if (userId == null) {
                _state.value = UiResult.Error("User not logged in.")
                return@launch
            }
            try {
                val data = repo.getPlaces()
                _state.value = UiResult.Success(data)
            } catch (e: Exception) {
                _state.value = UiResult.Error(e.message ?: "Gagal memuat data tempat")
            }
        }
    }

    fun addPlace(
        name: String,
        address: String,
        rating: Double?,
        userId: String,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            // The userId is now passed as a parameter, so we don't need to retrieve it again
            // and check for null here, as CreatePlaceScreen already handles passing a non-null string.
            try {
                repo.createPlace(
                    name = name,
                    address = address,
                    rating = rating ?: 0.0,
                    userId = userId,
                    imageUrl = imageUrl
                )
                loadPlaces()
            } catch (e: Exception) {
                _state.value = UiResult.Error(e.message ?: "Gagal menambah tempat")
            }
        }
    }

    fun deletePlace(id: Int) {
        viewModelScope.launch {
            val userId = currentUserId.value
            if (userId == null) {
                _state.value = UiResult.Error("User not logged in.")
                return@launch
            }
            try {
                repo.deletePlace(id, userId)
                loadPlaces()
            } catch (e: Exception) {
                _state.value = UiResult.Error(e.message ?: "Gagal menghapus tempat")
            }
        }
    }

    fun resetState() {
        _state.value = UiResult.Idle
    }

    private val _placeDetailState = MutableStateFlow<UiResult<Place>>(UiResult.Idle)
    val placeDetailState: StateFlow<UiResult<Place>> = _placeDetailState.asStateFlow()

    fun loadPlaceById(placeId: Int) {
        viewModelScope.launch {
            _placeDetailState.value = UiResult.Loading
            val userId = currentUserId.value
            if (userId == null) {
                _placeDetailState.value = UiResult.Error("User not logged in.")
                return@launch
            }
            try {
                val place = repo.getPlaceById(placeId, userId)
                if (place != null) {
                    _placeDetailState.value = UiResult.Success(place)
                } else {
                    _placeDetailState.value = UiResult.Error("Tempat tidak ditemukan")
                }
            } catch (e: Exception) {
                _placeDetailState.value =
                    UiResult.Error(e.message ?: "Gagal memuat detail tempat")
            }
        }
    }

    fun resetPlaceDetailState() {
        _placeDetailState.value = UiResult.Idle
    }

    fun getPlaceById(placeId: Int): Flow<Place?> {
        // This function is still getting from the _state list, which is loaded by loadPlaces
        // loadPlaces now uses the userId, so this should implicitly respect RLS.
        return state.map { result ->
            when (result) {
                is UiResult.Success -> result.data.find { it.id == placeId }
                else -> null
            }
        }
    }
}