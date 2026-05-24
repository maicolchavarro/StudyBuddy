package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Validaciones en tiempo real
    val isEmailValid = _email.map { android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() || it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val canLogin = combine(_email, _password) { email, pass ->
        email.isNotBlank() && pass.length >= 6 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onEmailChange(newValue: String) { _email.value = newValue }
    fun onPasswordChange(newValue: String) { _password.value = newValue }

    fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = loginUseCase(_email.value, _password.value)
            result.onSuccess {
                _uiState.value = LoginUiState.Success
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(error.message ?: "Credenciales incorrectas")
            }
        }
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
