package com.example.cobasupabase.domain.model

data class Schedule(
    val id: Long,
    val userId: String?,
    val teacherId: Long?,
    val day: String,
    val timeRange: String,
    val status: String,

    // Data tambahan dari JOIN dengan tabel teachers (untuk UI)
    val teacherName: String = "",
    val teacherSubject: String = ""
) {
    // Hitung durasi otomatis
    val duration: String
        get() {
            return if (timeRange.isNotEmpty() && timeRange.contains("-")) {
                try {
                    val times = timeRange.replace(" ", "").split("-")
                    val start = times[0].split(".")[0].toInt()
                    val end = times[1].split(".")[0].toInt()
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