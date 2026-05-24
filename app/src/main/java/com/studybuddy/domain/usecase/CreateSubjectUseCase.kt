package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.repository.SubjectRepository
import javax.inject.Inject

class CreateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(name: String, teacherName: String, color: Int, icon: String): Result<Unit> {
        if (name.isBlank()) return Result.failure(Exception("El nombre es obligatorio"))
        if (name.length < 3) return Result.failure(Exception("El nombre debe tener al menos 3 caracteres"))
        
        val subject = Subject(
            name = name,
            teacherName = teacherName,
            color = color,
            icon = icon
        )
        return repository.createSubject(subject)
    }
}
