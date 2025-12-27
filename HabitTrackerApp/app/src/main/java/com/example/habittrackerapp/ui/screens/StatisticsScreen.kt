package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.ui.viewmodel.HabitListViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Статистические данные
    val totalHabits = state.habits.size
    val completedToday = state.habits.count { it.completedToday }
    val totalCompletions = state.habits.sumOf { it.completions.count { c -> c.completed } }
    val averageStreak = if (totalHabits > 0) state.habits.map { it.habit.currentStreak }.average().toInt() else 0
    val maxStreak = if (state.habits.isNotEmpty()) state.habits.maxOf { it.habit.currentStreak } else 0
    // Распределение по приоритетам
    val priorityStats = mapOf(
        Priority.HIGH to state.habits.count { it.habit.priority == Priority.HIGH },
        Priority.MEDIUM to state.habits.count { it.habit.priority == Priority.MEDIUM },
        Priority.LOW to state.habits.count { it.habit.priority == Priority.LOW }
    )

    // Распределение по типам
    val typeStats = mapOf(
        HabitType.DAILY to state.habits.count { it.habit.type == HabitType.DAILY },
        HabitType.WEEKLY to state.habits.count { it.habit.type == HabitType.WEEKLY }
    )

    // Топ привычек по серии
    val topHabits = state.habits.sortedByDescending { it.habit.currentStreak }.take(5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Статистика",
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Обзор статистики
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Обзор статистики",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                Icons.Default.Insights,
                                contentDescription = "Статистика",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Основные метрики
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatMetric(
                                title = "Всего привычек",
                                value = totalHabits.toString(),
                                icon = Icons.Default.List,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )

                            StatMetric(
                                title = "Выполнено сегодня",
                                value = completedToday.toString(),
                                icon = Icons.Default.CheckCircle,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatMetric(
                                title = "Средняя серия",
                                value = averageStreak.toString(),
                                icon = Icons.Default.TrendingUp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f)
                            )

                            StatMetric(
                                title = "Макс. серия",
                                value = maxStreak.toString(),
                                icon = Icons.Default.Star,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Топ привычек по серии
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Топ привычек",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = "Топ привычек",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (topHabits.isEmpty()) {
                            Text(
                                text = "Привычек пока нет",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            topHabits.forEachIndexed { index, habitWithCompletions ->
                                TopHabitItem(
                                    habitWithCompletions = habitWithCompletions,
                                    position = index + 1,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItemPlacement()
                                )

                                if (index < topHabits.lastIndex) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Распределение по приоритетам
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "По приоритетам",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                Icons.Default.PriorityHigh,
                                contentDescription = "Приоритеты",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PriorityDistribution(priorityStats = priorityStats)
                    }
                }
            }

            // Распределение по типам
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "По типам",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                Icons.Default.Category,
                                contentDescription = "Типы",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TypeDistribution(typeStats = typeStats)
                    }
                }
            }

            // Еженедельный прогресс
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Прогресс за неделю",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Неделя",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        WeeklyProgress(
                            habits = state.habits,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Интересные факты
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Интересные факты",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = "Факты",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        InterestingFacts(
                            habits = state.habits,
                            totalCompletions = totalCompletions,
                            averageStreak = averageStreak
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetric(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

@Composable
fun TopHabitItem(
    habitWithCompletions: com.example.habittrackerapp.domain.model.HabitWithCompletions,
    position: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Позиция с медалью
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (position) {
                        1 -> Color(0xFFFFD700).copy(alpha = 0.2f)
                        2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                        3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = position.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = when (position) {
                    1 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Информация о привычке
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = habitWithCompletions.habit.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            if (habitWithCompletions.habit.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = habitWithCompletions.habit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Серия
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timeline,
                    contentDescription = "Серия",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${habitWithCompletions.habit.currentStreak}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PriorityDistribution(
    priorityStats: Map<Priority, Int>,
    modifier: Modifier = Modifier
) {
    val total = priorityStats.values.sum()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        priorityStats.forEach { (priority, count) ->
            val percentage = if (total > 0) (count.toFloat() / total * 100).roundToInt() else 0
            val color = when (priority) {
                Priority.HIGH -> MaterialTheme.colorScheme.error
                Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                Priority.LOW -> MaterialTheme.colorScheme.primary
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = priority.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TypeDistribution(
    typeStats: Map<HabitType, Int>,
    modifier: Modifier = Modifier
) {
    val total = typeStats.values.sum()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        typeStats.forEach { (type, count) ->
            val percentage = if (total > 0) (count.toFloat() / total * 100).roundToInt() else 0
            val color = when (type) {
                HabitType.DAILY -> MaterialTheme.colorScheme.primary
                HabitType.WEEKLY -> MaterialTheme.colorScheme.secondary
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (type == HabitType.DAILY) Icons.Default.Today else Icons.Default.DateRange,
                        contentDescription = type.displayName,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyProgress(
    habits: List<com.example.habittrackerapp.domain.model.HabitWithCompletions>,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val maxCompletions = habits.size.coerceAtLeast(1) // Максимальное значение для шкалы

    Column(modifier = modifier) {
        // Диаграмма
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            daysOfWeek.forEachIndexed { index, day ->
                val completions = habits.sumOf { habit ->
                    habit.completions.count {
                        it.completed && it.date == LocalDate.now().minusDays((6 - index).toLong())
                    }
                }

                val heightPercentage = (completions.toFloat() / maxCompletions).coerceIn(0f, 1f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Столбец
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height((120 * heightPercentage).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // День недели
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Количество
                    Text(
                        text = completions.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun InterestingFacts(
    habits: List<com.example.habittrackerapp.domain.model.HabitWithCompletions>,
    totalCompletions: Int,
    averageStreak: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Факт 1: Общее количество выполненных задач
        if (totalCompletions > 0) {
            FactItem(
                icon = Icons.Default.Check,
                title = "Всего выполнено",
                description = "Вы завершили $totalCompletions привычек"
            )
        }

        // Факт 2: Средняя серия
        if (averageStreak > 0) {
            FactItem(
                icon = Icons.Default.Timeline,
                title = "Средняя серия",
                description = "Ваша средняя серия составляет $averageStreak дней"
            )
        }

        // Факт 3: Самая старая привычка
        val oldestHabit = habits.minByOrNull { it.habit.createdAt }
        oldestHabit?.let {
            val daysSinceCreation = (System.currentTimeMillis() - it.habit.createdAt) / (1000 * 60 * 60 * 24)
            if (daysSinceCreation > 0) {
                FactItem(
                    icon = Icons.Default.History,
                    title = "Самая старая привычка",
                    description = "${it.habit.name} существует уже $daysSinceCreation дней"
                )
            }
        }

        // Факт 4: Привычки без напоминаний
        val habitsWithoutReminder = habits.count { !it.habit.hasReminder }
        if (habitsWithoutReminder > 0) {
            FactItem(
                icon = Icons.Default.NotificationsOff,
                title = "Привычки без напоминаний",
                description = "$habitsWithoutReminder привычек можно снабдить напоминаниями"
            )
        }

        // Факт 5: Консистентность
        if (habits.isNotEmpty()) {
            val completedTodayCount = habits.count { it.completedToday }
            val consistencyRate = (completedTodayCount.toFloat() / habits.size * 100).roundToInt()
            FactItem(
                icon = Icons.Default.TrendingUp,
                title = "Консистентность",
                description = "Сегодня вы выполнили $consistencyRate% привычек"
            )
        }
    }
}

@Composable
fun FactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    HabitTrackerAppTheme {
        StatisticsScreen(onNavigateBack = {})
    }
}