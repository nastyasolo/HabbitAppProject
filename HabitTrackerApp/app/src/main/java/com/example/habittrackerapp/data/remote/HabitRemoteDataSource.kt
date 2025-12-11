package com.example.habittrackerapp.data.remote

import com.example.habittrackerapp.data.remote.model.FirestoreCompletion
import com.example.habittrackerapp.data.remote.model.FirestoreHabit
import kotlinx.coroutines.flow.Flow

interface HabitRemoteDataSource {
    suspend fun saveHabit(habit: FirestoreHabit): Result<Unit>
    suspend fun getHabits(userId: String): Result<List<FirestoreHabit>>
    suspend fun deleteHabit(habitId: String, userId: String): Result<Unit>
    suspend fun updateHabit(habit: FirestoreHabit): Result<Unit>
    fun observeHabits(userId: String): Flow<List<FirestoreHabit>>

    // методы для истории выполнения
    suspend fun saveCompletion(completion: FirestoreCompletion): Result<Unit>
    suspend fun getCompletions(userId: String, habitId: String): Result<List<FirestoreCompletion>>
    suspend fun deleteCompletion(completionId: String, userId: String, habitId: String): Result<Unit>
}