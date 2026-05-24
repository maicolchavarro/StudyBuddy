package com.studybuddy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
    indices = [Index(value = ["name"], unique = true)]
)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val teacherName: String,
    val color: Int,
    val icon: String,
    val createdAt: Long = System.currentTimeMillis()
)