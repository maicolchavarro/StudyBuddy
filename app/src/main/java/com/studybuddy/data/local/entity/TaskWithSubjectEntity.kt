package com.studybuddy.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Representa la unión de una tarea con su materia asociada en la base de datos.
 */
data class TaskWithSubjectEntity(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: SubjectEntity
)
