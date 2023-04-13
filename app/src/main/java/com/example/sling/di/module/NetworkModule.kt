package com.example.sling.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesKtorClient(): HttpClient = HttpClient(CIO) {
        defaultRequest {
            url("https://5c41a23eceb1f00014456525.mockapi.io/petit-pdf/bookmarks")
            header("Content-Type", "application/json")
        }
        install(Logging) {
            level = LogLevel.BODY
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
}