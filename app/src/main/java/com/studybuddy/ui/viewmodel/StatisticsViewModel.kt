package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.domain.model.StatisticsModel
import com.studybuddy.domain.model.SubjectLoadStats
import com.studybuddy.domain.model.WeeklyTaskStats
import com.studybuddy.domain.usecase.GetDashboardStatsUseCase
import com.studybuddy.domain.usecase.GetSubjectStatsUseCase
import com.studybuddy.domain.usecase.GetWeeklyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase,
    private val getSubjectStatsUseCase: GetSubjectStatsUseCase
) : ViewModel() {

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    val statistics: StateFlow<StatisticsModel?> = getDashboardStatsUseCase()
        .onEach { _loading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weeklyStats: StateFlow<List<WeeklyTaskStats>> = getWeeklyStatsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectStats: StateFlow<List<SubjectLoadStats>> = getSubjectStatsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
