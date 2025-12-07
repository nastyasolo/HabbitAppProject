package com.example.habittrackerapp.data.database

import androidx.room.TypeConverter
import com.example.habittrackerapp.data.model.HabitType
import com.example.habittrackerapp.data.model.Priority
import com.example.habittrackerapp.domain.model.SyncStatus

class Converters {
    @TypeConverter
    fun fromHabitType(habitType: HabitType): String {
        return habitType.name
    }

    @TypeConverter
    fun toHabitType(value: String): HabitType {
        return HabitType.valueOf(value)
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(name: String): SyncStatus = SyncStatus.valueOf(name)

}