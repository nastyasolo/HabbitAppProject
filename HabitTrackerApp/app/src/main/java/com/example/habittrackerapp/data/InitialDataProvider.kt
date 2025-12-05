package com.example.habittrackerapp.data

import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.mapper.toEntity
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

class InitialDataProvider(
    private val habitDao: HabitDao,
    private val scope: CoroutineScope
) {
    fun populateIfEmpty() {
        scope.launch {
            val count = habitDao.getAllHabitsSimple().size
            if (count == 0) {
                val initialHabits = listOf(
                    Habit(
                        id = UUID.randomUUID().toString(),
                        name = "Утренняя зарядка",
                        description = "15 минут упражнений",
                        type = HabitType.DAILY,
                        priority = Priority.HIGH,
                        streak = 7,
                        isCompleted = false
                    ),
                    Habit(
                        id = UUID.randomUUID().toString(),
                        name = "Чтение книги",
                        description = "30 минут перед сном",
                        type = HabitType.DAILY,
                        priority = Priority.MEDIUM,
                        streak = 14,
                        isCompleted = true
                    )
                )

                initialHabits.forEach { habit ->
                    habitDao.insertHabit(habit.toEntity())
                }
            }
        }
    }
}