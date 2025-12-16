package com.example.cobasupabase.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleCreateDto(
    @SerialName("teacher_id")
    val teacherId: Long? = null,
    val day: String,
    @SerialName("time_range")
    val timeRange: String,
    val status: String = "Available"
)