package com.studybuddy.data.local.dao

import androidx.room.*
import com.studybuddy.data.local.entity.TaskEntity
import com.studybuddy.data.local.entity.TaskWithSubjectEntity
import com.studybuddy.domain.model.TaskType
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskWithSubjectEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskWithSubjectEntity?

    @Transaction
    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId")
    fun getTasksBySubject(subjectId: Int): Flow<List<TaskWithSubjectEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE dueDate >= :startDate AND dueDate <= :endDate ORDER BY dueDate ASC, dueTime ASC")
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<TaskWithSubjectEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE dueDate = :date")
    fun getTasksByDate(date: Long): Flow<List<TaskWithSubjectEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getPendingTasks(): Flow<List<TaskWithSubjectEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY dueDate DESC")
    fun getCompletedTasks(): Flow<List<TaskWithSubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Int, completed: Boolean)

    // Consultas para Estadísticas
    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE taskType = 'EXAM'")
    fun getTotalExamsCount(): Flow<Int>
}
