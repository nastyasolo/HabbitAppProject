package com.example.habittrackerapp.data.remote.model

import com.google.firebase.Timestamp

data class FirestoreOnlyHabit(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val type: String = "DAILY", // "DAILY" или "WEEKLY"
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val reminderTime: String? = null,
    val hasReminder: Boolean = false,
    val reminderDays: List<String> = emptyList(),
    val targetDays: List<String> = emptyList(), // Для WEEKLY привычек
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis(),
    val lastCompleted: String? = null, // Формат: "2024-12-11"
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val reminderId: String? = null
) {
    constructor() : this(id = "", userId = "", name = "", description = "", type = "DAILY")
}