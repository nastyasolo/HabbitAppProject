package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.model.HabitCompletion as HabitCompletionEntity
import com.example.habittrackerapp.domain.model.HabitCompletion as HabitCompletionDomain
import com.example.habittrackerapp.domain.model.SyncStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object HabitCompletionMapper {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun toDomain(entity: HabitCompletionEntity): HabitCompletionDomain {
        return HabitCompletionDomain(
            id = entity.id,
            habitId = entity.habitId,
            date = LocalDate.parse(entity.date, dateFormatter),
            completed = entity.completed,
            completedAt = entity.completedAt,
            syncStatus = SyncStatus.valueOf(entity.syncStatus),
            lastSynced = entity.lastSynced
        )
    }

    fun toEntity(domain: HabitCompletionDomain): HabitCompletionEntity {
        return HabitCompletionEntity(
            id = domain.id,
            habitId = domain.habitId,
            date = domain.date.format(dateFormatter),
            completed = domain.completed,
            completedAt = domain.completedAt,
            syncStatus = domain.syncStatus.name,
            lastSynced = domain.lastSynced
        )
    }
}