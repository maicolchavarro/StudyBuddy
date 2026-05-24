package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Int): TaskWithSubject? {
        return repository.getTaskById(taskId)
    }
}
