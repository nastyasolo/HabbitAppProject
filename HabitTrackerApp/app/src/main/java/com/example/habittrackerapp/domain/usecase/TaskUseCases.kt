package com.example.habittrackerapp.domain.usecase

import android.content.Context
import com.example.habittrackerapp.domain.model.Task
import com.example.habittrackerapp.domain.repository.TaskRepository
import com.example.habittrackerapp.utils.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class TaskUseCases @Inject constructor(
    private val repository: TaskRepository,
    private val reminderScheduler: ReminderScheduler
) {
    init {
        println("DEBUG: TaskUseCases created with repository: $repository and reminderScheduler: $reminderScheduler")
    }

    fun getAllTasks(): Flow<List<Task>> = repository.getAllTasks()
    fun getTasksByDate(date: LocalDate): Flow<List<Task>> = repository.getTasksByDate(date)
    fun getUpcomingTasks(limit: Int = 5): Flow<List<Task>> = repository.getUpcomingTasks(limit)
    suspend fun getTaskById(id: String): Task? = repository.getTaskById(id)
//    suspend fun addTask(task: Task) = repository.insertTask(task)
    suspend fun addTask(task: Task) {
        repository.insertTask(task)
        if (task.hasReminder) {
            reminderScheduler.scheduleTaskReminder(task)
        }
    }
    suspend fun updateTask(task: Task) {
        // Отменяем старое напоминание
        reminderScheduler.cancelReminder(task.id, false)

        repository.updateTask(task)

        // Создаем новое напоминание, если нужно
        if (task.hasReminder) {
            reminderScheduler.scheduleTaskReminder(task)
        }
    }

    suspend fun deleteTask(task: Task) {
        // Отменяем напоминание
        reminderScheduler.cancelReminder(task.id, false)
        repository.deleteTask(task)
    }
    suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean) {
        repository.toggleTaskCompletion(id, isCompleted)

        // Если задача выполнена, отменяем напоминание
        if (isCompleted) {
            val task = repository.getTaskById(id)
            task?.let {
                reminderScheduler.cancelReminder(id, false)
            }
        }
    }
    suspend fun getOverdueTasksCount(): Int = repository.getOverdueTasksCount()
    suspend fun getDueTodayTasksCount(): Int = repository.getDueTodayTasksCount()
}