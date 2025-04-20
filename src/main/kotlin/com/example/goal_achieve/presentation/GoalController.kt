package com.example.goal_achieve.presentation.controller

import com.example.goal_achieve.application.service.GoalApplicationService
import com.example.goal_achieve.domain.model.Goal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Goal", description = "ゴール管理API")
class GoalController(private val goalService: GoalApplicationService) {

    @PostMapping
    @Operation(
        summary = "ゴール作成",
        description = "新しいゴールを作成する",
        responses = [
            ApiResponse(responseCode = "201", description = "ゴール作成成功",
                content = [Content(schema = Schema(implementation = Goal::class))]),
            ApiResponse(responseCode = "400", description = "不正なリクエスト"),
            ApiResponse(responseCode = "500", description = "サーバーエラー")
        ]
    )
    suspend fun createGoal(@RequestBody goal: Goal.CreateParams): ResponseEntity<Goal> {
        val createdGoal = goalService.createGoal(
            userId = goal.userId,
            title = goal.title,
            description = goal.description,
            category = goal.category,
            targetDate = goal.targetDate,
            parentGoalId = goal.parentGoalId
        )
        return ResponseEntity(createdGoal, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "ゴール取得",
        description = "指定したIDのゴールを取得する",
        responses = [
            ApiResponse(responseCode = "200", description = "ゴール取得成功",
                content = [Content(schema = Schema(implementation = Goal::class))]),
            ApiResponse(responseCode = "404", description = "ゴールが見つからない")
        ]
    )
    suspend fun getGoal(
        @Parameter(description = "ゴールID", required = true)
        @PathVariable id: String
    ): ResponseEntity<Goal> {
        val goal = goalService.getGoal(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(goal, HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "ゴール一覧取得",
        description = "ユーザーIDに紐づくゴール一覧を取得する。ステータスでフィルタリング可能。",
        responses = [
            ApiResponse(responseCode = "200", description = "ゴール一覧取得成功")
        ]
    )
    suspend fun getGoalsByUserId(
        @Parameter(description = "ユーザーID", required = true)
        @RequestParam userId: String,

        @Parameter(description = "ゴールステータス (ACTIVE, COMPLETED, ARCHIVED, DELETED)")
        @RequestParam(required = false) status: String?
    ): ResponseEntity<List<Goal>> {
        val goals = if (status != null) {
            goalService.getGoalsByUserIdAndStatus(userId, status)
        } else {
            goalService.getGoalsByUserId(userId)
        }
        return ResponseEntity(goals, HttpStatus.OK)
    }

    @GetMapping("/parent/{parentGoalId}")
    @Operation(
        summary = "サブゴール一覧取得",
        description = "親ゴールIDに紐づくサブゴール一覧を取得する",
        responses = [
            ApiResponse(responseCode = "200", description = "サブゴール一覧取得成功")
        ]
    )
    suspend fun getGoalsByParentGoalId(
        @Parameter(description = "親ゴールID", required = true)
        @PathVariable parentGoalId: String
    ): ResponseEntity<List<Goal>> {
        val goals = goalService.getGoalsByParentGoalId(parentGoalId)
        return ResponseEntity(goals, HttpStatus.OK)
    }

    @GetMapping("/recommended")
    @Operation(
        summary = "おすすめゴール取得",
        description = "おすすめゴールを取得する",
        responses = [
            ApiResponse(responseCode = "200", description = "おすすめゴール取得成功")
        ]
    )
    suspend fun getRecommendedGoals(
        @Parameter(description = "ユーザーID", required = true)
        @RequestParam userId: String,

        @Parameter(description = "取得上限数", example = "5")
        @RequestParam(defaultValue = "5") limit: Int
    ): ResponseEntity<List<Goal>> {
        val goals = goalService.getRecommendedGoals(userId, limit)
        return ResponseEntity(goals, HttpStatus.OK)
    }

    @GetMapping("/upcoming")
    @Operation(
        summary = "期限が近いゴール取得",
        description = "指定した日数以内に期限を迎えるゴールを取得する",
        responses = [
            ApiResponse(responseCode = "200", description = "期限が近いゴール取得成功")
        ]
    )
    suspend fun getUpcomingGoals(
        @Parameter(description = "ユーザーID", required = true)
        @RequestParam userId: String,

        @Parameter(description = "何日先までを「近い」と見なすか", example = "7")
        @RequestParam(defaultValue = "7") daysAhead: Int
    ): ResponseEntity<List<Goal>> {
        val goals = goalService.getUpcomingGoals(userId, daysAhead)
        return ResponseEntity(goals, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "ゴール更新",
        description = "ゴールを更新する",
        responses = [
            ApiResponse(responseCode = "200", description = "ゴール更新成功"),
            ApiResponse(responseCode = "404", description = "ゴールが見つからない")
        ]
    )
    suspend fun updateGoal(
        @Parameter(description = "ゴールID", required = true)
        @PathVariable id: String,

        @RequestBody params: Goal.UpdateParams
    ): ResponseEntity<Goal> {
        val updatedGoal = goalService.updateGoal(
            id = id,
            title = params.title,
            description = params.description,
            category = params.category,
            targetDate = params.targetDate,
            status = params.status
        ) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(updatedGoal, HttpStatus.OK)
    }

    @PatchMapping("/{id}/progress")
    @Operation(
        summary = "進捗更新",
        description = "ゴールの進捗状況を更新する",
        responses = [
            ApiResponse(responseCode = "200", description = "進捗更新成功"),
            ApiResponse(responseCode = "404", description = "ゴールが見つからない")
        ]
    )
    suspend fun updateProgress(
        @Parameter(description = "ゴールID", required = true)
        @PathVariable id: String,

        @Parameter(description = "進捗度 (0-100)", required = true, example = "50")
        @RequestParam progress: Int
    ): ResponseEntity<Goal> {
        val updatedGoal = goalService.updateProgress(id, progress)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(updatedGoal, HttpStatus.OK)
    }

    @PatchMapping("/{id}/archive")
    @Operation(
        summary = "ゴールアーカイブ",
        description = "ゴールをアーカイブする",
        responses = [
            ApiResponse(responseCode = "200", description = "アーカイブ成功"),
            ApiResponse(responseCode = "404", description = "ゴールが見つからない")
        ]
    )
    suspend fun archiveGoal(
        @Parameter(description = "ゴールID", required = true)
        @PathVariable id: String
    ): ResponseEntity<Goal> {
        val archivedGoal = goalService.archiveGoal(id)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(archivedGoal, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "ゴール削除",
        description = "ゴールを論理削除する",
        responses = [
            ApiResponse(responseCode = "204", description = "削除成功"),
            ApiResponse(responseCode = "404", description = "ゴールが見つからない")
        ]
    )
    suspend fun deleteGoal(
        @Parameter(description = "ゴールID", required = true)
        @PathVariable id: String
    ): ResponseEntity<Unit> {
        val deletedGoal = goalService.markGoalAsDeleted(id)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}