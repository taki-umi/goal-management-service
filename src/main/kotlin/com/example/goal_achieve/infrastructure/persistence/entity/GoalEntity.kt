package com.example.goal_achieve.infrastructure.persistence.entity

import com.example.goal_achieve.domain.model.Goal
import com.example.goal_achieve.domain.value.GoalStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * ゴールのデータベースエンティティ
 */
@Entity
@Table(
    name = "goals",
    indexes = [
        Index(name = "idx_goals_user_id", columnList = "userId"),
        Index(name = "idx_goals_parent_goal_id", columnList = "parentGoalId"),
        Index(name = "idx_goals_user_id_status", columnList = "userId,status"),
        Index(name = "idx_goals_target_date", columnList = "targetDate")
    ]
)
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
    /**
     * エンティティからドメインモデルへの変換
     */
    fun toDomain(): Goal {
        return Goal.reconstitute(
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
        /**
         * ドメインモデルからエンティティへの変換
         */
        fun fromDomain(goal: Goal): GoalEntity {
            return GoalEntity(
                id = goal.id.value,
                userId = goal.userId.value,
                title = goal.getTitle(),
                description = goal.getDescription(),
                category = goal.getCategoryValue(),
                targetDate = goal.getTargetDate(),
                status = goal.getStatus(),
                progress = goal.getProgressValue(),
                parentGoalId = goal.parentGoalId?.value,
                createdAt = goal.createdAt,
                updatedAt = goal.getUpdatedAt()
            )
        }
    }
}