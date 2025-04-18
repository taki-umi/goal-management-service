package com.example.goal_achieve.domain.value

/**
 * ゴールの状態を表す値オブジェクト
 */
enum class GoalStatus {
    ACTIVE,     // 進行中
    COMPLETED,  // 完了
    ARCHIVED,   // アーカイブ済み
    DELETED;    // 削除済み

    companion object {
        /**
         * 文字列からGoalStatusを取得する
         *
         * @param status ステータスを表す文字列
         * @return GoalStatus
         * @throws IllegalArgumentException 無効なステータス文字列の場合
         */
        fun fromString(status: String): GoalStatus {
            return try {
                valueOf(status.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid goal status: $status")
            }
        }

        /**
         * 有効なステータスかどうかを判定する
         *
         * @param status ステータスを表す文字列
         * @return 有効なステータスの場合true
         */
        fun isValid(status: String): Boolean {
            return try {
                valueOf(status.uppercase())
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }
    }
}