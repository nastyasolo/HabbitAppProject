package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.model.DayOfWeek // Ваш кастомный DayOfWeek
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.components.PriorityChip
import com.example.habittrackerapp.ui.components.UniversalTimePickerDialog
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
    val state by viewModel.state.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    // Анимация прокрутки для AppBar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(habitId) {
        viewModel.loadHabit(habitId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (habitId == null) "Новая привычка" else "Редактировать",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurface
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
                        enabled = state.isValid,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Сохранить",
                            tint = if (state.isValid)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Заголовок формы
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = if (habitId == null) "Создайте новую привычку" else "Редактируйте привычку",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Малыми шагами к большим изменениям",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Карточка с формой
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Название привычки
                        Column {
                            Text(
                                text = "Название привычки *",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = state.name,
                                onValueChange = { viewModel.onEvent(AddEditHabitEvent.NameChanged(it)) },
                                label = { Text("Например: Утренняя зарядка") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    errorBorderColor = MaterialTheme.colorScheme.error,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                isError = state.nameError != null,
                                singleLine = true,
                                maxLines = 1, // Фиксируем одну строку
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Create,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    )
                                }
                            )
                            state.nameError?.let { errorText ->
                                Text(
                                    text = errorText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // Описание
                        Column {
                            Text(
                                text = "Описание (необязательно)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = state.description,
                                onValueChange = { viewModel.onEvent(AddEditHabitEvent.DescriptionChanged(it)) },
                                label = { Text("Краткое описание привычки") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        }

                        // Тип привычки
                        Column {
                            Text(
                                text = "Тип привычки",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                HabitType.values().forEach { type ->
                                    val isSelected = state.type == type
                                    ElevatedFilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.onEvent(AddEditHabitEvent.TypeChanged(type)) },
                                        label = {
                                            Text(
                                                type.displayName,
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        },
                                        colors = FilterChipDefaults.elevatedFilterChipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else null,
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                                            selectedBorderColor = MaterialTheme.colorScheme.secondary,
                                            borderWidth = 1.dp,
                                            selectedBorderWidth = 2.dp
                                        ),
                                        elevation = FilterChipDefaults.elevatedFilterChipElevation(),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        // Приоритет
                        Column {
                            Text(
                                text = "Приоритет",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Priority.values().forEach { priority ->
                                    PriorityChip(
                                        priority = priority,
                                        isSelected = state.priority == priority,
                                        onClick = { viewModel.onEvent(AddEditHabitEvent.PriorityChanged(priority)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Переключатель напоминания
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Включить напоминание",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Switch(
                                checked = state.hasReminder,
                                onCheckedChange = {
                                    viewModel.onEvent(AddEditHabitEvent.HasReminderChanged(it))
                                    if (!it) {
                                        viewModel.onEvent(AddEditHabitEvent.ReminderTimeChanged(null))
                                    }
                                }
                            )
                        }

                        // Показывать TimePicker только если напоминание включено
                        if (state.hasReminder) {
                            // Время напоминания
                            Column {
                                Text(
                                    text = "Время напоминания",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                OutlinedTextField(
                                    value = state.reminderTime ?: "",
                                    onValueChange = { },
                                    label = { Text("Например: 09:00") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTimePicker = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    placeholder = { Text("ЧЧ:ММ") },
                                    leadingIcon = {
                                        IconButton(
                                            onClick = { showTimePicker = true },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = "Выбрать время",
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        if (state.reminderTime != null) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.onEvent(AddEditHabitEvent.ReminderTimeChanged(null))
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Очистить",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    },
                                    readOnly = true,
                                    singleLine = true,
                                    maxLines = 1
                                )

                                // Добавить текст о том, что для WEEKLY привычек можно выбрать дни
                                if (state.type == HabitType.WEEKLY) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Выберите дни для напоминания:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    // Добавить выбор дней недели для WEEKLY привычек
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        // Используем ваш кастомный DayOfWeek
                                        val daysOfWeek = DayOfWeek.entries
                                        daysOfWeek.forEach { day ->
                                            val isSelected = state.reminderDays.contains(day)
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = {
                                                    val currentDays = state.reminderDays
                                                    val newDays = if (isSelected) {
                                                        currentDays.toMutableList().apply { remove(day) }
                                                    } else {
                                                        currentDays.toMutableList().apply { add(day) }
                                                    }
                                                    viewModel.onEvent(AddEditHabitEvent.ReminderDaysChanged(newDays))
                                                },
                                                label = { Text(getDayName(day)) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "Напоминание поможет не забыть о привычке",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }
                    }
                }
                if (showTimePicker) {
                    UniversalTimePickerDialog(
                        initialTime = state.reminderTime,
                        onTimeSelected = { time ->
                            viewModel.onEvent(AddEditHabitEvent.ReminderTimeChanged(time))
                            showTimePicker = false
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}

private fun getDayName(day: DayOfWeek): String {
    // Проверьте, какие значения есть в вашем enum DayOfWeek
    return when (day) {
        DayOfWeek.MONDAY -> "Пн"
        DayOfWeek.TUESDAY -> "Вт"
        DayOfWeek.WEDNESDAY -> "Ср"
        DayOfWeek.THURSDAY -> "Чт"
        DayOfWeek.FRIDAY -> "Пт"
        DayOfWeek.SATURDAY -> "Сб"
        DayOfWeek.SUNDAY -> "Вс"
        else -> "?"
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