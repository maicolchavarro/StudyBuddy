package com.studybuddy.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.ui.components.StudyBuddyBrandHeader
import com.studybuddy.ui.components.StudyBuddyInfoPill
import com.studybuddy.ui.components.StudyBuddyMetricCard
import com.studybuddy.ui.components.StudyBuddyOverflowButton
import com.studybuddy.ui.components.StudyBuddyPillButton
import com.studybuddy.ui.screens.subjects.getIconByName
import com.studybuddy.ui.viewmodel.DashboardViewModel
import com.studybuddy.ui.viewmodel.StatisticsViewModel
import com.studybuddy.ui.viewmodel.TaskViewModel
import java.util.Calendar

@Composable
fun DashboardScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    statsViewModel: StatisticsViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToTaskEditor: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onLogout: () -> Unit
) {
    val pendingTasks by taskViewModel.pendingTasks.collectAsState()
    val todayTasks by taskViewModel.todayTasks.collectAsState()
    val stats by statsViewModel.statistics.collectAsState()
    val user by dashboardViewModel.currentUser.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    val tasksForToday = if (todayTasks.isNotEmpty()) todayTasks else pendingTasks.take(4)
    val upcomingTodayCount = pendingTasks.count { task ->
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        task.task.dueDate < tomorrow
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(18.dp))
            StudyBuddyBrandHeader(
                title = "Hola, ${user?.name?.substringBefore(" ") ?: "Estudiante"}",
                subtitle = "Lista para organizar tu carga académica de hoy",
                userName = user?.name,
                trailingContent = {
                    Box {
                        StudyBuddyOverflowButton(onClick = { showMenu = true })
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        }

        item {
            stats?.let { value ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StudyBuddyMetricCard(
                        title = "Tareas pendientes",
                        value = value.pendingTasks.toString(),
                        supportingText = "Organiza tus próximas entregas",
                        icon = Icons.Default.PendingActions,
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StudyBuddyMetricCard(
                            title = "Completadas",
                            value = value.completedTasks.toString(),
                            supportingText = "Avance acumulado",
                            icon = Icons.Default.CheckCircle,
                            accentColor = Color(0xFF4B9B5C),
                            modifier = Modifier.weight(1f)
                        )
                        StudyBuddyMetricCard(
                            title = "Próximas",
                            value = upcomingTodayCount.toString(),
                            supportingText = "Antes de mañana",
                            icon = Icons.Default.NotificationsActive,
                            accentColor = Color(0xFFD94141),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StudyBuddyPillButton(
                    text = "Añadir tarea",
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTaskEditor
                )
                StudyBuddyPillButton(
                    text = "Ver calendario",
                    icon = Icons.Default.CalendarMonth,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = onNavigateToCalendar
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tareas de hoy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "VER TODO",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (tasksForToday.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tienes tareas activas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Crea una nueva tarea para empezar a planificar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        } else {
            items(tasksForToday, key = { it.task.id }) { taskWithSubject ->
                DashboardTaskCard(
                    taskWithSubject = taskWithSubject,
                    onClick = { onNavigateToTaskDetail(taskWithSubject.task.id) }
                )
            }
        }

        item {
            stats?.let { value ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StudyBuddyMetricCard(
                        title = "Para hoy",
                        value = "${tasksForToday.size} tareas",
                        supportingText = "En tu agenda inmediata",
                        modifier = Modifier.weight(1f)
                    )
                    StudyBuddyMetricCard(
                        title = "Esta semana",
                        value = "${value.totalExams} exámenes",
                        supportingText = "Carga evaluativa",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            val focus = ((stats?.completionPercentage ?: 0f) * 100).toInt()
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    Color(0xFF5E84F3)
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Text(
                            text = "Productividad",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "$focus% de enfoque",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Mantén el ritmo. Tus tareas completadas ya alimentan las estadísticas de la semana.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.88f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DashboardTaskCard(
    taskWithSubject: TaskWithSubject,
    onClick: () -> Unit
) {
    val priorityColor = when (taskWithSubject.task.priority) {
        Priority.HIGH -> Color(0xFFD94141)
        Priority.MEDIUM -> Color(0xFFF2A33B)
        Priority.LOW -> Color(0xFF5E84F3)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .clip(CircleShape)
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(taskWithSubject.subject.color).copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconByName(taskWithSubject.subject.icon),
                    contentDescription = null,
                    tint = Color(taskWithSubject.subject.color)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taskWithSubject.task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${taskWithSubject.subject.name} • ${taskWithSubject.task.dueTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                StudyBuddyInfoPill(
                    text = when (taskWithSubject.task.priority) {
                        Priority.HIGH -> "Alta prioridad"
                        Priority.MEDIUM -> "Prioridad media"
                        Priority.LOW -> "Prioridad baja"
                    },
                    background = priorityColor.copy(alpha = 0.12f),
                    contentColor = priorityColor
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
