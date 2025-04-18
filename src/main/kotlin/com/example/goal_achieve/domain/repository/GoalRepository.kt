package com.example.goal_achieve.domain.repository

import com.example.goal_achieve.domain.model.Goal
import com.example.goal_achieve.domain.value.GoalStatus
import java.time.LocalDate

/**
 * ゴールリポジトリのインターフェース
 *
 * ゴールの永続化と取得を担当する
 */
interface GoalRepository {
    /**
     * ゴールを保存する
     *
     * @param goal 保存するゴール
     * @return 保存されたゴール
     */
    suspend fun save(goal: Goal): Goal

    /**
     * IDでゴールを検索する
     *
     * @param id ゴールID
     * @return 見つかったゴールまたはnull
     */
    suspend fun findById(id: String): Goal?

    /**
     * ユーザーIDに紐づくゴール一覧を取得する
     *
     * @param userId ユーザーID
     * @return ゴール一覧
     */
    suspend fun findByUserId(userId: String): List<Goal>

    /**
     * ユーザーIDとステータスでゴールを検索する
     *
     * @param userId ユーザーID
     * @param status ゴールステータス
     * @return 条件に合うゴール一覧
     */
    suspend fun findByUserIdAndStatus(userId: String, status: GoalStatus): List<Goal>

    /**
     * 親ゴールIDに紐づくサブゴール一覧を取得する
     *
     * @param parentGoalId 親ゴールID
     * @return サブゴール一覧
     */
    suspend fun findByParentGoalId(parentGoalId: String): List<Goal>

    /**
     * おすすめゴールを取得する
     *
     * @param userId 除外するユーザーID
     * @param limit 取得上限数
     * @return おすすめゴール一覧
     */
    suspend fun findRecommendedGoals(userId: String, limit: Int): List<Goal>

    /**
     * ゴールを削除する（物理削除）
     *
     * @param id 削除するゴールのID
     */
    suspend fun delete(id: String)

    /**
     * 特定期間内に期限を迎えるゴールを検索する
     *
     * @param userId ユーザーID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 期間内のゴール一覧
     */
    suspend fun findByUserIdAndTargetDateBetween(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Goal>
}