package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.domain.model.Category
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.domain.model.Task
import com.example.habittrackerapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {
    init {
        println("DEBUG: AddEditTaskViewModel created with taskUseCases: $taskUseCases")
    }

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun loadTask(id: String?) {
        viewModelScope.launch {
            if (id == null) {
                // Создание новой задачи
                _task.value = Task(
                    id = "",
                    title = "",
                    description = "",
                    dueDate = null,
                    priority = Priority.MEDIUM,
                    category = Category.PERSONAL,
                    isCompleted = false,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    hasReminder = false
                )
            } else {
                // Редактирование существующей задачи
                _task.value = taskUseCases.getTaskById(id)
            }
        }
    }
    fun updateTitle(title: String) {
        _task.value = _task.value?.copy(title = title, updatedAt = LocalDateTime.now())
    }

    fun updateDescription(description: String) {
        _task.value = _task.value?.copy(description = description, updatedAt = LocalDateTime.now())
    }

    fun updateDueDate(dueDate: LocalDate?) {
        _task.value = _task.value?.copy(dueDate = dueDate, updatedAt = LocalDateTime.now())
    }

    fun updateDueTime(dueTime: LocalTime?) {
        _task.value = _task.value?.copy(dueTime = dueTime, updatedAt = LocalDateTime.now())
    }
    fun updatePriority(priority: Priority) {
        _task.value = _task.value?.copy(priority = priority, updatedAt = LocalDateTime.now())
    }

    fun updateCategory(category: Category?) {
        _task.value = _task.value?.copy(category = category, updatedAt = LocalDateTime.now())
    }

    fun updateHasReminder(hasReminder: Boolean) {
        _task.value = _task.value?.copy(hasReminder = hasReminder)
    }

    fun updateReminderTime(reminderTime: LocalTime?) {
        _task.value = _task.value?.copy(reminderTime = reminderTime)
    }


    fun saveTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val currentTask = _task.value
            if (currentTask != null) {
                if (currentTask.title.isBlank()) {
                    _isSaving.value = false
                    return@launch
                }

                try {
                    if (currentTask.id.isBlank()) {
                        // Новая задача - генерируем ID и добавляем
                        println("DEBUG: Adding new task...")
                        val newTask = currentTask.copy(id = UUID.randomUUID().toString())
                        taskUseCases.addTask(newTask)
                    } else {
                        // Существующая задача - обновляем
                        println("DEBUG: Updating existing task...")
                        taskUseCases.updateTask(currentTask)
                    }
                    onSuccess()
                } catch (e: Exception) {
                    println("DEBUG: Error saving task: $e")
                    e.printStackTrace()
                }
            }
            _isSaving.value = false
        }
    }
}