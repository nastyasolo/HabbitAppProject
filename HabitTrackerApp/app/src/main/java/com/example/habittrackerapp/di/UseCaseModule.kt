package com.example.habittrackerapp.di

import com.example.habittrackerapp.domain.usecase.HabitUseCases
import com.example.habittrackerapp.domain.usecase.TaskUseCases
import com.example.habittrackerapp.domain.repository.HabitRepository
import com.example.habittrackerapp.domain.repository.TaskRepository
import com.example.habittrackerapp.utils.reminder.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideTaskUseCases(
        taskRepository: TaskRepository,
        reminderScheduler: ReminderScheduler
    ): TaskUseCases {
        println("DEBUG: Providing TaskUseCases with reminderScheduler")
        return TaskUseCases(taskRepository, reminderScheduler)
    }

    @Provides
    @Singleton
    fun provideHabitUseCases(
        habitRepository: HabitRepository,
        reminderScheduler: ReminderScheduler
    ): HabitUseCases {
        println("DEBUG: Providing HabitUseCases with reminderScheduler")
        return HabitUseCases(habitRepository, reminderScheduler)
    }
}