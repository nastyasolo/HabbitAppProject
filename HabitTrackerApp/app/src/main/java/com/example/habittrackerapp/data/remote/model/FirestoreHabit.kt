package com.example.habittrackerapp.data.remote.model

data class FirestoreHabit(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val type: String = "DAILY",
    val priority: String = "MEDIUM",
    val reminderTime: String? = null,
    val targetDays: List<String> = emptyList(),
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis(),
    val lastCompleted: String? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)

data class FirestoreCompletion(
    val id: String = "",
    val habitId: String = "",
    val userId: String = "",
    val date: String = "",  // "2024-12-11"
    val completed: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)