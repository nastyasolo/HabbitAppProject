package com.example.habittrackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",
    val type: String = "DAILY",
    val priority: String = "MEDIUM",
    val reminderTime: String? = null,
    val targetDays: String = "",  // JSON список дней для WEEKLY
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis(),
    // Кэшированные поля (вычисляются из истории)
    val lastCompleted: String? = null,  // Формат: "2024-12-11"
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val syncStatus: String = "PENDING",
    val lastSynced: Long? = null,

    val reminderId: String = "",
    val hasReminder: Boolean = false,
    val reminderDays: String = "",
)


//enum class HabitType {
//    DAILY {
//        override val displayName: String get() = "Ежедневная"
//    },
//    WEEKLY {
//        override val displayName: String get() = "Еженедельная"
//    };
//
//    abstract val displayName: String
//}
//
//enum class Priority {
//    LOW, MEDIUM, HIGH
//}