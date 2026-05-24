package com.studybuddy.domain.usecase

import com.studybuddy.data.local.entity.UserEntity
import com.studybuddy.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Caso de uso para registrar un nuevo usuario.
 * Realiza validaciones de negocio antes de persistir.
 */
class RegisterUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit> {
        if (name.length < 3) return Result.failure(Exception("El nombre debe tener al menos 3 caracteres"))
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Correo electrónico no válido"))
        }
        if (password.length < 6) return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))

        val userEntity = UserEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            password = password
        )
        
        return repository.registerUser(userEntity)
    }
}