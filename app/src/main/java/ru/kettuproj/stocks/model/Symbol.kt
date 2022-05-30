package ru.kettuproj.stocks.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Symbol(
    @Required
    @SerialName("description")   val description: String,
    @Required
    @SerialName("displaySymbol") val displaySymbol: String,
    @Required
    @SerialName("symbol")        val symbol: String
)
