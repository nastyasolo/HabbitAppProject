package com.example.habittrackerapp.domain.model

data class Habit(
    val id: String,
    val name: String,
    val description: String = "",
    val type: HabitType = HabitType.DAILY,
    val streak: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderTime: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General"
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