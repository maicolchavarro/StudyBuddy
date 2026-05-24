package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.WeeklyTaskStats
import com.studybuddy.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyStatsUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    operator fun invoke(): Flow<List<WeeklyTaskStats>> = repository.getWeeklyStats()
}
