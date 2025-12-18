package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitWithCompletions
import com.example.habittrackerapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsWithCompletions: GetHabitsWithCompletionsUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HabitListState())
    val state: StateFlow<HabitListState> = _state

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsWithCompletions()
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
                viewModelScope.launch { toggleHabitCompletionUseCase(event.habitId) }
            }
            is HabitListEvent.DeleteHabit -> {
                viewModelScope.launch { deleteHabitUseCase(event.habit) }
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