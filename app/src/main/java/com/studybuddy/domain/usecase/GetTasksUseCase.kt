package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    fun getAll(): Flow<List<TaskWithSubject>> = repository.getTasks()
    fun getPending(): Flow<List<TaskWithSubject>> = repository.getPendingTasks()
    fun getCompleted(): Flow<List<TaskWithSubject>> = repository.getCompletedTasks()
    fun getToday(todayStart: Long): Flow<List<TaskWithSubject>> = repository.getTodayTasks(todayStart)
    fun getBySubject(subjectId: Int): Flow<List<TaskWithSubject>> = repository.getTasksBySubject(subjectId)
}
