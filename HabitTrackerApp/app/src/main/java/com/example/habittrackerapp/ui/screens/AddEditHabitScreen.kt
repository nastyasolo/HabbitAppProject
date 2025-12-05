package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.foundation.text.KeyboardOptions



import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.ui.viewmodel.AddEditHabitEvent
import com.example.habittrackerapp.ui.viewmodel.AddEditHabitViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    habitId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddEditHabitViewModel = hiltViewModel()
) {
    // Инициализируем ViewModel с habitId
    LaunchedEffect(habitId) {
        viewModel.loadHabit(habitId)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (habitId == null) "Добавить привычку" else "Редактировать привычку"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveHabit()
                            onNavigateBack()
                        },
                        enabled = state.isValid
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Поле для названия
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddEditHabitEvent.NameChanged(it)) },
                label = { Text("Название привычки") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameError != null
            )

            // Используем let для безопасного доступа к nameError
            state.nameError?.let { errorText ->
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле для описания
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(AddEditHabitEvent.DescriptionChanged(it)) },
                label = { Text("Описание (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Тип привычки
            Text(
                text = "Тип привычки",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HabitType.values().forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { viewModel.onEvent(AddEditHabitEvent.TypeChanged(type)) },
                        label = { Text(type.displayName) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Приоритет
            Text(
                text = "Приоритет",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.values().forEach { priority ->
                    FilterChip(
                        selected = state.priority == priority,
                        onClick = { viewModel.onEvent(AddEditHabitEvent.PriorityChanged(priority)) },
                        label = { Text(priority.displayName) },  // Используем displayName
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Время напоминания (опционально)
            Text(
                text = "Время напоминания (HH:MM)",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = state.reminderTime ?: "",
                onValueChange = {
                    val newValue = it.ifEmpty { null }
                    viewModel.onEvent(AddEditHabitEvent.ReminderTimeChanged(newValue))
                },
                label = { Text("Например: 09:00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                placeholder = { Text("Не задано") }


            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditHabitScreenPreview() {
    HabitTrackerAppTheme {
        AddEditHabitScreen(
            habitId = null,
            onNavigateBack = {}
        )
    }
}