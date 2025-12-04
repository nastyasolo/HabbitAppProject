package com.example.habittrackerapp.data.repository

import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.model.Habit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HabitRepository @Inject constructor(
    private val habitDao: HabitDao
) {
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun getHabitById(id: String): Habit? = habitDao.getHabitById(id)

    suspend fun addHabit(habit: Habit) = habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun deleteHabitById(id: String) = habitDao.deleteHabitById(id)

    suspend fun toggleHabitCompletion(id: String) {
        val habit = habitDao.getHabitById(id)
        habit?.let {
            val updatedHabit = it.copy(
                isCompleted = !it.isCompleted,
                streak = if (!it.isCompleted) it.streak + 1 else it.streak
            )
            habitDao.updateHabit(updatedHabit)
        }
    }
}