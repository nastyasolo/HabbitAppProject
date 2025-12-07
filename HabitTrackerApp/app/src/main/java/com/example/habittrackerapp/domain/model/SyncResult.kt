package com.example.habittrackerapp.domain.model

sealed class SyncResult {
    object Success : SyncResult()
    data class Error(val message: String) : SyncResult()
    object NoInternet : SyncResult()
}