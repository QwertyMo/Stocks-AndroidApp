package ru.kettuproj.stocks.repository

import android.util.Log
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import ru.kettuproj.stocks.common.Constant.TOKEN
import ru.kettuproj.stocks.common.Constant.WEBSOCKET_HOST
import ru.kettuproj.stocks.model.WebSocketRequest
import ru.kettuproj.stocks.model.WebSocketResponse
import ru.kettuproj.stocks.net.Client
import java.lang.Exception


class FinnhubWebSocket {

    private val commands = mutableListOf<String>();

    fun subscribe(symbol: String) {
        commands.add(
            Json.encodeToJsonElement(
                WebSocketRequest(
                    "subscribe",
                    symbol
                )
            ).toString()
        )

    }

    fun unsubscribe(symbol: String){
        commands.add(
            WebSocketRequest(
                "unsubscribe",
                symbol
            ).toString()
        )
    }

    suspend fun listen(callback:(data: WebSocketResponse, isClosed: Boolean)->Unit){
        try {
            Log.i("FINNHUNB", "Start listening $WEBSOCKET_HOST")
            Client.client.webSocket(WEBSOCKET_HOST,
                {
                    parameter("token", TOKEN)
                }) {
                while (true) {
                    for (command in commands) {
                        send(command).run {
                            Log.i("FINNHUNB", "send command: $command")
                        }
                    }
                    commands.clear()
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> {
                            callback(
                                Json.decodeFromString<WebSocketResponse>(frame.readText()),
                                false
                            )
                        }
                        is Frame.Close -> {
                            callback(WebSocketResponse("close"), true)
                            close()
                            return@webSocket
                        }
                        else -> {}
                    }
                }
            }
        }catch (e: Exception){
            callback(WebSocketResponse("close"), true)
        }
    }
}