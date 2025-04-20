//package com.example.goal_achieve.infrastructure.grpc.mapper
//
//import com.example.goal_achieve.domain.model.Goal
//import com.example.goal_achieve.infrastructure.grpc.proto.*
//import com.google.protobuf.StringValue
//import com.google.protobuf.Timestamp
//import java.time.Instant
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.ZoneId
//
///**
// * ドメインモデルとgRPCメッセージの変換を担当するマッパー
// */
//object GoalMapper {
//
//    /**
//     * ドメインモデルからgRPCレスポンスへの変換
//     *
//     * @param goal ドメインモデル
//     * @return gRPCレスポンス
//     */
//    fun toGrpcResponse(goal: Goal): GoalResponse {
//        val builder = GoalResponse.newBuilder()
//            .setId(goal.id.value)
//            .setUserId(goal.userId.value)
//            .setTitle(goal.getTitle())
//            .setStatus(goal.getStatus().name)
//            .setProgress(goal.getProgressValue())
//
//        // Optional fields
//        goal.getDescription()?.let {
//            builder.setDescription(StringValue.of(it))
//        }
//
//        goal.getCategoryValue()?.let {
//            builder.setCategory(StringValue.of(it))
//        }
//
//        goal.getTargetDate()?.let {
//            builder.setTargetDate(toTimestamp(it))
//        }
//
//        goal.parentGoalId?.let {
//            builder.setParentGoalId(StringValue.of(it.value))
//        }
//
//        // DateTime fields
//        builder.setCreatedAt(toTimestamp(goal.createdAt))
//        builder.setUpdatedAt(toTimestamp(goal.getUpdatedAt()))
//
//        return builder.build()
//    }
//
//    /**
//     * LocalDateTimeからTimestampへの変換
//     */
//    private fun toTimestamp(dateTime: LocalDateTime): Timestamp {
//        val instant = dateTime.atZone(ZoneId.systemDefault()).toInstant()
//        return Timestamp.newBuilder()
//            .setSeconds(instant.epochSecond)
//            .setNanos(instant.nano)
//            .build()
//    }
//
//    /**
//     * LocalDateからTimestampへの変換
//     */
//    private fun toTimestamp(date: LocalDate): Timestamp {
//        val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
//        return Timestamp.newBuilder()
//            .setSeconds(instant.epochSecond)
//            .setNanos(instant.nano)
//            .build()
//    }
//
//    /**
//     * TimestampからLocalDateへの変換
//     */
//    fun toLocalDate(timestamp: Timestamp): LocalDate {
//        return Instant.ofEpochSecond(timestamp.seconds, timestamp.nanos.toLong())
//            .atZone(ZoneId.systemDefault())
//            .toLocalDate()
//    }
//
//    /**
//     * 複数のドメインモデルからGoalsResponseへの変換
//     */
//    fun toGoalsResponse(goals: List<Goal>): GoalsResponse {
//        return GoalsResponse.newBuilder()
//            .addAllGoals(goals.map { toGrpcResponse(it) })
//            .build()
//    }
//
//    /**
//     * ゴール進捗更新からGoalProgressResponseへの変換
//     */
//    fun toProgressResponse(goal: Goal): GoalProgressResponse {
//        return GoalProgressResponse.newBuilder()
//            .setId(goal.id.value)
//            .setProgress(goal.getProgressValue())
//            .setStatus(goal.getStatus().name)
//            .setUpdatedAt(toTimestamp(goal.getUpdatedAt()))
//            .build()
//    }
//}