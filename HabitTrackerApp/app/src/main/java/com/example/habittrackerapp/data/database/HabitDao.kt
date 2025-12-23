package com.example.habittrackerapp.data.database

import androidx.room.*
import com.example.habittrackerapp.data.model.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: String): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: String)

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    suspend fun getAllHabitsSimple(): List<Habit>

    @Query("SELECT * FROM habits WHERE syncStatus = 'PENDING'")
    suspend fun getPendingHabits(): List<Habit>

    @Query("UPDATE habits SET syncStatus = :status, lastSynced = :timestamp WHERE id = :id")
    suspend fun updateHabitSyncStatus(id: String, status: String, timestamp: Long)

    @Query("SELECT * FROM habits WHERE category = :category ORDER BY createdAt DESC")
    fun getHabitsByCategory(category: String): Flow<List<Habit>>

    // методы для обновления кэшированных полей
    @Query("UPDATE habits SET currentStreak = :streak, lastCompleted = :lastCompleted WHERE id = :id")
    suspend fun updateHabitStreak(id: String, streak: Int, lastCompleted: String?)

    @Query("UPDATE habits SET longestStreak = :longestStreak WHERE id = :id")
    suspend fun updateLongestStreak(id: String, longestStreak: Int)


    @Query("UPDATE habits SET reminderTime = :reminderTime, reminderId = :reminderId WHERE id = :id")
    suspend fun updateHabitReminder(id: String, reminderTime: String?, reminderId: String?)

    @Query("UPDATE habits SET reminderId = NULL WHERE id = :id")
    suspend fun clearHabitReminder(id: String)

    @Query("SELECT * FROM habits WHERE reminderTime IS NOT NULL AND reminderId IS NOT NULL")
    fun getHabitsWithReminders(): Flow<List<Habit>>
}