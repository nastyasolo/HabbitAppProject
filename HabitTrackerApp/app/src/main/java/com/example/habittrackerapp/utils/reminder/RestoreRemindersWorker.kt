package com.example.habittrackerapp.utils.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittrackerapp.domain.repository.HabitRepository
import com.example.habittrackerapp.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class RestoreRemindersWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitRepository: HabitRepository,
    private val taskRepository: TaskRepository,
    private val reminderScheduler: ReminderScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            println("DEBUG: Restoring reminders after reboot...")

            // Восстанавливаем напоминания для привычек
            val habits = habitRepository.getAllHabits().first()
            habits.forEach { habit ->
                if (habit.hasReminder && habit.reminderTime != null) {
                    reminderScheduler.scheduleHabitReminder(habit)
                }
            }

            // Восстанавливаем напоминания для задач
            val tasks = taskRepository.getAllTasks().first()
            tasks.forEach { task ->
                if (task.hasReminder && task.reminderTime != null && !task.isCompleted) {
                    reminderScheduler.scheduleTaskReminder(task)
                }
            }

            println("DEBUG: Reminders restored successfully")
            Result.success()
        } catch (e: Exception) {
            println("DEBUG: Failed to restore reminders: ${e.message}")
            Result.failure()
        }
    }
}