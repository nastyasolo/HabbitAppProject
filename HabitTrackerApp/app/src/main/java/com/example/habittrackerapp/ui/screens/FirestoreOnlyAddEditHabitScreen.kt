package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.model.DayOfWeek
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.components.PriorityChip
import com.example.habittrackerapp.ui.viewmodel.FirestoreOnlyAddEditHabitEvent
import com.example.habittrackerapp.ui.viewmodel.FirestoreOnlyAddEditHabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirestoreOnlyAddEditHabitScreen(
    habitId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: FirestoreOnlyAddEditHabitViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(habitId) {
        viewModel.loadHabit(habitId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (habitId == null) "Новая привычка (Firestore)"
                        else "Редактировать (Firestore)",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (state.isValid) {
                                viewModel.saveHabit()
                                onNavigateBack()
                            }
                        },
                        enabled = state.isValid
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Сохранить"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Название
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.NameChanged(it)) },
                    label = { Text("Название привычки") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.nameError != null,
                    supportingText = {
                        state.nameError?.let { Text(it) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Описание
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.DescriptionChanged(it)) },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Тип привычки
                Text("Тип привычки", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.type == HabitType.DAILY,
                        onClick = { viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.TypeChanged(HabitType.DAILY)) },
                        label = { Text("Ежедневная") }
                    )
                    FilterChip(
                        selected = state.type == HabitType.WEEKLY,
                        onClick = { viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.TypeChanged(HabitType.WEEKLY)) },
                        label = { Text("Еженедельная") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Приоритет
                Text("Приоритет", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        PriorityChip(
                            priority = priority,
                            isSelected = state.priority == priority,
                            onClick = { viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.PriorityChanged(priority)) }
                        )
                    }
                }

                // Для WEEKLY привычек - выбор дней
                if (state.type == HabitType.WEEKLY) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Дни недели для выполнения", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DayOfWeek.entries.forEach { day ->
                            val isSelected = state.targetDays.contains(day)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newDays = if (isSelected) {
                                        state.targetDays.toMutableList().apply { remove(day) }
                                    } else {
                                        state.targetDays.toMutableList().apply { add(day) }
                                    }
                                    viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.TargetDaysChanged(newDays))
                                },
                                label = { Text(getShortDayName(day)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Переключатель напоминания
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Напоминание", style = MaterialTheme.typography.labelLarge)
                    Switch(
                        checked = state.hasReminder,
                        onCheckedChange = { viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.HasReminderChanged(it)) }
                    )
                }

                if (state.hasReminder) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.reminderTime ?: "",
                        onValueChange = { viewModel.onEvent(FirestoreOnlyAddEditHabitEvent.ReminderTimeChanged(it.takeIf { it.isNotBlank() })) },
                        label = { Text("Время напоминания (ЧЧ:ММ)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("09:00") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка сохранения
                Button(
                    onClick = {
                        if (state.isValid) {
                            viewModel.saveHabit()
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isValid
                ) {
                    Text("Сохранить привычку в Firestore")
                }
            }
        }
    )
}

private fun getShortDayName(day: DayOfWeek): String {
    return when (day) {
        DayOfWeek.MONDAY -> "Пн"
        DayOfWeek.TUESDAY -> "Вт"
        DayOfWeek.WEDNESDAY -> "Ср"
        DayOfWeek.THURSDAY -> "Чт"
        DayOfWeek.FRIDAY -> "Пт"
        DayOfWeek.SATURDAY -> "Сб"
        DayOfWeek.SUNDAY -> "Вс"
    }
}