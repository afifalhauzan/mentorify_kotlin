package com.example.cobasupabase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobasupabase.data.dto.ReviewDto
import com.example.cobasupabase.data.repositories.ReviewRepository
import com.example.cobasupabase.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.cobasupabase.data.remote.SupabaseHolder
import io.github.jan.supabase.gotrue.auth

class ReviewViewModel(
    private val repo: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<UiResult<List<ReviewDto>>>(UiResult.Idle)
    val state = _state.asStateFlow()

    private val _uploadState = MutableStateFlow<UiResult<Boolean>>(UiResult.Idle)
    val uploadState = _uploadState.asStateFlow()

    val userNameInput = MutableStateFlow("")
    val ratingInput = MutableStateFlow(5)
    val commentInput = MutableStateFlow("")
    val imageBytesInput = MutableStateFlow<ByteArray?>(null)

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    private val _deleteState = MutableStateFlow<UiResult<Boolean>>(UiResult.Idle)
    val deleteState = _deleteState.asStateFlow()

    init {
        // ... loadAllReviews() jika ada
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        _currentUserId.value = SupabaseHolder.client.auth.currentUserOrNull()?.id
    }

    fun loadReviews(teacherId: Int) {
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

    fun loadAllReviews() {
        viewModelScope.launch {
            _state.value = UiResult.Loading
            try {
                val data = repo.getAllReviews()
                _state.value = UiResult.Success(data)
            } catch (e: Exception) {
                _state.value = UiResult.Error(e.message ?: "Gagal memuat semua data review")
            }
        }
    }

    fun addReview(teacherId: Int, name: String, rating: Int, comment: String, image: ByteArray?) {
        viewModelScope.launch {
            _uploadState.value = UiResult.Loading
            try {
                repo.createReview(teacherId, name, rating, comment, image)
                _uploadState.value = UiResult.Success(true)
                loadReviews(teacherId)
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

    fun deleteReview(reviewId: Long, teacherId: Int?) {
        viewModelScope.launch {
            _deleteState.value = UiResult.Loading
            try {
                repo.deleteReview(reviewId)
                _deleteState.value = UiResult.Success(true)

                // Refresh list setelah hapus
                if (teacherId != null && teacherId != 0) {
                    loadReviews(teacherId)
                } else {
                    loadAllReviews()
                }
            } catch (e: Exception) {
                _deleteState.value = UiResult.Error(e.message ?: "Gagal menghapus review")
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = UiResult.Idle
    }
}