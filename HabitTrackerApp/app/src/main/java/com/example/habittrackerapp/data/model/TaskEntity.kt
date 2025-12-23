package com.example.habittrackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habittrackerapp.domain.model.Category
import com.example.habittrackerapp.domain.model.Priority
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val dueDate: LocalDate?,
    val dueTime: LocalTime?,
    val priority: Priority,
    val category: Category?,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    val reminderTime: LocalTime?,
    val hasReminder: Boolean,
    val reminderId: String?
)