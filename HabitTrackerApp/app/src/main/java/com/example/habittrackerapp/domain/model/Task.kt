package com.example.habittrackerapp.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: Category? = null,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

//enum class Priority {
//    LOW, MEDIUM, HIGH
//}

enum class Category {
    WORK, PERSONAL, HEALTH, EDUCATION, FINANCE, OTHER
}

fun getPriorityColor(priority: Priority): Long {
    return when (priority) {
        Priority.LOW -> 0xFF4CAF50 // Green
        Priority.MEDIUM -> 0xFFFF9800 // Orange
        Priority.HIGH -> 0xFFF44336 // Red
    }
}

fun isTaskOverdue(task: Task): Boolean {
    return task.dueDate?.isBefore(LocalDate.now()) == true && !task.isCompleted
}

fun isTaskDueToday(task: Task): Boolean {
    return task.dueDate?.isEqual(LocalDate.now()) == true
}

fun isTaskDueTomorrow(task: Task): Boolean {
    return task.dueDate?.isEqual(LocalDate.now().plusDays(1)) == true
}