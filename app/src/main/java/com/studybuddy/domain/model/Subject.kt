package com.studybuddy.domain.model

data class Subject(
    val id: Int = 0,
    val name: String,
    val teacherName: String,
    val color: Int,
    val icon: String,
    val createdAt: Long = System.currentTimeMillis()
)