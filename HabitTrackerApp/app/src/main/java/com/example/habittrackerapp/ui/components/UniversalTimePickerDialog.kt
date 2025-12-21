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
fun UniversalTimePickerDialog(
    initialTime: String? = null,
    onTimeSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(true) }

    // Инициализируем время из строки "HH:mm"
    LaunchedEffect(initialTime) {
        if (!initialTime.isNullOrBlank()) {
            try {
                val parts = initialTime.split(":")
                if (parts.size == 2) {
                    selectedHour = parts[0].toInt()
                    selectedMinute = parts[1].toInt()
                }
            } catch (e: Exception) {
                // Оставляем значения по умолчанию
            }
        }
    }

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

                    // Минуты (с шагом 5 минут для удобства)
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
                                selectedMinute = if (selectedMinute <= 0) 55 else selectedMinute - 5
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
                                selectedMinute = if (selectedMinute >= 55) 0 else selectedMinute + 5
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
                    Text(
                        text = "Распространённое время:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedHour = 8
                                selectedMinute = 0
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("8:00")
                        }

                        Button(
                            onClick = {
                                selectedHour = 12
                                selectedMinute = 0
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("12:00")
                        }

                        Button(
                            onClick = {
                                selectedHour = 18
                                selectedMinute = 0
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("18:00")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Поле для ручного ввода
                    OutlinedTextField(
                        value = String.format("%02d:%02d", selectedHour, selectedMinute),
                        onValueChange = { input ->
                            try {
                                if (input.length == 5 && input[2] == ':') {
                                    val hour = input.substring(0, 2).toInt()
                                    val minute = input.substring(3, 5).toInt()
                                    if (hour in 0..23 && minute in 0..59) {
                                        selectedHour = hour
                                        selectedMinute = minute
                                    }
                                }
                            } catch (e: Exception) {
                                // Игнорируем некорректный ввод
                            }
                        },
                        label = { Text("Ручной ввод (ЧЧ:ММ)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = {
                                onTimeSelected(null)
                                showDialog = false
                            }
                        ) {
                            Text("Без времени")
                        }

                        Row {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    onDismiss()
                                }
                            ) {
                                Text("Отмена")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    onTimeSelected(String.format("%02d:%02d", selectedHour, selectedMinute))
                                    showDialog = false
                                }
                            ) {
                                Text("Выбрать")
                            }
                        }
                    }
                }
            }
        }
    }
}