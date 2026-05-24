package com.studybuddy.domain.model

/**
 * Modelo de dominio que incluye el conteo de tareas.
 * Se usa Long para taskCount para asegurar compatibilidad con COUNT() de Room/SQLite.
 */
data class SubjectWithTaskCount(
    val subject: Subject,
    val taskCount: Long
)
