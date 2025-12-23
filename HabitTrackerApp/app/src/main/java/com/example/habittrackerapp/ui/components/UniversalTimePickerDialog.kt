package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalTimePickerDialog(
    initialTime: String? = null,
    onTimeSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }
    var manualInput by remember { mutableStateOf("") }
    var showManualInputError by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Инициализация времени
    LaunchedEffect(initialTime) {
        if (!initialTime.isNullOrBlank()) {
            try {
                val parts = initialTime.split(":")
                if (parts.size == 2) {
                    selectedHour = parts[0].toInt().coerceIn(0, 23)
                    selectedMinute = parts[1].toInt().coerceIn(0, 59)
                    manualInput = String.format("%02d:%02d", selectedHour, selectedMinute)
                }
            } catch (e: Exception) {
                selectedHour = 12
                selectedMinute = 0
                manualInput = "12:00"
            }
        } else {
            manualInput = String.format("%02d:%02d", selectedHour, selectedMinute)
        }
    }

    // Обновление ручного ввода при изменении времени
    LaunchedEffect(selectedHour, selectedMinute) {
        manualInput = String.format("%02d:%02d", selectedHour, selectedMinute)
    }

    fun parseManualInput(input: String): Boolean {
        return try {
            val parts = input.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toInt().coerceIn(0, 23)
                val minute = parts[1].toInt().coerceIn(0, 59)
                selectedHour = hour
                selectedMinute = minute
                showManualInputError = false
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Заголовок
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = "Выбор времени",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Выберите время",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Основной выбор времени
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Часы
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Кнопка увеличения
                            IconButton(
                                onClick = {
                                    selectedHour = (selectedHour + 1) % 24
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Увеличить час",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Отображение часов
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = String.format("%02d", selectedHour),
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Кнопка уменьшения
                            IconButton(
                                onClick = {
                                    selectedHour = if (selectedHour == 0) 23 else selectedHour - 1
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Уменьшить час",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = "Часы",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Двоеточие
                        Text(
                            text = ":",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Минуты
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Кнопка увеличения
                            IconButton(
                                onClick = {
                                    selectedMinute = (selectedMinute + 1) % 60
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Увеличить минуты",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Отображение минут
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = String.format("%02d", selectedMinute),
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            // Кнопка уменьшения
                            IconButton(
                                onClick = {
                                    selectedMinute = if (selectedMinute == 0) 59 else selectedMinute - 1
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Уменьшить минуты",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = "Минуты",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Ручной ввод (сначала, чтобы было важнее)
                    Text(
                        text = "Или введите вручную",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        OutlinedTextField(
                            value = manualInput,
                            onValueChange = { input ->
                                manualInput = input
                                if (input.length == 5 && input[2] == ':') {
                                    val parsed = parseManualInput(input)
                                    showManualInputError = !parsed
                                } else if (input.length > 5) {
                                    showManualInputError = true
                                }
                            },
                            label = { Text("ЧЧ:ММ (например: 14:30)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            isError = showManualInputError,
                            supportingText = {
                                if (showManualInputError) {
                                    Text(
                                        text = "Введите время в формате ЧЧ:ММ",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            trailingIcon = {
                                if (manualInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            manualInput = ""
                                            showManualInputError = false
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Очистить",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }

                    // Быстрый выбор времени с горизонтальной прокруткой
                    Text(
                        text = "Распространённое время",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 12.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    val quickTimes = listOf(
                        Triple(8, 0, "Утро"),
                        Triple(9, 0, "9:00"),
                        Triple(10, 0, "10:00"),
                        Triple(11, 0, "11:00"),
                        Triple(12, 0, "Обед"),
                        Triple(13, 0, "13:00"),
                        Triple(14, 0, "14:00"),
                        Triple(15, 0, "15:00"),
                        Triple(16, 0, "16:00"),
                        Triple(17, 0, "17:00"),
                        Triple(18, 0, "Вечер"),
                        Triple(19, 0, "19:00"),
                        Triple(20, 0, "20:00"),
                        Triple(21, 0, "21:00"),
                        Triple(22, 0, "22:00"),
                        Triple(23, 0, "Ночь")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickTimes.forEach { (hour, minute, label) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(70.dp)
                            ) {
                                Surface(
                                    onClick = {
                                        selectedHour = hour
                                        selectedMinute = minute
                                        focusManager.clearFocus()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedHour == hour && selectedMinute == minute) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = String.format("%02d:%02d", hour, minute),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = if (selectedHour == hour && selectedMinute == minute) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Кнопки действий
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onTimeSelected(null)
                                showDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Без времени")
                        }

                        Button(
                            onClick = {
                                if (manualInput.isNotEmpty() && manualInput.length == 5) {
                                    if (parseManualInput(manualInput)) {
                                        onTimeSelected(String.format("%02d:%02d", selectedHour, selectedMinute))
                                        showDialog = false
                                    }
                                } else {
                                    onTimeSelected(String.format("%02d:%02d", selectedHour, selectedMinute))
                                    showDialog = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Выбрать")
                        }
                    }

                    TextButton(
                        onClick = {
                            showDialog = false
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Отмена")
                    }
                }
            }
        }
    }
}