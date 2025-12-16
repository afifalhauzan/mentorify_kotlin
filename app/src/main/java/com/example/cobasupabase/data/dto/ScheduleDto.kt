package com.example.cobasupabase.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDto(
    val id: Long,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("teacher_id")
    val teacherId: String? = null,
    val day: String,
    @SerialName("time_range")
    val timeRange: String,
    val status: String
)