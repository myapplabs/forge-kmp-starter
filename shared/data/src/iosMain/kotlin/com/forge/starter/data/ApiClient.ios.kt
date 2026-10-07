package com.forge.starter.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

/**
 * iOS-specific Ktor HttpClient using Darwin engine.
 */
actual fun createHttpClient(): HttpClient = HttpClient(Darwin)
