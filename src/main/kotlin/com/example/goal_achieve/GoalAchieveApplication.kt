package com.example.goal_achieve

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
	info = Info(
		title = "ゴール管理API",
		version = "1.0.0",
		description = "ユーザーの目標設定・達成をサポートするシステムのREST API"
	)
)
class GoalAchieveApplication

fun main(args: Array<String>) {
	runApplication<GoalAchieveApplication>(*args)
	println("ゴール管理APIが起動しました。Swagger UIは http://localhost:9090/swagger-ui.html で利用可能です。")
}