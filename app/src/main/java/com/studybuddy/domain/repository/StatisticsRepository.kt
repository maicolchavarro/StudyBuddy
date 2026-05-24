package com.studybuddy.domain.repository

import com.studybuddy.domain.model.StatisticsModel
import com.studybuddy.domain.model.SubjectLoadStats
import com.studybuddy.domain.model.WeeklyTaskStats
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun getDashboardStats(): Flow<StatisticsModel>
    fun getWeeklyStats(): Flow<List<WeeklyTaskStats>>
    fun getSubjectStats(): Flow<List<SubjectLoadStats>>
}
