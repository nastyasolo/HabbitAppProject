package com.example.habittrackerapp.data.util

import com.example.habittrackerapp.domain.model.*
import java.time.LocalDate
import java.util.*

object TestDataGenerator {

    fun generateTestHabits(count: Int, userId: String = "test_user"): List<Pair<Habit, List<HabitCompletion>>> {
        val result = mutableListOf<Pair<Habit, List<HabitCompletion>>>()

        for (i in 1..count) {
            val habitId = UUID.randomUUID().toString()
            val isWeekly = i % 2 == 0

            val habit = Habit(
                id = habitId,
                name = getRandomHabitName(i),
                description = getRandomDescription(i),
                type = if (isWeekly) HabitType.WEEKLY else HabitType.DAILY,
                priority = getRandomPriority(i),
                targetDays = if (isWeekly) getRandomTargetDays() else emptyList(),
                currentStreak = 0, // Будет рассчитано из выполнений
                longestStreak = 0,
                createdAt = System.currentTimeMillis() - (i * 86400000L), // Разные даты создания
                syncStatus = SyncStatus.SYNCED
            )

            // Генерируем выполнения за последние 14 дней
            val completions = generateCompletionsForHabit(habitId, habit, daysBack = 14)

            // Рассчитываем стрик на основе выполнений с помощью StreakCalculator
            val completedDates = completions.filter { it.completed }.map { it.date }.toSet()
            val (currentStreak, lastCompleted) = StreakCalculator.calculateStreak(habit, completedDates)

            val finalHabit = habit.copy(
                currentStreak = currentStreak,
                longestStreak = currentStreak,
                lastCompleted = lastCompleted
            )

            result.add(Pair(finalHabit, completions))
        }

        return result
    }

    private fun getRandomHabitName(index: Int): String {
        val names = listOf(
            "Утренняя зарядка",
            "Чтение книги",
            "Медитация",
            "Прогулка на свежем воздухе",
            "Изучение английского",
            "Пить воду",
            "Запись в дневник",
            "Уборка в комнате",
            "Планирование дня",
            "Отдых от гаджетов"
        )
        return if (index <= names.size) names[index - 1] else "Привычка $index"
    }

    private fun getRandomDescription(index: Int): String {
        val descriptions = listOf(
            "15 минут утренней разминки",
            "20 страниц в день",
            "10 минут медитации для спокойствия",
            "30 минут прогулки",
            "Новые слова и грамматика",
            "2 литра воды в день",
            "Запись мыслей и идей",
            "Поддержание порядка",
            "Составление плана на день",
            "Цифровой детокс 1 час"
        )
        return if (index <= descriptions.size) descriptions[index - 1] else "Описание привычки $index"
    }

    private fun getRandomPriority(index: Int): Priority {
        return when (index % 3) {
            0 -> Priority.LOW
            1 -> Priority.MEDIUM
            else -> Priority.HIGH
        }
    }

    private fun getRandomTargetDays(): List<DayOfWeek> {
        val allDays = DayOfWeek.values().toList()
        return allDays.shuffled().take(3).sortedBy { it.ordinal }
    }

    private fun generateCompletionsForHabit(
        habitId: String,
        habit: Habit,
        daysBack: Int
    ): List<HabitCompletion> {
        val completions = mutableListOf<HabitCompletion>()

        for (day in 0..daysBack) {
            val date = LocalDate.now().minusDays(day.toLong())

            val shouldComplete = when (habit.type) {
                HabitType.DAILY -> {
                    // Для ежедневных: выполняем 70% дней
                    (0..100).random() < 70
                }
                HabitType.WEEKLY -> {
                    // Для еженедельных: выполняем только в целевые дни и в 80% случаев
                    val dayOfWeek = DayOfWeek.fromInt(date.dayOfWeek.value)
                    habit.targetDays.contains(dayOfWeek) && (0..100).random() < 80
                }
            }

            if (shouldComplete) {
                val completion = HabitCompletion(
                    id = UUID.randomUUID().toString(),
                    habitId = habitId,
                    date = date,
                    completed = true,
                    completedAt = System.currentTimeMillis() - (day * 86400000L),
                    syncStatus = SyncStatus.SYNCED
                )
                completions.add(completion)
            }
        }

        return completions
    }
}