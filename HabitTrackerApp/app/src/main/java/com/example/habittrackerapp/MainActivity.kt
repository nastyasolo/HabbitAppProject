package com.example.habittrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.repository.HabitRepository
import com.example.habittrackerapp.domain.repository.TaskRepository
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.ui.viewmodel.ThemeViewModel
import com.example.habittrackerapp.utils.reminder.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var habitRepository: HabitRepository
    @Inject lateinit var taskRepository: TaskRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habits = habitRepository.getAllHabits().first()
                habits.forEach { habit ->
                    if (habit.hasReminder && habit.reminderTime != null) {
                        reminderScheduler.scheduleHabitReminder(habit)
                    }
                }

                val tasks = taskRepository.getAllTasks().first()
                tasks.forEach { task ->
                    if (task.hasReminder && task.reminderTime != null && !task.isCompleted) {
                        reminderScheduler.scheduleTaskReminder(task)
                    }
                }
            } catch (e: Exception) {

            }
        }
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            HabitTrackerAppTheme(darkTheme = isDarkTheme) {
                MainApp()
            }
        }
    }
}
