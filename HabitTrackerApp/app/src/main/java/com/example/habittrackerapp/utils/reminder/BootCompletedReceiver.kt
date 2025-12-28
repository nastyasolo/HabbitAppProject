package com.example.habittrackerapp.utils.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habittrackerapp.domain.repository.HabitRepository
import com.example.habittrackerapp.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class BootCompletedReceiver : BroadcastReceiver() {

//    @Inject
//    lateinit var habitRepository: HabitRepository
//    @Inject
//    lateinit var taskRepository: TaskRepository
//    @Inject
//    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            println("DEBUG: BootCompletedReceiver received BOOT_COMPLETED")
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val habits = habitRepository.getAllHabits().first()
//                    habits.forEach { habit ->
//                        if (habit.hasReminder && habit.reminderTime != null) {
//                            reminderScheduler.scheduleHabitReminder(habit)
//                        }
//                    }
//
//                    val tasks = taskRepository.getAllTasks().first()
//                    tasks.forEach { task ->
//                        if (task.hasReminder && task.reminderTime != null && !task.isCompleted) {
//                            reminderScheduler.scheduleTaskReminder(task)
//                        }
//                    }
//                } catch (e: Exception) {
//
//                }
//            }


//            val restoreWork = OneTimeWorkRequestBuilder<RestoreRemindersWorker>()
//                .setInitialDelay(30, TimeUnit.SECONDS)
//                .addTag("restore_reminders")
//                .build()
//
//            WorkManager.getInstance(context).enqueue(restoreWork)
        }
    }
}