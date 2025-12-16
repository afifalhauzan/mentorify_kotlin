package com.example.cobasupabase.data.mapper

import com.example.cobasupabase.data.dto.ScheduleDto
import com.example.cobasupabase.domain.model.Schedule

object ScheduleMapper {
fun ScheduleDto.toDomain(): Schedule {
    return Schedule(
        id = id,
        userId = userId,
        teacherId = teacherId,
        day = day,
        timeRange = timeRange,
        status = status
    )
}
}