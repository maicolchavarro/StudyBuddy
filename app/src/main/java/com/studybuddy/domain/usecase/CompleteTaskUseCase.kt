package com.studybuddy.domain.usecase

import com.studybuddy.domain.repository.TaskRepository
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Int, isCompleted: Boolean): Result<Unit> {
        return repository.completeTask(taskId, isCompleted)
    }
}
