package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.model.Habit as HabitEntity
import com.example.habittrackerapp.domain.model.Habit as HabitDomain

// Extension functions для удобства
fun HabitEntity.toDomain(): HabitDomain = HabitMapper.toDomain(this)
fun HabitDomain.toEntity(): HabitEntity = HabitMapper.toEntity(this)

fun com.example.habittrackerapp.data.model.HabitCompletion.toDomain(): com.example.habittrackerapp.domain.model.HabitCompletion =
    HabitCompletionMapper.toDomain(this)

fun com.example.habittrackerapp.domain.model.HabitCompletion.toEntity(): com.example.habittrackerapp.data.model.HabitCompletion =
    HabitCompletionMapper.toEntity(this)