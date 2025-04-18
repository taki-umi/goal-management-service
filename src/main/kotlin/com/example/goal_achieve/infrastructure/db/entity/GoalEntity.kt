package com.example.goal_achieve.infrastructure.db.entity

import com.example.goal_achieve.domain.model.Goal
import com.example.goal_achieve.domain.model.GoalStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "goals")
data class GoalEntity(
    @Id
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val category: String?,
    val targetDate: LocalDate?,

    @Enumerated(EnumType.STRING)
    val status: GoalStatus,

    val progress: Int,
    val parentGoalId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(): Goal {
        return Goal(
            id = id,
            userId = userId,
            title = title,
            description = description,
            category = category,
            targetDate = targetDate,
            status = status,
            progress = progress,
            parentGoalId = parentGoalId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(goal: Goal): GoalEntity {
            return GoalEntity(
                id = goal.id,
                userId = goal.userId,
                title = goal.title,
                description = goal.description,
                category = goal.category,
                targetDate = goal.targetDate,
                status = goal.status,
                progress = goal.progress,
                parentGoalId = goal.parentGoalId,
                createdAt = goal.createdAt,
                updatedAt = goal.updatedAt
            )
        }
    }
}