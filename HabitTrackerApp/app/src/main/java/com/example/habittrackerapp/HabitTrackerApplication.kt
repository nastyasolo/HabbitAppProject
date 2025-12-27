package com.example.habittrackerapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HabitTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        println("DEBUG: Приложение запущено")

        FirebaseApp.initializeApp(this)

        // Включаем оффлайн-режим Firestore
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Включаем локальное кэширование
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED) // Неограниченный кэш
            .build()

        Firebase.firestore.firestoreSettings = settings
    }
}