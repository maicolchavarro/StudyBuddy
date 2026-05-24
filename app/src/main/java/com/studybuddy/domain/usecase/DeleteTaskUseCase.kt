package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Task
import com.studybuddy.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return repository.deleteTask(task)
    }
}
