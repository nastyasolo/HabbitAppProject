package com.example.habittrackerapp.domain.model

enum class SyncStatus {
    SYNCED,        // Синхронизировано с сервером
    PENDING,       // Ожидает синхронизации
    SYNCING,       // В процессе синхронизации
    FAILED         // Ошибка синхронизации
}