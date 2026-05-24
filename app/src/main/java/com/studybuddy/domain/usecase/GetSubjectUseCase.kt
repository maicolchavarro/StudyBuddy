package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.repository.SubjectRepository
import javax.inject.Inject

class GetSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(id: Int): Subject? {
        return repository.getSubject(id)
    }
}
