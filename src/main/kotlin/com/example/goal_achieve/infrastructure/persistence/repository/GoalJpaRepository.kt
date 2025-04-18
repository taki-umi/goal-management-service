package com.example.goal_achieve.infrastructure.persistence.repository

import com.example.goal_achieve.domain.value.GoalStatus
import com.example.goal_achieve.infrastructure.persistence.entity.GoalEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

/**
 * GoalEntityに対するJPAリポジトリインターフェース
 */
interface GoalJpaRepository : JpaRepository<GoalEntity, String> {
    /**
     * ユーザーIDでゴールを検索
     */
    fun findByUserId(userId: String): List<GoalEntity>

    /**
     * ユーザーIDとステータスでゴールを検索
     */
    fun findByUserIdAndStatus(userId: String, status: GoalStatus): List<GoalEntity>

    /**
     * 親ゴールIDでサブゴールを検索
     */
    fun findByParentGoalId(parentGoalId: String): List<GoalEntity>

    /**
     * おすすめゴールを取得
     * - ユーザーID以外のアクティブなゴールをランダムに取得
     */
    @Query("""
        SELECT g FROM GoalEntity g 
        WHERE g.userId != :userId 
          AND g.status = 'ACTIVE' 
        ORDER BY RANDOM() 
        LIMIT :limit
    """)
    fun findRecommendedGoals(
        @Param("userId") userId: String,
        @Param("limit") limit: Int
    ): List<GoalEntity>

    /**
     * ユーザーの期間内のゴールを取得
     */
    @Query("""
        SELECT g FROM GoalEntity g 
        WHERE g.userId = :userId 
          AND g.targetDate >= :startDate 
          AND g.targetDate <= :endDate 
          AND g.status = 'ACTIVE'
    """)
    fun findByUserIdAndTargetDateBetween(
        @Param("userId") userId: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<GoalEntity>

    /**
     * ユーザーの子ゴールを持たないゴールを取得（リーフノード）
     */
    @Query("""
        SELECT g FROM GoalEntity g 
        WHERE g.userId = :userId 
          AND g.status = 'ACTIVE' 
          AND g.id NOT IN (
              SELECT DISTINCT pg.parentGoalId 
              FROM GoalEntity pg 
              WHERE pg.parentGoalId IS NOT NULL
          )
    """)
    fun findLeafGoalsByUserId(@Param("userId") userId: String): List<GoalEntity>

    /**
     * ユーザーのルートゴールを取得（親を持たないゴール）
     */
    fun findByUserIdAndParentGoalIdIsNull(userId: String): List<GoalEntity>

    /**
     * 進捗率が一定以下のアクティブなゴールを取得
     */
    @Query("""
        SELECT g FROM GoalEntity g 
        WHERE g.userId = :userId 
          AND g.status = 'ACTIVE' 
          AND g.progress <= :maxProgress
    """)
    fun findLowProgressGoals(
        @Param("userId") userId: String,
        @Param("maxProgress") maxProgress: Int
    ): List<GoalEntity>
}