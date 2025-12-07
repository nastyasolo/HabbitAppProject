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

    // Новый метод для получения привычек, ожидающих синхронизации
    @Query("SELECT * FROM habits WHERE syncStatus = 'PENDING'")
    suspend fun getPendingHabits(): List<Habit>

    // Новый метод для обновления статуса синхронизации
    @Query("UPDATE habits SET syncStatus = :status, lastSynced = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, timestamp: Long)

    // Метод для получения привычек по категории
    @Query("SELECT * FROM habits WHERE category = :category ORDER BY createdAt DESC")
    fun getHabitsByCategory(category: String): Flow<List<Habit>>
}