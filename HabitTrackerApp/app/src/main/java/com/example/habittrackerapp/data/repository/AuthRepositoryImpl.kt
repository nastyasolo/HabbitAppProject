package com.example.habittrackerapp.data.repository

import com.example.habittrackerapp.domain.AuthState
import com.example.habittrackerapp.domain.model.User
import com.example.habittrackerapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    init {
        // Слушаем изменения состояния аутентификации
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            _authState.value = if (firebaseUser != null) {
                AuthState.Authenticated(
                    User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        name = firebaseUser.displayName
                    )
                )
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    override fun getAuthState(): Flow<AuthState> = _authState.asStateFlow()

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, name: String?): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            // Сохраняем информацию о пользователе в Firestore
            result.user?.let { firebaseUser ->
                val userData = hashMapOf(
                    "email" to email,
                    "name" to name,
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(userData)
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return firebaseUser?.let {
            User(
                id = it.uid,
                email = it.email ?: "",
                name = it.displayName
            )
        }
    }
}