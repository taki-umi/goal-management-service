// Goal.kt
package com.example.goal_achieve.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val description: String?,
    val category: String?, // TODO: Enum化する
    val targetDate: LocalDate?,
    val status: GoalStatus = GoalStatus.ACTIVE, // TODO: valueモデルにする
    val progress: Int = 0,
    val parentGoalId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {

    fun updateProgress(newProgress: Int): Goal {
        require(newProgress in 0..100) { "Progress must be between 0 and 100" }
        return this.copy(
            progress = newProgress,
            status = if (newProgress == 100) GoalStatus.COMPLETED else this.status,
            updatedAt = LocalDateTime.now()
        )
    }

    fun update(
        title: String? = null,
        description: String? = null,
        category: String? = null,
        targetDate: LocalDate? = null,
        status: GoalStatus? = null
    ): Goal {
        return this.copy(
            title = title ?: this.title,
            description = description ?: this.description,
            category = category ?: this.category,
            targetDate = targetDate ?: this.targetDate,
            status = status ?: this.status,
            updatedAt = LocalDateTime.now()
        )
    }
}

enum class GoalStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED,
    DELETED
}