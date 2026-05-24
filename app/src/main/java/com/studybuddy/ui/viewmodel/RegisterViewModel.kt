package com.studybuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studybuddy.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Real-time validations
    val isNameValid = _name.map { it.length >= 3 || it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isEmailValid = _email.map { android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() || it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isPasswordValid = _password.map { it.length >= 6 || it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val passwordsMatch = combine(_password, _confirmPassword) { pass, confirm ->
        pass == confirm || confirm.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val canRegister = combine(_name, _email, _password, _confirmPassword) { name, email, pass, confirm ->
        name.length >= 3 && 
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && 
        pass.length >= 6 && 
        pass == confirm
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onNameChange(newValue: String) { _name.value = newValue }
    fun onEmailChange(newValue: String) { _email.value = newValue }
    fun onPasswordChange(newValue: String) { _password.value = newValue }
    fun onConfirmPasswordChange(newValue: String) { _confirmPassword.value = newValue }

    fun register() {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            val result = registerUseCase(_name.value, _email.value, _password.value)
            result.onSuccess {
                _uiState.value = RegisterUiState.Success
            }.onFailure { error ->
                _uiState.value = RegisterUiState.Error(error.message ?: "Error al registrar usuario")
            }
        }
    }

    fun resetState() { _uiState.value = RegisterUiState.Idle }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
