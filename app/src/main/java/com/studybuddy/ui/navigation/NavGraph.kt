package com.studybuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.studybuddy.ui.screens.auth.LoginScreen
import com.studybuddy.ui.screens.auth.RegisterScreen
import com.studybuddy.ui.screens.calendar.CalendarScreen
import com.studybuddy.ui.screens.dashboard.DashboardScreen
import com.studybuddy.ui.screens.statistics.StatisticsScreen
import com.studybuddy.ui.screens.subjects.SubjectDetailScreen
import com.studybuddy.ui.screens.subjects.SubjectsScreen
import com.studybuddy.ui.screens.tasks.CreateTaskScreen
import com.studybuddy.ui.screens.tasks.TaskDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToTaskEditor = {
                    navController.navigate(Screen.TaskEditor.createRoute())
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onLogout = onLogout
            )
        }

        composable(Screen.Subjects.route) {
            SubjectsScreen(
                onNavigateToSubjectDetail = { subjectId ->
                    navController.navigate(Screen.SubjectDetail.createRoute(subjectId))
                },
                onLogout = onLogout
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onLogout = onLogout
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(onLogout = onLogout)
        }

        composable(
            route = Screen.TaskEditor.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("subjectId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")?.takeIf { it > 0 }
            val subjectId = backStackEntry.arguments?.getInt("subjectId")?.takeIf { it > 0 }
            CreateTaskScreen(
                taskId = taskId,
                preselectedSubjectId = subjectId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull() ?: 0
            TaskDetailScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() },
                onEditTask = { id ->
                    navController.navigate(Screen.TaskEditor.createRoute(id))
                }
            )
        }

        composable(Screen.SubjectDetail.route) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toIntOrNull() ?: 0
            SubjectDetailScreen(
                subjectId = subjectId,
                onBack = { navController.popBackStack() },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onCreateTask = {
                    navController.navigate(Screen.TaskEditor.createRoute(subjectId = subjectId))
                }
            )
        }
    }
}
