package com.example.goal_achieve.domain.model

import com.example.goal_achieve.domain.value.GoalStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * ゴールを表すドメインモデル
 *
 * ユーザーが達成したい目標(ゴール)を表現する
 */
@Schema(description = "ゴールオブジェクト")
class Goal private constructor(
    @Schema(description = "ゴールID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    val id: GoalId,

    @Schema(description = "ユーザーID", example = "user123")
    val userId: UserId,

    @Schema(description = "ゴールタイトル", example = "プログラミングスキルの向上")
    private var title: String,

    @Schema(description = "ゴールの説明", example = "Kotlinの基礎から応用まで学習する")
    private var description: String?,

    @Schema(description = "ゴールのカテゴリー", example = "スキル開発")
    private var category: GoalCategory?,

    @Schema(description = "目標達成日", example = "2025-12-31")
    private var targetDate: LocalDate?,

    @Schema(description = "ゴールのステータス", example = "ACTIVE",
        allowableValues = ["ACTIVE", "COMPLETED", "ARCHIVED", "DELETED"])
    private var status: GoalStatus,

    @Schema(description = "進捗度 (0-100)", example = "25")
    private var progress: GoalProgress,

    @Schema(description = "親ゴールID", example = "f7e6d5c4-b3a2-1098-fedc-ba9876543210")
    val parentGoalId: GoalId?,

    @Schema(description = "作成日時", example = "2025-04-18T14:30:00")
    val createdAt: LocalDateTime,

    @Schema(description = "更新日時", example = "2025-04-19T10:15:30")
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
    @Schema(description = "ゴールID値オブジェクト")
    data class GoalId(
        @Schema(description = "ゴールIDの値", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        val value: String
    ) {
        init {
            require(value.isNotBlank()) { "Goal ID cannot be blank" }
        }
    }

    @Schema(description = "ユーザーID値オブジェクト")
    data class UserId(
        @Schema(description = "ユーザーIDの値", example = "user123")
        val value: String
    ) {
        init {
            require(value.isNotBlank()) { "User ID cannot be blank" }
        }
    }

    @Schema(description = "ゴールカテゴリー値オブジェクト")
    data class GoalCategory(
        @Schema(description = "カテゴリー名", example = "スキル開発")
        val value: String
    ) {
        init {
            require(value.isNotBlank()) { "Category cannot be blank" }
        }
    }

    @Schema(description = "進捗状況値オブジェクト")
    data class GoalProgress(
        @Schema(description = "進捗値 (0-100)", example = "25")
        val value: Int
    ) {
        init {
            require(value in 0..100) { "Progress must be between 0 and 100, but was $value" }
        }
    }

    // リクエストパラメータ - ゴール作成用
    @Schema(description = "ゴール作成パラメータ")
    data class CreateParams(
        @Schema(description = "ユーザーID", example = "user123", required = true)
        val userId: String,

        @Schema(description = "ゴールタイトル", example = "プログラミングスキルの向上", required = true)
        val title: String,

        @Schema(description = "ゴールの説明", example = "Kotlinの基礎から応用まで学習する")
        val description: String? = null,

        @Schema(description = "ゴールのカテゴリー", example = "スキル開発")
        val category: String? = null,

        @Schema(description = "目標達成日", example = "2025-12-31")
        val targetDate: LocalDate? = null,

        @Schema(description = "親ゴールID", example = "f7e6d5c4-b3a2-1098-fedc-ba9876543210")
        val parentGoalId: String? = null
    )

    // リクエストパラメータ - ゴール更新用
    @Schema(description = "ゴール更新パラメータ")
    data class UpdateParams(
        @Schema(description = "ゴールタイトル", example = "プログラミングスキルの向上と実践")
        val title: String? = null,

        @Schema(description = "ゴールの説明", example = "Kotlinの基礎から応用まで学習し、実際のプロジェクトで活用する")
        val description: String? = null,

        @Schema(description = "ゴールのカテゴリー", example = "キャリア開発")
        val category: String? = null,

        @Schema(description = "目標達成日", example = "2026-03-31")
        val targetDate: LocalDate? = null,

        @Schema(description = "ゴールのステータス", example = "ACTIVE",
            allowableValues = ["ACTIVE", "COMPLETED", "ARCHIVED", "DELETED"])
        val status: String? = null
    )
}