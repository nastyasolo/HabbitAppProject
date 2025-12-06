package com.example.habittrackerapp.domain

import com.example.habittrackerapp.domain.model.User

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
}