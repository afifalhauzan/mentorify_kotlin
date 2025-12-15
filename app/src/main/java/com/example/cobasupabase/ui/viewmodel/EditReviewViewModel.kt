package com.example.cobasupabase.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobasupabase.data.repositories.ReviewRepository
import com.example.cobasupabase.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditReviewViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ReviewRepository = ReviewRepository()
) : ViewModel() {

    // Mengambil reviewId dari navigasi argument
    private val reviewId: Long = checkNotNull(savedStateHandle["reviewId"]).toString().toLong()

    // State Input
    var ratingInput = mutableStateOf(0)
    var commentInput = mutableStateOf("")
    var imageUrlInput = mutableStateOf("")

    // Gambar baru (byte array)
    private var newImageBytes: ByteArray? = null

    // UI State
    private val _uiState = MutableStateFlow<UiResult<Boolean>>(UiResult.Idle)
    val uiState = _uiState.asStateFlow()

    private val _loadState = MutableStateFlow<UiResult<Boolean>>(UiResult.Loading)
    val loadState = _loadState.asStateFlow()

    init {
        loadReviewData()
    }

    private fun loadReviewData() {
        viewModelScope.launch {
            _loadState.value = UiResult.Loading
            try {
                val review = repo.getReviewById(reviewId)
                if (review != null) {
                    ratingInput.value = review.rating ?: 0
                    commentInput.value = review.comment ?: ""
                    imageUrlInput.value = review.avatarUrl ?: ""
                    _loadState.value = UiResult.Success(true)
                } else {
                    _loadState.value = UiResult.Error("Review tidak ditemukan")
                }
            } catch (e: Exception) {
                _loadState.value = UiResult.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    fun onNewImageSelected(bytes: ByteArray) {
        newImageBytes = bytes
    }

    fun updateReview() {
        viewModelScope.launch {
            _uiState.value = UiResult.Loading
            try {
                repo.updateReview(
                    reviewId = reviewId,
                    rating = ratingInput.value,
                    comment = commentInput.value,
                    imageBytes = newImageBytes,
                    currentImageUrl = imageUrlInput.value
                )
                _uiState.value = UiResult.Success(true)
            } catch (e: Exception) {
                _uiState.value = UiResult.Error(e.message ?: "Gagal update review")
            }
        }
    }
}