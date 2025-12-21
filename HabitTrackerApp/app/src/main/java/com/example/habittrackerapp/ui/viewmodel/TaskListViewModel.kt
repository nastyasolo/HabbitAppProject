package com.example.habittrackerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.domain.model.Task
import com.example.habittrackerapp.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _overdueCount = MutableStateFlow(0)
    val overdueCount: StateFlow<Int> = _overdueCount

    private val _dueTodayCount = MutableStateFlow(0)
    val dueTodayCount: StateFlow<Int> = _dueTodayCount

    init {
        loadTasks()
        loadCounts()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskUseCases.getAllTasks().collectLatest { tasks ->
                _tasks.value = tasks
            }
        }
    }

    private fun loadCounts() {
        viewModelScope.launch {
            _overdueCount.value = taskUseCases.getOverdueTasksCount()
            _dueTodayCount.value = taskUseCases.getDueTodayTasksCount()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskUseCases.toggleTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskUseCases.deleteTask(task)
            loadCounts()
        }
    }

    fun refresh() {
        loadCounts()
    }
}