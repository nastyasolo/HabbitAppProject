package com.example.habittrackerapp.domain.model

import java.time.LocalDate
import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val type: HabitType = HabitType.DAILY,
    val priority: Priority = Priority.MEDIUM,
    val reminderTime: String? = null,
    val targetDays: List<DayOfWeek> = emptyList(),  // Для WEEKLY привычек
    val createdAt: Long = System.currentTimeMillis(),
    val category: String = "General",
    // Эти поля теперь вычисляются из истории
    val lastCompleted: LocalDate? = null,
    val currentStreak: Int = 0,  // Кэшированный стрик
    val longestStreak: Int = 0,  // Самый длинный стрик
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastSynced: Long? = null,

    val hasReminder: Boolean = false, //  флаг напоминания
    val reminderId: String = UUID.randomUUID().toString(),
    val reminderDays: List<DayOfWeek> = emptyList(), //  дни для WEEKLY
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                (type != HabitType.WEEKLY || targetDays.isNotEmpty()) &&
                (!hasReminder || reminderTime != null) &&
                (reminderDays.isEmpty() || reminderDays.all { it in targetDays })
//    val hasActiveReminder: Boolean
//        get() = reminderTime != null && reminderId != null
    companion object {
        fun create(
            name: String,
            description: String = "",
            type: HabitType = HabitType.DAILY,
            priority: Priority = Priority.MEDIUM,
            reminderTime: String? = null,
            category: String = "General",
            targetDays: List<DayOfWeek> = emptyList()
        ): Habit {
            return Habit(
                name = name,
                description = description,
                type = type,
                priority = priority,
                reminderTime = reminderTime,
                category = category,
                targetDays = targetDays
            )
        }
    }
}

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    companion object {
        fun fromInt(day: Int): DayOfWeek {
            return when (day) {
                1 -> MONDAY
                2 -> TUESDAY
                3 -> WEDNESDAY
                4 -> THURSDAY
                5 -> FRIDAY
                6 -> SATURDAY
                7 -> SUNDAY
                else -> MONDAY
            }
        }

        fun toInt(day: DayOfWeek): Int {
            return when (day) {
                MONDAY -> 1
                TUESDAY -> 2
                WEDNESDAY -> 3
                THURSDAY -> 4
                FRIDAY -> 5
                SATURDAY -> 6
                SUNDAY -> 7
            }
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