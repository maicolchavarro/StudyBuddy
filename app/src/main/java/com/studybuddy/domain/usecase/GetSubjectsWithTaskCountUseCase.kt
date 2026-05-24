package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.SubjectWithTaskCount
import com.studybuddy.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectsWithTaskCountUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    operator fun invoke(): Flow<List<SubjectWithTaskCount>> {
        return repository.getSubjectsWithTaskCount()
    }
}
