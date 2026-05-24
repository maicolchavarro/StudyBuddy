package com.studybuddy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.TaskType

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val subjectId: Int,
    val dueDate: Long,
    val dueTime: String,
    val priority: Priority,
    val taskType: TaskType,
    val isCompleted: Boolean = false,
    val reminderMinutes: Int,
    val createdAt: Long = System.currentTimeMillis()
)
