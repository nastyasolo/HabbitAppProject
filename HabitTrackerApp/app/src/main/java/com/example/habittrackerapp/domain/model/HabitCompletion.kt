package com.example.habittrackerapp.domain.model

import java.time.LocalDate
import java.util.*

data class HabitCompletion(
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: LocalDate,
    val completed: Boolean = true,
    val completedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastSynced: Long? = null
)

data class HabitWithCompletions(
    val habit: Habit,
    val completions: List<HabitCompletion>
) {
    // Вычисляемые свойства
//    val currentStreak: Int
//        get() = calculateCurrentStreak()

    val completedToday: Boolean
        get() = completions.any { it.completed && it.date == LocalDate.now() }

    val lastCompletedDate: LocalDate?
        get() = completions.filter { it.completed }
            .map { it.date }
            .maxOrNull()

    private fun calculateCurrentStreak(): Int {
        val completedDates = completions
            .filter { it.completed }
            .map { it.date }
            .toSet()

        return when (habit.type) {
            HabitType.DAILY -> calculateDailyStreak(completedDates)
            HabitType.WEEKLY -> calculateWeeklyStreak(completedDates)
        }
    }

    private fun calculateDailyStreak(completedDates: Set<LocalDate>): Int {
        var streak = 0
        var currentDate = LocalDate.now()

        while (completedDates.contains(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }

        // Проверяем, есть ли выполнение вчера, если сегодня нет
        if (streak == 0 && completedDates.contains(LocalDate.now().minusDays(1))) {
            streak = 1
        }

        return streak
    }

    private fun calculateWeeklyStreak(completedDates: Set<LocalDate>): Int {
        val targetDaysSet = habit.targetDays.toSet()
        if (targetDaysSet.isEmpty()) return 0

        var streak = 0
        var weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY)

        while (true) {
            val weekEnd = weekStart.plusDays(6)
            val weekCompletedDates = completedDates.filter {
                it >= weekStart && it <= weekEnd
            }

            val completedDaysInWeek = weekCompletedDates.map { date ->
                DayOfWeek.fromInt(date.dayOfWeek.value)
            }.toSet()

            val isWeekCompleted = targetDaysSet.all { it in completedDaysInWeek }

            if (isWeekCompleted) {
                streak++
                weekStart = weekStart.minusWeeks(1)
            } else {
                break
            }
        }

        return streak
    }
}