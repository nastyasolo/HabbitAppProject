package com.example.habittrackerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.habittrackerapp.domain.model.isTaskOverdue
import com.example.habittrackerapp.ui.components.TaskCard
import com.example.habittrackerapp.ui.viewmodel.TaskListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val overdueCount by viewModel.overdueCount.collectAsState()
    val dueTodayCount by viewModel.dueTodayCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задачи") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addEditTask/null") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Статистика сверху
            if (overdueCount > 0 || dueTodayCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (overdueCount > 0) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$overdueCount",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "просрочено",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    if (dueTodayCount > 0) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$dueTodayCount",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "на сегодня",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Нет задач",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Нажмите + чтобы добавить первую задачу",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Просроченные задачи
                    val overdueTasks = tasks.filter { isTaskOverdue(it) && !it.isCompleted }
                    if (overdueTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Просроченные",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(overdueTasks) { task ->
                            TaskCard(
                                task = task,
                                onTaskClick = { navController.navigate("addEditTask/${task.id}") },
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }

                    // Невыполненные задачи
                    val incompleteTasks = tasks.filter { !it.isCompleted && !isTaskOverdue(it) }
                    if (incompleteTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Активные",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(incompleteTasks) { task ->
                            TaskCard(
                                task = task,
                                onTaskClick = { navController.navigate("addEditTask/${task.id}") },
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }

                    // Выполненные задачи
                    val completedTasks = tasks.filter { it.isCompleted }
                    if (completedTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Выполненные",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(completedTasks) { task ->
                            TaskCard(
                                task = task,
                                onTaskClick = { navController.navigate("addEditTask/${task.id}") },
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }

                }
            }
        }
    }
}