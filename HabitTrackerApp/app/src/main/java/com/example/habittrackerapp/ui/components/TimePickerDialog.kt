package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalTime

@Composable
fun TimePickerDialog(
    initialTime: LocalTime?,
    onTimeSelected: (LocalTime?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialTime?.hour ?: 12) }
    var selectedMinute by remember { mutableStateOf(initialTime?.minute ?: 0) }
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        Dialog(onDismissRequest = {
            showDialog = false
            onDismiss()
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Выберите время",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Часы
                    Text(
                        text = "Часы:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                selectedHour = if (selectedHour <= 0) 23 else selectedHour - 1
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Уменьшить час"
                            )
                        }

                        Text(
                            text = String.format("%02d", selectedHour),
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        IconButton(
                            onClick = {
                                selectedHour = if (selectedHour >= 23) 0 else selectedHour + 1
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Увеличить час"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Минуты
                    Text(
                        text = "Минуты:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                selectedMinute = if (selectedMinute <= 0) 59 else selectedMinute - 1
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Уменьшить минуту"
                            )
                        }

                        Text(
                            text = String.format("%02d", selectedMinute),
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        IconButton(
                            onClick = {
                                selectedMinute = if (selectedMinute >= 59) 0 else selectedMinute + 1
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Увеличить минуту"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Быстрые кнопки
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedHour = 9
                                selectedMinute = 0
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("9:00")
                        }

                        Button(
                            onClick = {
                                selectedHour = 12
                                selectedMinute = 0
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("12:00")
                        }

                        Button(
                            onClick = {
                                selectedHour = 18
                                selectedMinute = 0
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("18:00")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = {
                                showDialog = false
                                onDismiss()
                            }
                        ) {
                            Text("Отмена")
                        }

                        Button(
                            onClick = {
                                onTimeSelected(LocalTime.of(selectedHour, selectedMinute))
                                showDialog = false
                            }
                        ) {
                            Text("Выбрать")
                        }

                        TextButton(
                            onClick = {
                                onTimeSelected(null)
                                showDialog = false
                            }
                        ) {
                            Text("Без времени")
                        }
                    }
                }
            }
        }
    }
}