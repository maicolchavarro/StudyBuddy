package com.studybuddy.ui.screens.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.studybuddy.domain.model.StatisticsModel
import com.studybuddy.domain.model.SubjectLoadStats
import com.studybuddy.domain.model.WeeklyTaskStats
import com.studybuddy.ui.components.StudyBuddyBrandHeader
import com.studybuddy.ui.components.StudyBuddyMetricCard
import com.studybuddy.ui.components.StudyBuddyOverflowButton
import com.studybuddy.ui.viewmodel.StatisticsViewModel

@Composable
fun StatisticsScreen(
    onLogout: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.statistics.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val subjectStats by viewModel.subjectStats.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
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
                title = "Estadísticas",
                subtitle = "Tu rendimiento académico y consistencia semanal",
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

        stats?.let { currentStats ->
            item {
                StudyBuddyMetricCard(
                    title = "Resumen semanal",
                    value = "${(currentStats.completionPercentage * 100).toInt()}% productivo",
                    supportingText = "Racha actual: ${currentStats.currentStreak} días • mejor día: ${currentStats.mostProductiveDay}",
                    icon = Icons.Default.Speed,
                    emphasized = true
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StudyBuddyMetricCard(
                        title = "Completadas",
                        value = currentStats.completedTasks.toString(),
                        supportingText = "Tareas resueltas",
                        icon = Icons.Default.CheckCircle,
                        accentColor = Color(0xFF4B9B5C),
                        modifier = Modifier.weight(1f)
                    )
                    StudyBuddyMetricCard(
                        title = "Pendientes",
                        value = currentStats.pendingTasks.toString(),
                        supportingText = "Aún activas",
                        icon = Icons.Default.PieChart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                DonutAndSummaryCard(currentStats)
            }
        }

        if (weeklyStats.isNotEmpty()) {
            item {
                WeeklyBarCard(weeklyStats)
            }
        }

        if (subjectStats.isNotEmpty()) {
            item {
                SubjectDistributionCard(subjectStats)
            }
        }

        stats?.let { currentStats ->
            item {
                AchievementsCard(currentStats)
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun DonutAndSummaryCard(stats: StatisticsModel) {
    val progress by animateFloatAsState(
        targetValue = stats.completionPercentage.coerceIn(0f, 1f),
        animationSpec = tween(900),
        label = "completion"
    )
    val progressColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(124.dp)) {
                    drawArc(
                        color = Color(0xFFE7EBF8),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360 * progress,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(stats.completionPercentage * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Hecho",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(18.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Resumen de tareas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                StatLine("Completadas", "${stats.completedTasks} tareas", Color(0xFF4B9B5C))
                StatLine("Pendientes", "${stats.pendingTasks} tareas", MaterialTheme.colorScheme.primary)
                StatLine("Exámenes", "${stats.totalExams} programados", Color(0xFFD94141))
            }
        }
    }
}

@Composable
private fun StatLine(title: String, value: String, bulletColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(bulletColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WeeklyBarCard(data: List<WeeklyTaskStats>) {
    val maxValue = data.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Actividad semanal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { item ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(((item.count.toFloat() / maxValue) * 112).dp.coerceAtLeast(10.dp))
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectDistributionCard(data: List<SubjectLoadStats>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Distribución de enfoque",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(18.dp))
            data.forEach { item ->
                Text(
                    text = item.subjectName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(Color(0xFFE7EBF8), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((item.count / (data.maxOf { it.count }.toFloat())).coerceIn(0.08f, 1f))
                            .height(10.dp)
                            .background(Color(item.color), CircleShape)
                    )
                }
                Text(
                    text = "${item.count} actividades",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun AchievementsCard(stats: StatisticsModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Logros destacados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            AchievementRow(
                icon = Icons.Default.LocalFireDepartment,
                title = "Racha",
                value = "${stats.currentStreak} días"
            )
            AchievementRow(
                icon = Icons.Default.Star,
                title = "Mejor día",
                value = stats.mostProductiveDay
            )
            AchievementRow(
                icon = Icons.Default.PieChart,
                title = "Cumplimiento semanal",
                value = "${(stats.weeklyCompletionRate * 100).toInt()}%"
            )
        }
    }
}

@Composable
private fun AchievementRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
