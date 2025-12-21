package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.model.Category
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.components.DatePickerDialog
import com.example.habittrackerapp.ui.components.TimePickerDialog
import com.example.habittrackerapp.ui.viewmodel.AddEditTaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: String?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {
    val task by viewModel.task.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Новая задача" else "Редактировать задачу") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(
                            onClick = {
                                println("DEBUG: Save button clicked, task: ${task?.title}")
                                viewModel.saveTask(onSuccess = onNavigateBack)
                            },
                            enabled = task?.title?.isNotBlank() == true
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Сохранить")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Название задачи
                OutlinedTextField(
                    value = task!!.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Название задачи") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    isError = task!!.title.isBlank(),
                    supportingText = {
                        if (task!!.title.isBlank()) {
                            Text("Название обязательно")
                        }
                    }
                )

                // Описание
                OutlinedTextField(
                    value = task!!.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Описание") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    singleLine = false,
                    maxLines = 4
                )

                // Срок выполнения
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                OutlinedTextField(
                    value = task!!.dueDate?.format(formatter) ?: "",
                    onValueChange = { },
                    label = { Text("Срок выполнения") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Выбрать дату")
                        }
                    }
                )

                // Приоритет
                Text(
                    text = "Приоритет",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        FilterChip(
                            selected = task!!.priority == priority,
                            onClick = { viewModel.updatePriority(priority) },
                            label = { Text(getPriorityLabel(priority)) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (priority) {
                                    Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                    Priority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                                    Priority.LOW -> MaterialTheme.colorScheme.primaryContainer
                                },
                                selectedLabelColor = when (priority) {
                                    Priority.HIGH -> MaterialTheme.colorScheme.error
                                    Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                                    Priority.LOW -> MaterialTheme.colorScheme.primary
                                }
                            )
                        )
                    }
                }

                // Категория
                Text(
                    text = "Категория",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.values().take(3).forEach { category ->
                        FilterChip(
                            selected = task!!.category == category,
                            onClick = { viewModel.updateCategory(category) },
                            label = { Text(getCategoryLabel(category)) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.values().drop(3).forEach { category ->
                        FilterChip(
                            selected = task!!.category == category,
                            onClick = { viewModel.updateCategory(category) },
                            label = { Text(getCategoryLabel(category)) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                // Время выполнения
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = task!!.dueTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                        onValueChange = { },
                        label = { Text("Время (ЧЧ:ММ)") },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showTimePicker = true },
                        readOnly = true,
                        placeholder = { Text("Не указано") },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Выбрать время")
                            }
                        }
                    )

                    Button(
                        onClick = {
                            viewModel.updateDueTime(null)
                        },
                        modifier = Modifier.weight(0.4f),
                        enabled = task!!.dueTime != null
                    ) {
                        Text("Сброс")
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                initialDate = task?.dueDate,
                onDateSelected = { date ->
                    viewModel.updateDueDate(date)
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
        if (showTimePicker) {
            TimePickerDialog(
                initialTime = task?.dueTime,
                onTimeSelected = { time ->
                    viewModel.updateDueTime(time)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }

    }
}

private fun getPriorityLabel(priority: Priority): String {
    return when (priority) {
        Priority.HIGH -> "Высокий"
        Priority.MEDIUM -> "Средний"
        Priority.LOW -> "Низкий"
    }
}

private fun getCategoryLabel(category: Category): String {
    return when (category) {
        Category.WORK -> "Работа"
        Category.PERSONAL -> "Личное"
        Category.HEALTH -> "Здоровье"
        Category.EDUCATION -> "Обучение"
        Category.FINANCE -> "Финансы"
        Category.OTHER -> "Другое"
    }
}