package com.example.habittrackerapp.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittrackerapp.domain.usecase.SyncUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncUseCases: SyncUseCases
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val result = syncUseCases.syncPendingHabits()
            when (result) {
                is com.example.habittrackerapp.domain.model.SyncResult.Success -> Result.success()
                is com.example.habittrackerapp.domain.model.SyncResult.Error -> Result.failure()
                is com.example.habittrackerapp.domain.model.SyncResult.NoInternet -> Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}