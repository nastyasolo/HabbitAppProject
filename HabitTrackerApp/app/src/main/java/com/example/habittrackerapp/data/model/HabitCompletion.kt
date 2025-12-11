package com.example.habittrackerapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),  // Индекс для внешнего ключа
        Index(value = ["date"]),     // Индекс для поиска по дате
        Index(value = ["habitId", "date"], unique = true)  // Уникальный индекс: одна запись на день
    ]
)
data class HabitCompletion(
    @PrimaryKey
    val id: String,
    val habitId: String,
    val date: String,  // Формат: "2024-12-11"
    val completed: Boolean = true,
    val completedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING",
    val lastSynced: Long? = null
)