package com.forge.starter.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android-specific Ktor HttpClient using OkHttp engine.
 */
actual fun createHttpClient(): HttpClient = HttpClient(OkHttp)
