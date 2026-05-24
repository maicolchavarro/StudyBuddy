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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.studybuddy.domain.model.Subject
import com.studybuddy.domain.model.SubjectWithTaskCount
import com.studybuddy.ui.components.StudyBuddyBrandHeader
import com.studybuddy.ui.components.StudyBuddyInfoPill
import com.studybuddy.ui.components.StudyBuddyMetricCard
import com.studybuddy.ui.components.StudyBuddyOverflowButton
import com.studybuddy.ui.viewmodel.SubjectUiState
import com.studybuddy.ui.viewmodel.SubjectViewModel

@Composable
fun SubjectsScreen(
    viewModel: SubjectViewModel = hiltViewModel(),
    onNavigateToSubjectDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val subjectsWithCount by viewModel.subjects.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.error.collectAsState(initial = "")

    var showAddDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val totalPending = subjectsWithCount.sumOf { it.taskCount.toInt() }
    val busiestSubject = subjectsWithCount.maxByOrNull { it.taskCount }?.subject?.name ?: "Sin datos"

    LaunchedEffect(error) {
        if (error.isNotEmpty()) {
            snackbarHostState.showSnackbar(error)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is SubjectUiState.Success) {
            showAddDialog = false
            subjectToEdit = null
            viewModel.resetUiState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar materia")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                StudyBuddyBrandHeader(
                    title = "Tus materias",
                    subtitle = "Gestiona tus áreas de enfoque académico",
                    trailingContent = {
                        Box {
                            StudyBuddyOverflowButton(onClick = { showMenu = true })
                            androidx.compose.material3.DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                androidx.compose.material3.DropdownMenuItem(
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
                StudyBuddyMetricCard(
                    title = "Enfoque semanal",
                    value = "${subjectsWithCount.size} materias activas",
                    supportingText = if (subjectsWithCount.isEmpty()) {
                        "Empieza creando tu primera materia"
                    } else {
                        "$totalPending tareas pendientes • más carga en $busiestSubject"
                    },
                    emphasized = true
                )
            }

            if (subjectsWithCount.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay materias registradas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Usa el botón + para crear tu primera materia y conectar luego tus tareas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            } else {
                items(subjectsWithCount, key = { it.subject.id }) { item ->
                    SubjectCard(
                        subjectWithCount = item,
                        onOpen = { onNavigateToSubjectDetail(item.subject.id) },
                        onEdit = { subjectToEdit = it },
                        onDelete = { subjectToDelete = it }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    if (showAddDialog || subjectToEdit != null) {
        AddEditSubjectDialog(
            subject = subjectToEdit,
            onDismiss = {
                showAddDialog = false
                subjectToEdit = null
            },
            onConfirm = { name, teacher, color, icon ->
                if (subjectToEdit == null) {
                    viewModel.addSubject(name, teacher, color, icon)
                } else {
                    viewModel.updateSubject(
                        subjectToEdit!!.copy(
                            name = name,
                            teacherName = teacher,
                            color = color,
                            icon = icon
                        )
                    )
                }
            }
        )
    }

    if (subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Eliminar materia") },
            text = {
                Text(
                    "Se eliminará ${subjectToDelete?.name} junto con todas sus tareas asociadas."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSubject(subjectToDelete!!)
                        subjectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SubjectCard(
    subjectWithCount: SubjectWithTaskCount,
    onOpen: () -> Unit,
    onEdit: (Subject) -> Unit,
    onDelete: (Subject) -> Unit
) {
    val subject = subjectWithCount.subject

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
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
                    .width(6.dp)
                    .height(72.dp)
                    .clip(CircleShape)
                    .background(Color(subject.color))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
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
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    StudyBuddyInfoPill(
                        text = if (subjectWithCount.taskCount == 1L) {
                            "1 tarea"
                        } else {
                            "${subjectWithCount.taskCount} tareas"
                        },
                        background = Color(subject.color).copy(alpha = 0.12f),
                        contentColor = Color(subject.color)
                    )
                }
                Text(
                    text = subject.teacherName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onEdit(subject) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { onDelete(subject) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddEditSubjectDialog(
    subject: Subject? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(subject?.name ?: "") }
    var teacher by remember { mutableStateOf(subject?.teacherName ?: "") }
    var selectedColor by remember { mutableStateOf(subject?.color ?: SubjectColors.first().hex) }
    var selectedIcon by remember { mutableStateOf(subject?.icon ?: SubjectIcons.first().name) }

    val isFormValid = name.length >= 3 && teacher.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (subject == null) "Nueva materia" else "Editar materia",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la materia") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Docente") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SubjectColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color.color)
                                .clickable { selectedColor = color.hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == color.hex) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Icono",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SubjectIcons.forEach { icon ->
                        Surface(
                            onClick = { selectedIcon = icon.name },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedIcon == icon.name) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            }
                        ) {
                            Icon(
                                imageVector = icon.icon,
                                contentDescription = icon.name,
                                tint = if (selectedIcon == icon.name) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, teacher, selectedColor, selectedIcon) },
                enabled = isFormValid,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (subject == null) "Crear" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
