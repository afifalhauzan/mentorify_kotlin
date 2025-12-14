package com.example.cobasupabase.data.repositories

import com.example.cobasupabase.data.dto.ReviewDto
import com.example.cobasupabase.data.dto.ReviewInsertDto
import com.example.cobasupabase.data.remote.SupabaseHolder
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.gotrue.auth

class ReviewRepository {
    // [PERBAIKAN] Gunakan SupabaseHolder.client
    private val client = SupabaseHolder.client

    suspend fun getReviews(teacherId: String): List<ReviewDto> {
        // Mengambil data dari tabel 'reviews' berdasarkan teacher_id
        return client.postgrest["reviews"].select {
            filter { eq("teacher_id", teacherId) }
            order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
        }.decodeList<ReviewDto>()
    }

    suspend fun createReview(teacherId: String, name: String, rating: Int, comment: String, imageBytes: ByteArray?) {
        val user = client.auth.currentUserOrNull() ?: throw Exception("Wajib Login")

        var publicUrl: String? = null
        if (imageBytes != null) {
            val fileName = "${System.currentTimeMillis()}.jpg"
            // Pastikan bucket 'review-images' sudah dibuat di Supabase Storage & diset Public
            val bucket = client.storage["review-images"]
            bucket.upload(fileName, imageBytes)
            publicUrl = bucket.publicUrl(fileName)
        }

        val newReview = ReviewInsertDto(
            userId = user.id,
            teacherId = teacherId,
            reviewerName = name,
            rating = rating,
            comment = comment,
            avatarUrl = publicUrl
        )
        client.postgrest["reviews"].insert(newReview)
    }
}