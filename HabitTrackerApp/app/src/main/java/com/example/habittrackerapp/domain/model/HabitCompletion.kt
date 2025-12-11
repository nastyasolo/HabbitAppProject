package com.example.habittrackerapp.domain.model

import java.time.LocalDate
import java.util.UUID

data class HabitCompletion(
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: LocalDate,  // Дата выполнения в формате "2024-12-11"
    val completed: Boolean = true,
    val completedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastSynced: Long? = null
)

data class HabitWithCompletions(
    val habit: Habit,
    val completions: List<HabitCompletion>
) {
    // Вычисляемый streak на основе истории
    val currentStreak: Int
        get() = calculateStreak(completions.sortedBy { it.date })

    // Выполнена ли привычка сегодня
    val completedToday: Boolean
        get() = completions.any {
            it.date == LocalDate.now() && it.completed
        }

    private fun calculateStreak(completions: List<HabitCompletion>): Int {
        var streak = 0
        var currentDate = LocalDate.now()
        val sortedCompletions = completions
            .filter { it.completed }
            .sortedByDescending { it.date }
            .map { it.date }

        for (completionDate in sortedCompletions) {
            if (completionDate == currentDate ||
                (streak == 0 && completionDate == currentDate.minusDays(1))) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }
}