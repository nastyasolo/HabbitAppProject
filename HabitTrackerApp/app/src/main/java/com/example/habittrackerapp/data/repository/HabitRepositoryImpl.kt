package com.example.habittrackerapp.data.repository

import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.mapper.toDomain
import com.example.habittrackerapp.data.mapper.toEntity
import com.example.habittrackerapp.data.model.Habit as HabitEntity
import com.example.habittrackerapp.domain.model.Habit as HabitDomain
import com.example.habittrackerapp.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override fun getAllHabits(): Flow<List<HabitDomain>> {
        return habitDao.getAllHabits().map { habits ->
            habits.map { it.toDomain() }
        }
    }

    override suspend fun getHabitById(id: String): HabitDomain? {
        return habitDao.getHabitById(id)?.toDomain()
    }

    override suspend fun insertHabit(habit: HabitDomain) {
        habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun updateHabit(habit: HabitDomain) {
        habitDao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habit: HabitDomain) {
        habitDao.deleteHabit(habit.toEntity())
    }

    override suspend fun toggleHabitCompletion(id: String) {
        val habit = habitDao.getHabitById(id)
        habit?.let {
            val updatedHabit = it.copy(
                isCompleted = !it.isCompleted,
                streak = if (!it.isCompleted) it.streak + 1 else it.streak
            )
            habitDao.updateHabit(updatedHabit)
        }
    }

    override suspend fun getTodayHabits(): List<HabitDomain> {
        val habits = habitDao.getAllHabitsSimple()
        return habits.map { it.toDomain() }
    }

    override suspend fun getWeeklyHabits(): List<HabitDomain> {
        val habits = habitDao.getAllHabitsSimple()
        return habits.map { it.toDomain() }
    }
}