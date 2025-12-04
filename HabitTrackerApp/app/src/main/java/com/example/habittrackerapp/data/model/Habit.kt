package com.example.habittrackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",
    val type: HabitType = HabitType.DAILY,
    val streak: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderTime: String? = null,
    val priority: Priority = Priority.MEDIUM
)

enum class HabitType {
    DAILY {
        override val displayName: String get() = "Ежедневная"
    },
    WEEKLY {
        override val displayName: String get() = "Еженедельная"
    };

    abstract val displayName: String
}

enum class Priority {
    LOW, MEDIUM, HIGH
}