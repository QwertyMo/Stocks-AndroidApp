package ru.kettuproj.stocks.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketRequest(
    @Required
    @SerialName("type")   val type: String,
    @Required
    @SerialName("symbol") val symbol: String
)