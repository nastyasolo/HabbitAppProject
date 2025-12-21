package com.example.habittrackerapp.di

import com.example.habittrackerapp.domain.usecase.TaskUseCases
import com.example.habittrackerapp.domain.repository.TaskRepository
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
        taskRepository: TaskRepository
    ): TaskUseCases {
        println("DEBUG: Providing TaskUseCases")
        return TaskUseCases(taskRepository)
    }
}