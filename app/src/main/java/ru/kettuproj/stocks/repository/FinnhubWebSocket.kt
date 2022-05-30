package ru.kettuproj.stocks.repository

import android.util.Log
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import ru.kettuproj.stocks.common.Constant.WEBSOCKET_HOST
import ru.kettuproj.stocks.model.WebSocketRequest
import ru.kettuproj.stocks.model.WebSocketResponse
import ru.kettuproj.stocks.net.Client
import java.lang.Exception

class FinnhubWebSocket {

    private val commands = mutableListOf<String>()

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

    suspend fun listen(token: String, callback:(data: WebSocketResponse, isClosed: Boolean)->Unit){

        try {
            Log.i("FINNHUNB", "Start listening $WEBSOCKET_HOST")
            Client.client.webSocket(urlString = WEBSOCKET_HOST,
                {
                    parameter("token", token)
                    method = HttpMethod.Get
                }) {
                while (true) {

                    for (command in commands) {
                        send(command).run {
                            Log.i("FINNHUNB", "send command: $command")
                            delay(100)
                        }
                    }
                    commands.clear()
                    for (message in incoming) {
                        message as? Frame.Text ?: continue
                        callback(
                            Json.decodeFromString(message.readText()),
                            false
                        )
                    }
                    //val othersMessage = incoming.receive() as? Frame.Text ?: continue
                    //println(othersMessage.readText())
                    /*
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> {
                            Log.i("FINNHUB", frame.toString())
                            callback(
                                Json.decodeFromString<WebSocketResponse>(frame.readText()),
                                false
                            )
                        }
                        is Frame.Close -> {
                            Log.i("FINNHUB", "Connection closed")
                            callback(WebSocketResponse("close"), true)
                            close()
                            return@webSocket
                        }
                        else -> {}
                    }
                    */
                }
            }
        }catch (e: Exception){
            Log.e("FINNHUB", "Some error: ${e.message}")
            callback(WebSocketResponse("close"), true)
        }
    }
}