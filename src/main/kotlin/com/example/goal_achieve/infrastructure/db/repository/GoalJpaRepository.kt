package com.example.goal_achieve.infrastructure.db.repository

import com.example.goal_achieve.domain.model.GoalStatus
import com.example.goal_achieve.infrastructure.db.entity.GoalEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GoalJpaRepository : JpaRepository<GoalEntity, String> {
    fun findByUserId(userId: String): List<GoalEntity>
    fun findByUserIdAndStatus(userId: String, status: GoalStatus): List<GoalEntity>
    fun findByParentGoalId(parentGoalId: String): List<GoalEntity>

    @Query("SELECT g FROM GoalEntity g WHERE g.userId != :userId AND g.status = 'ACTIVE' ORDER BY RANDOM() LIMIT :limit")
    fun findRecommendedGoals(userId: String, limit: Int): List<GoalEntity>
}
