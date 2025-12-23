package com.example.habittrackerapp.domain.usecase

import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitWithCompletions
import com.example.habittrackerapp.domain.repository.HabitRepository
import com.example.habittrackerapp.utils.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HabitUseCases @Inject constructor(
    private val repository: HabitRepository,
    private val reminderScheduler: ReminderScheduler
) {

    // Получение всех привычек (invoke для совместимости с GetHabitsUseCase)
    operator fun invoke(): Flow<List<Habit>> = repository.getAllHabits()

    // Получение привычки по ID
    suspend fun getHabitById(id: String): Habit? = repository.getHabitById(id)

    // Получение привычки с её выполнениями
    suspend fun getHabitWithCompletions(id: String): HabitWithCompletions? =
        repository.getHabitWithCompletions(id)

    // Добавление привычки с напоминанием
    suspend fun insertHabit(habit: Habit) {
        repository.insertHabit(habit)
        if (habit.hasReminder) {
            reminderScheduler.scheduleHabitReminder(habit)  // ← ИЗМЕНИЛИ: через инстанс
        }
    }

    // Обновление привычки с напоминанием
    suspend fun updateHabit(habit: Habit) {
        // Отменяем старое напоминание
        reminderScheduler.cancelReminder(habit.id, true)  // ← ИЗМЕНИЛИ: через инстанс

        repository.updateHabit(habit)

        // Создаем новое напоминание
        if (habit.hasReminder) {
            reminderScheduler.scheduleHabitReminder(habit)  // ← ИЗМЕНИЛИ: через инстанс
        }
    }

    // Удаление привычки с напоминанием
    suspend fun deleteHabit(habit: Habit) {
        // Отменяем напоминание
        reminderScheduler.cancelReminder(habit.id, true)  // ← ИЗМЕНИЛИ: через инстанс
        repository.deleteHabit(habit)
    }

    // Переключение выполнения привычки
    suspend fun toggleHabitCompletion(id: String) = repository.toggleHabitCompletion(id)

    // Получение сегодняшних привычек
    suspend fun getTodayHabits(): List<Habit> = repository.getTodayHabits()

    // Получение выполненных дней для привычки
    suspend fun getHabitCompletions(habitId: String) = repository.getHabitCompletions(habitId)

    // Получение всех привычек с их выполнениями
    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>> =
        repository.getAllHabitsWithCompletions()
}