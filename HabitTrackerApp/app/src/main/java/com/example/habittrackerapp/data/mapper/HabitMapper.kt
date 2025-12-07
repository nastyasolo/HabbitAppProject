package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.model.Habit as HabitEntity
import com.example.habittrackerapp.domain.model.Habit as HabitDomain
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.domain.model.SyncStatus

object HabitMapper {

    fun toDomain(entity: HabitEntity): HabitDomain {
        return HabitDomain(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            type = HabitType.valueOf(entity.type),
            priority = Priority.valueOf(entity.priority),
            reminderTime = entity.reminderTime,
            isCompleted = entity.isCompleted,
            streak = entity.streak,
            createdAt = entity.createdAt,
            lastCompleted = entity.lastCompleted,
            category = entity.category,
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
            isCompleted = domain.isCompleted,
            streak = domain.streak,
            createdAt = domain.createdAt,
            lastCompleted = domain.lastCompleted,
            category = domain.category,
            syncStatus = domain.syncStatus.name,
            lastSynced = domain.lastSynced
        )
    }
}