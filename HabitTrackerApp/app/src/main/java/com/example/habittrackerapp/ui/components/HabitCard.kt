package com.example.habittrackerapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerapp.domain.model.DayOfWeek
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.theme.*
import java.time.LocalDate

@Composable
fun ModernChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color,
        tonalElevation = 2.dp,
        modifier = modifier.height(28.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCard(
    habit: Habit,
    isCompletedToday: Boolean,
    weeklyProgress: Float = 0f,
    completedDaysCount: Int = 0, //количество выполненных дней
    totalTargetDays: Int = 7,
    completedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    onToggleCompletion: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = weeklyProgress,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    val cardElevation by animateFloatAsState(
        targetValue = if (isCompletedToday) 8f else 2f,
        animationSpec = tween(durationMillis = 300),
        label = "elevation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isCompletedToday) 1.02f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )

    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompletedToday) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isCompletedToday) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isCompletedToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 200f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Заголовок и статус выполнения
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Приоритетный индикатор
                    Box(
                        modifier = Modifier
                            .size(4.dp, 24.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when (habit.priority) {
                                    Priority.HIGH -> MaterialTheme.colorScheme.error
                                    Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                                    Priority.LOW -> MaterialTheme.colorScheme.primary
                                }
                            )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isCompletedToday) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )

                        if (habit.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = habit.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isCompletedToday) {
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp
                            )
                        }

                        // Время напоминания
                        habit.reminderTime?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isCompletedToday) {
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isCompletedToday) {
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                        }
                    }

                    // Красивый переключатель выполнения
                    Surface(
                        onClick = onToggleCompletion,
                        shape = CircleShape,
                        color = if (isCompletedToday) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        tonalElevation = 4.dp,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (isCompletedToday) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Выполнено",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Отметить",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Статистика и теги
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Тип и приоритет
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModernChip(
                            text = habit.type.displayName,
                            icon = if (habit.type == HabitType.DAILY) Icons.Default.Today else Icons.Default.DateRange,
                            color = if (isCompletedToday) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                            },
                            textColor = if (isCompletedToday) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            }
                        )

                        ModernChip(
                            text = habit.priority.displayName,
                            icon = when (habit.priority) {
                                Priority.HIGH -> Icons.Default.Warning
                                Priority.MEDIUM -> Icons.Default.Info
                                Priority.LOW -> Icons.Default.CheckCircle
                            },
                            color = when (habit.priority) {
                                Priority.HIGH -> if (isCompletedToday) MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                else PriorityHighContainer.copy(alpha = 0.8f)
                                Priority.MEDIUM -> if (isCompletedToday) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                else PriorityMediumContainer.copy(alpha = 0.8f)
                                Priority.LOW -> if (isCompletedToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else PriorityLowContainer.copy(alpha = 0.8f)
                            },
                            textColor = when (habit.priority) {
                                Priority.HIGH -> if (isCompletedToday) MaterialTheme.colorScheme.onPrimaryContainer
                                else PriorityHigh
                                Priority.MEDIUM -> if (isCompletedToday) MaterialTheme.colorScheme.onPrimaryContainer
                                else PriorityMedium
                                Priority.LOW -> if (isCompletedToday) MaterialTheme.colorScheme.onPrimaryContainer
                                else PriorityLow
                            }
                        )
                    }

                    // Стрик
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCompletedToday) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        },
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Timeline,
                                contentDescription = "Стрик",
                                modifier = Modifier.size(16.dp),
                                tint = if (isCompletedToday) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${habit.currentStreak}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isCompletedToday) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Прогресс недели
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (habit.type == HabitType.WEEKLY) "Прогресс недели" else "Прогресс за неделю",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isCompletedToday) {
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Text(
                            text = if (habit.type == HabitType.WEEKLY) {
                                if (totalTargetDays > 0) "$completedDaysCount/${totalTargetDays}" else "0/0"
                            } else {
                                "${(animatedProgress * 100).toInt()}%"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isCompletedToday) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Красивая прогресс-бар с анимацией
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                )
                        )
                    }

                    // Индикаторы дней недели под прогресс-баром
                    if (habit.type == HabitType.WEEKLY) {
                        Spacer(modifier = Modifier.height(12.dp))
                        WeeklyDayIndicators(
                            targetDays = habit.targetDays,
                            completedDaysOfWeek = completedDaysOfWeek, // Передаем выполненные дни
                            isCompletedToday = isCompletedToday
                        )
                    }
                }

            }

            // Декоративный уголок для выполненных привычек
            if (isCompletedToday) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(60.dp)
                        .offset(x = 20.dp, y = (-20).dp)
                        .rotate(45f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                radius = 60f
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun WeeklyDayIndicators(
    targetDays: List<DayOfWeek>,
    completedDaysOfWeek: Set<DayOfWeek>,
    isCompletedToday: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val dayNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

        dayNames.forEachIndexed { index, dayName ->
            val dayOfWeek = DayOfWeek.fromInt(index + 1)
            val isActive = targetDays.contains(dayOfWeek)
            val isCompleted = completedDaysOfWeek.contains(dayOfWeek) // Теперь используем реальные выполненные дни

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        color = when {
                            !isActive -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                            isCompleted -> if (isCompletedToday) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        }
                    )
                    .border(
                        width = if (isActive) 1.5.dp else 0.dp,
                        color = if (isCompletedToday) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        },
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        !isActive -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        isCompleted -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}


// Вспомогательная функция для проверки дня недели
fun isTodayDayOfWeek(dayOfWeek: DayOfWeek): Boolean {
    val today = java.time.LocalDate.now()
    val todayDayOfWeek = today.dayOfWeek.value // 1-7 (Monday-Sunday)
    return DayOfWeek.fromInt(todayDayOfWeek) == dayOfWeek
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    HabitTrackerAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HabitCard(
                habit = Habit(
                    name = "Утренняя зарядка",
                    description = "15 минут упражнений для бодрости",
                    type = HabitType.DAILY,
                    priority = Priority.HIGH,
                    reminderTime = "07:00",
                    currentStreak = 21
                ),
                isCompletedToday = false,
                weeklyProgress = 0.6f,
                onToggleCompletion = {},
                onCardClick = {}
            )

            HabitCard(
                habit = Habit(
                    name = "Чтение книги",
                    description = "30 минут чтения перед сном",
                    type = HabitType.WEEKLY,
                    priority = Priority.LOW,
                    reminderTime = "21:30",
                    currentStreak = 14,
                    targetDays = listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.FRIDAY
                    )
                ),
                isCompletedToday = true,
                weeklyProgress = 0.85f,
                onToggleCompletion = {},
                onCardClick = {}
            )
        }
    }
}