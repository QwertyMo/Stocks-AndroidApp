package ru.kettuproj.stocks.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketResponse(
    @Required
    @SerialName("type") val type: String,
    @SerialName("data") val data: List<Trade>? = null,
    @SerialName("msg")  val msg : String?      = null
)