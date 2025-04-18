package com.example.goal_achieve

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GoalAchieveApplication

fun main(args: Array<String>) {
	runApplication<GoalAchieveApplication>(*args)
	println("Hello")
}
