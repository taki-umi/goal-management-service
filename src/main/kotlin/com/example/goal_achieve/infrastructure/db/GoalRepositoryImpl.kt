package com.example.goal_achieve.infrastructure.db

import com.example.goal_achieve.domain.model.Goal
import com.example.goal_achieve.domain.model.GoalStatus
import com.example.goal_achieve.domain.repository.GoalRepository
import com.example.goal_achieve.infrastructure.db.entity.GoalEntity
import com.example.goal_achieve.infrastructure.db.repository.GoalJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class GoalRepositoryImpl(
    private val goalJpaRepository: GoalJpaRepository
) : GoalRepository {

    @Transactional
    override suspend fun save(goal: Goal): Goal {
        val entity = GoalEntity.fromDomain(goal)
        return goalJpaRepository.save(entity).toDomain()
    }

    override suspend fun findById(id: String): Goal? {
        return goalJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override suspend fun findByUserId(userId: String): List<Goal> {
        return goalJpaRepository.findByUserId(userId).map { it.toDomain() }
    }

    override suspend fun findByUserIdAndStatus(userId: String, status: String): List<Goal> {
        return goalJpaRepository.findByUserIdAndStatus(userId, GoalStatus.valueOf(status))
            .map { it.toDomain() }
    }

    override suspend fun findByParentGoalId(parentGoalId: String): List<Goal> {
        return goalJpaRepository.findByParentGoalId(parentGoalId).map { it.toDomain() }
    }

    override suspend fun findRecommendedGoals(userId: String, limit: Int): List<Goal> {
        return goalJpaRepository.findRecommendedGoals(userId, limit).map { it.toDomain() }
    }

    @Transactional
    override suspend fun delete(id: String) {
        goalJpaRepository.deleteById(id)
    }

    @Transactional
    override suspend fun updateProgress(id: String, progress: Int): Goal? {
        val existingGoal = findById(id) ?: return null
        val updatedGoal = existingGoal.updateProgress(progress)
        return save(updatedGoal)
    }
}
