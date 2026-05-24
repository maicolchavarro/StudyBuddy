package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.domain.usecase.GetSubjectUseCase
import com.studybuddy.domain.usecase.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SubjectDetailViewModel @Inject constructor(
    private val getSubjectUseCase: GetSubjectUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private val _subject = MutableStateFlow<Subject?>(null)
    val subject: StateFlow<Subject?> = _subject

    private val _subjectId = MutableStateFlow(0)

    val tasks: StateFlow<List<TaskWithSubject>> = _subjectId
        .flatMapLatest { subjectId ->
            if (subjectId <= 0) {
                flowOf(emptyList())
            } else {
                getTasksUseCase.getBySubject(subjectId)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun load(subjectId: Int) {
        if (subjectId == _subjectId.value) return
        _subjectId.value = subjectId
        viewModelScope.launch {
            _subject.value = getSubjectUseCase(subjectId)
        }
    }
}
