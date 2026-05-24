package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.repository.SubjectRepository
import javax.inject.Inject

class UpdateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subject: Subject): Result<Unit> {
        if (subject.name.isBlank()) return Result.failure(Exception("El nombre es obligatorio"))
        if (subject.name.length < 3) return Result.failure(Exception("El nombre debe tener al menos 3 caracteres"))
        
        return repository.updateSubject(subject)
    }
}
