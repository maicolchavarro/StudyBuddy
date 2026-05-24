package com.studybuddy.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.repository.TaskRepository
import com.studybuddy.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getInt("taskId", -1)
        if (taskId == -1) return Result.failure()

        val taskWithSubject = taskRepository.getTaskById(taskId) ?: return Result.failure()
        val task = taskWithSubject.task
        val subject = taskWithSubject.subject

        val title = "StudyBuddy • Recordatorio"
        val priorityLabel = when (task.priority) {
            Priority.HIGH -> "Prioridad alta"
            Priority.MEDIUM -> "Prioridad media"
            Priority.LOW -> "Prioridad baja"
        }
        val typeLabel = if (task.taskType.name == "EXAM") "Examen" else "Tarea"
        val message = "$typeLabel: ${task.title}\n${subject.name} • ${task.dueTime}\n$priorityLabel"

        notificationHelper.showNotification(title, message, taskId)

        return Result.success()
    }
}
