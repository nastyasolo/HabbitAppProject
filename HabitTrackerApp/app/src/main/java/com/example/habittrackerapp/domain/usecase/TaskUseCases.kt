package com.example.habittrackerapp.domain.usecase

import com.example.habittrackerapp.domain.model.Task
import com.example.habittrackerapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class TaskUseCases @Inject constructor(
    private val repository: TaskRepository
) {
    init {
        println("DEBUG: TaskUseCases created with repository: $repository")
    }

    fun getAllTasks(): Flow<List<Task>> = repository.getAllTasks()
    fun getTasksByDate(date: LocalDate): Flow<List<Task>> = repository.getTasksByDate(date)
    fun getUpcomingTasks(limit: Int = 5): Flow<List<Task>> = repository.getUpcomingTasks(limit)
    suspend fun getTaskById(id: String): Task? = repository.getTaskById(id)
    suspend fun addTask(task: Task) = repository.insertTask(task)
    suspend fun updateTask(task: Task) = repository.updateTask(task)
    suspend fun deleteTask(task: Task) = repository.deleteTask(task)
    suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean) =
        repository.toggleTaskCompletion(id, isCompleted)
    suspend fun getOverdueTasksCount(): Int = repository.getOverdueTasksCount()
    suspend fun getDueTodayTasksCount(): Int = repository.getDueTodayTasksCount()
}