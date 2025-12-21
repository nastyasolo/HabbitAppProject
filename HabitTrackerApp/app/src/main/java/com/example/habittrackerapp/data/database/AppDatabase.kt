package com.example.habittrackerapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.habittrackerapp.data.model.Habit
import com.example.habittrackerapp.data.model.HabitCompletion
import com.example.habittrackerapp.data.model.TaskEntity

@Database(
    entities = [Habit::class, HabitCompletion::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                println("DEBUG: Creating AppDatabase instance")
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "habit_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}