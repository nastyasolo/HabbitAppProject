package com.example.habittrackerapp.domain.model

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val type: HabitType = HabitType.DAILY,
    val priority: Priority = Priority.MEDIUM,
    val reminderTime: String? = null,
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastCompleted: Long? = null,
    val category: String = "General",
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastSynced: Long? = null
) {
    companion object {
        fun create(
            name: String,
            description: String = "",
            type: HabitType = HabitType.DAILY,
            priority: Priority = Priority.MEDIUM,
            reminderTime: String? = null,
            category: String = "General"
        ): Habit {
            return Habit(
                name = name,
                description = description,
                type = type,
                priority = priority,
                reminderTime = reminderTime,
                category = category
            )
        }
    }
}
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
    LOW {
        override val displayName: String get() = "Низкий"
    },
    MEDIUM {
        override val displayName: String get() = "Средний"
    },
    HIGH {
        override val displayName: String get() = "Высокий"
    };

    abstract val displayName: String
}