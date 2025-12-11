package com.example.habittrackerapp.domain.repository

import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    // Возвращает Flow<List<Habit>> для списка привычек (без детальной истории)
    fun getAllHabits(): Flow<List<Habit>>

    // Возвращает HabitWithCompletions для детального просмотра (с историей)
    suspend fun getHabitWithCompletions(id: String): HabitWithCompletions?

    suspend fun getHabitById(id: String): Habit?
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun toggleHabitCompletion(id: String)
    suspend fun getTodayHabits(): List<Habit>
    suspend fun getWeeklyHabits(): List<Habit>

    // Методы для синхронизации
    suspend fun syncHabits(): Boolean
    suspend fun syncPendingHabits(): Boolean
    suspend fun markHabitAsSynced(habitId: String)
    suspend fun getPendingHabits(): List<Habit>

    // Новые методы для работы с историей
    suspend fun getHabitCompletions(habitId: String): List<com.example.habittrackerapp.domain.model.HabitCompletion>
    suspend fun addCompletion(completion: com.example.habittrackerapp.domain.model.HabitCompletion)
    suspend fun removeCompletion(completion: com.example.habittrackerapp.domain.model.HabitCompletion)
}