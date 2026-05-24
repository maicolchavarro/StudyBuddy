package com.studybuddy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object Dashboard : Screen("dashboard", "Panel", Icons.Default.Dashboard)
    object Subjects : Screen("subjects", "Materias", Icons.AutoMirrored.Filled.List)
    object Calendar : Screen("calendar", "Calendario", Icons.Default.CalendarMonth)
    object Statistics : Screen("statistics", "Estadísticas", Icons.Default.PieChart)

    object Login : Screen("login")
    object Register : Screen("register")

    object TaskEditor : Screen("task_editor?taskId={taskId}&subjectId={subjectId}") {
        fun createRoute(taskId: Int? = null, subjectId: Int? = null): String {
            val params = buildList {
                taskId?.let { add("taskId=$it") }
                subjectId?.let { add("subjectId=$it") }
            }
            return if (params.isEmpty()) "task_editor" else "task_editor?${params.joinToString("&")}"
        }
    }

    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail/$taskId"
    }

    object SubjectDetail : Screen("subject_detail/{subjectId}") {
        fun createRoute(subjectId: Int) = "subject_detail/$subjectId"
    }
}
