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

    private val _showGenerateDialog = MutableStateFlow(false)
    val showGenerateDialog: StateFlow<Boolean> = _showGenerateDialog

    private val _isGeneratingData = MutableStateFlow(false)
    val isGeneratingData: StateFlow<Boolean> = _isGeneratingData

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
                viewModelScope.launch {
                    println("DEBUG: [HabitListViewModel] ToggleCompletion для привычки ${event.habitId}")
                    habitUseCases.toggleHabitCompletion(event.habitId)
//                    loadHabits()
                }
            }
            is HabitListEvent.DeleteHabit -> {
                viewModelScope.launch {
                    habitUseCases.deleteHabit(event.habit)
                    loadHabits()
                }
            }
            HabitListEvent.Reload -> {
                loadHabits()
            }
            HabitListEvent.ShowGenerateDialog -> {
                _showGenerateDialog.value = true
            }
            HabitListEvent.HideGenerateDialog -> {
                _showGenerateDialog.value = false
            }
            is HabitListEvent.GenerateTestHabits -> {
                viewModelScope.launch {
                    _isGeneratingData.value = true
                    _state.update { it.copy(isLoading = true) }

                    try {
                        habitUseCases.generateTestHabits(event.count)
                        _showGenerateDialog.value = false
                        loadHabits()
                    } finally {
                        _isGeneratingData.value = false
                    }
                }
            }
            HabitListEvent.ClearAllData -> {
                viewModelScope.launch {
                    _isGeneratingData.value = true
                    _state.update { it.copy(isLoading = true) }

                    try {
                        habitUseCases.clearAllData()
                        loadHabits()
                    } finally {
                        _isGeneratingData.value = false
                    }
                }
            }
        }
    }
}

data class HabitListState(
    val habits: List<HabitWithCompletions> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGeneratingData: Boolean = false  // Добавили поле
)

sealed class HabitListEvent {
    data class ToggleCompletion(val habitId: String) : HabitListEvent()
    data class DeleteHabit(val habit: Habit) : HabitListEvent()
    object Reload : HabitListEvent()
    object ShowGenerateDialog : HabitListEvent()
    object HideGenerateDialog : HabitListEvent()
    data class GenerateTestHabits(val count: Int) : HabitListEvent()
    object ClearAllData : HabitListEvent()
}