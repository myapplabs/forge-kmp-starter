package com.forge.starter.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory for creating the platform-specific Ktor HttpClient.
 * Platform-specific engines (OkHttp for Android, Darwin for iOS)
 * are provided via expect/actual in androidMain and iosMain.
 */
expect fun createHttpClient(): HttpClient

/**
 * Shared Ktor client configuration.
 * Wrap the platform engine with common plugins.
 */
fun createConfiguredHttpClient(): HttpClient = createHttpClient().config {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}
