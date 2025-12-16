package com.example.cobasupabase.domain.model

data class Schedule(
    val id: Long,
    val userId: String?,
    val teacherId: String?,
    val day: String,
    val timeRange: String,
    val status: String
) {
    // Parse nama guru dari status (sebelum |)
    val teacherName: String
        get() {
            val parts = status.split("|")
            return parts.getOrNull(0)?.trim() ?: status
        }

    // Parse mata pelajaran dari status (setelah |)
    val subject: String
        get() {
            val parts = status.split("|")
            return parts.getOrNull(1)?.trim() ?: ""
        }

    // Hitung durasi otomatis
    val duration: String
        get() {
            return if (timeRange.isNotEmpty() && timeRange.contains("-")) {
                try {
                    val times = timeRange.split("-").map { it.trim() }
                    val start = times[0].split(":")[0].toInt()
                    val end = times[1].split(":")[0].toInt()
                    val diff = end - start
                    "$diff jam"
                } catch (e: Exception) {
                    "1 jam"
                }
            } else {
                "1 jam"
            }
        }
}