package com.example.cobasupabase.data.repositories

import android.util.Log
import com.example.cobasupabase.data.dto.ReviewDto
import com.example.cobasupabase.data.dto.ReviewInsertDto
import com.example.cobasupabase.data.remote.SupabaseHolder
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.gotrue.auth

class ReviewRepository {
    private val client = SupabaseHolder.client

    suspend fun getReviews(teacherId: Int): List<ReviewDto> {
        Log.d("ReviewRepository", "Fetching all reviews")
        Log.d("ReviewRepository", "User ID: ${client.auth.currentUserOrNull()?.id}")
        Log.d("ReviewRepository", "User Email: ${client.auth.currentUserOrNull()?.email}")
        Log.d("ReviewRepository", "review data: ${client.postgrest["reviews"].select()}")

        return client.postgrest["reviews"].select {
            filter { eq("teacher_id", teacherId) }
            order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
        }.decodeList<ReviewDto>()
    }

    suspend fun getAllReviews(): List<ReviewDto> {
        Log.d("ReviewRepository", "Fetching all reviews")
        Log.d("ReviewRepository", "User ID: ${client.auth.currentUserOrNull()?.id}")
        Log.d("ReviewRepository", "User Email: ${client.auth.currentUserOrNull()?.email}")
        Log.d("ReviewRepository", "review data: ${client.postgrest["reviews"].select()}")

        return client.postgrest["reviews"].select {
            order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
        }.decodeList<ReviewDto>()
    }

    suspend fun createReview(teacherId: Int, name: String, rating: Int, comment: String, imageBytes: ByteArray?) {
        val user = client.auth.currentUserOrNull() ?: throw Exception("Wajib Login")

        var publicUrl: String? = null
        if (imageBytes != null) {
            val fileName = "${System.currentTimeMillis()}.jpg"
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