package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.domain.usecase.GetHabitByIdUseCase
import com.example.habittrackerapp.domain.usecase.InsertHabitUseCase
import com.example.habittrackerapp.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val insertHabitUseCase: InsertHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditHabitState())
    val state: StateFlow<AddEditHabitState> = _state.asStateFlow()

    private var currentHabitId: String? = null

    init {
        val habitId = savedStateHandle.get<String>("habitId")
        if (habitId != null) {
            loadHabit(habitId)
        }
    }

    fun loadHabit(habitId: String?) {
        habitId?.let { id ->
            currentHabitId = id
            viewModelScope.launch {
                getHabitByIdUseCase(id)?.let { habit ->
                    _state.update {
                        it.copy(
                            name = habit.name,
                            description = habit.description,
                            type = habit.type,
                            priority = habit.priority,
                            reminderTime = habit.reminderTime
                        )
                    }
                }
            }
        }
    }

    fun saveHabit() {
        val currentState = _state.value
        if (currentState.isValid) {
            viewModelScope.launch {
                val habit = Habit(
                    id = currentHabitId ?: UUID.randomUUID().toString(),
                    name = currentState.name,
                    description = currentState.description,
                    type = currentState.type,
                    priority = currentState.priority,
                    reminderTime = currentState.reminderTime
                )

                if (currentHabitId == null) {
                    insertHabitUseCase(habit)
                } else {
                    updateHabitUseCase(habit)
                }
            }
        }
    }

    fun onEvent(event: AddEditHabitEvent) {
        when (event) {
            is AddEditHabitEvent.NameChanged -> {
                _state.update {
                    it.copy(
                        name = event.name,
                        nameError = if (event.name.isBlank()) "Название не может быть пустым" else null
                    )
                }
            }
            is AddEditHabitEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.description) }
            }
            is AddEditHabitEvent.TypeChanged -> {
                _state.update { it.copy(type = event.type) }
            }
            is AddEditHabitEvent.PriorityChanged -> {
                _state.update { it.copy(priority = event.priority) }
            }
            is AddEditHabitEvent.ReminderTimeChanged -> {
                _state.update { it.copy(reminderTime = event.reminderTime) }
            }
        }
    }
}

// Выносим классы состояния и событий ВНЕ ViewModel
data class AddEditHabitState(
    val name: String = "",
    val description: String = "",
    val type: HabitType = HabitType.DAILY,
    val priority: Priority = Priority.MEDIUM,
    val reminderTime: String? = null,
    val nameError: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && nameError == null
}

sealed class AddEditHabitEvent {
    data class NameChanged(val name: String) : AddEditHabitEvent()
    data class DescriptionChanged(val description: String) : AddEditHabitEvent()
    data class TypeChanged(val type: HabitType) : AddEditHabitEvent()
    data class PriorityChanged(val priority: Priority) : AddEditHabitEvent()
    data class ReminderTimeChanged(val reminderTime: String?) : AddEditHabitEvent()
}