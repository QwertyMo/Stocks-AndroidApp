package ru.kettuproj.stocks.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trade(
    @Required
    @SerialName("s") val symbol: String,
    @Required
    @SerialName("p") val price : Float,
    @Required
    @SerialName("t") val time  : Long,
    @Required
    @SerialName("v") val volume: Float,
    @SerialName("c") val c     : String? = null,
)
