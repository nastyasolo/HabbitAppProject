package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.model.Category
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.components.DatePickerDialog
import com.example.habittrackerapp.ui.components.UniversalTimePickerDialog
import com.example.habittrackerapp.ui.viewmodel.AddEditTaskViewModel
import com.example.habittrackerapp.utils.TaskUtils
import java.time.LocalDate
import java.time.LocalTime
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
    var showReminderTimePicker by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (taskId == null) "Новая задача" else "Редактировать задачу",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
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
                            enabled = task?.title?.isNotBlank() == true,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Сохранить",
                                tint = if (task?.title?.isNotBlank() == true)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Карточка с формой
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Название задачи
                        Column {
                            Text(
                                text = "Название задачи *",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = task!!.title,
                                onValueChange = { viewModel.updateTitle(it) },
                                label = { Text("Введите название") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                ),
                                singleLine = true,
                                isError = task!!.title.isBlank(),
                                supportingText = {
                                    if (task!!.title.isBlank()) {
                                        Text(
                                            "Название обязательно",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            )
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
                                value = task!!.description,
                                onValueChange = { viewModel.updateDescription(it) },
                                label = { Text("Добавьте описание") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                maxLines = 4
                            )
                        }

                        // Секция: Срок выполнения
                        Column {
                            Text(
                                text = "Срок выполнения",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Дата и время выполнения в одной строке
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Дата выполнения
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Дата",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = task!!.dueDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "Не установлена",
                                        onValueChange = { },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showDatePicker = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        readOnly = true,
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = { showDatePicker = true }) {
                                                Icon(
                                                    Icons.Default.CalendarToday,
                                                    contentDescription = "Выбрать дату",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    )
                                }

                                // Время выполнения
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Время (ЧЧ:ММ)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = task!!.dueTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Не установлено",
                                        onValueChange = { },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showTimePicker = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        readOnly = true,
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = { showTimePicker = true }) {
                                                Icon(
                                                    Icons.Default.Schedule,
                                                    contentDescription = "Выбрать время",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    )
                                }
                            }

                            // Кнопка сброса даты/времени
                            if (task!!.dueDate != null || task!!.dueTime != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = {
                                        viewModel.updateDueDate(null)
                                        viewModel.updateDueTime(null)
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Сбросить",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Сбросить срок")
                                }
                            }
                        }

                        // Секция: Напоминание
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Напоминание",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (task?.hasReminder == true && task?.reminderTime != null) {
                                        Text(
                                            text = "Уведомление придет в ${task?.reminderTime?.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                Switch(
                                    checked = task?.hasReminder ?: false,
                                    onCheckedChange = {
                                        viewModel.updateHasReminder(it)
                                        // Если выключаем напоминание, очищаем время
                                        if (!it) {
                                            viewModel.updateReminderTime(null)
                                        }
                                    }
                                )
                            }

                            if (task?.hasReminder == true) {
                                Spacer(modifier = Modifier.height(8.dp))

                                // Контейнер для выбора времени напоминания
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Поле для отображения времени (не кликабельное)
                                    OutlinedTextField(
                                        value = task?.reminderTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                                        onValueChange = { }, // Не изменяем напрямую
                                        label = { Text("Время напоминания") },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        readOnly = true,
                                        placeholder = { Text("Выберите время") },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Notifications,
                                                contentDescription = "Напоминание",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        trailingIcon = {
                                            Row {
                                                // Кнопка выбора времени
                                                if (task?.reminderTime == null) {
                                                    IconButton(
                                                        onClick = { showReminderTimePicker = true }
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Schedule,
                                                            contentDescription = "Выбрать время",
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }

                                                // Кнопка очистки времени
                                                if (task?.reminderTime != null) {
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.updateReminderTime(null)
                                                        }
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Clear,
                                                            contentDescription = "Очистить",
                                                            tint = MaterialTheme.colorScheme.error
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    )

                                    // Накладываем невидимую кнопку на все поле для клика
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable(
                                                indication = null, // Без визуальной обратной связи
                                                interactionSource = remember { MutableInteractionSource() }
                                            ) {
                                                if (task?.hasReminder == true) {
                                                    showReminderTimePicker = true
                                                }
                                            }
                                    )
                                }

                                // Подсказка о напоминании
                                Text(
                                    text = "Напоминание поможет не забыть о задаче",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
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
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Priority.values().forEach { priority ->
                                    FilterChip(
                                        selected = task!!.priority == priority,
                                        onClick = { viewModel.updatePriority(priority) },
                                        label = {
                                            Text(
                                                TaskUtils.getPriorityLabel(priority),
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = when (priority) {
                                                Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                                Priority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                                                Priority.LOW -> MaterialTheme.colorScheme.primaryContainer
                                            },
                                            selectedLabelColor = when (priority) {
                                                Priority.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                                Priority.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
                                                Priority.LOW -> MaterialTheme.colorScheme.onPrimaryContainer
                                            }
                                        ),
                                        leadingIcon = if (task!!.priority == priority) {
                                            {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else null
                                    )
                                }
                            }
                        }

                        // Категория
                        Column {
                            Text(
                                text = "Категория",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Первая строка категорий
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Category.values().take(3).forEach { category ->
                                    FilterChip(
                                        selected = task!!.category == category,
                                        onClick = { viewModel.updateCategory(category) },
                                        label = {
                                            Text(
                                                TaskUtils.getCategoryLabel(category),
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        leadingIcon = if (task!!.category == category) {
                                            {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else null
                                    )
                                }
                            }

                            // Вторая строка категорий
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Category.values().drop(3).forEach { category ->
                                    FilterChip(
                                        selected = task!!.category == category,
                                        onClick = { viewModel.updateCategory(category) },
                                        label = {
                                            Text(
                                                TaskUtils.getCategoryLabel(category),
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        leadingIcon = if (task!!.category == category) {
                                            {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else null
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // DatePicker
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

        // TimePicker для времени выполнения
        if (showTimePicker) {
            UniversalTimePickerDialog(
                initialTime = task?.dueTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
                onTimeSelected = { timeString ->
                    val time = if (timeString != null) {
                        LocalTime.parse(timeString)
                    } else {
                        null
                    }
                    viewModel.updateDueTime(time)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }

        // TimePicker для напоминания
        if (showReminderTimePicker) {
            UniversalTimePickerDialog(
                initialTime = task?.reminderTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
                onTimeSelected = { timeString ->
                    val time = if (timeString != null) {
                        LocalTime.parse(timeString)
                    } else {
                        null
                    }
                    viewModel.updateReminderTime(time)
                    showReminderTimePicker = false
                },
                onDismiss = { showReminderTimePicker = false }
            )
        }
    }
}

fun getPriorityIcon(priority: Priority): ImageVector {
    return when (priority) {
        Priority.HIGH -> Icons.Filled.Warning
        Priority.MEDIUM -> Icons.Filled.Info
        Priority.LOW -> Icons.Filled.CheckCircle
    }
}