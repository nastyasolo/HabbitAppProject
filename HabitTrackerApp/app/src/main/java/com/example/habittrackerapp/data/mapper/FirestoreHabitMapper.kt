package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.remote.model.FirestoreHabit
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.HabitType
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.domain.model.SyncStatus

object FirestoreHabitMapper {

    fun toFirestoreHabit(habit: Habit, userId: String): FirestoreHabit {
        return FirestoreHabit(
            id = habit.id,
            userId = userId,
            name = habit.name,
            description = habit.description,
            type = habit.type.name,
            priority = habit.priority.name,
            reminderTime = habit.reminderTime,
            isCompleted = habit.isCompleted,
            streak = habit.streak,
            category = habit.category,
            createdAt = habit.createdAt,
            lastCompleted = habit.lastCompleted
        )
    }

    fun toDomainHabit(firestoreHabit: FirestoreHabit): Habit {
        return Habit(
            id = firestoreHabit.id,
            name = firestoreHabit.name,
            description = firestoreHabit.description,
            type = HabitType.valueOf(firestoreHabit.type),
            priority = Priority.valueOf(firestoreHabit.priority),
            reminderTime = firestoreHabit.reminderTime,
            isCompleted = firestoreHabit.isCompleted,
            streak = firestoreHabit.streak,
            category = firestoreHabit.category ?: "General",
            createdAt = firestoreHabit.createdAt,
            lastCompleted = firestoreHabit.lastCompleted,
            syncStatus = SyncStatus.SYNCED,
            lastSynced = System.currentTimeMillis()
        )
    }
}