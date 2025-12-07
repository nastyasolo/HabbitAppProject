package com.example.habittrackerapp.di

import com.example.habittrackerapp.data.repository.AuthRepositoryImpl
import com.example.habittrackerapp.data.repository.SyncHabitRepositoryImpl
import com.example.habittrackerapp.domain.repository.AuthRepository
import com.example.habittrackerapp.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        syncHabitRepositoryImpl: SyncHabitRepositoryImpl
    ): HabitRepository
}