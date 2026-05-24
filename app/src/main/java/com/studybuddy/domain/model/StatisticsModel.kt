package com.studybuddy.domain.model

data class StatisticsModel(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val totalExams: Int,
    val completionPercentage: Float,
    val mostProductiveDay: String,
    val highestSubjectLoad: String,
    val currentStreak: Int,
    val weeklyCompletionRate: Float
)

data class WeeklyTaskStats(
    val day: String,
    val count: Int
)

data class SubjectLoadStats(
    val subjectName: String,
    val count: Int,
    val color: Int
)
