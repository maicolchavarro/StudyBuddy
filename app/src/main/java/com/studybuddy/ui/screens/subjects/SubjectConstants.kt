package com.studybuddy.ui.screens.subjects

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SubjectColor(val name: String, val color: Color, val hex: Int)
data class SubjectIcon(val name: String, val icon: ImageVector)

val SubjectColors = listOf(
    SubjectColor("Azul", Color(0xFF2196F3), 0xFF2196F3.toInt()),
    SubjectColor("Rojo", Color(0xFFF44336), 0xFFF44336.toInt()),
    SubjectColor("Verde", Color(0xFF4CAF50), 0xFF4CAF50.toInt()),
    SubjectColor("Morado", Color(0xFF9C27B0), 0xFF9C27B0.toInt()),
    SubjectColor("Gris", Color(0xFF9E9E9E), 0xFF9E9E9E.toInt())
)

val SubjectIcons = listOf(
    SubjectIcon("Matemáticas", Icons.Default.Functions),
    SubjectIcon("Historia", Icons.Default.HistoryEdu),
    SubjectIcon("Física", Icons.Default.Science),
    SubjectIcon("Programación", Icons.Default.Code),
    SubjectIcon("Biología", Icons.Default.Psychology), // Or any other suitable icon
    SubjectIcon("General", Icons.Default.Book)
)

fun getIconByName(name: String): ImageVector {
    return SubjectIcons.find { it.name == name }?.icon ?: Icons.Default.Book
}
