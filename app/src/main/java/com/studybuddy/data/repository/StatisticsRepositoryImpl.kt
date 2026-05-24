package com.studybuddy.data.repository

import com.studybuddy.data.local.dao.SubjectDao
import com.studybuddy.data.local.dao.TaskDao
import com.studybuddy.data.local.entity.TaskEntity
import com.studybuddy.domain.model.StatisticsModel
import com.studybuddy.domain.model.SubjectLoadStats
import com.studybuddy.domain.model.TaskType
import com.studybuddy.domain.model.WeeklyTaskStats
import com.studybuddy.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subjectDao: SubjectDao
) : StatisticsRepository {

    override fun getDashboardStats(): Flow<StatisticsModel> {
        return combine(
            taskDao.getAllTasks(),
            subjectDao.getAllSubjects()
        ) { tasksWithSubject, subjects ->
            val tasks = tasksWithSubject.map { it.task }
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed
            val exams = tasks.count { it.taskType == TaskType.EXAM }
            
            val percentage = if (total > 0) completed.toFloat() / total else 0f

            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val productiveDay = tasks.filter { it.isCompleted }
                .groupBy { dayFormat.format(Date(it.dueDate)) }
                .maxByOrNull { it.value.size }?.key ?: "N/A"

            val highestSubject = subjects.map { subject ->
                subject.name to tasks.count { it.subjectId == subject.id }
            }.maxByOrNull { it.second }?.first ?: "N/A"

            StatisticsModel(
                totalTasks = total,
                completedTasks = completed,
                pendingTasks = pending,
                totalExams = exams,
                completionPercentage = percentage,
                mostProductiveDay = productiveDay,
                highestSubjectLoad = highestSubject,
                currentStreak = calculateStreak(tasks),
                weeklyCompletionRate = calculateWeeklyRate(tasks)
            )
        }
    }

    override fun getWeeklyStats(): Flow<List<WeeklyTaskStats>> {
        return taskDao.getAllTasks().map { tasksWithSubject ->
            val tasks = tasksWithSubject.map { it.task }
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            
            val last7Days = (0..6).map { i ->
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                cal
            }.reversed()

            last7Days.map { cal ->
                val count = tasks.count { 
                    val taskCal = Calendar.getInstance().apply { timeInMillis = it.dueDate }
                    taskCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                    taskCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
                }
                WeeklyTaskStats(dayFormat.format(cal.time), count)
            }
        }
    }

    override fun getSubjectStats(): Flow<List<SubjectLoadStats>> {
        return combine(
            taskDao.getAllTasks(),
            subjectDao.getAllSubjects()
        ) { tasksWithSubject, subjects ->
            val tasks = tasksWithSubject.map { it.task }
            subjects.map { subject ->
                SubjectLoadStats(
                    subjectName = subject.name,
                    count = tasks.count { it.subjectId == subject.id },
                    color = subject.color
                )
            }.filter { it.count > 0 }
        }
    }

    private fun calculateStreak(tasks: List<TaskEntity>): Int {
        if (tasks.none { it.isCompleted }) return 0
        
        val completedDates = tasks.filter { it.isCompleted }
            .map { 
                val cal = Calendar.getInstance().apply { timeInMillis = it.dueDate }
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }.distinct().sortedDescending()

        var streak = 0
        var currentCheck = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        for (date in completedDates) {
            if (date == currentCheck) {
                streak++
                currentCheck -= 24 * 3600000L
            } else if (date < currentCheck) {
                break
            }
        }
        return streak
    }

    private fun calculateWeeklyRate(tasks: List<TaskEntity>): Float {
        val weekAgo = System.currentTimeMillis() - 7 * 24 * 3600000L
        val weeklyTasks = tasks.filter { it.dueDate >= weekAgo }
        if (weeklyTasks.isEmpty()) return 0f
        return weeklyTasks.count { it.isCompleted }.toFloat() / weeklyTasks.size
    }
}
