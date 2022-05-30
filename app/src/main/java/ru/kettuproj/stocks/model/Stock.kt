package ru.kettuproj.stocks.model

class Stock(
    private val symbol          : Symbol,
    private val exchange        : String,
    private var price           : Float,
    private var highestPrice    : Float,
    private var lowestPrice     : Float,
    private val openPrice       : Float,
){
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
        return ((openPrice/price)*100)-100
    }
}
