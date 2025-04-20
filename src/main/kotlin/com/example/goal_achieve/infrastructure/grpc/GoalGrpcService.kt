//package com.example.goal_achieve.infrastructure.grpc
//
//import com.example.goal_achieve.application.service.GoalApplicationService
//import com.example.goal_achieve.infrastructure.grpc.mapper.GoalMapper
//import com.example.goal_achieve.infrastructure.grpc.proto.CreateGoalRequest
//import com.example.goal_achieve.infrastructure.grpc.proto.GoalIdRequest
//import com.example.goal_achieve.infrastructure.grpc.proto.GoalProgressResponse
//import com.example.goal_achieve.infrastructure.grpc.proto.GoalProgressUpdate
//import com.example.goal_achieve.infrastructure.grpc.proto.GoalResponse
//import com.example.goal_achieve.infrastructure.grpc.proto.GoalServiceGrpc
//import com.example.goal_achieve.infrastructure.grpc.proto.GoalsResponse
//import com.example.goal_achieve.infrastructure.grpc.proto.ParentGoalIdRequest
//import com.example.goal_achieve.infrastructure.grpc.proto.RecommendedGoalsRequest
//import com.example.goal_achieve.infrastructure.grpc.proto.UpdateGoalRequest
//import com.example.goal_achieve.infrastructure.grpc.proto.UpdateProgressRequest
//import com.example.goal_achieve.infrastructure.grpc.proto.UserIdRequest
//import com.example.goal_achieve.infrastructure.grpc.proto.UserIdStatusRequest
//import com.google.protobuf.Empty
//import io.grpc.Status
//import io.grpc.stub.StreamObserver
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.filter
//import kotlinx.coroutines.launch
//import net.devh.boot.grpc.server.service.GrpcService
//import org.slf4j.LoggerFactory
//
///**
// * gRPC Goal サービスの実装
// */
//@GrpcService
//class GoalGrpcService(
//    private val goalService: GoalApplicationService
//) : GoalServiceGrpc.GoalServiceImplBase() {
//
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    // ストリーミング用の共有フロー
//    private val progressUpdates = MutableSharedFlow<GoalProgressResponse>()
//
//    // コルーチンスコープ
//    private val scope = CoroutineScope(Dispatchers.Default)
//
//    /**
//     * ゴール作成
//     */
//    override fun createGoal(request: CreateGoalRequest, responseObserver: StreamObserver<GoalResponse>) {
//        scope.launch {
//            try {
//                val goal = goalService.createGoal(
//                    userId = request.userId,
//                    title = request.title,
//                    description = if (request.hasDescription()) request.description.value else null,
//                    category = if (request.hasCategory()) request.category.value else null,
//                    targetDate = if (request.hasTargetDate())
//                        GoalMapper.toLocalDate(request.targetDate) else null,
//                    parentGoalId = if (request.hasParentGoalId()) request.parentGoalId.value else null
//                )
//
//                val response = GoalMapper.toGrpcResponse(goal)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error creating goal", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error creating goal: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * ゴール取得
//     */
//    override fun getGoal(request: GoalIdRequest, responseObserver: StreamObserver<GoalResponse>) {
//        scope.launch {
//            try {
//                val goal = goalService.getGoal(request.id)
//
//                if (goal == null) {
//                    responseObserver.onError(
//                        Status.NOT_FOUND
//                            .withDescription("Goal not found with id: ${request.id}")
//                            .asRuntimeException()
//                    )
//                    return@launch
//                }
//
//                val response = GoalMapper.toGrpcResponse(goal)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error getting goal", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error getting goal: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * ユーザーのゴール一覧取得
//     */
//    override fun getGoalsByUserId(request: UserIdRequest, responseObserver: StreamObserver<GoalsResponse>) {
//        scope.launch {
//            try {
//                val goals = goalService.getGoalsByUserId(request.userId)
//                val response = GoalMapper.toGoalsResponse(goals)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error getting goals by user id", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error getting goals: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * ユーザーのゴール一覧をステータスでフィルタリング
//     */
//    override fun getGoalsByUserIdAndStatus(
//        request: UserIdStatusRequest,
//        responseObserver: StreamObserver<GoalsResponse>
//    ) {
//        scope.launch {
//            try {
//                val goals = goalService.getGoalsByUserIdAndStatus(
//                    userId = request.userId,
//                    status = request.status
//                )
//                val response = GoalMapper.toGoalsResponse(goals)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error getting goals by user id and status", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error getting goals: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * サブゴール一覧取得
//     */
//    override fun getGoalsByParentGoalId(
//        request: ParentGoalIdRequest,
//        responseObserver: StreamObserver<GoalsResponse>
//    ) {
//        scope.launch {
//            try {
//                val goals = goalService.getGoalsByParentGoalId(request.parentGoalId)
//                val response = GoalMapper.toGoalsResponse(goals)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error getting goals by parent goal id", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error getting goals: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * おすすめゴール取得
//     */
//    override fun getRecommendedGoals(
//        request: RecommendedGoalsRequest,
//        responseObserver: StreamObserver<GoalsResponse>
//    ) {
//        scope.launch {
//            try {
//                val goals = goalService.getRecommendedGoals(
//                    userId = request.userId,
//                    limit = request.limit
//                )
//                val response = GoalMapper.toGoalsResponse(goals)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error getting recommended goals", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error getting recommended goals: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * ゴール更新
//     */
//    override fun updateGoal(request: UpdateGoalRequest, responseObserver: StreamObserver<GoalResponse>) {
//        scope.launch {
//            try {
//                val updatedGoal = goalService.updateGoal(
//                    id = request.id,
//                    title = if (request.hasTitle()) request.title.value else null,
//                    description = if (request.hasDescription()) request.description.value else null,
//                    category = if (request.hasCategory()) request.category.value else null,
//                    targetDate = if (request.hasTargetDate())
//                        GoalMapper.toLocalDate(request.targetDate) else null,
//                    status = if (request.hasStatus()) request.status.value else null
//                )
//
//                if (updatedGoal == null) {
//                    responseObserver.onError(
//                        Status.NOT_FOUND
//                            .withDescription("Goal not found with id: ${request.id}")
//                            .asRuntimeException()
//                    )
//                    return@launch
//                }
//
//                val response = GoalMapper.toGrpcResponse(updatedGoal)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error updating goal", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error updating goal: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * 進捗更新
//     */
//    override fun updateProgress(
//        request: UpdateProgressRequest,
//        responseObserver: StreamObserver<GoalResponse>
//    ) {
//        scope.launch {
//            try {
//                val updatedGoal = goalService.updateProgress(
//                    id = request.id,
//                    progress = request.progress
//                )
//
//                if (updatedGoal == null) {
//                    responseObserver.onError(
//                        Status.NOT_FOUND
//                            .withDescription("Goal not found with id: ${request.id}")
//                            .asRuntimeException()
//                    )
//                    return@launch
//                }
//
//                // 進捗更新をストリーミングにも通知
//                val progressResponse = GoalMapper.toProgressResponse(updatedGoal)
//                progressUpdates.emit(progressResponse)
//
//                // 通常のレスポンス
//                val response = GoalMapper.toGrpcResponse(updatedGoal)
//                responseObserver.onNext(response)
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error updating goal progress", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error updating goal progress: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * ゴール削除
//     */
//    override fun deleteGoal(request: GoalIdRequest, responseObserver: StreamObserver<Empty>) {
//        scope.launch {
//            try {
//                // 論理削除を実行
//                val deletedGoal = goalService.markGoalAsDeleted(request.id)
//
//                if (deletedGoal == null) {
//                    responseObserver.onError(
//                        Status.NOT_FOUND
//                            .withDescription("Goal not found with id: ${request.id}")
//                            .asRuntimeException()
//                    )
//                    return@launch
//                }
//
//                responseObserver.onNext(Empty.getDefaultInstance())
//                responseObserver.onCompleted()
//            } catch (e: Exception) {
//                logger.error("Error deleting goal", e)
//                responseObserver.onError(
//                    Status.INTERNAL
//                        .withDescription("Error deleting goal: ${e.message}")
//                        .asRuntimeException()
//                )
//            }
//        }
//    }
//
//    /**
//     * 進捗更新のストリーミング（双方向ストリーミング）
//     */
//    override fun streamGoalProgress(
//        responseObserver: StreamObserver<GoalProgressResponse>
//    ): StreamObserver<GoalProgressUpdate> {
//
//        // クライアントからのリクエストを処理するためのStreamObserver
//        val requestObserver = object : StreamObserver<GoalProgressUpdate> {
//
//            private val userGoals = mutableSetOf<String>() // ユーザーが監視しているゴールIDを保存
//
//            override fun onNext(request: GoalProgressUpdate) {
//                scope.launch {
//                    try {
//                        // リクエストを受け取ったらそのゴールを監視リストに追加
//                        userGoals.add(request.id)
//
//                        // 進捗の更新があればそれを処理
//                        if (request.progress > 0) {
//                            val updatedGoal = goalService.updateProgress(
//                                id = request.id,
//                                progress = request.progress
//                            )
//
//                            if (updatedGoal != null) {
//                                val progressResponse = GoalMapper.toProgressResponse(updatedGoal)
//                                progressUpdates.emit(progressResponse)
//                            }
//                        }
//                    } catch (e: Exception) {
//                        logger.error("Error processing progress update", e)
//                        // エラーは無視して接続を維持
//                    }
//                }
//            }
//
//            override fun onError(t: Throwable) {
//                logger.error("Error in progress stream from client", t)
//                // 接続が切れても他のクライアントには影響しない
//            }
//
//            override fun onCompleted() {
//                // クライアントがストリームを完了したらレスポンスも完了
//                responseObserver.onCompleted()
//            }
//        }
//
//        // 進捗更新をクライアントに送信するフロー
//        scope.launch {
//            try {
//                progressUpdates
//                    .filter { it.id in userGoals } // ユーザーが監視しているゴールのみをフィルタリング
//                    .collect {
//                        try {
//                            responseObserver.onNext(it)
//                        } catch (e: Exception) {
//                            logger.error("Error sending progress update to client", e)
//                            // エラーは無視して他のクライアントには影響しない
//                        }
//                    }
//            } catch (e: Exception) {
//                logger.error("Error in progress updates flow", e)
//                // フローのエラーはクライアントに伝えない
//            }
//        }
//
//        return requestObserver
//    }
//}