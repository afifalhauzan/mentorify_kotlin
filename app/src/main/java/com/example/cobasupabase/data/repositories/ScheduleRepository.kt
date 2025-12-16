package com.example.cobasupabase.data.repositories

import com.example.cobasupabase.data.dto.ScheduleCreateDto
import com.example.cobasupabase.data.dto.ScheduleDto
import com.example.cobasupabase.data.remote.SupabaseHolder
import com.example.cobasupabase.domain.model.Schedule
import io.github.jan.supabase.postgrest.from

class ScheduleRepository {

    suspend fun getSchedules(): List<Schedule> {
        return try {
            println("🔵 [Repository] Fetching schedules from Supabase...")

            val schedules = SupabaseHolder.client
                .from("schedules")
                .select()
                .decodeList<ScheduleDto>()

            println("🟢 [Repository] Successfully fetched ${schedules.size} schedules")

            schedules.map { dto ->
                Schedule(
                    id = dto.id,
                    userId = dto.userId,
                    teacherId = dto.teacherId,
                    day = dto.day,
                    timeRange = dto.timeRange,
                    status = dto.status,
                    teacherName = dto.teacherName ?: "",
                    teacherSubject = dto.teacherSubject ?: ""
                )
            }
        } catch (e: Exception) {
            println("🔴 [Repository] Error fetching schedules: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addSchedule(dto: ScheduleCreateDto) {
        try {
            println("🔵 [Repository] Adding schedule: $dto")

            SupabaseHolder.client
                .from("schedules")
                .insert(dto)

            println("🟢 [Repository] Schedule added successfully")
        } catch (e: Exception) {
            println("🔴 [Repository] Error adding schedule: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun updateSchedule(id: Long, dto: ScheduleCreateDto) {
        try {
            println("🔵 [Repository] Updating schedule ID: $id with data: $dto")

            SupabaseHolder.client
                .from("schedules")
                .update(dto) {
                    filter { eq("id", id) }
                }

            println("🟢 [Repository] Schedule updated successfully")
        } catch (e: Exception) {
            println("🔴 [Repository] Error updating schedule: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun deleteSchedule(id: Long) {
        try {
            println("🔵 [Repository] Deleting schedule ID: $id")

            SupabaseHolder.client
                .from("schedules")
                .delete {
                    filter { eq("id", id) }
                }

            println("🟢 [Repository] Schedule deleted successfully")
        } catch (e: Exception) {
            println("🔴 [Repository] Error deleting schedule: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}