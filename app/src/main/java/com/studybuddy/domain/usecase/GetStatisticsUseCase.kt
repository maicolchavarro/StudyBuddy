package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.TaskType
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class AcademicStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val completionRate: Float,
    val highPriorityPending: Int,
    val tasksByType: Map<TaskType, Int>,
    val tasksBySubject: Map<String, Int>
)

class GetStatisticsUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<AcademicStats> {
        return repository.getTasks().map { tasks ->
            val completed = tasks.count { it.task.isCompleted }
            val total = tasks.size
            val pending = total - completed
            
            AcademicStats(
                totalTasks = total,
                completedTasks = completed,
                pendingTasks = pending,
                completionRate = if (total > 0) completed.toFloat() / total else 0f,
                highPriorityPending = tasks.count { !it.task.isCompleted && it.task.priority == Priority.HIGH },
                tasksByType = tasks.groupBy { it.task.taskType }.mapValues { it.value.size },
                tasksBySubject = tasks.groupBy { it.subject.name }.mapValues { it.value.size }
            )
        }
    }
}
