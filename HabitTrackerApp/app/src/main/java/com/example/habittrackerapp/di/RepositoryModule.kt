package com.example.habittrackerapp.di

import com.example.habittrackerapp.data.mapper.TaskMapper
import com.example.habittrackerapp.data.repository.AuthRepositoryImpl
import com.example.habittrackerapp.data.repository.SyncHabitRepositoryImpl
import com.example.habittrackerapp.data.repository.TaskRepositoryImpl
import com.example.habittrackerapp.domain.repository.AuthRepository
import com.example.habittrackerapp.domain.repository.HabitRepository
import com.example.habittrackerapp.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository {
        return authRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideHabitRepository(
        syncHabitRepositoryImpl: SyncHabitRepositoryImpl
    ): HabitRepository {
        return syncHabitRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideTaskMapper(): TaskMapper {
        return TaskMapper()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: com.example.habittrackerapp.data.database.TaskDao,
        taskMapper: TaskMapper
    ): TaskRepository {
        return TaskRepositoryImpl(taskDao, taskMapper)
    }
}