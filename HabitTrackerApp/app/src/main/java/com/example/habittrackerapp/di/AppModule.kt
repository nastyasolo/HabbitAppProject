package com.example.habittrackerapp.di

import android.content.Context
import com.example.habittrackerapp.data.InitialDataProvider
import com.example.habittrackerapp.data.database.AppDatabase
import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.repository.HabitRepositoryImpl
import com.example.habittrackerapp.domain.repository.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

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

    @Provides
    @Singleton
    fun provideInitialDataProvider(
        habitDao: HabitDao,
        scope: CoroutineScope
    ): InitialDataProvider {
        val provider = InitialDataProvider(habitDao, scope)
        provider.populateIfEmpty() // Инициализируем данные при создании
        return provider
    }
}