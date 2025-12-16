package com.example.cobasupabase.data.repositories

import com.example.cobasupabase.data.dto.ScheduleCreateDto
import com.example.cobasupabase.data.dto.ScheduleDto
import com.example.cobasupabase.data.remote.SupabaseHolder
import com.example.cobasupabase.domain.model.Schedule
import io.github.jan.supabase.postgrest.from

class ScheduleRepository {

    suspend fun getSchedules(): List<Schedule> {
        return SupabaseHolder.client
            .from("schedules")
            .select()
            .decodeList<ScheduleDto>()
            .map { dto ->
                Schedule(
                    id = dto.id,
                    userId = dto.userId,
                    teacherId = dto.teacherId,
                    day = dto.day,
                    timeRange = dto.timeRange,
                    status = dto.status
                )
            }
    }

    suspend fun addSchedule(dto: ScheduleCreateDto) {
        SupabaseHolder.client
            .from("schedules")
            .insert(dto)
    }

    suspend fun updateSchedule(id: Long, dto: ScheduleCreateDto) {
        SupabaseHolder.client
            .from("schedules")
            .update(dto) {
                filter { eq("id", id) }
            }
    }

    suspend fun deleteSchedule(id: Long) {
        SupabaseHolder.client
            .from("schedules")
            .delete {
                filter { eq("id", id) }
            }
    }
}