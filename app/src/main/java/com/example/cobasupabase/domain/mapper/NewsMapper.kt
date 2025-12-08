package com.example.cobasupabase.domain.mapper

import com.example.cobasupabase.data.dto.NewsDto
import com.example.cobasupabase.domain.model.News
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NewsMapper {
    private val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    fun map(dto: NewsDto): News {
        // Tanggal publikasi
        var publishedDate: Date = Date()
        dto.datePublished?.let { dateString ->
            try {
                publishedDate = isoDateTimeFormat.parse(dateString) ?: Date()
            } catch (e: Exception) {
                e.printStackTrace()
                publishedDate = Date()
            }
        }

        // Tanggal dibuat (created_at)
        var createdAtDate: Date = Date()
        dto.createdAt?.let { dateString ->
            try {
                createdAtDate = isoDateTimeFormat.parse(dateString) ?: Date()
            } catch (e: Exception) {
                e.printStackTrace()
                createdAtDate = Date()
            }
        }

        val newsId = dto.id ?: throw IllegalStateException("News ID cannot be null")
        val newsUserId = dto.userId ?: throw IllegalStateException("User ID cannot be null")

        return News(
            id = newsId,
            userId = newsUserId,
            title = dto.title,
            content = dto.content,
            author = dto.author,
            datePublished = publishedDate,
            imageUrl = dto.imageUrl ?: "https://via.placeholder.com/150",
            createdAt = createdAtDate // Ditambahkan
        )
    }
}