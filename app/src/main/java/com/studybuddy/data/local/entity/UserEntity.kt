package com.studybuddy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa a un usuario en la base de datos local.
 * Implementa persistencia offline para el módulo de autenticación.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String, // Almacenada localmente para auth offline
    val createdAt: Long = System.currentTimeMillis()
)