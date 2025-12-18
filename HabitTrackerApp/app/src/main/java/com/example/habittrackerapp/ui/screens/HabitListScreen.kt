package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.ui.components.EmptyState
import com.example.habittrackerapp.ui.components.HabitCard
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.ui.viewmodel.HabitListEvent
import com.example.habittrackerapp.ui.viewmodel.HabitListViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    onAddHabitClick: () -> Unit,
    onHabitClick: (String) -> Unit,
    viewModel: HabitListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(HabitListEvent.Reload)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Трекер привычек") },
                actions = {
                    IconButton(onClick = {
                        viewModel.onEvent(HabitListEvent.Reload)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить"
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
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить привычку",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ошибка: ${state.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.onEvent(HabitListEvent.Reload)
                        }) {
                            Text("Повторить")
                        }
                    }
                }
                state.habits.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = "Привычек пока нет",
                            subtitle = "Начните свой путь к новым привычкам прямо сейчас",
                            showAction = true
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.habits) { habitWithCompletions ->

                            val habit = habitWithCompletions.habit
                            val isCompletedToday = habitWithCompletions.completedToday

                            val weeklyProgress = habitWithCompletions.completions
                                .filter { it.completed }
                                .filter { it.date >= LocalDate.now().minusDays(6) }
                                .size / 7f

                            HabitCard(
                                habit = habit.copy(currentStreak = habitWithCompletions.currentStreak),
                                isCompletedToday = isCompletedToday,
                                weeklyProgress = weeklyProgress,
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

@Preview(showBackground = true)
@Composable
fun HabitListScreenPreview() {
    HabitTrackerAppTheme {
        HabitListScreen(
            onAddHabitClick = {},
            onHabitClick = {}
        )
    }
}