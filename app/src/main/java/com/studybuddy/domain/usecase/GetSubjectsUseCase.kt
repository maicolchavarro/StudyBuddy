package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    operator fun invoke(): Flow<List<Subject>> {
        return repository.getSubjects()
    }
}
