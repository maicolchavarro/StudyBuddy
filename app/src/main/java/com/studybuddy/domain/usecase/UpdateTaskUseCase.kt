package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Task
import com.studybuddy.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        if (task.title.isBlank()) return Result.failure(Exception("El título es obligatorio"))
        return repository.updateTask(task)
    }
}
