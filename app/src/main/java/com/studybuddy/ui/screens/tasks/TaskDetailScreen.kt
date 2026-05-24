package com.studybuddy.ui.screens.tasks

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.TaskType
import com.studybuddy.ui.components.StudyBuddyDetailHeader
import com.studybuddy.ui.components.StudyBuddyInfoPill
import com.studybuddy.ui.screens.subjects.getIconByName
import com.studybuddy.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskDetailScreen(
    taskId: Int,
    onBack: () -> Unit,
    onEditTask: (Int) -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val selectedTask by viewModel.selectedTask.collectAsState()
    val dateFormatter = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())

    LaunchedEffect(taskId) {
        viewModel.getTaskById(taskId)
    }

    selectedTask?.let { item ->
        val task = item.task
        val subject = item.subject
        val priorityColor = when (task.priority) {
            Priority.HIGH -> Color(0xFFD94141)
            Priority.MEDIUM -> Color(0xFFF2A33B)
            Priority.LOW -> Color(0xFF5E84F3)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            StudyBuddyDetailHeader(
                title = task.title,
                subtitle = subject.name,
                onBack = onBack,
                trailingContent = {
                    Row {
                        IconButton(onClick = { onEditTask(task.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar tarea")
                        }
                        IconButton(
                            onClick = {
                                viewModel.deleteTask(task)
                                onBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar tarea",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StudyBuddyInfoPill(
                    text = when (task.priority) {
                        Priority.HIGH -> "Prioridad alta"
                        Priority.MEDIUM -> "Prioridad media"
                        Priority.LOW -> "Prioridad baja"
                    },
                    background = priorityColor.copy(alpha = 0.14f),
                    contentColor = priorityColor
                )
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
                if (task.isCompleted) {
                    StudyBuddyInfoPill(
                        text = "Completada",
                        background = Color(0xFFE6F4EA),
                        contentColor = Color(0xFF4B9B5C)
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subject.teacherName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    label = "Fecha y hora",
                    title = dateFormatter.format(Date(task.dueDate)).replaceFirstChar { it.uppercase() },
                    subtitle = task.dueTime
                )
                DetailCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    label = "Recordatorio",
                    title = "${task.reminderMinutes} min",
                    subtitle = "Antes de la entrega"
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Notas de estudio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = task.description ?: "No se registraron notas adicionales para esta actividad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Mantén foco en esta entrega. Si la completas ahora, tus estadísticas semanales se actualizan al instante.",
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    viewModel.toggleTaskCompletion(task.id, !task.isCompleted)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.primary,
                    contentColor = if (task.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (task.isCompleted) "Marcar como pendiente" else "Marcar como completada",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DetailCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
