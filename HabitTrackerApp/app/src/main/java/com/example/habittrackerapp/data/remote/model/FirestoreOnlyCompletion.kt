package com.example.habittrackerapp.data.remote.model

data class FirestoreOnlyCompletion(
    val id: String = "",
    val habitId: String = "",
    val userId: String = "",
    val date: String = "", // Формат: "2025-10-01"
    val completed: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
) {
    constructor() : this(id = "", habitId = "", userId = "", date = "")
}