package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.StatisticsModel
import com.studybuddy.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    operator fun invoke(): Flow<StatisticsModel> = repository.getDashboardStats()
}
