package com.example.habittrackerapp.di

import android.content.Context
import com.example.habittrackerapp.data.database.AppDatabase
import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.repository.HabitRepositoryImpl
import com.example.habittrackerapp.domain.repository.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideHabitRepository(habitDao: HabitDao): HabitRepository {
        return HabitRepositoryImpl(habitDao)
    }
}