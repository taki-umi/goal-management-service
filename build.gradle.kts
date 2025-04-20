import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

val coroutinesVersion = "1.7.3"

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation ("org.flywaydb:flyway-database-postgresql:latest.release")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")

	// Database
	runtimeOnly("org.postgresql:postgresql")

	// Swagger/OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
	testRuntimeOnly("com.h2database:h2")  // テスト用のH2データベース
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("spring.datasource.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
	systemProperty("spring.datasource.username", "sa")
	systemProperty("spring.datasource.password", "")
	systemProperty("spring.datasource.driver-class-name", "org.h2.Driver")
	systemProperty("spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect")
	systemProperty("spring.flyway.enabled", "false")  // Flywayを無効化
}

// Dokkaによるドキュメント生成（オプション）
buildscript {
	dependencies {
		classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.8.10")
	}
}

apply(plugin = "org.jetbrains.dokka")

tasks.register("generateDocs") {
	dependsOn("dokkaHtml")

	doLast {
		println("ドキュメント生成完了")
		println("Dokkaドキュメント: $buildDir/dokka/html")
	}
}

tasks.withType<ProcessResources> {
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}