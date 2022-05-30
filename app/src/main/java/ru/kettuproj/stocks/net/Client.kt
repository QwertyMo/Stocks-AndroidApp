package ru.kettuproj.stocks.net

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object Client {
    @OptIn(ExperimentalSerializationApi::class)
    val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint       = true
                isLenient         = true
                explicitNulls     = true
            })
        }
        install(WebSockets){
            pingInterval = 500
        }
    }
}