package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.model.Habit as HabitEntity
import com.example.habittrackerapp.domain.model.Habit as HabitDomain
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.domain.model.SyncStatus
import com.example.habittrackerapp.domain.model.DayOfWeek
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object HabitMapper {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val gson = Gson()

    fun toDomain(entity: HabitEntity): HabitDomain {
        val targetDays = if (entity.targetDays.isNotEmpty()) {
            val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
            val stringList: List<String> = gson.fromJson(entity.targetDays, type)
            stringList.map { DayOfWeek.valueOf(it) }
        } else {
            emptyList()
        }

        return HabitDomain(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            type = HabitType.valueOf(entity.type),
            priority = Priority.valueOf(entity.priority),
            reminderTime = entity.reminderTime,
            targetDays = targetDays,
            createdAt = entity.createdAt,
            category = entity.category,
            lastCompleted = entity.lastCompleted?.let { LocalDate.parse(it, dateFormatter) },
            currentStreak = entity.currentStreak,
            longestStreak = entity.longestStreak,
            syncStatus = SyncStatus.valueOf(entity.syncStatus),
            lastSynced = entity.lastSynced
        )
    }

    fun toEntity(domain: HabitDomain): HabitEntity {
        return HabitEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            type = domain.type.name,
            priority = domain.priority.name,
            reminderTime = domain.reminderTime,
            targetDays = gson.toJson(domain.targetDays.map { it.name }),
            category = domain.category,
            createdAt = domain.createdAt,
            lastCompleted = domain.lastCompleted?.format(dateFormatter),
            currentStreak = domain.currentStreak,
            longestStreak = domain.longestStreak,
            syncStatus = domain.syncStatus.name,
            lastSynced = domain.lastSynced
        )
    }
}