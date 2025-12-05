package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.model.Habit as HabitEntity
import com.example.habittrackerapp.domain.model.Habit as HabitDomain
import com.example.habittrackerapp.domain.model.HabitType as DomainHabitType
import com.example.habittrackerapp.domain.model.Priority as DomainPriority
import com.example.habittrackerapp.data.model.HabitType as EntityHabitType
import com.example.habittrackerapp.data.model.Priority as EntityPriority

fun HabitEntity.toDomain(): HabitDomain {
    return HabitDomain(
        id = id,
        name = name,
        description = description,
        type = when (type) {
            EntityHabitType.DAILY -> DomainHabitType.DAILY
            EntityHabitType.WEEKLY -> DomainHabitType.WEEKLY
        },
        streak = streak,
        isCompleted = isCompleted,
        createdAt = createdAt,
        reminderTime = reminderTime,
        priority = when (priority) {
            EntityPriority.LOW -> DomainPriority.LOW
            EntityPriority.MEDIUM -> DomainPriority.MEDIUM
            EntityPriority.HIGH -> DomainPriority.HIGH
        }
    )
}

fun HabitDomain.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        description = description,
        type = when (type) {
            DomainHabitType.DAILY -> EntityHabitType.DAILY
            DomainHabitType.WEEKLY -> EntityHabitType.WEEKLY
        },
        streak = streak,
        isCompleted = isCompleted,
        createdAt = createdAt,
        reminderTime = reminderTime,
        priority = when (priority) {
            DomainPriority.LOW -> EntityPriority.LOW
            DomainPriority.MEDIUM -> EntityPriority.MEDIUM
            DomainPriority.HIGH -> EntityPriority.HIGH
        }
    )
}