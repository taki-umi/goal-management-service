package com.example.goal_achieve.application.service

import com.example.goal_achieve.domain.model.Goal
import com.example.goal_achieve.domain.repository.GoalRepository
import com.example.goal_achieve.domain.value.GoalStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * ゴール関連のユースケースを実装するアプリケーションサービス
 */
@Service
class GoalApplicationService(
    private val goalRepository: GoalRepository
) {
    /**
     * ゴールを作成する
     *
     * @param userId ユーザーID
     * @param title タイトル
     * @param description 説明
     * @param category カテゴリー
     * @param targetDate 目標日
     * @param parentGoalId 親ゴールID
     * @return 作成されたゴール
     */
    @Transactional
    suspend fun createGoal(
        userId: String,
        title: String,
        description: String?,
        category: String?,
        targetDate: LocalDate?,
        parentGoalId: String?
    ): Goal {
        // 親ゴールが指定されている場合は存在確認
        parentGoalId?.let {
            goalRepository.findById(it) ?: throw IllegalArgumentException("Parent goal not found: $it")
        }

        val goal = Goal.create(
            userId = userId,
            title = title,
            description = description,
            category = category,
            targetDate = targetDate,
            parentGoalId = parentGoalId
        )

        return goalRepository.save(goal)
    }

    /**
     * ゴールを取得する
     *
     * @param id ゴールID
     * @return ゴールまたはnull
     */
    suspend fun getGoal(id: String): Goal? {
        return goalRepository.findById(id)
    }

    /**
     * ユーザーのゴール一覧を取得する
     *
     * @param userId ユーザーID
     * @return ゴール一覧
     */
    suspend fun getGoalsByUserId(userId: String): List<Goal> {
        return goalRepository.findByUserId(userId)
    }

    /**
     * ユーザーのゴール一覧をステータスでフィルタリングして取得する
     *
     * @param userId ユーザーID
     * @param status ステータス文字列
     * @return フィルタリングされたゴール一覧
     */
    suspend fun getGoalsByUserIdAndStatus(userId: String, status: String): List<Goal> {
        val goalStatus = try {
            GoalStatus.fromString(status)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid goal status: $status")
        }

        return goalRepository.findByUserIdAndStatus(userId, goalStatus)
    }

    /**
     * サブゴール一覧を取得する
     *
     * @param parentGoalId 親ゴールID
     * @return サブゴール一覧
     */
    suspend fun getGoalsByParentGoalId(parentGoalId: String): List<Goal> {
        return goalRepository.findByParentGoalId(parentGoalId)
    }

    /**
     * おすすめゴールを取得する
     *
     * @param userId ユーザーID
     * @param limit 取得上限数
     * @return おすすめゴール一覧
     */
    suspend fun getRecommendedGoals(userId: String, limit: Int): List<Goal> {
        return goalRepository.findRecommendedGoals(userId, limit)
    }

    /**
     * ゴールを更新する
     *
     * @param id ゴールID
     * @param title 新しいタイトル（変更なしの場合null）
     * @param description 新しい説明（変更なしの場合null）
     * @param category 新しいカテゴリー（変更なしの場合null）
     * @param targetDate 新しい目標日（変更なしの場合null）
     * @param status 新しいステータス（変更なしの場合null）
     * @return 更新されたゴールまたはnull
     */
    @Transactional
    suspend fun updateGoal(
        id: String,
        title: String?,
        description: String?,
        category: String?,
        targetDate: LocalDate?,
        status: String?
    ): Goal? {
        val existingGoal = goalRepository.findById(id) ?: return null

        // ステータス変換
        val goalStatus = status?.let {
            try {
                GoalStatus.fromString(it)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid goal status: $it")
            }
        }

        // ゴール更新
        val updatedGoal = existingGoal.update(
            title = title,
            description = description,
            category = category,
            targetDate = targetDate,
            status = goalStatus
        )

        return goalRepository.save(updatedGoal)
    }

    /**
     * 進捗状況を更新する
     *
     * @param id ゴールID
     * @param progress 進捗度（0-100）
     * @return 更新されたゴールまたはnull
     */
    @Transactional
    suspend fun updateProgress(id: String, progress: Int): Goal? {
        val existingGoal = goalRepository.findById(id) ?: return null

        try {
            val updatedGoal = existingGoal.updateProgress(progress)
            return goalRepository.save(updatedGoal)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid progress value: $progress. ${e.message}")
        }
    }

    /**
     * ゴールをアーカイブする
     *
     * @param id ゴールID
     * @return アーカイブされたゴールまたはnull
     */
    @Transactional
    suspend fun archiveGoal(id: String): Goal? {
        val existingGoal = goalRepository.findById(id) ?: return null
        val archivedGoal = existingGoal.archive()
        return goalRepository.save(archivedGoal)
    }

    /**
     * ゴールを削除する（論理削除）
     *
     * @param id ゴールID
     * @return 削除済みマークがつけられたゴールまたはnull
     */
    @Transactional
    suspend fun markGoalAsDeleted(id: String): Goal? {
        val existingGoal = goalRepository.findById(id) ?: return null
        val deletedGoal = existingGoal.delete()
        return goalRepository.save(deletedGoal)
    }

    /**
     * ゴールを物理削除する
     *
     * @param id ゴールID
     */
    @Transactional
    suspend fun deleteGoal(id: String) {
        goalRepository.delete(id)
    }

    /**
     * 期限が近いゴールを取得する
     *
     * @param userId ユーザーID
     * @param daysAhead 何日先までを「近い」と見なすか
     * @return 期限が近いゴール一覧
     */
    suspend fun getUpcomingGoals(userId: String, daysAhead: Int): List<Goal> {
        val today = LocalDate.now()
        val futureDate = today.plusDays(daysAhead.toLong())

        return goalRepository.findByUserIdAndTargetDateBetween(
            userId = userId,
            startDate = today,
            endDate = futureDate
        )
    }
}