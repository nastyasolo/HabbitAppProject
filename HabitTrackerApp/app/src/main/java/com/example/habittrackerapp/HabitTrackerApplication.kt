package com.example.habittrackerapp

import android.app.Application
import com.example.habittrackerapp.data.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class HabitTrackerApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Room инициализируется через Hilt, начальные данные добавятся через InitialData
    }
}