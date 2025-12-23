package com.example.habittrackerapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.model.DayOfWeek
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.HabitWithCompletions
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.components.HabitCard
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.ui.viewmodel.HabitListEvent
import com.example.habittrackerapp.ui.viewmodel.HabitListViewModel
import java.time.LocalDate


data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HabitListScreen(
    onAddHabitClick: () -> Unit,
    onHabitClick: (String) -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("date") }
    var showSearch by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(HabitListEvent.Reload)
    }

    // Фильтрация и сортировка привычек
    val filteredAndSortedHabits = remember(state.habits, searchQuery, sortBy) {
        state.habits
            .filter { habitWithCompletions ->
                val habit = habitWithCompletions.habit
                searchQuery.isEmpty() || habit.name.contains(searchQuery, ignoreCase = true) ||
                        habit.description.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith(
                when (sortBy) {
                    "streak" -> compareByDescending<HabitWithCompletions> { it.currentStreak }
                    "priority" -> compareByDescending<HabitWithCompletions> {
                        when (it.habit.priority) {
                            Priority.HIGH -> 3
                            Priority.MEDIUM -> 2
                            Priority.LOW -> 1
                        }
                    }
                    "name" -> compareBy { it.habit.name }
                    else -> compareByDescending { it.habit.createdAt }
                }
            )
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Основной AppBar
                    TopAppBar(
                        title = {
                            Text(
                                "Мои привычки",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        actions = {
                            // Кнопка поиска
                            IconButton(
                                onClick = { showSearch = !showSearch }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Поиск",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Кнопка сортировки
                            var showSortMenu by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { showSortMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = "Сортировка",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("По дате создания") },
                                        onClick = { sortBy = "date"; showSortMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("По стрику") },
                                        onClick = { sortBy = "streak"; showSortMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Timeline,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("По приоритету") },
                                        onClick = { sortBy = "priority"; showSortMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Warning,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("По названию") },
                                        onClick = { sortBy = "name"; showSortMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.SortByAlpha,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )

                    // Поисковая строка с анимацией
                    AnimatedVisibility(
                        visible = showSearch,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                placeholder = { Text("Поиск привычек...") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Поиск",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Очистить",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                singleLine = true
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabitClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .size(60.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    )
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
                state.isLoading -> {
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
                                text = "Загружаем ваши привычки...",
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
                            shape = RoundedCornerShape(24.dp),
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
                                    text = "Что-то пошло не так",
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
                                    onClick = { viewModel.onEvent(HabitListEvent.Reload) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Повторить")
                                }
                            }
                        }
                    }
                }

                filteredAndSortedHabits.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .clip(RoundedCornerShape(28.dp)),
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
                                    Icons.Default.List,
                                    contentDescription = "Нет привычек",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (searchQuery.isEmpty()) {
                                        "Привычек пока нет"
                                    } else {
                                        "Ничего не найдено"
                                    },
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (searchQuery.isEmpty()) {
                                        "Начните свой путь к новым привычкам прямо сейчас"
                                    } else {
                                        "Попробуйте изменить поисковый запрос"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                if (searchQuery.isEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = onAddHabitClick,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Создать первую привычку")
                                    }
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
                        items(filteredAndSortedHabits) { habitWithCompletions ->
                            val habit = habitWithCompletions.habit
                            val isCompletedToday = habitWithCompletions.completedToday

                            // Вычисляем прогресс в зависимости от типа привычки
                            val (weeklyProgress, completedDaysCount, totalTargetDays, completedDaysOfWeek) = when (habit.type) {
                                HabitType.DAILY -> {
                                    // Для ежедневных: прогресс за последние 7 дней
                                    val completedCount = habitWithCompletions.completions
                                        .filter { it.completed }
                                        .filter { it.date >= LocalDate.now().minusDays(6) }
                                        .size
                                    Quadruple(completedCount / 7f, completedCount, 7, emptySet<DayOfWeek>())
                                }
                                HabitType.WEEKLY -> {
                                    // Для еженедельных: считаем выполненные дни из targetDays за текущую неделю
                                    val startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
                                    val targetDaysSet = habit.targetDays.toSet()

                                    // Получаем выполненные дни за текущую неделю
                                    val completedDates = habitWithCompletions.completions
                                        .filter { it.completed && it.date >= startOfWeek }
                                        .map { it.date }

                                    // Считаем сколько из targetDays было выполнено
                                    val completedTargetDays = completedDates.count { date ->
                                        val dayOfWeek = DayOfWeek.fromInt(date.dayOfWeek.value)
                                        targetDaysSet.contains(dayOfWeek)
                                    }

                                    // Собираем выполненные дни недели в Set
                                    val completedDaysOfWeek = completedDates.mapNotNull { date ->
                                        val dayOfWeek = DayOfWeek.fromInt(date.dayOfWeek.value)
                                        if (targetDaysSet.contains(dayOfWeek)) dayOfWeek else null
                                    }.toSet()

                                    val progress = if (targetDaysSet.isNotEmpty()) {
                                        completedTargetDays.toFloat() / targetDaysSet.size
                                    } else 0f

                                    Quadruple(progress, completedTargetDays, targetDaysSet.size, completedDaysOfWeek)
                                }
                            }

                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ) {
                                HabitCard(
                                    habit = habit.copy(currentStreak = habitWithCompletions.currentStreak),
                                    isCompletedToday = isCompletedToday,
                                    weeklyProgress = weeklyProgress,
                                    completedDaysCount = completedDaysCount,
                                    totalTargetDays = totalTargetDays,
                                    completedDaysOfWeek = completedDaysOfWeek, // Передаем выполненные дни
                                    onToggleCompletion = {
                                        viewModel.onEvent(
                                            HabitListEvent.ToggleCompletion(habit.id)
                                        )
                                    },
                                    onCardClick = { onHabitClick(habit.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}