package com.example.habittrackerapp.data.remote

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
}