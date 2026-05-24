package com.studybuddy.domain.usecase

import com.studybuddy.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para cerrar la sesión del usuario.
 */
class LogoutUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}