package com.example.habittrackerapp

import android.app.Application
import com.example.habittrackerapp.data.InitialDataProvider
import com.example.habittrackerapp.data.sync.SyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HabitTrackerApplication : Application() {

//    @Inject
//    lateinit var initialDataProvider: InitialDataProvider
//
//    override fun onCreate() {
//        super.onCreate()
//        // Инициализация начальных данных
//        initialDataProvider.populateIfEmpty()
//        println(" Приложение запущено, начальные данные инициализированы")
//    }

    @Inject
    lateinit var syncManager: SyncManager

    override fun onCreate() {
        super.onCreate()

        // Запускаем периодическую синхронизацию
        syncManager.schedulePeriodicSync()

        println(" Приложение запущено, синхронизация настроена")
    }
}