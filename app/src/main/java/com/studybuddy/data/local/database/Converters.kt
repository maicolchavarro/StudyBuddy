package com.studybuddy.data.local.database

import androidx.room.TypeConverter
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.TaskType

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return try {
            Priority.valueOf(priority)
        } catch (e: Exception) {
            Priority.MEDIUM // Valor por defecto si hay basura en la DB
        }
    }

    @TypeConverter
    fun fromTaskType(taskType: TaskType): String = taskType.name

    @TypeConverter
    fun toTaskType(taskType: String): TaskType {
        return try {
            TaskType.valueOf(taskType)
        } catch (e: Exception) {
            TaskType.TASK
        }
    }
}
