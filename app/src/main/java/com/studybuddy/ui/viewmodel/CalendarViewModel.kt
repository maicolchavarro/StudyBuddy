package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.domain.model.TaskType
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate = _selectedDate.asStateFlow()

    private val _filterType = MutableStateFlow<TaskType?>(null) // null = Todo
    val filterType = _filterType.asStateFlow()

    private val _filterStatus = MutableStateFlow<Boolean?>(null) // null = Todo, true = Completada, false = Pendiente
    val filterStatus = _filterStatus.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks = combine(_selectedDate, _filterType, _filterStatus) { date, type, status ->
        Triple(date, type, status)
    }.flatMapLatest { (date, type, status) ->
        val startOfDay = date.clone() as Calendar
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)
        
        val endOfDay = date.clone() as Calendar
        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)
        endOfDay.set(Calendar.SECOND, 59)
        endOfDay.set(Calendar.MILLISECOND, 999)

        repository.getTasksByDateRange(startOfDay.timeInMillis, endOfDay.timeInMillis)
            .map { list ->
                list.filter { item ->
                    val matchType = type == null || item.task.taskType == type
                    val matchStatus = status == null || item.task.isCompleted == status
                    matchType && matchStatus
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDateSelected(date: Calendar) {
        _selectedDate.value = date
    }

    fun nextWeek() {
        val newDate = _selectedDate.value.clone() as Calendar
        newDate.add(Calendar.DAY_OF_YEAR, 7)
        _selectedDate.value = newDate
    }

    fun previousWeek() {
        val newDate = _selectedDate.value.clone() as Calendar
        newDate.add(Calendar.DAY_OF_YEAR, -7)
        _selectedDate.value = newDate
    }

    fun setFilterType(type: TaskType?) {
        _filterType.value = type
    }

    fun setFilterStatus(status: Boolean?) {
        _filterStatus.value = status
    }
}
