package com.studybuddy.domain.repository

import com.studybuddy.data.local.entity.UserEntity
import com.studybuddy.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de usuarios para el dominio.
 */
interface UserRepository {
    suspend fun registerUser(userEntity: UserEntity): Result<Unit>
    suspend fun login(email: String, password: String): Result<User?>
    suspend fun logout()
    fun getCurrentUser(userId: String): Flow<User?>
    suspend fun getUserByEmail(email: String): UserEntity?
}