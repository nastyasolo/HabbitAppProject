package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitWithCompletions
import com.example.habittrackerapp.domain.usecase.HabitUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val habitUseCases: HabitUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(HabitListState())
    val state: StateFlow<HabitListState> = _state

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            habitUseCases.getAllHabitsWithCompletions()
                .onStart { _state.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
                .collect { habits ->
                    _state.update {
                        it.copy(
                            habits = habits,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onEvent(event: HabitListEvent) {
        when (event) {
            is HabitListEvent.ToggleCompletion -> {
                viewModelScope.launch { habitUseCases.toggleHabitCompletion(event.habitId) }
            }
            is HabitListEvent.DeleteHabit -> {
                viewModelScope.launch { habitUseCases.deleteHabit(event.habit) }
            }
            HabitListEvent.Reload -> {
                loadHabits()
            }
        }
    }
}

data class HabitListState(
    val habits: List<HabitWithCompletions> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HabitListEvent {
    data class ToggleCompletion(val habitId: String) : HabitListEvent()
    data class DeleteHabit(val habit: Habit) : HabitListEvent()
    object Reload : HabitListEvent()
}