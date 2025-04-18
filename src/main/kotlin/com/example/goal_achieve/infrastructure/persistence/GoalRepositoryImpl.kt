package com.example.goal_achieve.infrastructure.persistence

import com.example.goal_achieve.domain.model.Goal
import com.example.goal_achieve.domain.repository.GoalRepository
import com.example.goal_achieve.domain.value.GoalStatus
import com.example.goal_achieve.infrastructure.persistence.entity.GoalEntity
import com.example.goal_achieve.infrastructure.persistence.repository.GoalJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * ゴールリポジトリの実装クラス
 */
@Repository
class GoalRepositoryImpl(
    private val goalJpaRepository: GoalJpaRepository
) : GoalRepository {

    /**
     * ゴールを保存する
     */
    @Transactional
    override suspend fun save(goal: Goal): Goal {
        val entity = GoalEntity.fromDomain(goal)
        return goalJpaRepository.save(entity).toDomain()
    }

    /**
     * IDでゴールを検索する
     */
    override suspend fun findById(id: String): Goal? {
        return goalJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    /**
     * ユーザーIDでゴールを検索する
     */
    override suspend fun findByUserId(userId: String): List<Goal> {
        return goalJpaRepository.findByUserId(userId).map { it.toDomain() }
    }

    /**
     * ユーザーIDとステータスでゴールを検索する
     */
    override suspend fun findByUserIdAndStatus(userId: String, status: GoalStatus): List<Goal> {
        return goalJpaRepository.findByUserIdAndStatus(userId, status)
            .map { it.toDomain() }
    }

    /**
     * 親ゴールIDでサブゴールを検索する
     */
    override suspend fun findByParentGoalId(parentGoalId: String): List<Goal> {
        return goalJpaRepository.findByParentGoalId(parentGoalId)
            .map { it.toDomain() }
    }

    /**
     * おすすめゴールを取得する
     */
    override suspend fun findRecommendedGoals(userId: String, limit: Int): List<Goal> {
        return goalJpaRepository.findRecommendedGoals(userId, limit)
            .map { it.toDomain() }
    }

    /**
     * ゴールを削除する
     */
    @Transactional
    override suspend fun delete(id: String) {
        goalJpaRepository.deleteById(id)
    }

    /**
     * 期間内のゴールを検索する
     */
    override suspend fun findByUserIdAndTargetDateBetween(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Goal> {
        return goalJpaRepository.findByUserIdAndTargetDateBetween(userId, startDate, endDate)
            .map { it.toDomain() }
    }

    /**
     * リーフゴール（子を持たないゴール）を検索する
     */
    suspend fun findLeafGoalsByUserId(userId: String): List<Goal> {
        return goalJpaRepository.findLeafGoalsByUserId(userId)
            .map { it.toDomain() }
    }

    /**
     * ルートゴール（親を持たないゴール）を検索する
     */
    suspend fun findRootGoalsByUserId(userId: String): List<Goal> {
        return goalJpaRepository.findByUserIdAndParentGoalIdIsNull(userId)
            .map { it.toDomain() }
    }

    /**
     * 進捗が低いゴールを検索する
     */
    suspend fun findLowProgressGoals(userId: String, maxProgress: Int): List<Goal> {
        return goalJpaRepository.findLowProgressGoals(userId, maxProgress)
            .map { it.toDomain() }
    }
}