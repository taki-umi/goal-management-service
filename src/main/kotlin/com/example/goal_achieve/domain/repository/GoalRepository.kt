package com.example.goal_achieve.domain.repository

import com.example.goal_achieve.domain.model.Goal
import java.time.LocalDate

interface GoalRepository {
    suspend fun save(goal: Goal): Goal
    suspend fun findById(id: String): Goal?
    suspend fun findByUserId(userId: String): List<Goal>
    suspend fun findByUserIdAndStatus(userId: String, status: String): List<Goal>
    suspend fun findByParentGoalId(parentGoalId: String): List<Goal>
    suspend fun findRecommendedGoals(userId: String, limit: Int): List<Goal>
    suspend fun delete(id: String)
    suspend fun updateProgress(id: String, progress: Int): Goal?
}
