package com.example.habittrackerapp.domain.usecase

import com.example.habittrackerapp.domain.model.SyncResult
import com.example.habittrackerapp.domain.repository.HabitRepository
import javax.inject.Inject

class SyncUseCases @Inject constructor(
    private val habitRepository: HabitRepository
) {

    suspend fun syncHabits(): SyncResult {
        return try {
            habitRepository.syncHabits()
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Ошибка синхронизации")
        }
    }

    suspend fun syncPendingHabits(): SyncResult {
        return try {
            habitRepository.syncPendingHabits()
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Ошибка синхронизации")
        }
    }
}