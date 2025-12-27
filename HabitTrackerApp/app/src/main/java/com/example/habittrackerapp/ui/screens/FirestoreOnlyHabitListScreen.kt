package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.data.util.StreakCalculator
import com.example.habittrackerapp.domain.model.DayOfWeek
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.HabitWithCompletions
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.components.HabitCard
import com.example.habittrackerapp.ui.viewmodel.FirestoreOnlyHabitListEvent
import com.example.habittrackerapp.ui.viewmodel.FirestoreOnlyHabitListViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirestoreOnlyHabitListScreen(
    onAddHabitClick: () -> Unit,
    onHabitClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FirestoreOnlyHabitListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showTestDataDialog by remember { mutableStateOf(false) }
    var isInitialLoad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isInitialLoad) {
            viewModel.onEvent(FirestoreOnlyHabitListEvent.Reload)
            isInitialLoad = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Firestore-only привычки",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    // Кнопка для генерации тестовых данных
                    IconButton(
                        onClick = { showTestDataDialog = true },
                        enabled = !state.isGeneratingData
                    ) {
                        Icon(
                            imageVector = Icons.Default.DataUsage,
                            contentDescription = "Тестовые данные",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabitClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить привычку",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            when {
                state.isLoading || state.isGeneratingData -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (state.isGeneratingData) "Генерируем тестовые данные..."
                                else "Загружаем привычки...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Ошибка",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Ошибка",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.error.orEmpty(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.onEvent(FirestoreOnlyHabitListEvent.Reload) },
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text("Повторить")
                                }
                            }
                        }
                    }
                }

                state.habits.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.8f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.DataUsage,
                                    contentDescription = "Нет данных",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Нет привычек в Firestore",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Создайте привычку или сгенерируйте тестовые данные",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { showTestDataDialog = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Сгенерировать тестовые данные")
                                }
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.habits) { habitWithCompletions ->
                            val habit = habitWithCompletions.habit

                            // Проверяем, выполнена ли привычка сегодня
                            val isCompletedToday = habitWithCompletions.completions.any {
                                it.date == LocalDate.now() && it.completed
                            }

                            // Вычисляем текущий стрик
                            val completedDates = habitWithCompletions.completions
                                .filter { it.completed }
                                .map { it.date }
                                .toSet()

                            val (currentStreak, _) = StreakCalculator.calculateStreak(habit, completedDates)

                            // Вычисляем прогресс (как в обычном экране)
                            val (weeklyProgress, completedDaysCount, totalTargetDays) = when (habit.type) {
                                HabitType.DAILY -> {
                                    val completedCount = habitWithCompletions.completions
                                        .filter { it.completed }
                                        .filter { it.date >= LocalDate.now().minusDays(6) }
                                        .size
                                    Triple(completedCount / 7f, completedCount, 7)
                                }
                                HabitType.WEEKLY -> {
                                    val startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
                                    val targetDaysSet = habit.targetDays.toSet()

                                    val completedDatesList = habitWithCompletions.completions
                                        .filter { it.completed && it.date >= startOfWeek }
                                        .map { it.date }

                                    val completedTargetDays = completedDatesList.count { date ->
                                        val dayOfWeek = DayOfWeek.fromInt(date.dayOfWeek.value)
                                        targetDaysSet.contains(dayOfWeek)
                                    }

                                    val progress = if (targetDaysSet.isNotEmpty()) {
                                        completedTargetDays.toFloat() / targetDaysSet.size
                                    } else 0f

                                    Triple(progress, completedTargetDays, targetDaysSet.size)
                                }
                            }

                            HabitCard(
                                habit = habit.copy(currentStreak = currentStreak),
                                isCompletedToday = isCompletedToday,
                                weeklyProgress = weeklyProgress,
                                completedDaysCount = completedDaysCount,
                                totalTargetDays = totalTargetDays,
                                completedDaysOfWeek = emptySet(),
                                onToggleCompletion = {
                                    viewModel.onEvent(
                                        FirestoreOnlyHabitListEvent.ToggleCompletion(habit.id)
                                    )
                                },
                                onCardClick = { onHabitClick(habit.id) }
                            )
                        }
                    }
                }
            }
        }

        // Диалог для генерации тестовых данных
        if (showTestDataDialog) {
            AlertDialog(
                onDismissRequest = { showTestDataDialog = false },
                title = { Text("Генерация тестовых данных") },
                text = {
                    Text("Сколько привычек сгенерировать? Привычки будут созданы с тестовыми выполнениями для демонстрации стриков.")
                },
                confirmButton = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.onEvent(FirestoreOnlyHabitListEvent.GenerateTestData(1))
                                    showTestDataDialog = false
                                }
                            ) {
                                Text("1 привычка")
                            }
                            Button(
                                onClick = {
                                    viewModel.onEvent(FirestoreOnlyHabitListEvent.GenerateTestData(10))
                                    showTestDataDialog = false
                                }
                            ) {
                                Text("10 привычек")
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.onEvent(FirestoreOnlyHabitListEvent.GenerateTestData(100))
                                    showTestDataDialog = false
                                }
                            ) {
                                Text("100 привычек")
                            }
                            Button(
                                onClick = {
                                    viewModel.onEvent(FirestoreOnlyHabitListEvent.ClearAllData)
                                    showTestDataDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Text("Очистить всё")
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTestDataDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

//private fun calculateDailyStreak(completedDates: Set<LocalDate>): Pair<Int, LocalDate?> {
//    var currentStreak = 0
//    var currentDate = LocalDate.now()
//    val sortedDates = completedDates.sortedDescending()
//    val lastCompleted = sortedDates.firstOrNull()
//
//    while (completedDates.contains(currentDate) ||
//        (currentStreak == 0 && completedDates.contains(currentDate.minusDays(1)))) {
//        currentStreak++
//        currentDate = currentDate.minusDays(1)
//    }
//
//    return Pair(currentStreak, lastCompleted)
//}
//
//private fun calculateWeeklyStreak(habit: com.example.habittrackerapp.domain.model.Habit, completedDates: Set<LocalDate>): Pair<Int, LocalDate?> {
//    val targetDaysSet = habit.targetDays.toSet()
//    if (targetDaysSet.isEmpty()) return Pair(0, null)
//
//    var currentStreak = 0
//    var weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
//    val lastCompleted = completedDates.maxOrNull()
//
//    while (true) {
//        val weekEnd = weekStart.plusDays(6)
//        val weekCompletedDates = completedDates.filter {
//            it >= weekStart && it <= weekEnd
//        }
//
//        val completedDaysInWeek = weekCompletedDates.map { date ->
//            DayOfWeek.fromInt(date.dayOfWeek.value)
//        }.toSet()
//
//        val isWeekCompleted = targetDaysSet.all { it in completedDaysInWeek }
//
//        if (isWeekCompleted) {
//            currentStreak++
//            weekStart = weekStart.minusWeeks(1)
//        } else {
//            break
//        }
//    }
//
//    return Pair(currentStreak, lastCompleted)
//}

