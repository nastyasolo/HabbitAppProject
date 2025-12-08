package com.example.habittrackerapp.domain.repository

import com.example.habittrackerapp.domain.AuthState
import com.example.habittrackerapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, name: String? = null): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    fun getAuthState(): Flow<AuthState>
}