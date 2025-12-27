package com.example.habittrackerapp.data.repository

import com.example.habittrackerapp.data.remote.model.FirestoreOnlyCompletion
import com.example.habittrackerapp.data.remote.model.FirestoreOnlyHabit
import com.example.habittrackerapp.data.util.StreakCalculator
import com.example.habittrackerapp.data.util.TestDataGenerator
import com.example.habittrackerapp.domain.model.*
import com.example.habittrackerapp.domain.repository.AuthRepository
import com.example.habittrackerapp.domain.repository.HabitRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class FirestoreOnlyHabitRepository @Inject constructor(
    private val authRepository: AuthRepository
) : HabitRepository {

    private val firestore = Firebase.firestore

    // Отдельные коллекции
    private val habitsCollection = firestore.collection("firestore_only_habits")
    private val completionsCollection = firestore.collection("firestore_only_completions")

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // Дополнительные данные для хранения вычисленных значений
    private val habitDetailsCache = mutableMapOf<String, HabitWithCompletions>()

    private val _habitsWithCompletions = MutableStateFlow<List<HabitWithCompletions>>(emptyList())
    val habitsWithCompletions: StateFlow<List<HabitWithCompletions>> = _habitsWithCompletions.asStateFlow()

    // Добавляем кэш в памяти
    private val memoryCache = mutableMapOf<String, List<HabitWithCompletions>>()
    private var cacheTimestamp: Long = 0
    private val CACHE_VALIDITY = 5 * 60 * 1000 // 5 минут


    override fun getAllHabits(): Flow<List<Habit>> = callbackFlow {
        val userId = authRepository.getCurrentUser()?.id ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = habitsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val habits = snapshot?.documents?.mapNotNull { doc ->
                    val firestoreHabit = doc.toObject<FirestoreOnlyHabit>()
                    firestoreHabit?.toDomainHabit()
                } ?: emptyList()

                trySend(habits)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getHabitWithCompletions(id: String): HabitWithCompletions? {
        val habit = getHabitById(id) ?: return null
        val completions = getHabitCompletions(id)


        return HabitWithCompletions(
            habit = habit,
            completions = completions
        ).apply {

            habitDetailsCache[id] = this
        }
    }
    override suspend fun getHabitById(id: String): Habit? {
        return try {
            val doc = habitsCollection.document(id).get().await()
            doc.toObject<FirestoreOnlyHabit>()?.toDomainHabit()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun insertHabit(habit: Habit) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        val firestoreHabit = habit.toFirestoreOnlyHabit(userId)

        try {
            habitsCollection.document(habit.id).set(firestoreHabit).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        val firestoreHabit = habit.toFirestoreOnlyHabit(userId)

        try {
            habitsCollection.document(habit.id).set(firestoreHabit).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        try {
            val userId = authRepository.getCurrentUser()?.id ?: return
            val completionsQuery = completionsCollection
                .whereEqualTo("habitId", habit.id)
                .whereEqualTo("userId", userId)
                .get().await()

            val batch = firestore.batch()
            completionsQuery.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.delete(habitsCollection.document(habit.id))
            batch.commit().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>> {
        return callbackFlow {

            val userId = authRepository.getCurrentUser()?.id ?: run {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val currentTime = System.currentTimeMillis()
            if (currentTime - cacheTimestamp < CACHE_VALIDITY) {
                memoryCache[userId]?.let { cached ->
                    trySend(cached)
                }
            }

            val habitsListener = habitsCollection
                .whereEqualTo("userId", userId)

                .addSnapshotListener { habitsSnapshot, habitsError ->
                    if (habitsError != null) {
                        close(habitsError)
                        return@addSnapshotListener
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        val habits = habitsSnapshot?.documents?.mapNotNull { doc ->
                            doc.toObject<FirestoreOnlyHabit>()?.toDomainHabit()
                        } ?: emptyList()

                        // Для каждой привычки загружаем выполнения
                        val habitsWithCompletionsList = mutableListOf<HabitWithCompletions>()

                        habits.forEach { habit ->
                            val completions = getHabitCompletions(habit.id)

                            habitsWithCompletionsList.add(
                                HabitWithCompletions(
                                    habit = habit,
                                    completions = completions
                                )
                            )
                        }


                        memoryCache[userId] = habitsWithCompletionsList
                        cacheTimestamp = currentTime

                        trySend(habitsWithCompletionsList)
                    }
                }

            val completionsListener = completionsCollection
                .whereEqualTo("userId", userId)
                .addSnapshotListener { completionsSnapshot, completionsError ->
                    if (completionsError != null) {
                        close(completionsError)
                        return@addSnapshotListener
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        // При изменениях в выполнениях перезагружаем все
                        val habitsSnapshot = habitsCollection
                            .whereEqualTo("userId", userId)
                            .get().await()

                        val habits = habitsSnapshot.documents.mapNotNull { doc ->
                            doc.toObject<FirestoreOnlyHabit>()?.toDomainHabit()
                        }

                        val habitsWithCompletionsList = mutableListOf<HabitWithCompletions>()

                        habits.forEach { habit ->
                            val completions = getHabitCompletions(habit.id)

                            habitsWithCompletionsList.add(
                                HabitWithCompletions(
                                    habit = habit,
                                    completions = completions
                                )
                            )
                        }

                        _habitsWithCompletions.value = habitsWithCompletionsList
                        trySend(habitsWithCompletionsList)
                    }
                }

            awaitClose {
                habitsListener.remove()
                completionsListener.remove()
            }
        }
    }



    override suspend fun toggleHabitCompletion(id: String) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        val today = LocalDate.now().format(dateFormatter)

        try {
            val existingCompletion = completionsCollection
                .whereEqualTo("habitId", id)
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", today)
                .get()
//                .get(Source.CACHE)
                .await()
                .documents
                .firstOrNull()

            if (existingCompletion != null) {
                // Удаляем выполнение
                existingCompletion.reference.delete().await()
            } else {
                // Создаем новое выполнение
                val completionId = UUID.randomUUID().toString()
                val completion = FirestoreOnlyCompletion(
                    id = completionId,
                    habitId = id,
                    userId = userId,
                    date = today,
                    completed = true,
                    completedAt = System.currentTimeMillis()
                )
                completionsCollection.document(completionId).set(completion).await()
            }

            // Пересчитываем стрик
            recalculateStreak(id)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }





    override suspend fun getTodayHabits(): List<Habit> {
        val userId = authRepository.getCurrentUser()?.id ?: return emptyList()

        return try {
            val snapshot = habitsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject<FirestoreOnlyHabit>()?.toDomainHabit()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getWeeklyHabits(): List<Habit> {
        return getTodayHabits().filter { it.type == HabitType.WEEKLY }
    }

    override suspend fun getWeeklyProgress(habitId: String): Float {
        val habit = getHabitById(habitId) ?: return 0f
        val completions = getHabitCompletions(habitId)

        return when (habit.type) {
            HabitType.DAILY -> {
                val lastWeekCompletions = completions
                    .filter { it.completed }
                    .filter { it.date >= LocalDate.now().minusDays(6) }
                lastWeekCompletions.size / 7f
            }
            HabitType.WEEKLY -> {
                val startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
                val targetDaysSet = habit.targetDays.toSet()

                val completedDates = completions
                    .filter { it.completed && it.date >= startOfWeek }
                    .map { it.date }

                val completedTargetDays = completedDates.count { date ->
                    val dayOfWeek = DayOfWeek.fromInt(date.dayOfWeek.value)
                    targetDaysSet.contains(dayOfWeek)
                }

                if (targetDaysSet.isNotEmpty()) {
                    completedTargetDays.toFloat() / targetDaysSet.size
                } else 0f
            }
        }
    }

    override suspend fun syncHabits(): Boolean {
        // В Firestore-only реализации синхронизация не нужна
        return true
    }

    override suspend fun syncPendingHabits(): Boolean = true
    override suspend fun markHabitAsSynced(habitId: String) {}
    override suspend fun getPendingHabits(): List<Habit> = emptyList()

    override suspend fun getHabitCompletions(habitId: String): List<HabitCompletion> {
        val userId = authRepository.getCurrentUser()?.id ?: return emptyList()

        return try {
            val snapshot = completionsCollection
                .whereEqualTo("habitId", habitId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val firestoreCompletion = doc.toObject<FirestoreOnlyCompletion>()
                firestoreCompletion?.toDomainCompletion()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addCompletion(completion: HabitCompletion) {
        val userId = authRepository.getCurrentUser()?.id ?: return

        val firestoreCompletion = FirestoreOnlyCompletion(
            id = completion.id,
            habitId = completion.habitId,
            userId = userId,
            date = completion.date.format(dateFormatter),
            completed = completion.completed,
            completedAt = completion.completedAt
        )

        try {
            completionsCollection.document(completion.id).set(firestoreCompletion).await()
            recalculateStreak(completion.habitId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun removeCompletion(completion: HabitCompletion) {
        try {
            completionsCollection.document(completion.id).delete().await()
            recalculateStreak(completion.habitId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private suspend fun getHabitCompletionsSync(habitId: String): List<HabitCompletion> {
        return getHabitCompletions(habitId)
    }

    private suspend fun recalculateStreak(habitId: String) {
        val habit = getHabitById(habitId) ?: return

        // Получаем все выполнения
        val completions = getHabitCompletions(habitId)
            .filter { it.completed }
            .map { it.date }
            .toSet()

        // Используем общий калькулятор
        val (currentStreak, lastCompleted) = StreakCalculator.calculateStreak(habit, completions)

        // Обновляем в Firestore
        val userId = authRepository.getCurrentUser()?.id ?: return

        val updates = mutableMapOf<String, Any>(
            "currentStreak" to currentStreak,
            "lastCompleted" to (lastCompleted?.format(dateFormatter) ?: "")
        )

        if (currentStreak > habit.longestStreak) {
            updates["longestStreak"] = currentStreak
        }

        habitsCollection.document(habitId).update(updates).await()
    }




    // Генерация тестовых данных

    override suspend fun generateTestHabits(count: Int) {
        println("DEBUG: Начало генерации $count тестовых привычек")

        val userId = authRepository.getCurrentUser()?.id ?: run {
            println("ERROR: Пользователь не авторизован")
            return
        }

        val testData = TestDataGenerator.generateTestHabits(count, userId)
        println("DEBUG: Сгенерировано ${testData.size} привычек")

        var totalSuccess = 0
        var totalErrors = 0

        // Разбиваем на небольшие батчи для надежности
        val batchSize = 50 // Небольшие батчи для оффлайн-режима

        testData.chunked(batchSize).forEachIndexed { batchIndex, chunk ->
            println("DEBUG: Обработка батча $batchIndex из ${testData.size / batchSize}")

            val batch = firestore.batch()

            chunk.forEach { (habit, completions) ->
                try {
                    // 1. Привычка
                    val firestoreHabit = habit.toFirestoreOnlyHabit(userId)
                    val habitRef = habitsCollection.document(habit.id)
                    batch.set(habitRef, firestoreHabit)

                    // 2. Выполнения
                    completions.forEach { completion ->
                        val firestoreCompletion = FirestoreOnlyCompletion(
                            id = completion.id,
                            habitId = completion.habitId,
                            userId = userId,
                            date = completion.date.format(dateFormatter),
                            completed = completion.completed,
                            completedAt = completion.completedAt
                        )
                        val completionRef = completionsCollection.document(completion.id)
                        batch.set(completionRef, firestoreCompletion)
                    }

                    totalSuccess++
                } catch (e: Exception) {
                    totalErrors++
                    println("WARN: Ошибка при подготовке привычки ${habit.id}: ${e.message}")
                }
            }

            // Пытаемся отправить батч
            try {

                batch.commit().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("DEBUG: Батч $batchIndex успешно отправлен/поставлен в очередь")
                    } else {
                        println("WARN: Батч $batchIndex не отправлен: ${task.exception?.message}")

                    }
                }


                kotlinx.coroutines.delay(10)

            } catch (e: Exception) {
                println("ERROR: Критическая ошибка батча $batchIndex: ${e.message}")
            }
        }

        println("DEBUG: Генерация завершена. Успешно: $totalSuccess, ошибок: $totalErrors")

        // Даже при ошибках показываем пользователю успех, так как в оффлайн-режиме
        // данные будут в локальной очереди Firestore
    }


    override suspend fun clearAllData() {
        val userId = authRepository.getCurrentUser()?.id ?: return

        try {
            // Удаляем все привычки пользователя
            val habitsSnapshot = habitsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val batch = firestore.batch()
            habitsSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            // Удаляем все выполнения пользователя
            val completionsSnapshot = completionsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val batch2 = firestore.batch()
            completionsSnapshot.documents.forEach { doc ->
                batch2.delete(doc.reference)
            }
            batch2.commit().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    suspend fun getLocalHabits(): List<Habit> {
        val userId = authRepository.getCurrentUser()?.id ?: return emptyList()

        return try {
            // Используем Source.CACHE для чтения только из кэша
            val snapshot = habitsCollection
                .whereEqualTo("userId", userId)
                .get(Source.CACHE)
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject<FirestoreOnlyHabit>()?.toDomainHabit()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLocalCompletions(habitId: String): List<HabitCompletion> {
        val userId = authRepository.getCurrentUser()?.id ?: return emptyList()

        return try {
            val snapshot = completionsCollection
                .whereEqualTo("habitId", habitId)
                .whereEqualTo("userId", userId)
                .get(Source.CACHE)
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject<FirestoreOnlyCompletion>()?.toDomainCompletion()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Метод для проверки подключения к интернету
    private suspend fun isNetworkAvailable(): Boolean {
        return try {
            // Проверка через ConnectivityManager или другой способ
            true // Замените на реальную проверку
        } catch (e: Exception) {
            false
        }
    }
}

// Расширения для преобразования моделей
private fun Habit.toFirestoreOnlyHabit(userId: String): FirestoreOnlyHabit {
    return FirestoreOnlyHabit(
        id = id,
        userId = userId,
        name = name,
        description = description,
        type = type.name,
        priority = priority.name,
        reminderTime = reminderTime,
        hasReminder = hasReminder,
        reminderDays = reminderDays.map { it.name },
        targetDays = targetDays.map { it.name },
        category = category,
        createdAt = createdAt,
        lastCompleted = lastCompleted?.format(DateTimeFormatter.ISO_LOCAL_DATE),
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        reminderId = reminderId
    )
}

private fun FirestoreOnlyHabit.toDomainHabit(): Habit {
    return Habit(
        id = id,
        name = name,
        description = description,
        type = HabitType.valueOf(type),
        priority = Priority.valueOf(priority),
        reminderTime = reminderTime,
        hasReminder = hasReminder,
        reminderDays = reminderDays.map { DayOfWeek.valueOf(it) },
        targetDays = targetDays.map { DayOfWeek.valueOf(it) },
        category = category,
        createdAt = createdAt,
        lastCompleted = lastCompleted?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) },
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        reminderId = reminderId ?: "",
        syncStatus = SyncStatus.SYNCED // Всегда синхронизировано в Firestore-only
    )
}

private fun FirestoreOnlyCompletion.toDomainCompletion(): HabitCompletion {
    return HabitCompletion(
        id = id,
        habitId = habitId,
        date = LocalDate.parse(date),
        completed = completed,
        completedAt = completedAt,
        syncStatus = SyncStatus.SYNCED
    )
}