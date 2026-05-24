package com.studybuddy.domain.repository

import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.model.SubjectWithTaskCount
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    suspend fun createSubject(subject: Subject): Result<Unit>
    suspend fun updateSubject(subject: Subject): Result<Unit>
    suspend fun deleteSubject(subject: Subject): Result<Unit>
    fun getSubjects(): Flow<List<Subject>>
    fun getSubjectsWithTaskCount(): Flow<List<SubjectWithTaskCount>>
    suspend fun getSubject(id: Int): Subject?
}
