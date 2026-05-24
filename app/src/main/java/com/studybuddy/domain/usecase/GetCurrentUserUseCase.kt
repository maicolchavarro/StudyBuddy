package com.studybuddy.domain.usecase

import com.studybuddy.data.local.preferences.SessionManager
import com.studybuddy.domain.model.User
import com.studybuddy.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Caso de uso para obtener el flujo del usuario actualmente autenticado.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<User?> {
        return sessionManager.currentUserId.flatMapLatest { userId ->
            if (userId.isNullOrEmpty()) {
                flowOf(null)
            } else {
                repository.getCurrentUser(userId)
            }
        }
    }
}