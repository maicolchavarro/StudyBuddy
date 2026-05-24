package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.model.SubjectWithTaskCount
import com.studybuddy.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val getSubjectsWithTaskCountUseCase: GetSubjectsWithTaskCountUseCase,
    private val createSubjectUseCase: CreateSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<SubjectWithTaskCount>>(emptyList())
    val subjects = _subjects.asStateFlow()

    private val _uiState = MutableStateFlow<SubjectUiState>(SubjectUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        getSubjectsWithTaskCountUseCase().onEach { list ->
            _subjects.value = list
        }.launchIn(viewModelScope)
    }

    fun addSubject(name: String, teacher: String, color: Int, icon: String) {
        viewModelScope.launch {
            _uiState.value = SubjectUiState.Loading
            createSubjectUseCase(name, teacher, color, icon)
                .onSuccess {
                    _uiState.value = SubjectUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = SubjectUiState.Idle
                    _error.emit(e.message ?: "Error al crear materia")
                }
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            _uiState.value = SubjectUiState.Loading
            updateSubjectUseCase(subject)
                .onSuccess {
                    _uiState.value = SubjectUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = SubjectUiState.Idle
                    _error.emit(e.message ?: "Error al actualizar materia")
                }
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            deleteSubjectUseCase(subject)
                .onFailure { e ->
                    _error.emit(e.message ?: "Error al eliminar materia")
                }
        }
    }

    fun resetUiState() {
        _uiState.value = SubjectUiState.Idle
    }
}

sealed class SubjectUiState {
    object Idle : SubjectUiState()
    object Loading : SubjectUiState()
    object Success : SubjectUiState()
}
