package com.example.habittrackerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HabitTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        println("DEBUG: Приложение запущено")

        // WorkManager автоматически инициализируется Hilt
        // через аннотацию @HiltWorker в Worker'ах
    }
}