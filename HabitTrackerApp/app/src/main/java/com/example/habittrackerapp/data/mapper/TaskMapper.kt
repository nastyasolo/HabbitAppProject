package com.example.habittrackerapp.data.mapper

import com.example.habittrackerapp.data.model.TaskEntity
import com.example.habittrackerapp.domain.model.Task

class TaskMapper {
    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            dueDate = entity.dueDate,
            dueTime = entity.dueTime,
            priority = entity.priority,
            category = entity.category,
            isCompleted = entity.isCompleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Task): TaskEntity {
        return TaskEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            dueDate = domain.dueDate,
            dueTime = domain.dueTime,
            priority = domain.priority,
            category = domain.category,
            isCompleted = domain.isCompleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}