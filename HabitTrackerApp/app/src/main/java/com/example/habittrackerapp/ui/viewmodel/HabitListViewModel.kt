package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HabitListState())
    val state: StateFlow<HabitListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Даем небольшую задержку чтобы база успела инициализироваться
            delay(100)
            loadHabits()
        }
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase()
                .catch { throwable ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = throwable.message
                    ) }
                }
                .collect { habits ->
                    _state.update { it.copy(
                        habits = habits,
                        isLoading = false,
                        error = null
                    ) }
                }
        }
    }

    fun onEvent(event: HabitListEvent) {
        when (event) {
            is HabitListEvent.ToggleCompletion -> {
                viewModelScope.launch {
                    toggleHabitCompletionUseCase(event.habitId)
                }
            }
            is HabitListEvent.DeleteHabit -> {
                viewModelScope.launch {
                    deleteHabitUseCase(event.habit)
                }
            }
            HabitListEvent.Reload -> {
                _state.update { it.copy(isLoading = true) }
                loadHabits()
            }
        }
    }
}

// Выносим классы за пределы ViewModel
data class HabitListState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class HabitListEvent {
    data class ToggleCompletion(val habitId: String) : HabitListEvent()
    data class DeleteHabit(val habit: Habit) : HabitListEvent()
    object Reload : HabitListEvent()
}