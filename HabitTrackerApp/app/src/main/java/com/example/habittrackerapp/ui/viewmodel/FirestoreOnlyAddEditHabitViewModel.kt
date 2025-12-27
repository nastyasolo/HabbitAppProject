package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.data.repository.FirestoreOnlyHabitRepository
import com.example.habittrackerapp.domain.model.DayOfWeek
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FirestoreOnlyAddEditHabitViewModel @Inject constructor(
    private val firestoreOnlyRepository: FirestoreOnlyHabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(FirestoreOnlyAddEditHabitState())
    val state: StateFlow<FirestoreOnlyAddEditHabitState> = _state.asStateFlow()

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
                firestoreOnlyRepository.getHabitById(id)?.let { habit ->
                    _state.update {
                        it.copy(
                            name = habit.name,
                            description = habit.description,
                            type = habit.type,
                            priority = habit.priority,
                            reminderTime = habit.reminderTime,
                            hasReminder = habit.hasReminder,
                            reminderDays = habit.reminderDays ?: emptyList(),
                            targetDays = habit.targetDays ?: emptyList()
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
                    targetDays = currentState.targetDays
                )

                if (currentHabitId == null) {
                    firestoreOnlyRepository.insertHabit(habit)
                } else {
                    firestoreOnlyRepository.updateHabit(habit)
                }
            }
        }
    }

    fun onEvent(event: FirestoreOnlyAddEditHabitEvent) {
        when (event) {
            is FirestoreOnlyAddEditHabitEvent.NameChanged -> {
                _state.update {
                    it.copy(
                        name = event.name,
                        nameError = if (event.name.isBlank()) "Название не может быть пустым" else null
                    )
                }
            }
            is FirestoreOnlyAddEditHabitEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.description) }
            }
            is FirestoreOnlyAddEditHabitEvent.TypeChanged -> {
                _state.update {
                    it.copy(
                        type = event.type,
                        targetDays = if (event.type == HabitType.WEEKLY) it.targetDays else emptyList()
                    )
                }
            }
            is FirestoreOnlyAddEditHabitEvent.PriorityChanged -> {
                _state.update { it.copy(priority = event.priority) }
            }
            is FirestoreOnlyAddEditHabitEvent.ReminderTimeChanged -> {
                _state.update { it.copy(reminderTime = event.reminderTime) }
            }
            is FirestoreOnlyAddEditHabitEvent.HasReminderChanged -> {
                _state.update {
                    it.copy(
                        hasReminder = event.hasReminder,
                        reminderDays = if (!event.hasReminder) emptyList() else it.reminderDays
                    )
                }
            }
            is FirestoreOnlyAddEditHabitEvent.ReminderDaysChanged -> {
                _state.update { it.copy(reminderDays = event.days) }
            }
            is FirestoreOnlyAddEditHabitEvent.TargetDaysChanged -> {
                _state.update { it.copy(targetDays = event.days) }
            }
        }
    }
}

data class FirestoreOnlyAddEditHabitState(
    val name: String = "",
    val description: String = "",
    val type: HabitType = HabitType.DAILY,
    val priority: Priority = Priority.MEDIUM,
    val reminderTime: String? = null,
    val hasReminder: Boolean = false,
    val reminderDays: List<DayOfWeek> = emptyList(),
    val targetDays: List<DayOfWeek> = emptyList(),
    val nameError: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && nameError == null
}

sealed class FirestoreOnlyAddEditHabitEvent {
    data class NameChanged(val name: String) : FirestoreOnlyAddEditHabitEvent()
    data class DescriptionChanged(val description: String) : FirestoreOnlyAddEditHabitEvent()
    data class TypeChanged(val type: HabitType) : FirestoreOnlyAddEditHabitEvent()
    data class PriorityChanged(val priority: Priority) : FirestoreOnlyAddEditHabitEvent()
    data class ReminderTimeChanged(val reminderTime: String?) : FirestoreOnlyAddEditHabitEvent()
    data class HasReminderChanged(val hasReminder: Boolean) : FirestoreOnlyAddEditHabitEvent()
    data class ReminderDaysChanged(val days: List<DayOfWeek>) : FirestoreOnlyAddEditHabitEvent()
    data class TargetDaysChanged(val days: List<DayOfWeek>) : FirestoreOnlyAddEditHabitEvent()
}