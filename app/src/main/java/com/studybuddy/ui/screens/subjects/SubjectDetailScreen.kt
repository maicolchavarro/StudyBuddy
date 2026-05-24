package com.studybuddy.ui.screens.subjects

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.studybuddy.domain.model.Priority
import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.model.TaskWithSubject
import com.studybuddy.ui.components.StudyBuddyDetailHeader
import com.studybuddy.ui.components.StudyBuddyInfoPill
import com.studybuddy.ui.components.StudyBuddyMetricCard
import com.studybuddy.ui.components.StudyBuddyPillButton
import com.studybuddy.ui.viewmodel.SubjectDetailViewModel
import com.studybuddy.ui.viewmodel.SubjectViewModel

@Composable
fun SubjectDetailScreen(
    subjectId: Int,
    onBack: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onCreateTask: () -> Unit,
    detailViewModel: SubjectDetailViewModel = hiltViewModel(),
    subjectViewModel: SubjectViewModel = hiltViewModel()
) {
    val subject by detailViewModel.subject.collectAsState()
    val tasks by detailViewModel.tasks.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(subjectId) {
        detailViewModel.load(subjectId)
    }

    subject?.let { item ->
        val completedTasks = tasks.count { it.task.isCompleted }
        val progress = if (tasks.isEmpty()) 0 else ((completedTasks * 100f) / tasks.size).toInt()
        val upcomingTasks = tasks.filterNot { it.task.isCompleted }.take(4)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                StudyBuddyDetailHeader(
                    title = item.name,
                    subtitle = item.teacherName,
                    onBack = onBack,
                    trailingContent = {
                        Row {
                            IconButton(onClick = { showEditDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar materia")
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar materia",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )
            }

            item {
                StudyBuddyMetricCard(
                    title = "Progreso general",
                    value = "$progress%",
                    supportingText = if (tasks.isEmpty()) {
                        "Aún no hay tareas asociadas"
                    } else {
                        "$completedTasks de ${tasks.size} tareas completadas"
                    },
                    emphasized = true
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StudyBuddyMetricCard(
                        title = "Pendientes",
                        value = upcomingTasks.size.toString(),
                        supportingText = "Próximas entregas",
                        modifier = Modifier.weight(1f)
                    )
                    StudyBuddyMetricCard(
                        title = "Color",
                        value = "Materia activa",
                        supportingText = "Identidad visual asignada",
                        modifier = Modifier.weight(1f),
                        accentColor = Color(item.color)
                    )
                }
            }

            item {
                StudyBuddyPillButton(
                    text = "Crear tarea para esta materia",
                    icon = Icons.Default.Add,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCreateTask
                )
            }

            item {
                Text(
                    text = "Próximas tareas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (upcomingTasks.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "No hay tareas pendientes en esta materia.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(upcomingTasks, key = { it.task.id }) { task ->
                    SubjectTaskCard(
                        taskWithSubject = task,
                        onClick = { onNavigateToTaskDetail(task.task.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        if (showEditDialog) {
            AddEditSubjectDialog(
                subject = item,
                onDismiss = { showEditDialog = false },
                onConfirm = { name, teacher, color, icon ->
                    subjectViewModel.updateSubject(
                        item.copy(
                            name = name,
                            teacherName = teacher,
                            color = color,
                            icon = icon
                        )
                    )
                    showEditDialog = false
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar materia") },
                text = { Text("Se eliminará ${item.name} y todas sus tareas.") },
                confirmButton = {
                    Button(
                        onClick = {
                            subjectViewModel.deleteSubject(item)
                            showDeleteDialog = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun SubjectTaskCard(
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
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taskWithSubject.task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = taskWithSubject.task.dueTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StudyBuddyInfoPill(
                text = when (taskWithSubject.task.priority) {
                    Priority.HIGH -> "Alta"
                    Priority.MEDIUM -> "Media"
                    Priority.LOW -> "Baja"
                },
                background = priorityColor.copy(alpha = 0.12f),
                contentColor = priorityColor
            )
        }
    }
}
