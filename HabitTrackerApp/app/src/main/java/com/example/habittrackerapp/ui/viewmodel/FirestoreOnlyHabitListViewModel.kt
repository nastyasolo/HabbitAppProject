package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.data.repository.FirestoreOnlyHabitRepository
import com.example.habittrackerapp.data.util.StreakCalculator
import com.example.habittrackerapp.domain.model.DayOfWeek
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitCompletion
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.HabitWithCompletions
import com.example.habittrackerapp.domain.model.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FirestoreOnlyHabitListViewModel @Inject constructor(
    private val firestoreOnlyRepository: FirestoreOnlyHabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FirestoreOnlyHabitListState())
    val state: StateFlow<FirestoreOnlyHabitListState> = _state

    private val optimisticUpdates = mutableMapOf<String, HabitWithCompletions>()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            firestoreOnlyRepository.getAllHabitsWithCompletions()
                .onStart { _state.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
                .collect { habits ->
                    val mergedHabits = mergeWithOptimisticUpdates(habits)
                    _state.update {
                        it.copy(
                            habits = mergedHabits,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onEvent(event: FirestoreOnlyHabitListEvent) {
        when (event) {
            is FirestoreOnlyHabitListEvent.ToggleCompletion -> {
                viewModelScope.launch {
                    val previousState = _state.value.habits.toList()

                    val updatedHabits = applyOptimisticToggle(
                        habits = previousState,
                        habitId = event.habitId
                    )

                    _state.update { it.copy(habits = updatedHabits) }

                    try {
                        firestoreOnlyRepository.toggleHabitCompletion(event.habitId)
                    } catch (e: Exception) {
                        _state.update { it.copy(habits = previousState, error = e.message) }
                    }
                }
            }

            is FirestoreOnlyHabitListEvent.DeleteHabit -> {
                viewModelScope.launch {
                    try {
                        firestoreOnlyRepository.deleteHabit(event.habit)
                        optimisticUpdates.remove(event.habit.id)
                        loadHabits()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }

            is FirestoreOnlyHabitListEvent.GenerateTestData -> {
                viewModelScope.launch {
                    _state.update { it.copy(isGeneratingData = true) }
                    try {
                        firestoreOnlyRepository.generateTestHabits(event.count)
                        loadHabits()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    } finally {
                        _state.update { it.copy(isGeneratingData = false) }
                    }
                }
            }

            is FirestoreOnlyHabitListEvent.ClearAllData -> {
                viewModelScope.launch {
                    _state.update { it.copy(isGeneratingData = true) }
                    try {
                        firestoreOnlyRepository.clearAllData()
                        optimisticUpdates.clear()
                        loadHabits()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    } finally {
                        _state.update { it.copy(isGeneratingData = false) }
                    }
                }
            }

            FirestoreOnlyHabitListEvent.Reload -> {
                loadHabits()
            }
        }
    }

    private fun applyOptimisticToggle(
        habits: List<HabitWithCompletions>,
        habitId: String
    ): List<HabitWithCompletions> {
        return habits.map { habitWithCompletions ->
            if (habitWithCompletions.habit.id == habitId) {
                val habit = habitWithCompletions.habit
                val today = LocalDate.now()
                val wasCompleted = habitWithCompletions.completions.any {
                    it.date == today && it.completed
                }

                val newCompletions = if (wasCompleted) {
                    habitWithCompletions.completions.filterNot { it.date == today }
                } else {
                    habitWithCompletions.completions + HabitCompletion(
                        id = "temp-${today}",
                        habitId = habitId,
                        date = today,
                        completed = true,
                        completedAt = System.currentTimeMillis(),
                        syncStatus = SyncStatus.SYNCED
                    )
                }

                // Используем общий калькулятор
                val completedDates = newCompletions
                    .filter { it.completed }
                    .map { it.date }
                    .toSet()

                val (currentStreak, _) = StreakCalculator.calculateStreak(habit, completedDates)

                HabitWithCompletions(
                    habit = habit.copy(currentStreak = currentStreak),
                    completions = newCompletions
                ).also { optimisticUpdates[habitId] = it }
            } else {
                habitWithCompletions
            }
        }
    }
    private fun mergeWithOptimisticUpdates(
        habits: List<HabitWithCompletions>
    ): List<HabitWithCompletions> {
        return habits.map { habitWithCompletions ->
            optimisticUpdates[habitWithCompletions.habit.id] ?: habitWithCompletions
        }
    }

    private fun calculateDailyStreak(completedDates: Set<LocalDate>): Pair<Int, LocalDate?> {
        var currentStreak = 0
        var currentDate = LocalDate.now()
        val sortedDates = completedDates.sortedDescending()
        val lastCompleted = sortedDates.firstOrNull()

        while (completedDates.contains(currentDate) ||
            (currentStreak == 0 && completedDates.contains(currentDate.minusDays(1)))) {
            currentStreak++
            currentDate = currentDate.minusDays(1)
        }

        return Pair(currentStreak, lastCompleted)
    }

    private fun calculateWeeklyStreak(
        habit: Habit,
        completedDates: Set<LocalDate>
    ): Pair<Int, LocalDate?> {
        val targetDaysSet = habit.targetDays.toSet()
        if (targetDaysSet.isEmpty()) return Pair(0, null)

        var currentStreak = 0
        var weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
        val lastCompleted = completedDates.maxOrNull()

        while (true) {
            val weekEnd = weekStart.plusDays(6)
            val weekCompletedDates = completedDates.filter {
                it >= weekStart && it <= weekEnd
            }

            val completedDaysInWeek = weekCompletedDates.map { date ->
                DayOfWeek.fromInt(date.dayOfWeek.value)
            }.toSet()

            val isWeekCompleted = targetDaysSet.all { it in completedDaysInWeek }

            if (isWeekCompleted) {
                currentStreak++
                weekStart = weekStart.minusWeeks(1)
            } else {
                break
            }
        }

        return Pair(currentStreak, lastCompleted)
    }
}

data class FirestoreOnlyHabitListState(
    val habits: List<HabitWithCompletions> = emptyList(),
    val isLoading: Boolean = false,
    val isGeneratingData: Boolean = false,
    val error: String? = null
)

sealed class FirestoreOnlyHabitListEvent {
    data class ToggleCompletion(val habitId: String) : FirestoreOnlyHabitListEvent()
    data class DeleteHabit(val habit: Habit) : FirestoreOnlyHabitListEvent()
    data class GenerateTestData(val count: Int) : FirestoreOnlyHabitListEvent()
    object ClearAllData : FirestoreOnlyHabitListEvent()
    object Reload : FirestoreOnlyHabitListEvent()
}