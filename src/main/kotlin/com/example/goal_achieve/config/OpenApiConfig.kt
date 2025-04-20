package com.example.goal_achieve.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("ゴール管理API")
                    .version("1.0.0")
                    .description("""
                        ユーザーの目標設定・達成をサポートするシステムのREST API
                        
                        ## 概要
                        このAPIは、ユーザーが個人の目標（ゴール）を設定し、進捗を管理するためのものです。
                        階層構造のゴール設定、進捗トラッキング、期限管理などの機能を提供します。
                        
                        ## 主な機能
                        - ゴールの作成、更新、削除
                        - ゴールの階層構造管理（親ゴール-サブゴール）
                        - 進捗状況の更新
                        - ゴールの検索とフィルタリング
                        - おすすめゴールの提案
                    """.trimIndent())
                    .contact(
                        Contact()
                            .name("開発チーム")
                            .email("dev@example.com")
                            .url("https://example.com")
                    )
                    .license(
                        License()
                            .name("MIT")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:9090").description("開発サーバー"),
                    Server().url("https://api-dev.example.com").description("開発環境"),
                    Server().url("https://api-stg.example.com").description("ステージング環境"),
                    Server().url("https://api.example.com").description("本番環境")
                )
            )
            .tags(
                listOf(
                    Tag().name("Goal").description("ゴール管理API"),
                    Tag().name("User").description("ユーザー管理API（将来実装予定）"),
                    Tag().name("Timeline").description("タイムラインAPI（将来実装予定）")
                )
            )
            .components(Components())
    }

    @Bean
    fun goalApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Goals")
            .pathsToMatch("/api/goals/**")
            .build()
    }

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Public API")
            .pathsToMatch("/api/**")
            .build()
    }
}