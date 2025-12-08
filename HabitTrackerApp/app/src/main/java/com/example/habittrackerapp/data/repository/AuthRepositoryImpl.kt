package com.example.habittrackerapp.data.repository

import android.content.Context
import com.example.habittrackerapp.domain.AuthState
import com.example.habittrackerapp.domain.model.User
import com.example.habittrackerapp.domain.repository.AuthRepository
import com.example.habittrackerapp.utils.GoogleSignInHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val context: Context
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    init {
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

            result.user?.let { firebaseUser ->
                saveUserToFirestore(firebaseUser.uid, email, name)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            result.user?.let { firebaseUser ->
                saveUserToFirestore(
                    firebaseUser.uid,
                    firebaseUser.email ?: "",
                    firebaseUser.displayName
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // Игнорируем ошибки при выходе
        }
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

    // Новый метод для получения Google Sign-In Intent
    fun getGoogleSignInIntent(): android.content.Intent {
        val webClientId = context.getString(com.example.habittrackerapp.R.string.default_web_client_id)
        val client = GoogleSignInHelper.getGoogleSignInClient(context, webClientId)
        return GoogleSignInHelper.getGoogleSignInIntent(client)
    }

    private suspend fun saveUserToFirestore(userId: String, email: String, name: String?) {
        val userData = hashMapOf(
            "email" to email,
            "name" to name,
            "createdAt" to System.currentTimeMillis(),
            "lastLogin" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userId)
            .set(userData)
            .await()
    }
}