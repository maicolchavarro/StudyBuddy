package com.studybuddy.di

import android.content.Context
import androidx.room.Room
import com.studybuddy.data.local.dao.SubjectDao
import com.studybuddy.data.local.dao.TaskDao
import com.studybuddy.data.local.dao.UserDao
import com.studybuddy.data.local.database.AppDatabase
import com.studybuddy.data.local.preferences.SessionManager
import com.studybuddy.data.repository.StatisticsRepositoryImpl
import com.studybuddy.data.repository.SubjectRepositoryImpl
import com.studybuddy.data.repository.TaskRepositoryImpl
import com.studybuddy.data.repository.UserRepositoryImpl
import com.studybuddy.domain.repository.StatisticsRepository
import com.studybuddy.domain.repository.SubjectRepository
import com.studybuddy.domain.repository.TaskRepository
import com.studybuddy.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer instancias globales como la base de datos y DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideSubjectDao(database: AppDatabase): SubjectDao = database.subjectDao()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        sessionManager: SessionManager
    ): UserRepository = UserRepositoryImpl(userDao, sessionManager)

    @Provides
    @Singleton
    fun provideSubjectRepository(
        subjectDao: SubjectDao
    ): SubjectRepository = SubjectRepositoryImpl(subjectDao)

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao
    ): TaskRepository = TaskRepositoryImpl(taskDao)

    @Provides
    @Singleton
    fun provideStatisticsRepository(
        taskDao: TaskDao,
        subjectDao: SubjectDao
    ): StatisticsRepository = StatisticsRepositoryImpl(taskDao, subjectDao)
}
