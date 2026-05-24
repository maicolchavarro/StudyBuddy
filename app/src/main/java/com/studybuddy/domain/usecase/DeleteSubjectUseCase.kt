package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.repository.SubjectRepository
import javax.inject.Inject

class DeleteSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subject: Subject): Result<Unit> {
        return repository.deleteSubject(subject)
    }
}
