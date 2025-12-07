package com.example.habittrackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habittrackerapp.domain.model.SyncStatus

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",
    val type: String = "DAILY",  // Изменяем HabitType -> String
    val streak: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderTime: String? = null,
    val priority: String = "MEDIUM",  // Изменяем Priority -> String
    val category: String = "General",
    val lastCompleted: Long? = null,
    val syncStatus: String = SyncStatus.PENDING.name,  // Добавляем syncStatus как String
    val lastSynced: Long? = null  // Добавляем lastSynced
)

// Эти enum'ы нужно вынести в domain слой, но оставим пока здесь
// Вместо этого, удалите отсюда HabitType и Priority, они должны быть в domain

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