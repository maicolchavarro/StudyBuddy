package com.studybuddy.data.repository

import com.studybuddy.data.local.dao.SubjectDao
import com.studybuddy.data.local.entity.SubjectEntity
import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.model.SubjectWithTaskCount
import com.studybuddy.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao
) : SubjectRepository {

    override suspend fun createSubject(subject: Subject): Result<Unit> {
        return try {
            val existing = subjectDao.getSubjectByName(subject.name)
            if (existing != null) {
                Result.failure(Exception("Ya existe una materia con ese nombre"))
            } else {
                subjectDao.insertSubject(subject.toEntity())
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSubject(subject: Subject): Result<Unit> {
        return try {
            subjectDao.updateSubject(subject.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSubject(subject: Subject): Result<Unit> {
        return try {
            subjectDao.deleteSubject(subject.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubjectsWithTaskCount(): Flow<List<SubjectWithTaskCount>> {
        return subjectDao.getSubjectsWithTaskCount().map { list ->
            list.map { item ->
                SubjectWithTaskCount(item.subject.toDomain(), item.taskCount)
            }
        }
    }

    override suspend fun getSubject(id: Int): Subject? {
        return subjectDao.getSubjectById(id)?.toDomain()
    }

    private fun SubjectEntity.toDomain() = Subject(
        id = id,
        name = name,
        teacherName = teacherName,
        color = color,
        icon = icon,
        createdAt = createdAt
    )

    private fun Subject.toEntity() = SubjectEntity(
        id = id,
        name = name,
        teacherName = teacherName,
        color = color,
        icon = icon,
        createdAt = createdAt
    )
}
