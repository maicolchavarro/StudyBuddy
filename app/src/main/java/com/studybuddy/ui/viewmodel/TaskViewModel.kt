package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.Task
import com.studybuddy.domain.model.TaskType
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.domain.usecase.*
import com.studybuddy.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _pendingTasks = MutableStateFlow<List<TaskWithSubject>>(emptyList())
    val pendingTasks = _pendingTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<TaskWithSubject>>(emptyList())
    val completedTasks = _completedTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<List<TaskWithSubject>>(emptyList())
    val todayTasks = _todayTasks.asStateFlow()

    private val _selectedTask = MutableStateFlow<TaskWithSubject?>(null)
    val selectedTask = _selectedTask.asStateFlow()

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        getTasksUseCase.getPending().onEach { _pendingTasks.value = it }.launchIn(viewModelScope)
        getTasksUseCase.getCompleted().onEach { _completedTasks.value = it }.launchIn(viewModelScope)
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        getTasksUseCase.getToday(calendar.timeInMillis).onEach { _todayTasks.value = it }.launchIn(viewModelScope)
    }

    fun getTaskById(id: Int) {
        viewModelScope.launch {
            _selectedTask.value = getTaskUseCase(id)
        }
    }

    fun addTask(
        title: String,
        description: String?,
        subjectId: Int,
        dueDate: Long,
        dueTime: String,
        priority: Priority,
        taskType: TaskType,
        reminderMinutes: Int
    ) {
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            val task = Task(
                title = title,
                description = description,
                subjectId = subjectId,
                dueDate = dueDate,
                dueTime = dueTime,
                priority = priority,
                taskType = taskType,
                reminderMinutes = reminderMinutes
            )
            createTaskUseCase(task)
                .onSuccess { taskId ->
                    // Programar recordatorio con el ID generado
                    reminderScheduler.scheduleReminder(taskId, dueDate, dueTime, reminderMinutes)
                    _uiState.value = TaskUiState.Success 
                }
                .onFailure { e ->
                    _uiState.value = TaskUiState.Idle
                    _error.emit(e.message ?: "Error al crear tarea")
                }
        }
    }

    fun updateTask(
        taskId: Int,
        title: String,
        description: String?,
        subjectId: Int,
        dueDate: Long,
        dueTime: String,
        priority: Priority,
        taskType: TaskType,
        isCompleted: Boolean,
        reminderMinutes: Int
    ) {
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            val updatedTask = Task(
                id = taskId,
                title = title,
                description = description,
                subjectId = subjectId,
                dueDate = dueDate,
                dueTime = dueTime,
                priority = priority,
                taskType = taskType,
                isCompleted = isCompleted,
                reminderMinutes = reminderMinutes
            )

            updateTaskUseCase(updatedTask)
                .onSuccess {
                    reminderScheduler.cancelReminder(taskId)
                    if (!isCompleted) {
                        reminderScheduler.scheduleReminder(taskId, dueDate, dueTime, reminderMinutes)
                    }
                    _uiState.value = TaskUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = TaskUiState.Idle
                    _error.emit(e.message ?: "Error al actualizar tarea")
                }
        }
    }

    fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            completeTaskUseCase(taskId, isCompleted)
                .onSuccess {
                    if (isCompleted) {
                        reminderScheduler.cancelReminder(taskId)
                    }
                }
                .onFailure { e -> _error.emit(e.message ?: "Error al actualizar tarea") }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
                .onSuccess {
                    reminderScheduler.cancelReminder(task.id)
                }
                .onFailure { e -> _error.emit(e.message ?: "Error al eliminar tarea") }
        }
    }

    fun resetUiState() {
        _uiState.value = TaskUiState.Idle
    }

    fun clearSelectedTask() {
        _selectedTask.value = null
    }
}

sealed class TaskUiState {
    object Idle : TaskUiState()
    object Loading : TaskUiState()
    object Success : TaskUiState()
}
