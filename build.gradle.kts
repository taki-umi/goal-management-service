import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	// id("com.github.pseudomuto.protoc-gen-doc") version "1.6.0"

	// gRPC用
	id("com.google.protobuf") version "0.9.4"
}

repositories {
	gradlePluginPortal()
	google()
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
val grpcVersion = "1.57.2"
val grpcKotlinVersion = "1.3.0"
val protobufVersion = "3.24.0"

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.flywaydb:flyway-core")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")

	// Database
	runtimeOnly("org.postgresql:postgresql")

	// gRPC
	implementation("io.grpc:grpc-protobuf:$grpcVersion")
	implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
	implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
	implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
	implementation("net.devh:grpc-server-spring-boot-starter:2.14.0.RELEASE")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
	testImplementation("io.grpc:grpc-testing:$grpcVersion")
	testRuntimeOnly("com.h2database:h2")  // テスト用のH2データベース
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:$protobufVersion"
	}
}

sourceSets {
	main {
		proto {
			srcDir("src/main/proto")
		}
	}
}

// gRPC APIドキュメント生成タスク
tasks.register<com.google.protobuf.gradle.GenerateProtoTask>("generateGrpcDocs") {
	dependsOn("generateProto")

	val docOutDir = "$buildDir/generated/docs"

	outputs.dir(docOutDir)

	doLast {
		exec {
			commandLine(
				"protoc",
				"--proto_path=src/main/proto",
				"--doc_out=$docOutDir",
				"--doc_opt=markdown,api.md",
				"src/main/proto/goal.proto"
			)
		}
	}
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

// Dokkaによるドキュメント生成
buildscript {
	dependencies {
		classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.8.10")
	}
}

apply(plugin = "org.jetbrains.dokka")

// ドキュメント生成タスク
tasks.register("generateAllDocs") {
	dependsOn("dokkaHtml", "generateGrpcDocs")

	doLast {
		println("ドキュメント生成完了")
		println("Dokkaドキュメント: $buildDir/dokka/html")
		println("gRPC APIドキュメント: $buildDir/generated/docs")
	}
}

tasks.withType<ProcessResources> {
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}