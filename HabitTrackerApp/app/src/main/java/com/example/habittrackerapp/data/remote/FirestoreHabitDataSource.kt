package com.example.habittrackerapp.data.remote

import com.example.habittrackerapp.data.remote.model.FirestoreCompletion
import com.example.habittrackerapp.data.remote.model.FirestoreHabit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreHabitDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : HabitRemoteDataSource {

    override suspend fun saveHabit(habit: FirestoreHabit): Result<Unit> {
        return try {
            firestore.collection("habits")
                .document(habit.id)
                .set(habit)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHabits(userId: String): Result<List<FirestoreHabit>> {
        return try {
            val snapshot = firestore.collection("habits")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val habits = snapshot.documents.mapNotNull { document ->
                document.toObject(FirestoreHabit::class.java)
            }
            Result.success(habits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteHabit(habitId: String, userId: String): Result<Unit> {
        return try {
            firestore.collection("habits")
                .document(habitId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHabit(habit: FirestoreHabit): Result<Unit> {
        return try {
            firestore.collection("habits")
                .document(habit.id)
                .set(habit)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeHabits(userId: String): Flow<List<FirestoreHabit>> {
        return firestore.collection("habits")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.toObject(FirestoreHabit::class.java)
                }
            }
    }

    // Реализация новых методов для истории выполнения
    override suspend fun saveCompletion(completion: FirestoreCompletion): Result<Unit> {
        return try {
            firestore.collection("habits")
                .document(completion.habitId)
                .collection("history")
                .document(completion.id)
                .set(completion)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCompletions(userId: String, habitId: String): Result<List<FirestoreCompletion>> {
        return try {
            val snapshot = firestore.collection("habits")
                .document(habitId)
                .collection("history")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val completions = snapshot.documents.mapNotNull { document ->
                document.toObject(FirestoreCompletion::class.java)
            }
            Result.success(completions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCompletion(completionId: String, userId: String, habitId: String): Result<Unit> {
        return try {
            firestore.collection("habits")
                .document(habitId)
                .collection("history")
                .document(completionId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}