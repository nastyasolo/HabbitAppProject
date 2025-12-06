package com.example.habittrackerapp.data

import android.util.Log
import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.mapper.toEntity
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class InitialDataProvider(
    private val habitDao: HabitDao
) {
    fun populateIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val count = habitDao.getAllHabitsSimple().size
                Log.d("InitialDataProvider", "Количество привычек в БД: $count")

                if (count == 0) {
                    Log.d("InitialDataProvider", "Добавляю начальные привычки...")

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
                        ),
                        Habit(
                            id = UUID.randomUUID().toString(),
                            name = "Прогулка на свежем воздухе",
                            description = "Ежедневная прогулка 1 час",
                            type = HabitType.DAILY,
                            priority = Priority.MEDIUM,
                            streak = 3,
                            isCompleted = false
                        )
                    )

                    initialHabits.forEach { habit ->
                        habitDao.insertHabit(habit.toEntity())
                    }

                    Log.d("InitialDataProvider", "✅ Добавлено ${initialHabits.size} начальных привычек")
                } else {
                    Log.d("InitialDataProvider", "ℹ️ В БД уже есть $count привычек")
                }
            } catch (e: Exception) {
                Log.e("InitialDataProvider", "Ошибка при добавлении начальных данных", e)
            }
        }
    }
}