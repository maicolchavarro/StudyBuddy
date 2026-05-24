package com.studybuddy.ui.screens.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.TaskType
import com.studybuddy.ui.components.StudyBuddyDetailHeader
import com.studybuddy.ui.components.StudyBuddyPillButton
import com.studybuddy.ui.viewmodel.SubjectViewModel
import com.studybuddy.ui.viewmodel.TaskUiState
import com.studybuddy.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CreateTaskScreen(
    onBack: () -> Unit,
    taskId: Int? = null,
    preselectedSubjectId: Int? = null,
    taskViewModel: TaskViewModel = hiltViewModel(),
    subjectViewModel: SubjectViewModel = hiltViewModel()
) {
    val subjectsWithCount by subjectViewModel.subjects.collectAsState()
    val subjects = subjectsWithCount.map { it.subject }
    val uiState by taskViewModel.uiState.collectAsState()
    val error by taskViewModel.error.collectAsState(initial = "")
    val selectedTask by taskViewModel.selectedTask.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableIntStateOf(0) }
    var dueDate by remember { mutableLongStateOf(0L) }
    var dueTime by remember { mutableStateOf("12:00") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var taskType by remember { mutableStateOf(TaskType.TASK) }
    var reminderMinutes by remember { mutableIntStateOf(60) }
    var isCompleted by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(taskId) {
        if (taskId != null) {
            taskViewModel.getTaskById(taskId)
        } else {
            taskViewModel.clearSelectedTask()
        }
    }

    LaunchedEffect(preselectedSubjectId, taskId) {
        if (taskId == null && selectedSubjectId == 0) {
            selectedSubjectId = preselectedSubjectId ?: 0
        }
    }

    LaunchedEffect(selectedTask, taskId) {
        val item = selectedTask ?: return@LaunchedEffect
        if (taskId != null && item.task.id == taskId) {
            title = item.task.title
            description = item.task.description.orEmpty()
            selectedSubjectId = item.subject.id
            dueDate = item.task.dueDate
            dueTime = item.task.dueTime
            priority = item.task.priority
            taskType = item.task.taskType
            reminderMinutes = item.task.reminderMinutes
            isCompleted = item.task.isCompleted
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is TaskUiState.Success) {
            taskViewModel.resetUiState()
            taskViewModel.clearSelectedTask()
            onBack()
        }
    }

    LaunchedEffect(error) {
        if (error.isNotEmpty()) {
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            StudyBuddyDetailHeader(
                title = if (taskId == null) "Nueva tarea" else "Editar tarea",
                subtitle = "Organiza tu estudio con claridad",
                onBack = onBack,
                trailingContent = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )

            if (subjects.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "Primero debes crear al menos una materia para asociar la tarea.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nombre de la tarea") },
                placeholder = { Text("Ej: Resolver guía de derivadas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Materia",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    subjects.forEach { subject ->
                        Surface(
                            onClick = { selectedSubjectId = subject.id },
                            shape = RoundedCornerShape(999.dp),
                            color = if (selectedSubjectId == subject.id) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            }
                        ) {
                            Text(
                                text = subject.name,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedSubjectId == subject.id) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = if (dueDate == 0L) "" else dateFormatter.format(Date(dueDate)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selected = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    dueDate = selected.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    enabled = false,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
                )

                OutlinedTextField(
                    value = dueTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    dueTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                },
                                12,
                                0,
                                true
                            ).show()
                        },
                    enabled = false,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) }
                )
            }

            SelectionSection(
                title = "Prioridad",
                options = listOf(
                    SelectionOption("Alta", Color(0xFFD94141), priority == Priority.HIGH) { priority = Priority.HIGH },
                    SelectionOption("Media", Color(0xFFF2A33B), priority == Priority.MEDIUM) { priority = Priority.MEDIUM },
                    SelectionOption("Baja", Color(0xFF5E84F3), priority == Priority.LOW) { priority = Priority.LOW }
                )
            )

            SelectionSection(
                title = "Tipo",
                options = listOf(
                    SelectionOption("Tarea", MaterialTheme.colorScheme.primary, taskType == TaskType.TASK) { taskType = TaskType.TASK },
                    SelectionOption("Examen", Color(0xFFD94141), taskType == TaskType.EXAM) { taskType = TaskType.EXAM }
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recordatorio",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(15, 30, 60, 120).forEach { minutes ->
                        Surface(
                            onClick = { reminderMinutes = minutes },
                            shape = RoundedCornerShape(999.dp),
                            color = if (reminderMinutes == minutes) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            }
                        ) {
                            Text(
                                text = "${minutes}m",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (reminderMinutes == minutes) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Notas") },
                placeholder = { Text("Detalles, enlaces o pasos clave para esta entrega") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                shape = RoundedCornerShape(18.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Text(
                    text = "\"El éxito es la suma de pequeños esfuerzos repetidos día tras día.\"",
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            StudyBuddyPillButton(
                text = if (taskId == null) "Guardar tarea" else "Guardar cambios",
                icon = Icons.Default.Check,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (taskId == null) {
                        taskViewModel.addTask(
                            title = title,
                            description = description.ifBlank { null },
                            subjectId = selectedSubjectId,
                            dueDate = dueDate,
                            dueTime = dueTime,
                            priority = priority,
                            taskType = taskType,
                            reminderMinutes = reminderMinutes
                        )
                    } else {
                        taskViewModel.updateTask(
                            taskId = taskId,
                            title = title,
                            description = description.ifBlank { null },
                            subjectId = selectedSubjectId,
                            dueDate = dueDate,
                            dueTime = dueTime,
                            priority = priority,
                            taskType = taskType,
                            isCompleted = isCompleted,
                            reminderMinutes = reminderMinutes
                        )
                    }
                }
            )

            if (uiState is TaskUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private data class SelectionOption(
    val label: String,
    val color: Color,
    val selected: Boolean,
    val onClick: () -> Unit
)

@Composable
private fun SelectionSection(
    title: String,
    options: List<SelectionOption>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                Surface(
                    onClick = option.onClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = if (option.selected) option.color.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (option.selected) option.color else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        text = option.label,
                        modifier = Modifier.padding(vertical = 14.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (option.selected) option.color else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
