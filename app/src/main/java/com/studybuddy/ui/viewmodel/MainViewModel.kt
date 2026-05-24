package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.data.local.preferences.SessionManager
import com.studybuddy.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestionar el estado inicial de la aplicación (sesión activa).
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    // Usamos stateIn para asegurar que siempre haya un valor inicial y no se quede en null infinitamente
    val isLoggedIn: StateFlow<Boolean?> = sessionManager.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
