package com.studybuddy.domain.usecase

import com.studybuddy.domain.model.User
import com.studybuddy.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para iniciar sesión localmente.
 */
class LoginUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User?> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }
        return repository.login(email, password)
    }
}