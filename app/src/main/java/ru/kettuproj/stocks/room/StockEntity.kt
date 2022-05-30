package ru.kettuproj.stocks.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StockEntity(
    @PrimaryKey val symbol: String,
    @ColumnInfo(name = "display")     val display    : String,
    @ColumnInfo(name = "exchange")    val exchange   : String,
    @ColumnInfo(name = "description") val description: String
)
