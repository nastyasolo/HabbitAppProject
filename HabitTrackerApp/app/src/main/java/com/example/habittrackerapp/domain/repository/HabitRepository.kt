package com.example.habittrackerapp.domain.repository

import com.example.habittrackerapp.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabits(): Flow<List<Habit>>
    suspend fun getHabitById(id: String): Habit?
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun toggleHabitCompletion(id: String)
    suspend fun getTodayHabits(): List<Habit>
    suspend fun getWeeklyHabits(): List<Habit>
}