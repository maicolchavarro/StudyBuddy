package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Task
import com.studybuddy.domain.repository.TaskRepository
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Int> {
        if (task.title.isBlank()) return Result.failure(Exception("El título es obligatorio"))
        if (task.subjectId == 0) return Result.failure(Exception("La materia es obligatoria"))
        if (task.dueDate == 0L) return Result.failure(Exception("La fecha es obligatoria"))
        
        return repository.createTask(task)
    }
}
