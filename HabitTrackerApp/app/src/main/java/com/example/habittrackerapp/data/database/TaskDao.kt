package com.example.habittrackerapp.data.database

import androidx.room.*
import com.example.habittrackerapp.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY " +
            "CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, " +
            "dueDate ASC, " +
            "CASE priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "END ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY " +
            "CASE priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "END ASC")
    fun getTasksByDate(date: LocalDate): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND " +
            "(dueDate IS NULL OR dueDate >= :startDate) " +
            "ORDER BY " +
            "CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, " +
            "dueDate ASC, " +
            "CASE priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "END ASC " +
            "LIMIT :limit")
    fun getUpcomingTasks(startDate: LocalDate, limit: Int = 5): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: String, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND dueDate < :today")
    suspend fun getOverdueTasksCount(today: LocalDate): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND dueDate = :today")
    suspend fun getDueTodayTasksCount(today: LocalDate): Int
}