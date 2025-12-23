package com.example.habittrackerapp.di

import android.content.Context
import com.example.habittrackerapp.data.database.AppDatabase
import com.example.habittrackerapp.data.database.HabitCompletionDao
import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.database.TaskDao
import com.example.habittrackerapp.utils.reminder.ReminderScheduler
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
    fun provideHabitCompletionDao(appDatabase: AppDatabase): HabitCompletionDao {
        return appDatabase.habitCompletionDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context
    ): ReminderScheduler {
        return ReminderScheduler(context)
    }
}