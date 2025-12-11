package com.example.habittrackerapp.data.database

import androidx.room.*
import com.example.habittrackerapp.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date DESC")
    fun getCompletionsByHabit(habitId: String): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getCompletionByDate(habitId: String, date: String): HabitCompletion?

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND completed = 1 ORDER BY date DESC")
    fun getCompletedDates(habitId: String): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Delete
    suspend fun deleteCompletion(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletionByDate(habitId: String, date: String)

    @Query("SELECT * FROM habit_completions WHERE syncStatus = 'PENDING'")
    suspend fun getPendingCompletions(): List<HabitCompletion>

    @Query("UPDATE habit_completions SET syncStatus = :status, lastSynced = :timestamp WHERE id = :id")
    suspend fun updateCompletionSyncStatus(id: String, status: String, timestamp: Long)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId")
    suspend fun getCompletionsForHabitSimple(habitId: String): List<HabitCompletion>

    @Query("SELECT date FROM habit_completions WHERE habitId = :habitId AND completed = 1")
    suspend fun getCompletedDatesSimple(habitId: String): List<String>
}