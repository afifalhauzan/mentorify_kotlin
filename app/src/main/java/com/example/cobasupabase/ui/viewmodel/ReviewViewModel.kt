package com.example.cobasupabase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobasupabase.data.dto.ReviewDto
import com.example.cobasupabase.data.repositories.ReviewRepository
import com.example.cobasupabase.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val repo: ReviewRepository = ReviewRepository()
) : ViewModel() {

    // State untuk menampung List Review (Data dari server)
    private val _state = MutableStateFlow<UiResult<List<ReviewDto>>>(UiResult.Idle)
    val state = _state.asStateFlow()

    // State untuk status Upload (Loading, Success, Error)
    // Pastikan namanya 'uploadState' agar cocok dengan AddReviewScreen
    private val _uploadState = MutableStateFlow<UiResult<Boolean>>(UiResult.Idle)
    val uploadState = _uploadState.asStateFlow()

    // --- FORM INPUTS (Ini yang menyebabkan error "Unresolved reference") ---
    val userNameInput = MutableStateFlow("")
    val ratingInput = MutableStateFlow(5)
    val commentInput = MutableStateFlow("")
    val imageBytesInput = MutableStateFlow<ByteArray?>(null)

    // Fungsi mengambil data review
    fun loadReviews(teacherId: String) {
        viewModelScope.launch {
            _state.value = UiResult.Loading
            try {
                val data = repo.getReviews(teacherId)
                _state.value = UiResult.Success(data)
            } catch (e: Exception) {
                _state.value = UiResult.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    // Fungsi mengirim review baru
    fun addReview(teacherId: String, name: String, rating: Int, comment: String, image: ByteArray?) {
        viewModelScope.launch {
            _uploadState.value = UiResult.Loading
            try {
                repo.createReview(teacherId, name, rating, comment, image)
                _uploadState.value = UiResult.Success(true)

                // Refresh data list setelah berhasil upload
                loadReviews(teacherId)

                // Reset form setelah sukses
                resetForm()
            } catch (e: Exception) {
                _uploadState.value = UiResult.Error(e.message ?: "Gagal mengirim review")
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = UiResult.Idle
    }

    private fun resetForm() {
        userNameInput.value = ""
        ratingInput.value = 5
        commentInput.value = ""
        imageBytesInput.value = null
    }
}