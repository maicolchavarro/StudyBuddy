package com.studybuddy.data.repository

import com.studybuddy.data.local.dao.TaskDao
import com.studybuddy.data.local.entity.SubjectEntity
import com.studybuddy.data.local.entity.TaskEntity
import com.studybuddy.data.local.entity.TaskWithSubjectEntity
import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.model.Task
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun createTask(task: Task): Result<Int> {
        return try {
            val id = taskDao.insertTask(task.toEntity())
            Result.success(id.toInt())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            taskDao.updateTask(task.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(task: Task): Result<Unit> {
        return try {
            taskDao.deleteTask(task.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeTask(taskId: Int, isCompleted: Boolean): Result<Unit> {
        return try {
            taskDao.updateTaskCompletion(taskId, isCompleted)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTasks(): Flow<List<TaskWithSubject>> {
        return taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }
    }

    override fun getPendingTasks(): Flow<List<TaskWithSubject>> {
        return taskDao.getPendingTasks().map { list -> list.map { it.toDomain() } }
    }

    override fun getCompletedTasks(): Flow<List<TaskWithSubject>> {
        return taskDao.getCompletedTasks().map { list -> list.map { it.toDomain() } }
    }

    override fun getTodayTasks(todayStart: Long): Flow<List<TaskWithSubject>> {
        return taskDao.getTasksByDate(todayStart).map { list -> list.map { it.toDomain() } }
    }

    override fun getTasksBySubject(subjectId: Int): Flow<List<TaskWithSubject>> {
        return taskDao.getTasksBySubject(subjectId).map { list -> list.map { it.toDomain() } }
    }

    override fun getTasksByDateRange(start: Long, end: Long): Flow<List<TaskWithSubject>> {
        return taskDao.getTasksByDateRange(start, end).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getTaskById(taskId: Int): TaskWithSubject? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    private fun TaskWithSubjectEntity.toDomain() = TaskWithSubject(
        task = task.toDomain(),
        subject = subject.toDomain()
    )

    private fun TaskEntity.toDomain() = Task(
        id = id,
        title = title,
        description = description,
        subjectId = subjectId,
        dueDate = dueDate,
        dueTime = dueTime,
        priority = priority,
        taskType = taskType,
        isCompleted = isCompleted,
        reminderMinutes = reminderMinutes,
        createdAt = createdAt
    )

    private fun SubjectEntity.toDomain() = Subject(
        id = id,
        name = name,
        teacherName = teacherName,
        color = color,
        icon = icon,
        createdAt = createdAt
    )

    private fun Task.toEntity() = TaskEntity(
        id = id,
        title = title,
        description = description,
        subjectId = subjectId,
        dueDate = dueDate,
        dueTime = dueTime,
        priority = priority,
        taskType = taskType,
        isCompleted = isCompleted,
        reminderMinutes = reminderMinutes,
        createdAt = createdAt
    )
}
