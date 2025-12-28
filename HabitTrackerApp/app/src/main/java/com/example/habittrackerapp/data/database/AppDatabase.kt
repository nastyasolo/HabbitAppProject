package com.example.habittrackerapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.habittrackerapp.data.model.Habit
import com.example.habittrackerapp.data.model.HabitCompletion
import com.example.habittrackerapp.data.model.TaskEntity

@Database(
    entities = [Habit::class, HabitCompletion::class, TaskEntity::class],
    version = 2, //
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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Столбцы для задач
                database.execSQL("ALTER TABLE tasks ADD COLUMN reminderTime TEXT")
                database.execSQL("ALTER TABLE tasks ADD COLUMN hasReminder INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE tasks ADD COLUMN reminderId TEXT DEFAULT ''")

                // Новые столбцы для привычек
                database.execSQL("ALTER TABLE habits ADD COLUMN reminderId TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE habits ADD COLUMN hasReminder INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE habits ADD COLUMN reminderDays TEXT DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "habit_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() //  Оставляем для безопасности
                    .build()
                    .also { Instance = it }
            }
        }
    }
}