plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"

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
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// gRPC
	implementation("io.grpc:grpc-netty-shaded:1.59.0")
	implementation("io.grpc:grpc-stub:1.59.0")

	// Database
	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.grpc:grpc-testing:1.59.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// ビルド時にprotobufからJavaファイルを生成する設定
protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.24.0"
	}

	plugins {
		register("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:1.59.0"
		}
	}

	generateProtoTasks {
		all().configureEach {
			plugins {
				register("grpc")
			}
		}
	}
}
