package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.remote.model.FirestoreCompletion
import com.example.habittrackerapp.data.remote.model.FirestoreHabit
import com.example.habittrackerapp.domain.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object FirestoreHabitMapper {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun toFirestoreHabit(habit: Habit, userId: String): FirestoreHabit {
        return FirestoreHabit(
            id = habit.id,
            userId = userId,
            name = habit.name,
            description = habit.description,
            type = habit.type.name,
            priority = habit.priority.name,
            reminderTime = habit.reminderTime,
            targetDays = habit.targetDays.map { it.name },
            category = habit.category,
            createdAt = habit.createdAt,
            lastCompleted = habit.lastCompleted?.format(dateFormatter),
            currentStreak = habit.currentStreak,
            longestStreak = habit.longestStreak
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
            targetDays = firestoreHabit.targetDays.map { DayOfWeek.valueOf(it) },
            createdAt = firestoreHabit.createdAt,
            category = firestoreHabit.category,
            lastCompleted = firestoreHabit.lastCompleted?.let { LocalDate.parse(it, dateFormatter) },
            currentStreak = firestoreHabit.currentStreak,
            longestStreak = firestoreHabit.longestStreak,
            syncStatus = SyncStatus.SYNCED
        )
    }

    fun toFirestoreCompletion(completion: HabitCompletion, userId: String): FirestoreCompletion {
        return FirestoreCompletion(
            id = completion.id,
            habitId = completion.habitId,
            userId = userId,
            date = completion.date.format(dateFormatter),
            completed = completion.completed,
            completedAt = completion.completedAt
        )
    }

    fun toDomainCompletion(firestoreCompletion: FirestoreCompletion): HabitCompletion {
        return HabitCompletion(
            id = firestoreCompletion.id,
            habitId = firestoreCompletion.habitId,
            date = LocalDate.parse(firestoreCompletion.date, dateFormatter),
            completed = firestoreCompletion.completed,
            completedAt = firestoreCompletion.completedAt,
            syncStatus = SyncStatus.SYNCED
        )
    }
}