package com.example.habittrackerapp.data.database


import androidx.room.TypeConverter
import com.example.habittrackerapp.domain.model.DayOfWeek

import com.example.habittrackerapp.domain.model.SyncStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val gson = Gson()
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME



    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(name: String): SyncStatus = SyncStatus.valueOf(name)

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }
    @TypeConverter
    fun fromDayOfWeekList(days: List<DayOfWeek>): String {
        return gson.toJson(days.map { it.name })
    }

    @TypeConverter
    fun toDayOfWeekList(json: String): List<DayOfWeek> {
        if (json.isEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        val stringList: List<String> = gson.fromJson(json, type)
        return stringList.map { DayOfWeek.valueOf(it) }
    }


}