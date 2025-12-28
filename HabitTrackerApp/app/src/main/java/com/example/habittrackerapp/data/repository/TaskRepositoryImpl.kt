package com.example.habittrackerapp.data.repository

import com.example.habittrackerapp.data.database.TaskDao
import com.example.habittrackerapp.data.mapper.TaskMapper
import com.example.habittrackerapp.domain.model.Task
import com.example.habittrackerapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val mapper: TaskMapper
) : TaskRepository {

    init {
        println("DEBUG: TaskRepositoryImpl created with taskDao: $taskDao")
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { tasks ->
            println("DEBUG: Got ${tasks.size} tasks from database")
            tasks.map { mapper.toDomain(it) }
        }
    }

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDate(date).map { tasks ->
            tasks.map { mapper.toDomain(it) }
        }
    }

    override fun getUpcomingTasks(limit: Int): Flow<List<Task>> {
        return taskDao.getUpcomingTasks(LocalDate.now(), limit).map { tasks ->
            tasks.map { mapper.toDomain(it) }
        }
    }

    override suspend fun getTaskById(id: String): Task? {
        println("DEBUG: Getting task by id: $id")
        return taskDao.getTaskById(id)?.let {
            println("DEBUG: Found task: $it")
            mapper.toDomain(it)
        }
    }

    override suspend fun insertTask(task: Task) {
        println("DEBUG: Inserting task: $task")
        taskDao.insertTask(mapper.toEntity(task))
    }

    override suspend fun updateTask(task: Task) {
        println("DEBUG: Updating task: $task")
        taskDao.updateTask(mapper.toEntity(task))
    }

    override suspend fun deleteTask(task: Task) {
        println("DEBUG: Deleting task: $task")
        taskDao.deleteTask(mapper.toEntity(task))
    }

    override suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean) {
        println("DEBUG: Toggling task $id to $isCompleted")
        taskDao.updateTaskCompletion(id, isCompleted)
    }

    override suspend fun getOverdueTasksCount(): Int {
        return taskDao.getOverdueTasksCount(LocalDate.now())
    }

    override suspend fun getDueTodayTasksCount(): Int {
        return taskDao.getDueTodayTasksCount(LocalDate.now())
    }
}