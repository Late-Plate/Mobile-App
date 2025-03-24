package com.example.late_plate.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json
fun createHttpClient(engine: HttpClientEngine):HttpClient{
    return HttpClient(engine){
        install(Logging){
            level = LogLevel.ALL
        }
        install(ContentNegotiation){
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )

        }
    }
}