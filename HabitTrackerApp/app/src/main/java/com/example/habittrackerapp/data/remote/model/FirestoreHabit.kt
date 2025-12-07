package com.example.habittrackerapp.data.remote.model

import com.google.firebase.firestore.PropertyName

data class FirestoreHabit(
    @PropertyName("id")
    val id: String = "",

    @PropertyName("userId")
    val userId: String = "",

    @PropertyName("name")
    val name: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("type")
    val type: String = "DAILY",

    @PropertyName("priority")
    val priority: String = "MEDIUM",

    @PropertyName("reminderTime")
    val reminderTime: String? = null,

    @PropertyName("isCompleted")
    val isCompleted: Boolean = false,

    @PropertyName("streak")
    val streak: Int = 0,

    @PropertyName("category")
    val category: String = "General",

    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @PropertyName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),

    @PropertyName("lastCompleted")
    val lastCompleted: Long? = null
)