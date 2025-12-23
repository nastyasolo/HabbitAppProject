package com.example.habittrackerapp.utils

import com.example.habittrackerapp.domain.model.Category
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.domain.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TaskUtils {

    fun getPriorityLabel(priority: Priority): String {
        return when (priority) {
            Priority.HIGH -> "Высокий"
            Priority.MEDIUM -> "Средний"
            Priority.LOW -> "Низкий"
        }
    }

    fun getCategoryLabel(category: Category): String {
        return when (category) {
            Category.WORK -> "Работа"
            Category.PERSONAL -> "Личное"
            Category.HEALTH -> "Здоровье"
            Category.EDUCATION -> "Обучение"
            Category.FINANCE -> "Финансы"
            Category.OTHER -> "Другое"
        }
    }

    fun formatTaskDateTime(task: Task): String {
        val date = task.dueDate
        val time = task.dueTime

        return buildString {
            if (date != null) {
                when {
                    date.isEqual(LocalDate.now()) -> append("Сегодня")
                    date.isEqual(LocalDate.now().plusDays(1)) -> append("Завтра")
                    date.isEqual(LocalDate.now().minusDays(1)) -> append("Вчера")
                    else -> append(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                }
            }

            if (time != null) {
                if (date != null) append(", ")
                append(time.format(DateTimeFormatter.ofPattern("HH:mm")))
            }

            if (this.isEmpty()) {
                append("Без срока")
            }
        }
    }
}