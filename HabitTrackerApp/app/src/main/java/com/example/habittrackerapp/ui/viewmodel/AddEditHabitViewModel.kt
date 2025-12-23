package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.domain.model.DayOfWeek // Импортируем ваш DayOfWeek
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.domain.usecase.HabitUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val habitUseCases: HabitUseCases,
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
                habitUseCases.getHabitById(id)?.let { habit ->
                    _state.update {
                        it.copy(
                            name = habit.name,
                            description = habit.description,
                            type = habit.type,
                            priority = habit.priority,
                            reminderTime = habit.reminderTime,
                            hasReminder = habit.hasReminder,
                            reminderDays = habit.reminderDays ?: emptyList(),
                            targetDays = habit.targetDays ?: emptyList() // Загружаем targetDays
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
                    reminderTime = currentState.reminderTime,
                    hasReminder = currentState.hasReminder,
                    reminderDays = if (currentState.hasReminder) currentState.reminderDays else emptyList(),
                    targetDays = currentState.targetDays // Сохраняем targetDays
                )

                if (currentHabitId == null) {
                    habitUseCases.insertHabit(habit)
                } else {
                    habitUseCases.updateHabit(habit)
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
                _state.update {
                    it.copy(
                        type = event.type,
                        // Сбрасываем targetDays при смене типа с WEEKLY на DAILY
                        targetDays = if (event.type == HabitType.WEEKLY) it.targetDays else emptyList()
                    )
                }
            }
            is AddEditHabitEvent.PriorityChanged -> {
                _state.update { it.copy(priority = event.priority) }
            }
            is AddEditHabitEvent.ReminderTimeChanged -> {
                _state.update { it.copy(reminderTime = event.reminderTime) }
            }
            is AddEditHabitEvent.HasReminderChanged -> {
                _state.update {
                    it.copy(
                        hasReminder = event.hasReminder,
                        reminderDays = if (!event.hasReminder) emptyList() else it.reminderDays
                    )
                }
            }
            is AddEditHabitEvent.ReminderDaysChanged -> {
                _state.update { it.copy(reminderDays = event.days) }
            }
            is AddEditHabitEvent.TargetDaysChanged -> {
                _state.update { it.copy(targetDays = event.days) }
            }
        }
    }
}

data class AddEditHabitState(
    val name: String = "",
    val description: String = "",
    val type: HabitType = HabitType.DAILY,
    val priority: Priority = Priority.MEDIUM,
    val reminderTime: String? = null,
    val hasReminder: Boolean = false,
    val reminderDays: List<DayOfWeek> = emptyList(),
    val targetDays: List<DayOfWeek> = emptyList(), // Дни выполнения для WEEKLY привычек
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
    data class HasReminderChanged(val hasReminder: Boolean) : AddEditHabitEvent()
    data class ReminderDaysChanged(val days: List<DayOfWeek>) : AddEditHabitEvent()
    data class TargetDaysChanged(val days: List<DayOfWeek>) : AddEditHabitEvent()
}