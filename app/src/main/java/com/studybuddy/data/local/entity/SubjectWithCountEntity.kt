package com.studybuddy.data.local.entity

import androidx.room.Embedded

/**
 * POJO de Kotlin para recibir el resultado de la consulta Join.
 * Usamos Long para taskCount ya que es lo que devuelve COUNT() en SQL.
 */
data class SubjectWithCountEntity(
    @Embedded val subject: SubjectEntity,
    val taskCount: Long
)
