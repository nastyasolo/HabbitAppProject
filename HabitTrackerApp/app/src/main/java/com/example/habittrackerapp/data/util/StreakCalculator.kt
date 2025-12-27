package com.example.habittrackerapp.data.util

import com.example.habittrackerapp.domain.model.DayOfWeek
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import java.time.LocalDate

object StreakCalculator {

    fun calculateDailyStreak(completedDates: Set<LocalDate>): Pair<Int, LocalDate?> {
        var currentStreak = 0
        var currentDate = LocalDate.now()
        val sortedDates = completedDates.sortedDescending()
        val lastCompleted = sortedDates.firstOrNull()

        println("DEBUG: [StreakCalculator] calculateDailyStreak для дат: $completedDates")
        println("DEBUG: [StreakCalculator] Сегодня: $currentDate")
        println("DEBUG: [StreakCalculator] Последнее выполнение: $lastCompleted")

        // Если сегодня выполнено
        if (completedDates.contains(currentDate)) {
            println("DEBUG: [StreakCalculator] Сегодня выполнено - считаем включая сегодня")
            // Считаем стрик с сегодняшнего дня назад
            while (completedDates.contains(currentDate)) {
                currentStreak++
                currentDate = currentDate.minusDays(1)
            }
        } else {
            println("DEBUG: [StreakCalculator] Сегодня НЕ выполнено - считаем только прошлые дни")
            // Сегодня не выполнено, начинаем с вчера
            currentDate = LocalDate.now().minusDays(1)

            // Считаем выполненные дни в прошлом
            while (completedDates.contains(currentDate)) {
                currentStreak++
                currentDate = currentDate.minusDays(1)
            }

            // Если стрик = 0 и вчера было выполнено, то стрик = 1
            if (currentStreak == 0 && completedDates.contains(LocalDate.now().minusDays(1))) {
                currentStreak = 1
            }
        }

        println("DEBUG: [StreakCalculator] Рассчитанный стрик: $currentStreak")
        return Pair(currentStreak, lastCompleted)
    }

    fun calculateWeeklyStreak(habit: Habit, completedDates: Set<LocalDate>): Pair<Int, LocalDate?> {
        val targetDaysSet = habit.targetDays.toSet()
        if (targetDaysSet.isEmpty()) return Pair(0, null)

        println("DEBUG: [StreakCalculator] calculateWeeklyStreak для привычки: ${habit.name}")
        println("DEBUG: [StreakCalculator] Целевые дни: $targetDaysSet")
        println("DEBUG: [StreakCalculator] Выполненные даты: $completedDates")

        var currentStreak = 0
        var weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
        val lastCompleted = completedDates.maxOrNull()

        // Проверяем, должна ли привычка выполняться на текущей неделе
        val currentWeekShouldBeCompleted = targetDaysSet.any { day ->
            val dayValue = when (day) {
                DayOfWeek.MONDAY -> 1
                DayOfWeek.TUESDAY -> 2
                DayOfWeek.WEDNESDAY -> 3
                DayOfWeek.THURSDAY -> 4
                DayOfWeek.FRIDAY -> 5
                DayOfWeek.SATURDAY -> 6
                DayOfWeek.SUNDAY -> 7
            }
            val weekDay = weekStart.plusDays((dayValue - 1).toLong())
            weekDay <= LocalDate.now() // День уже прошел
        }

        println("DEBUG: [StreakCalculator] Текущая неделя должна быть завершена: $currentWeekShouldBeCompleted")

        while (true) {
            val weekEnd = weekStart.plusDays(6)
            val weekCompletedDates = completedDates.filter {
                it >= weekStart && it <= weekEnd
            }

            val completedDaysInWeek = weekCompletedDates.map { date ->
                DayOfWeek.fromInt(date.dayOfWeek.value)
            }.toSet()

            val isWeekCompleted = targetDaysSet.all { it in completedDaysInWeek }

            println("DEBUG: [StreakCalculator] Неделя $weekStart - $weekEnd")
            println("DEBUG: [StreakCalculator] Выполнено в неделе: $completedDaysInWeek")
            println("DEBUG: [StreakCalculator] Неделя завершена: $isWeekCompleted")

            // Для текущей недели разрешаем неполное выполнение
            val isCurrentWeek = weekStart <= LocalDate.now() && weekEnd >= LocalDate.now()

            if (isWeekCompleted || (isCurrentWeek && !currentWeekShouldBeCompleted)) {
                currentStreak++
                weekStart = weekStart.minusWeeks(1)
                println("DEBUG: [StreakCalculator] Увеличиваем стрик до: $currentStreak")
            } else {
                println("DEBUG: [StreakCalculator] Прерываем стрик на: $currentStreak")
                break
            }
        }

        return Pair(currentStreak, lastCompleted)
    }

    fun calculateStreak(habit: Habit, completedDates: Set<LocalDate>): Pair<Int, LocalDate?> {
        println("DEBUG: [StreakCalculator] calculateStreak для ${habit.type}")
        return when (habit.type) {
            HabitType.DAILY -> calculateDailyStreak(completedDates)
            HabitType.WEEKLY -> calculateWeeklyStreak(habit, completedDates)
        }
    }
}