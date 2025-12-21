package com.example.habittrackerapp.domain.repository

import com.example.habittrackerapp.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    fun getUpcomingTasks(limit: Int): Flow<List<Task>>
    suspend fun getTaskById(id: String): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean)
    suspend fun getOverdueTasksCount(): Int
    suspend fun getDueTodayTasksCount(): Int
}