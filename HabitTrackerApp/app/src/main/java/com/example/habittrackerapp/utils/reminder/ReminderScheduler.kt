package com.example.habittrackerapp.utils.reminder

import android.content.Context
import androidx.work.*
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek as JavaDayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun scheduleTaskReminder(task: Task) {
        if (!task.hasReminder || task.reminderTime == null) return

        val delay = calculateTaskDelay(task)
        if (delay < 0) return // Задача уже просрочена

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "entityId" to task.id,
                    "title" to "Напоминание о задаче",
                    "message" to "${task.title} - пора выполнить!",
                    "isHabit" to false
                )
            )
            .addTag("task_${task.id}")
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_task_${task.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleHabitReminder(habit: Habit) {
        if (!habit.hasReminder || habit.reminderTime == null) return

        val delay = calculateHabitDelay(habit)
        if (delay < 0) return

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "entityId" to habit.id,
                    "title" to "Напоминание о привычке",
                    "message" to "${habit.name} - время выполнить привычку!",
                    "isHabit" to true
                )
            )
            .addTag("habit_${habit.id}")
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_habit_${habit.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(entityId: String, isHabit: Boolean) {
        val tag = if (isHabit) "habit_$entityId" else "task_$entityId"
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }

    private fun calculateTaskDelay(task: Task): Long {
        val now = LocalDateTime.now()
        val reminderDateTime = LocalDateTime.of(
            task.dueDate ?: LocalDate.now(),
            task.reminderTime ?: LocalTime.now()
        )

        return if (reminderDateTime.isBefore(now)) {
            val tomorrow = reminderDateTime.plusDays(1)
            java.time.Duration.between(now, tomorrow).toMillis()
        } else {
            java.time.Duration.between(now, reminderDateTime).toMillis()
        }
    }

    private fun calculateHabitDelay(habit: Habit): Long {
        val now = LocalDateTime.now()
        val reminderTimeParts = habit.reminderTime?.split(":")
        if (reminderTimeParts?.size != 2) return -1

        val reminderHour = reminderTimeParts[0].toIntOrNull() ?: return -1
        val reminderMinute = reminderTimeParts[1].toIntOrNull() ?: return -1
        val reminderLocalTime = LocalTime.of(reminderHour, reminderMinute)

        if (habit.type.name == "DAILY") {
            val todayReminder = LocalDateTime.of(LocalDate.now(), reminderLocalTime)
            return if (todayReminder.isAfter(now)) {
                java.time.Duration.between(now, todayReminder).toMillis()
            } else {
                val tomorrowReminder = todayReminder.plusDays(1)
                java.time.Duration.between(now, tomorrowReminder).toMillis()
            }
        }

        if (habit.type.name == "WEEKLY" && habit.reminderDays.isNotEmpty()) {
            val currentDayOfWeek = now.dayOfWeek.value
            val habitDays = habit.reminderDays.map { day ->
                when (day) {
                    com.example.habittrackerapp.domain.model.DayOfWeek.MONDAY -> JavaDayOfWeek.MONDAY
                    com.example.habittrackerapp.domain.model.DayOfWeek.TUESDAY -> JavaDayOfWeek.TUESDAY
                    com.example.habittrackerapp.domain.model.DayOfWeek.WEDNESDAY -> JavaDayOfWeek.WEDNESDAY
                    com.example.habittrackerapp.domain.model.DayOfWeek.THURSDAY -> JavaDayOfWeek.THURSDAY
                    com.example.habittrackerapp.domain.model.DayOfWeek.FRIDAY -> JavaDayOfWeek.FRIDAY
                    com.example.habittrackerapp.domain.model.DayOfWeek.SATURDAY -> JavaDayOfWeek.SATURDAY
                    com.example.habittrackerapp.domain.model.DayOfWeek.SUNDAY -> JavaDayOfWeek.SUNDAY
                }
            }

            var daysToAdd = 0
            while (daysToAdd <= 7) {
                val targetDay = now.plusDays(daysToAdd.toLong())
                val targetDayOfWeek = targetDay.dayOfWeek

                if (habitDays.contains(targetDayOfWeek)) {
                    val targetDateTime = LocalDateTime.of(targetDay.toLocalDate(), reminderLocalTime)
                    if (daysToAdd == 0 && targetDateTime.isAfter(now)) {
                        return java.time.Duration.between(now, targetDateTime).toMillis()
                    } else if (daysToAdd > 0 || (daysToAdd == 0 && targetDateTime.isBefore(now))) {
                        return java.time.Duration.between(now, targetDateTime).toMillis()
                    }
                }
                daysToAdd++
            }
        }

        return -1
    }
}