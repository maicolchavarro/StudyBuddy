package com.studybuddy.ui.screens.calendar

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.studybuddy.domain.model.TaskType
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.ui.components.StudyBuddyBrandHeader
import com.studybuddy.ui.components.StudyBuddyInfoPill
import com.studybuddy.ui.components.StudyBuddyOverflowButton
import com.studybuddy.ui.screens.subjects.getIconByName
import com.studybuddy.ui.viewmodel.CalendarViewModel
import com.studybuddy.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    onNavigateToTaskDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    val tasks by calendarViewModel.tasks.collectAsState()
    val filterType by calendarViewModel.filterType.collectAsState()
    val filterStatus by calendarViewModel.filterStatus.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    val groupedTasks = tasks.groupBy { it.subject.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(18.dp))
            StudyBuddyBrandHeader(
                title = "Calendario semanal",
                subtitle = "Visualiza tus actividades por fecha y por materia",
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
            CalendarOverviewCard(
                selectedDate = selectedDate,
                onPreviousWeek = { calendarViewModel.previousWeek() },
                onNextWeek = { calendarViewModel.nextWeek() }
            )
        }

        item {
            WeekDaySelector(
                selectedDate = selectedDate,
                onDateSelected = { calendarViewModel.onDateSelected(it) }
            )
        }

        item {
            FilterSection(
                selectedType = filterType,
                onTypeSelected = { calendarViewModel.setFilterType(it) },
                selectedStatus = filterStatus,
                onStatusSelected = { calendarViewModel.setFilterStatus(it) }
            )
        }

        if (groupedTasks.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "No hay actividades para este día",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = "Prueba con otra fecha o cambia los filtros activos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        } else {
            groupedTasks.values.forEach { subjectTasks ->
                item(key = "group_${subjectTasks.first().subject.id}") {
                    CalendarSubjectGroup(
                        tasks = subjectTasks,
                        onNavigateToTaskDetail = onNavigateToTaskDetail,
                        onToggleCompletion = { taskId, isCompleted ->
                            taskViewModel.toggleTaskCompletion(taskId, isCompleted)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(34.dp))
        }
    }
}

@Composable
private fun CalendarOverviewCard(
    selectedDate: Calendar,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val startOfWeek = (selectedDate.clone() as Calendar).apply { set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }
    val endOfWeek = (startOfWeek.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 6) }
    val weekRange = "${SimpleDateFormat("dd MMM", Locale.getDefault()).format(startOfWeek.time)} - " +
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(endOfWeek.time)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPreviousWeek) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Semana anterior")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = weekRange,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate.time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onNextWeek) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Semana siguiente")
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = SimpleDateFormat("EEE", Locale.getDefault()).format(selectedDate.time).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = selectedDate.get(Calendar.DAY_OF_MONTH).toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun WeekDaySelector(
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    val calendar = selectedDate.clone() as Calendar
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val days = (0..6).map {
        val date = calendar.clone() as Calendar
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        date
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { date ->
            val isSelected = isSameDay(date, selectedDate)
            Surface(
                modifier = Modifier.width(44.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                onClick = { onDateSelected(date) }
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = SimpleDateFormat("EE", Locale.getDefault()).format(date.time).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = date.get(Calendar.DAY_OF_MONTH).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    selectedType: TaskType?,
    onTypeSelected: (TaskType?) -> Unit,
    selectedStatus: Boolean?,
    onStatusSelected: (Boolean?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            FilterPill(
                text = "Todo",
                selected = selectedType == null && selectedStatus == null,
                onClick = {
                    onTypeSelected(null)
                    onStatusSelected(null)
                }
            )
        }
        item {
            FilterPill(
                text = "Tareas",
                selected = selectedType == TaskType.TASK,
                icon = Icons.AutoMirrored.Filled.Assignment,
                onClick = { onTypeSelected(if (selectedType == TaskType.TASK) null else TaskType.TASK) }
            )
        }
        item {
            FilterPill(
                text = "Exámenes",
                selected = selectedType == TaskType.EXAM,
                icon = Icons.Default.School,
                onClick = { onTypeSelected(if (selectedType == TaskType.EXAM) null else TaskType.EXAM) }
            )
        }
        item {
            FilterPill(
                text = "Pendientes",
                selected = selectedStatus == false,
                onClick = { onStatusSelected(if (selectedStatus == false) null else false) }
            )
        }
        item {
            FilterPill(
                text = "Completadas",
                selected = selectedStatus == true,
                icon = Icons.Default.CheckCircle,
                onClick = { onStatusSelected(if (selectedStatus == true) null else true) }
            )
        }
    }
}

@Composable
private fun FilterPill(
    text: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CalendarSubjectGroup(
    tasks: List<TaskWithSubject>,
    onNavigateToTaskDetail: (Int) -> Unit,
    onToggleCompletion: (Int, Boolean) -> Unit
) {
    val subject = tasks.first().subject

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(subject.color).copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconByName(subject.icon),
                    contentDescription = null,
                    tint = Color(subject.color)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(10.dp))
            StudyBuddyInfoPill(
                text = "${tasks.size} actividades",
                background = Color(subject.color).copy(alpha = 0.12f),
                contentColor = Color(subject.color)
            )
        }

        tasks.forEach { task ->
            CalendarTaskCard(
                taskWithSubject = task,
                onClick = { onNavigateToTaskDetail(task.task.id) },
                onToggleCompletion = {
                    onToggleCompletion(task.task.id, !task.task.isCompleted)
                }
            )
        }
    }
}

@Composable
private fun CalendarTaskCard(
    taskWithSubject: TaskWithSubject,
    onClick: () -> Unit,
    onToggleCompletion: () -> Unit
) {
    val task = taskWithSubject.task
    val priorityColor = when (task.priority) {
        com.studybuddy.domain.model.Priority.HIGH -> Color(0xFFD94141)
        com.studybuddy.domain.model.Priority.MEDIUM -> Color(0xFFF2A33B)
        com.studybuddy.domain.model.Priority.LOW -> Color(0xFF5E84F3)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
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
                    .width(5.dp)
                    .height(60.dp)
                    .clip(CircleShape)
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StudyBuddyInfoPill(
                        text = if (task.taskType == TaskType.EXAM) "Examen" else "Tarea",
                        background = if (task.taskType == TaskType.EXAM) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        contentColor = if (task.taskType == TaskType.EXAM) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.dueTime,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            IconButton(onClick = onToggleCompletion) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = "Actualizar estado",
                    tint = if (task.isCompleted) Color(0xFF4B9B5C) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
