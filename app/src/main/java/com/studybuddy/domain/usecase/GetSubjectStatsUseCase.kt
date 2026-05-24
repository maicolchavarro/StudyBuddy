package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.SubjectLoadStats
import com.studybuddy.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectStatsUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    operator fun invoke(): Flow<List<SubjectLoadStats>> = repository.getSubjectStats()
}
