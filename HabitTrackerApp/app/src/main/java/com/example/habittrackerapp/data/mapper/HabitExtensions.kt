package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.model.Habit as HabitEntity
import com.example.habittrackerapp.domain.model.Habit as HabitDomain

// Extension functions для удобства
fun HabitEntity.toDomain(): HabitDomain = HabitMapper.toDomain(this)
fun HabitDomain.toEntity(): HabitEntity = HabitMapper.toEntity(this)