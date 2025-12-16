package com.example.cobasupabase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobasupabase.data.dto.ScheduleCreateDto
import com.example.cobasupabase.data.repositories.ScheduleRepository
import com.example.cobasupabase.domain.model.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {

    private val repository = ScheduleRepository()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchSchedules()
    }

    fun fetchSchedules() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("🔵 [ViewModel] Fetching schedules...")

                val rawSchedules = repository.getSchedules()
                println("🟢 [ViewModel] Fetched ${rawSchedules.size} schedules")

                // ✅ PERBAIKAN: Implementasi parsing untuk mengisi nama/mapel dan status
                _schedules.value = rawSchedules.map { schedule ->
                    // Memisahkan string status menjadi [Nama Guru, Mata Pelajaran, Status Aktual]
                    val parts = schedule.status.split("|").map { it.trim() }
                    val teacherName = parts.getOrNull(0) ?: ""
                    val teacherSubject = parts.getOrNull(1) ?: ""

                    // Asumsi: Status sebenarnya ada di bagian ketiga (indeks 2)
                    val actualStatus = parts.getOrNull(2) ?: ""

                    schedule.copy(
                        teacherName = teacherName,
                        teacherSubject = teacherSubject,
                        // Gunakan status aktual untuk properti 'status', yang dipakai untuk filtering
                        status = actualStatus.ifEmpty { schedule.status }
                    )
                }

                println("📋 [ViewModel] Schedules loaded successfully")
                _schedules.value.forEach {
                    println("   - ${it.teacherName} | ${it.teacherSubject} | Status: ${it.status}")
                }
            } catch (e: Exception) {
                println("🔴 [ViewModel] Error fetching schedules: ${e.message}")
                e.printStackTrace()
                _schedules.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addSchedule(dto: ScheduleCreateDto) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("🔵 [ViewModel] Adding schedule: $dto")

                repository.addSchedule(dto)
                println("🟢 [ViewModel] Schedule added successfully")

                fetchSchedules()
            } catch (e: Exception) {
                println("🔴 [ViewModel] Error adding schedule: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSchedule(id: Long, dto: ScheduleCreateDto) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("🔵 [ViewModel] Updating schedule ID: $id")

                repository.updateSchedule(id, dto)
                println("🟢 [ViewModel] Schedule updated successfully")

                fetchSchedules()
            } catch (e: Exception) {
                println("🔴 [ViewModel] Error updating schedule: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSchedule(id: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("🔵 [ViewModel] Deleting schedule ID: $id")

                repository.deleteSchedule(id)
                println("🟢 [ViewModel] Schedule deleted successfully")

                // Refresh jadwal untuk update UI
                fetchSchedules()
            } catch (e: Exception) {
                println("🔴 [ViewModel] Error deleting schedule: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getScheduleById(id: Long): Schedule? {
        return _schedules.value.find { it.id == id }
    }
}