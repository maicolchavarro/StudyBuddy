package com.studybuddy.domain.repository

import com.studybuddy.domain.model.Task
import com.studybuddy.domain.model.TaskWithSubject
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(task: Task): Result<Int>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(task: Task): Result<Unit>
    suspend fun completeTask(taskId: Int, isCompleted: Boolean): Result<Unit>
    fun getTasks(): Flow<List<TaskWithSubject>>
    fun getPendingTasks(): Flow<List<TaskWithSubject>>
    fun getCompletedTasks(): Flow<List<TaskWithSubject>>
    fun getTodayTasks(todayStart: Long): Flow<List<TaskWithSubject>>
    fun getTasksBySubject(subjectId: Int): Flow<List<TaskWithSubject>>
    fun getTasksByDateRange(start: Long, end: Long): Flow<List<TaskWithSubject>>
    suspend fun getTaskById(taskId: Int): TaskWithSubject?
}
