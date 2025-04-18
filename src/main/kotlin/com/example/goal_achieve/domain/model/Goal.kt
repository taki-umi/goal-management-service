// Goal.kt
package com.example.goal_achieve.domain.model

import com.example.goal_achieve.domain.value.GoalStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * ゴールを表すドメインモデル
 *
 * ユーザーが達成したい目標(ゴール)を表現する
 */
class Goal private constructor(
    val id: GoalId,
    val userId: UserId,
    private var title: String,
    private var description: String?,
    private var category: GoalCategory?,
    private var targetDate: LocalDate?,
    private var status: GoalStatus,
    private var progress: GoalProgress,
    val parentGoalId: GoalId?,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        /**
         * ゴールを新規作成する
         *
         * @param userId ユーザーID
         * @param title タイトル
         * @param description 説明
         * @param category カテゴリー
         * @param targetDate 目標日
         * @param parentGoalId 親ゴールID
         * @return 新しいゴールインスタンス
         */
        fun create(
            userId: String,
            title: String,
            description: String?,
            category: String?,
            targetDate: LocalDate?,
            parentGoalId: String?
        ): Goal {
            require(title.isNotBlank()) { "Title cannot be blank" }

            val now = LocalDateTime.now()

            return Goal(
                id = GoalId(UUID.randomUUID().toString()),
                userId = UserId(userId),
                title = title,
                description = description,
                category = category?.let { GoalCategory(it) },
                targetDate = targetDate,
                status = GoalStatus.ACTIVE,
                progress = GoalProgress(0),
                parentGoalId = parentGoalId?.let { GoalId(it) },
                createdAt = now,
                updatedAt = now
            )
        }

        /**
         * 既存データからゴールを再構築する（リポジトリ用）
         */
        fun reconstitute(
            id: String,
            userId: String,
            title: String,
            description: String?,
            category: String?,
            targetDate: LocalDate?,
            status: GoalStatus,
            progress: Int,
            parentGoalId: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Goal {
            return Goal(
                id = GoalId(id),
                userId = UserId(userId),
                title = title,
                description = description,
                category = category?.let { GoalCategory(it) },
                targetDate = targetDate,
                status = status,
                progress = GoalProgress(progress),
                parentGoalId = parentGoalId?.let { GoalId(it) },
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    /**
     * 進捗を更新する
     *
     * @param newProgress 新しい進捗状況（0-100）
     * @return 更新されたゴール
     */
    fun updateProgress(newProgress: Int): Goal {
        this.progress = GoalProgress(newProgress)
        this.status = if (newProgress == 100) GoalStatus.COMPLETED else this.status
        this.updatedAt = LocalDateTime.now()
        return this
    }

    /**
     * ゴールを更新する
     *
     * @param title 新しいタイトル（nullの場合は変更なし）
     * @param description 新しい説明（nullの場合は変更なし）
     * @param category 新しいカテゴリー（nullの場合は変更なし）
     * @param targetDate 新しい目標日（nullの場合は変更なし）
     * @param status 新しい状態（nullの場合は変更なし）
     * @return 更新されたゴール
     */
    fun update(
        title: String? = null,
        description: String? = null,
        category: String? = null,
        targetDate: LocalDate? = null,
        status: GoalStatus? = null
    ): Goal {
        title?.let {
            require(it.isNotBlank()) { "Title cannot be blank" }
            this.title = it
        }
        this.description = description ?: this.description
        this.category = category?.let { GoalCategory(it) } ?: this.category
        this.targetDate = targetDate ?: this.targetDate
        this.status = status ?: this.status
        this.updatedAt = LocalDateTime.now()
        return this
    }

    /**
     * ゴールをアーカイブする
     *
     * @return アーカイブされたゴール
     */
    fun archive(): Goal {
        this.status = GoalStatus.ARCHIVED
        this.updatedAt = LocalDateTime.now()
        return this
    }

    /**
     * ゴールを削除する
     *
     * @return 削除済みマークのついたゴール
     */
    fun delete(): Goal {
        this.status = GoalStatus.DELETED
        this.updatedAt = LocalDateTime.now()
        return this
    }

    // Getter methods
    fun getTitle(): String = title
    fun getDescription(): String? = description
    fun getCategory(): GoalCategory? = category
    fun getCategoryValue(): String? = category?.value
    fun getTargetDate(): LocalDate? = targetDate
    fun getStatus(): GoalStatus = status
    fun getProgress(): GoalProgress = progress
    fun getProgressValue(): Int = progress.value
    fun getUpdatedAt(): LocalDateTime = updatedAt

    // Value Objects for Goal aggregate
    data class GoalId(val value: String) {
        init {
            require(value.isNotBlank()) { "Goal ID cannot be blank" }
        }
    }

    data class UserId(val value: String) {
        init {
            require(value.isNotBlank()) { "User ID cannot be blank" }
        }
    }

    data class GoalCategory(val value: String) {
        init {
            require(value.isNotBlank()) { "Category cannot be blank" }
        }
    }

    data class GoalProgress(val value: Int) {
        init {
            require(value in 0..100) { "Progress must be between 0 and 100, but was $value" }
        }
    }
}