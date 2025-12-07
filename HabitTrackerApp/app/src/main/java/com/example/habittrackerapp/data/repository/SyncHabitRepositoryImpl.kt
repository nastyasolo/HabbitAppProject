package com.example.habittrackerapp.data.repository

import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.mapper.FirestoreHabitMapper
import com.example.habittrackerapp.data.mapper.HabitMapper
import com.example.habittrackerapp.data.mapper.toDomain
import com.example.habittrackerapp.data.mapper.toEntity
import com.example.habittrackerapp.data.remote.HabitRemoteDataSource
import com.example.habittrackerapp.domain.model.Habit
import com.example.habittrackerapp.domain.model.SyncStatus
import com.example.habittrackerapp.domain.repository.AuthRepository
import com.example.habittrackerapp.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SyncHabitRepositoryImpl @Inject constructor(
    private val localDataSource: HabitDao,
    private val remoteDataSource: HabitRemoteDataSource,
    private val authRepository: AuthRepository
) : HabitRepository {

    override fun getAllHabits(): Flow<List<Habit>> {
        return localDataSource.getAllHabits().map { habits ->
            habits.map { it.toDomain() }
        }
    }

    override suspend fun getHabitById(id: String): Habit? {
        return localDataSource.getHabitById(id)?.let { it.toDomain() }
    }

    override suspend fun insertHabit(habit: Habit) {
        // Сохраняем локально
        val entity = habit.toEntity().copy(syncStatus = SyncStatus.PENDING.name)
        localDataSource.insertHabit(entity)

        // Пытаемся синхронизировать с сервером
        authRepository.getCurrentUser()?.let { user ->
            val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(habit, user.id)
            val result = remoteDataSource.saveHabit(firestoreHabit)

            if (result.isSuccess) {
                // Помечаем как синхронизированную
                markHabitAsSynced(habit.id)
            }
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        // Обновляем локально
        val entity = habit.toEntity().copy(syncStatus = SyncStatus.PENDING.name)
        localDataSource.updateHabit(entity)

        // Пытаемся синхронизировать с сервером
        authRepository.getCurrentUser()?.let { user ->
            val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(habit, user.id)
            remoteDataSource.updateHabit(firestoreHabit)

            // Помечаем как синхронизированную
            markHabitAsSynced(habit.id)
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        // Удаляем локально
        localDataSource.deleteHabit(habit.toEntity())

        // Пытаемся удалить с сервера
        authRepository.getCurrentUser()?.let { user ->
            remoteDataSource.deleteHabit(habit.id, user.id)
        }
    }

    override suspend fun toggleHabitCompletion(id: String) {
        val habitEntity = localDataSource.getHabitById(id)
        habitEntity?.let {
            val updatedHabit = it.copy(
                isCompleted = !it.isCompleted,
                streak = if (!it.isCompleted) it.streak + 1 else it.streak,
                lastCompleted = if (!it.isCompleted) System.currentTimeMillis() else null
            )
            localDataSource.updateHabit(updatedHabit)

            // Обновляем на сервере
            authRepository.getCurrentUser()?.let { user ->
                val domainHabit = updatedHabit.toDomain()
                val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(domainHabit, user.id)
                val result = remoteDataSource.updateHabit(firestoreHabit)

                if (result.isSuccess) {
                    // Помечаем как синхронизированную
                    localDataSource.updateHabit(
                        updatedHabit.copy(
                            syncStatus = SyncStatus.SYNCED.name,
                            lastSynced = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    override suspend fun getTodayHabits(): List<Habit> {
        val habits = localDataSource.getAllHabitsSimple()
        return habits.map { it.toDomain() }
    }

    override suspend fun getWeeklyHabits(): List<Habit> {
        val habits = localDataSource.getAllHabitsSimple()
        return habits.map { it.toDomain() }
    }

    override suspend fun syncHabits(): Boolean {
        return try {
            val user = authRepository.getCurrentUser() ?: return false

            // Загружаем привычки с сервера
            val remoteHabitsResult = remoteDataSource.getHabits(user.id)
            if (remoteHabitsResult.isSuccess) {
                val remoteHabits = remoteHabitsResult.getOrThrow()

                // Преобразуем в Domain модели
                val domainHabits = remoteHabits.map { FirestoreHabitMapper.toDomainHabit(it) }

                // Получаем локальные привычки
                val localHabits = localDataSource.getAllHabitsSimple()

                // Логика слияния
                domainHabits.forEach { remoteHabit ->
                    val localHabit = localHabits.find { it.id == remoteHabit.id }

                    if (localHabit == null) {
                        // Добавляем новую привычку
                        localDataSource.insertHabit(
                            remoteHabit.toEntity()
                        )
                    } else {
                        // Сравниваем временные метки
                        val localUpdated = localHabit.lastSynced ?: localHabit.createdAt
                        val remoteUpdated = remoteHabit.lastCompleted ?: remoteHabit.createdAt

                        if (remoteUpdated > localUpdated) {
                            // Обновляем локальную версию
                            localDataSource.updateHabit(
                                remoteHabit.toEntity()
                            )
                        }
                    }
                }

                // Отправляем на сервер привычки, которых там нет
                val pendingHabits = getPendingHabits()
                pendingHabits.forEach { habit ->
                    val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(habit, user.id)
                    remoteDataSource.saveHabit(firestoreHabit)

                    // Помечаем как синхронизированную
                    markHabitAsSynced(habit.id)
                }

                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun syncPendingHabits(): Boolean {
        return try {
            val user = authRepository.getCurrentUser() ?: return false
            val pendingHabits = getPendingHabits()

            pendingHabits.forEach { habit ->
                val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(habit, user.id)
                val result = remoteDataSource.saveHabit(firestoreHabit)

                if (result.isSuccess) {
                    markHabitAsSynced(habit.id)
                }
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun markHabitAsSynced(habitId: String) {
        localDataSource.getHabitById(habitId)?.let { habit ->
            localDataSource.updateHabit(
                habit.copy(
                    syncStatus = SyncStatus.SYNCED.name,
                    lastSynced = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun getPendingHabits(): List<Habit> {
        val habits = localDataSource.getAllHabitsSimple()
        return habits
            .filter { it.syncStatus == SyncStatus.PENDING.name }
            .map { it.toDomain() }
    }
}