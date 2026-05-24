package com.studybuddy.data.local.dao

import androidx.room.*
import com.studybuddy.data.local.entity.SubjectEntity
import com.studybuddy.data.local.entity.SubjectWithCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY createdAt DESC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("""
        SELECT subjects.*, COUNT(tasks.id) as taskCount 
        FROM subjects 
        LEFT JOIN tasks ON subjects.id = tasks.subjectId AND tasks.isCompleted = 0
        GROUP BY subjects.id 
        ORDER BY subjects.createdAt DESC
    """)
    fun getSubjectsWithTaskCount(): Flow<List<SubjectWithCountEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Int): SubjectEntity?

    @Query("SELECT * FROM subjects WHERE name = :name LIMIT 1")
    suspend fun getSubjectByName(name: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSubject(subject: SubjectEntity)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)
}
