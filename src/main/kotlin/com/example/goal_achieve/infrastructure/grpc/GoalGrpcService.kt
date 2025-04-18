//package com.example.goal_achieve.infrastructure.grpc
//
////import com.google.protobuf.Empty
////import com.google.protobuf.Timestamp
//import com.example.goal_achieve.domain.model.Goal
//import com.example.goal_achieve.domain.repository.GoalRepository
//import io.grpc.Status
//import io.grpc.stub.StreamObserver
//import org.springframework.beans.factory.annotation.Autowired
//import java.time.Instant
//import java.time.ZoneId
//
//@GrpcService
//class GoalGrpcService @Autowired constructor(
//    private val goalRepository: GoalRepository
//) : GoalServiceGrpc.GoalServiceImplBase() {
//
//    override fun createGoal(request: CreateGoalRequest, responseObserver: StreamObserver<GoalResponse>) {
//        try {
//            val goal = Goal(
//                userId = request.userId,
//                title = request.title,
//                description = request.description,
//                category = request.category,
//                targetDate = if (request.hasTargetDate()) {
//                    Instant.ofEpochSecond(
//                        request.targetDate.seconds,
//                        request.targetDate.nanos.toLong()
//                    )
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDate()
//                } else null,
//                parentGoalId = if (request.hasParentGoalId()) request.parentGoalId else null
//            )
//
//            val savedGoal = goalRepository.save(goal)
//            val response = savedGoal.toGoalResponse()
//
//            responseObserver.onNext(response)
//            responseObserver.onCompleted()
//        } catch (e: Exception) {
//            responseObserver.onError(
//                Status.INTERNAL
//                    .withDescription("Error creating goal: ${e.message}")
//                    .asRuntimeException()
//            )
//        }
//    }
//
//    // 他のメソッド実装...
//
//    private fun Goal.toGoalResponse(): GoalResponse {
//        val builder = GoalResponse.newBuilder()
//            .setId(this.id)
//            .setUserId(this.userId)
//            .setTitle(this.title)
//            .setDescription(this.description)
//            .setCategory(this.category)
//            .setProgress(this.progress)
//            .setStatus(this.status.toString())
//
//        this.targetDate?.let {
//            val instant = it.atStartOfDay(ZoneId.systemDefault()).toInstant()
//            builder.setTargetDate(
//                Timestamp.newBuilder()
//                    .setSeconds(instant.epochSecond)
//                    .setNanos(instant.nano)
//                    .build()
//            )
//        }
//
//        this.parentGoalId?.let {
//            builder.setParentGoalId(it)
//        }
//
//        return builder.build()
//    }
//}
