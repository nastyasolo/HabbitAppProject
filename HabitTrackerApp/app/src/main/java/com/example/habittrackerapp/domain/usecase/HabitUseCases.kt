package com.example.habittrackerapp.domain.usecase

import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> = repository.getAllHabits()
}

class GetHabitByIdUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(id: String): Habit? = repository.getHabitById(id)
}

class InsertHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) = repository.insertHabit(habit)
}

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) = repository.updateHabit(habit)
}

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) = repository.deleteHabit(habit)
}

class ToggleHabitCompletionUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(id: String) = repository.toggleHabitCompletion(id)
}

class GetTodayHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(): List<Habit> = repository.getTodayHabits()
}