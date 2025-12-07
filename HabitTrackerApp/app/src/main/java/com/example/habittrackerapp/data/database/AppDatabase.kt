package com.example.habittrackerapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.habittrackerapp.data.model.Habit
import com.example.habittrackerapp.data.database.HabitDao
import android.content.Context

@Database(
    entities = [Habit::class],
    version = 2,  //
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "habit_database"
                )
                    .fallbackToDestructiveMigration()  // Временное решение для миграции
                    .build()
                    .also { Instance = it }
            }
        }
    }
}