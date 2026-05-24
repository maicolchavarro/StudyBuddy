package com.studybuddy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.studybuddy.data.local.dao.SubjectDao
import com.studybuddy.data.local.dao.TaskDao
import com.studybuddy.data.local.dao.UserDao
import com.studybuddy.data.local.entity.SubjectEntity
import com.studybuddy.data.local.entity.TaskEntity
import com.studybuddy.data.local.entity.UserEntity

/**
 * Base de datos principal de StudyBuddy.
 * Versión 40: Cambio de nombre y versión para asegurar estabilidad absoluta en el arranque.
 */
@Database(
    entities = [UserEntity::class, SubjectEntity::class, TaskEntity::class],
    version = 40,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "studybuddy_v40_stable.db"
    }
}
