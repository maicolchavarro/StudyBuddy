package com.studybuddy.data.repository

import com.studybuddy.data.local.dao.UserDao
import com.studybuddy.data.local.entity.UserEntity
import com.studybuddy.data.local.preferences.SessionManager
import com.studybuddy.domain.model.User
import com.studybuddy.domain.repository.UserRepository
import com.studybuddy.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : UserRepository {

    override suspend fun registerUser(userEntity: UserEntity): Result<Unit> {
        return try {
            val existingUser = userDao.getUserByEmail(userEntity.email)
            if (existingUser != null) {
                Result.failure(Exception("El correo ya está registrado"))
            } else {
                userDao.registerUser(
                    userEntity.copy(password = PasswordHasher.hash(userEntity.password))
                )
                sessionManager.saveSession(userEntity.id)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User?> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            val hashedPassword = PasswordHasher.hash(password)

            if (userEntity != null && (userEntity.password == password || userEntity.password == hashedPassword)) {
                sessionManager.saveSession(userEntity.id)
                Result.success(User(userEntity.id, userEntity.name, userEntity.email, null))
            } else {
                Result.failure(Exception("Correo o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override fun getCurrentUser(userId: String): Flow<User?> {
        return userDao.getUserById(userId).map { entity ->
            entity?.let { User(it.id, it.name, it.email, null) }
        }
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
}
