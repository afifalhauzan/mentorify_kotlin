package com.example.cobasupabase.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsDto(
    val id: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("created_at") val createdAt: String,
    val title: String,
    val content: String,
    val author: String,
    @SerialName("date_published") val datePublished: String?, // Tetap String dari DB
    @SerialName("image_url") val imageUrl: String?
)