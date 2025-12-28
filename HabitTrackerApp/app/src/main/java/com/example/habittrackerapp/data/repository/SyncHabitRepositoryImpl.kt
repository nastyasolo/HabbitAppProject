package com.example.habittrackerapp.data.repository

import com.example.habittrackerapp.data.database.HabitCompletionDao
import com.example.habittrackerapp.data.database.HabitDao
import com.example.habittrackerapp.data.mapper.FirestoreHabitMapper
import com.example.habittrackerapp.data.mapper.HabitCompletionMapper
import com.example.habittrackerapp.data.mapper.HabitMapper
import com.example.habittrackerapp.data.remote.HabitRemoteDataSource
import com.example.habittrackerapp.domain.model.*
import com.example.habittrackerapp.domain.repository.AuthRepository
import com.example.habittrackerapp.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncHabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao,
    private val remoteDataSource: HabitRemoteDataSource,
    private val authRepository: AuthRepository
) : HabitRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { habits ->
            habits.map { HabitMapper.toDomain(it) }
        }
    }

    override suspend fun getHabitWithCompletions(id: String): HabitWithCompletions? {
        val habit = habitDao.getHabitById(id) ?: return null
        val completions = completionDao.getCompletionsForHabitSimple(id)

        return HabitWithCompletions(
            habit = HabitMapper.toDomain(habit),
            completions = completions.map { HabitCompletionMapper.toDomain(it) }
        )
    }

    override suspend fun getHabitById(id: String): Habit? {
        return habitDao.getHabitById(id)?.let { HabitMapper.toDomain(it) }
    }

    override suspend fun insertHabit(habit: Habit) {
        // Сохраняем локально
        val entity = HabitMapper.toEntity(habit)
        habitDao.insertHabit(entity)

        // Синхронизируем с сервером
        authRepository.getCurrentUser()?.let { user ->
            val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(habit, user.id)
            remoteDataSource.saveHabit(firestoreHabit)
            habitDao.updateHabitSyncStatus(habit.id, SyncStatus.SYNCED.name, System.currentTimeMillis())
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        val entity = HabitMapper.toEntity(habit).copy(syncStatus = SyncStatus.PENDING.name)
        habitDao.updateHabit(entity)

        authRepository.getCurrentUser()?.let { user ->
            val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(habit, user.id)
            remoteDataSource.updateHabit(firestoreHabit)
            habitDao.updateHabitSyncStatus(habit.id, SyncStatus.SYNCED.name, System.currentTimeMillis())
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(HabitMapper.toEntity(habit))

        authRepository.getCurrentUser()?.let { user ->
            remoteDataSource.deleteHabit(habit.id, user.id)
        }
    }

    override fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>> {
        return habitDao.getAllHabits().map { habitEntities ->
            habitEntities.map { habitEntity ->
                val domainHabit = HabitMapper.toDomain(habitEntity)
                val completions = completionDao
                    .getCompletionsForHabitSimple(habitEntity.id)
                    .map { HabitCompletionMapper.toDomain(it) }

                HabitWithCompletions(
                    habit = domainHabit,
                    completions = completions
                )
            }
        }
    }

    override suspend fun toggleHabitCompletion(id: String) {
        val today = LocalDate.now()
        val todayString = today.format(dateFormatter)

        // Проверяем, есть ли запись за сегодня
        val existingCompletion = completionDao.getCompletionByDate(id, todayString)

        if (existingCompletion != null) {
            // Отмена выполнения - удаляем запись
            completionDao.deleteCompletion(existingCompletion)
        } else {
            // Создаем новую запись о выполнении
            val completion = HabitCompletion(
                habitId = id,
                date = today,
                completed = true,
                completedAt = System.currentTimeMillis()
            )
            completionDao.insertCompletion(HabitCompletionMapper.toEntity(completion))
        }

        // Пересчитываем стрик
        recalculateStreak(id)

        // Синхронизируем с сервером
        authRepository.getCurrentUser()?.let { user ->
            if (existingCompletion != null) {
                // Удаляем запись с сервера
                remoteDataSource.deleteCompletion(existingCompletion.id, user.id, id)
            } else {
                // Сохраняем новую запись
                val firestoreCompletion = FirestoreHabitMapper.toFirestoreCompletion(
                    HabitCompletion(
                        habitId = id,
                        date = today,
                        completed = true
                    ),
                    user.id
                )
                remoteDataSource.saveCompletion(firestoreCompletion)
            }
        }
    }

    override suspend fun getTodayHabits(): List<Habit> {
        val habits = habitDao.getAllHabitsSimple()
        return habits.map { HabitMapper.toDomain(it) }
    }

    override suspend fun getWeeklyHabits(): List<Habit> {
        val habits = habitDao.getAllHabitsSimple()
        return habits.filter { it.type == "WEEKLY" }.map { HabitMapper.toDomain(it) }
    }


    override suspend fun getWeeklyProgress(habitId: String): Float {
        val habit = getHabitById(habitId) ?: return 0f

        return when (habit.type) {
            HabitType.DAILY -> {
                // Логика для ежедневных привычек
                val completions = getHabitCompletions(habitId)
                val lastWeekCompletions = completions
                    .filter { it.completed }
                    .filter { it.date >= LocalDate.now().minusDays(6) }
                lastWeekCompletions.size / 7f
            }
            HabitType.WEEKLY -> {
                // Логика для еженедельных привычек
                val completions = getHabitCompletions(habitId)
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
        return try {
            val user = authRepository.getCurrentUser() ?: return false

            // Загружаем привычки с сервера
            val remoteHabitsResult = remoteDataSource.getHabits(user.id)
            if (remoteHabitsResult.isSuccess) {
                val remoteHabits = remoteHabitsResult.getOrThrow()

                // Синхронизируем привычки
                remoteHabits.forEach { remoteHabit ->
                    val localHabit = habitDao.getHabitById(remoteHabit.id)
                    val domainHabit = FirestoreHabitMapper.toDomainHabit(remoteHabit)

                    if (localHabit == null) {
                        habitDao.insertHabit(HabitMapper.toEntity(domainHabit))
                    } else {
                        // Логика разрешения конфликтов по времени
                        val localUpdated = localHabit.lastSynced ?: localHabit.createdAt
                        val remoteUpdated = remoteHabit.lastCompleted?.let {
                            try {
                                LocalDate.parse(it).toEpochDay() * 1000
                            } catch (e: Exception) {
                                remoteHabit.createdAt
                            }
                        } ?: remoteHabit.createdAt

                        if (remoteUpdated > localUpdated) {
                            habitDao.updateHabit(HabitMapper.toEntity(domainHabit))
                        }
                    }

                    // Синхронизируем историю выполнения
                    val completionsResult = remoteDataSource.getCompletions(user.id, remoteHabit.id)
                    if (completionsResult.isSuccess) {
                        val remoteCompletions = completionsResult.getOrThrow()
                        remoteCompletions.forEach { remoteCompletion ->
                            val localCompletion = completionDao.getCompletionByDate(
                                remoteHabit.id,
                                remoteCompletion.date
                            )

                            if (localCompletion == null) {
                                val domainCompletion = FirestoreHabitMapper.toDomainCompletion(remoteCompletion)
                                completionDao.insertCompletion(HabitCompletionMapper.toEntity(domainCompletion))
                            }
                        }
                    }
                }

                // Отправляем локальные изменения на сервер
                syncPendingHabits()
                syncPendingCompletions()

                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun syncPendingHabits(): Boolean {
        return try {
            val user = authRepository.getCurrentUser() ?: return false
            val pendingHabits = habitDao.getPendingHabits()

            pendingHabits.forEach { habit ->
                val domainHabit = HabitMapper.toDomain(habit)
                val firestoreHabit = FirestoreHabitMapper.toFirestoreHabit(domainHabit, user.id)
                val result = remoteDataSource.saveHabit(firestoreHabit)

                if (result.isSuccess) {
                    habitDao.updateHabitSyncStatus(
                        habit.id,
                        SyncStatus.SYNCED.name,
                        System.currentTimeMillis()
                    )
                }
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun markHabitAsSynced(habitId: String) {
        habitDao.updateHabitSyncStatus(habitId, SyncStatus.SYNCED.name, System.currentTimeMillis())
    }

    override suspend fun getPendingHabits(): List<Habit> {
        return habitDao.getPendingHabits().map { HabitMapper.toDomain(it) }
    }

    override suspend fun getHabitCompletions(habitId: String): List<com.example.habittrackerapp.domain.model.HabitCompletion> {
        return completionDao.getCompletionsForHabitSimple(habitId).map { HabitCompletionMapper.toDomain(it) }
    }

    override suspend fun addCompletion(completion: com.example.habittrackerapp.domain.model.HabitCompletion) {
        completionDao.insertCompletion(HabitCompletionMapper.toEntity(completion))
        recalculateStreak(completion.habitId)
    }

    override suspend fun removeCompletion(completion: com.example.habittrackerapp.domain.model.HabitCompletion) {
        completionDao.deleteCompletion(HabitCompletionMapper.toEntity(completion))
        recalculateStreak(completion.habitId)
    }

    private suspend fun recalculateStreak(habitId: String) {
        val completions = completionDao.getCompletedDatesSimple(habitId)
        val habit = habitDao.getHabitById(habitId) ?: return

        val domainHabit = HabitMapper.toDomain(habit)

        when (domainHabit.type) {
            HabitType.DAILY -> {
                var currentStreak = 0
                var longestStreak = habit.longestStreak
                var currentDate = LocalDate.now()
                val sortedDates = completions
                    .mapNotNull { dateStr ->
                        try {
                            LocalDate.parse(dateStr)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    .sortedDescending()

                for (completionDate in sortedDates) {
                    if (completionDate == currentDate ||
                        (currentStreak == 0 && completionDate == currentDate.minusDays(1))) {
                        currentStreak++
                        currentDate = currentDate.minusDays(1)
                    } else {
                        break
                    }
                }

                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak
                    habitDao.updateLongestStreak(habitId, longestStreak)
                }

                val lastCompleted = sortedDates.firstOrNull()?.format(dateFormatter)
                habitDao.updateHabitStreak(habitId, currentStreak, lastCompleted)
            }

            HabitType.WEEKLY -> {
                val targetDaysSet = domainHabit.targetDays.toSet()
                if (targetDaysSet.isEmpty()) return

                var currentStreak = 0
                var longestStreak = habit.longestStreak


                val completedDates = completions.mapNotNull { dateStr ->
                    try {
                        LocalDate.parse(dateStr)
                    } catch (e: Exception) {
                        null
                    }
                }


                var weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY)

                while (true) {
                    val weekEnd = weekStart.plusDays(6)
                    val weekCompletedDates = completedDates.filter {
                        it >= weekStart && it <= weekEnd
                    }

                    // Проверяем, выполнены ли все целевые дни на этой неделе
                    val completedDaysInWeek = weekCompletedDates.map { date ->
                        DayOfWeek.fromInt(date.dayOfWeek.value)
                    }.toSet()

                    val isWeekCompleted = targetDaysSet.all { it in completedDaysInWeek }

                    if (isWeekCompleted) {
                        currentStreak++
                        weekStart = weekStart.minusWeeks(1)
                    } else {
                        break
                    }
                }

                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak
                    habitDao.updateLongestStreak(habitId, longestStreak)
                }

                val lastCompleted = completedDates.maxOrNull()?.format(dateFormatter)
                habitDao.updateHabitStreak(habitId, currentStreak, lastCompleted)
            }
        }
    }

    private suspend fun syncPendingCompletions(): Boolean {
        return try {
            val user = authRepository.getCurrentUser() ?: return false
            val pendingCompletions = completionDao.getPendingCompletions()

            pendingCompletions.forEach { completion ->
                val domainCompletion = HabitCompletionMapper.toDomain(completion)
                val firestoreCompletion = FirestoreHabitMapper.toFirestoreCompletion(domainCompletion, user.id)
                val result = remoteDataSource.saveCompletion(firestoreCompletion)

                if (result.isSuccess) {
                    completionDao.updateCompletionSyncStatus(
                        completion.id,
                        SyncStatus.SYNCED.name,
                        System.currentTimeMillis()
                    )
                }
            }

            true
        } catch (e: Exception) {
            false
        }
    }
}