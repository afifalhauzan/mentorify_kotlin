package com.example.cobasupabase.domain.model

import java.time.LocalDate

data class News(
    val id: Int,
    val title: String,
    val content: String,
    val author: String,
    val datePublished: LocalDate,
    val imageUrl: String
)