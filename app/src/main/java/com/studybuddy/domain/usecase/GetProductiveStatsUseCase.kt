package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.StatisticsModel
import com.studybuddy.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ProductivityStats(
    val mostProductiveDay: String,
    val highestSubjectLoad: String,
    val currentStreak: Int,
    val weeklyCompletionRate: Float
)

class GetProductiveStatsUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    operator fun invoke(): Flow<ProductivityStats> {
        return repository.getDashboardStats().map { 
            ProductivityStats(
                mostProductiveDay = it.mostProductiveDay,
                highestSubjectLoad = it.highestSubjectLoad,
                currentStreak = it.currentStreak,
                weeklyCompletionRate = it.weeklyCompletionRate
            )
        }
    }
}
