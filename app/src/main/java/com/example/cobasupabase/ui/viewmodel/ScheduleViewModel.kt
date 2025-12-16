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
                _schedules.value = repository.getSchedules()
            } catch (e: Exception) {
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
                repository.addSchedule(dto)
                fetchSchedules()
            } catch (e: Exception) {
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
                repository.updateSchedule(id, dto)
                fetchSchedules()
            } catch (e: Exception) {
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
                repository.deleteSchedule(id)
                fetchSchedules()
            } catch (e: Exception) {
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