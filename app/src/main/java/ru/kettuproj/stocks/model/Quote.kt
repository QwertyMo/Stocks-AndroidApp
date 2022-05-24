package ru.kettuproj.stocks.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    @Required
    @SerialName("c")  val price    : Float,
    @Required
    @SerialName("d")  val change   : Float?  = null,
    @Required
    @SerialName("dp") val percent  : Float?  = null,
    @Required
    @SerialName("h")  val highPrice: Float,
    @Required
    @SerialName("l")  val lowPrice : Float,
    @Required
    @SerialName("o")  val openPrice: Float,
    @Required
    @SerialName("t")  val time     : String,
)