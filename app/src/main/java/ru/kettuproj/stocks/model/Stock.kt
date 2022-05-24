package ru.kettuproj.stocks.model

import androidx.room.Entity

class Stock(
    symbol: Symbol,
    exchange: String,
    price: Float,
    highestPrice: Float,
    lowestPrice: Float,
    openPrice: Float,
){
    private val symbol      : Symbol = symbol
    private val exchange    : String = exchange
    private val openPrice   : Float  = openPrice
    private var highestPrice: Float  = highestPrice
    private var lowestPrice : Float  = lowestPrice
    private var price       : Float  = price


    fun getHighestPrice(): Float  = highestPrice
    fun getLowestPrice() : Float  = lowestPrice
    fun getOpenPrice()   : Float  = openPrice
    fun getPrice()       : Float  = price
    fun getSymbol()      : Symbol = symbol
    fun getExchange()    : String = exchange

    fun updatePrice(price: Float){
        this.price = price
        if (lowestPrice  > price) lowestPrice  = price
        if (highestPrice < price) highestPrice = price
    }

    fun getPercent(): Float{
        return (openPrice/price)*100
    }
}
