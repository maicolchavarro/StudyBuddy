package com.studybuddy.domain.model

data class Task(
    val id: Int = 0,
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
