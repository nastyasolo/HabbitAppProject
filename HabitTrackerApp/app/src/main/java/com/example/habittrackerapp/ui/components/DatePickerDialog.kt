package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate ?: LocalDate.now()) }
    var dateInput by remember { mutableStateOf("") }
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    Dialog(onDismissRequest = onDismiss) {
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
                    text = "Выберите дату",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Быстрые кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedDate = LocalDate.now() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сегодня")
                    }

                    Button(
                        onClick = { selectedDate = LocalDate.now().plusDays(1) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Завтра")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedDate = LocalDate.now().plusDays(7) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Через неделю")
                    }

                    Button(
                        onClick = {
                            onDateSelected(null)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Без срока")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ручной ввод даты
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Или введите дату (дд.мм.гггг)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Например: ${LocalDate.now().format(formatter)}") }
                )

                Button(
                    onClick = {
                        try {
                            val inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            selectedDate = LocalDate.parse(dateInput, inputFormatter)
                        } catch (e: Exception) {
                            // Неправильный формат
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = dateInput.isNotBlank()
                ) {
                    Text("Использовать эту дату")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Выбрана дата: ${selectedDate.format(formatter)}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }

                    Button(
                        onClick = {
                            onDateSelected(selectedDate)
                            onDismiss()
                        }
                    ) {
                        Text("Выбрать")
                    }
                }
            }
        }
    }
}