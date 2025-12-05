package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCard(
    habit: Habit,
    onToggleCompletion: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (habit.priority) {
                Priority.HIGH -> Color(0xFFFFEBEE) // Light red
                Priority.MEDIUM -> Color(0xFFE8F5E9) // Light green
                Priority.LOW -> Color(0xFFE3F2FD) // Light blue
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = habit.type.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Стрик: ${habit.streak}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = { onToggleCompletion() },
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    HabitTrackerAppTheme {
        HabitCard(
            habit = Habit(
                id = "1",
                name = "Утренняя зарядка",
                description = "15 минут упражнений каждый день",
                type = HabitType.DAILY,
                streak = 7,
                isCompleted = false,
                priority = Priority.HIGH
            ),
            onToggleCompletion = {},
            onCardClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardCompletedPreview() {
    HabitTrackerAppTheme {
        HabitCard(
            habit = Habit(
                id = "2",
                name = "Чтение книги",
                description = "Читать 30 минут перед сном",
                type = HabitType.DAILY,
                streak = 14,
                isCompleted = true,
                priority = Priority.MEDIUM
            ),
            onToggleCompletion = {},
            onCardClick = {}
        )
    }
}