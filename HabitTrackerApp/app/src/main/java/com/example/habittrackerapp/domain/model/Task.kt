package com.example.habittrackerapp.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null, // timestamp дедлайна
    val priority: Priority = Priority.MEDIUM,
    val tags: List<String> = emptyList()
)