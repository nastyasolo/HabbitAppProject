package com.example.habittrackerapp.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.data.repository.AuthRepositoryImpl
import com.example.habittrackerapp.domain.AuthState
import com.example.habittrackerapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getAuthState().collect { authState ->
                _uiState.value = when (authState) {
                    is AuthState.Loading -> _uiState.value.copy(isLoading = true)
                    is AuthState.Authenticated -> _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = authState.user
                    )
                    is AuthState.Unauthenticated -> _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        currentUser = null
                    )
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = authRepository.login(email, password)
            handleAuthResult(result)
        }
    }

    fun register(email: String, password: String, name: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = authRepository.register(email, password, name)
            handleAuthResult(result)
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = authRepository.loginWithGoogle(idToken)
            handleAuthResult(result)
        }
    }
    fun checkAuthStatus() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(
                isAuthenticated = user != null,
                currentUser = user
            )
        }
    }

    fun getGoogleSignInIntent(): Intent {
        return (authRepository as AuthRepositoryImpl).getGoogleSignInIntent()
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            authRepository.logout()
            _uiState.value = _uiState.value.copy(
                isLoading = false
            )
        }
    }

    fun setError(errorMessage: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = errorMessage
        )
    }

    private fun handleAuthResult(result: Result<Unit>) {
        result.fold(
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Неизвестная ошибка"
                )
            }
        )
    }
}

data class AuthUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val currentUser: com.example.habittrackerapp.domain.model.User? = null,
    val error: String? = null
)