package ru.kettuproj.stocks.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kettuproj.stocks.model.Stock
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.repository.FinnhubRepository
import ru.kettuproj.stocks.repository.FinnhubWebSocket
import ru.kettuproj.stocks.room.AppDatabase
import ru.kettuproj.stocks.room.StockEntity

class StockViewModel(application: Application) : AndroidViewModel(application) {

    val storedStock  = MutableStateFlow<List<StockEntity>>(listOf())
    val loadedStocks = MutableStateFlow<List<Stock>>(listOf())

    private val _loaded = mutableListOf<Stock>()

    private val database: AppDatabase
    private val socket = FinnhubWebSocket()
    private val loadedItems: MutableList<StockEntity> = mutableListOf()

    init{
        database = AppDatabase.getDatabase(application)
    }

    fun loadItems(items: List<StockEntity>){

        for(item in loadedItems){
            if(!items.map { it.symbol }.contains(item.symbol)) {
                loadedItems.remove(item)
                socket.unsubscribe(item.symbol)
                Log.i("FINNHUNB", "Add command to unsub")
            }
        }

        for(item in items) {
            if(!loadedItems.map { it.symbol }.contains(item.symbol)){
                loadedItems.add(item)
                if(!_loaded.map { it.getSymbol().symbol }.contains(item.symbol))
                    addNewQuote(item){
                        if(it)socket.subscribe(item.symbol)
                        Log.i("FINNHUNB", "Add command to sub")
                    }
            }
        }
    }

    fun addNewQuote(symbol: StockEntity, callback: (added:Boolean)->Unit){
        viewModelScope.launch {
            kotlin.runCatching {
                FinnhubRepository.getQuote(symbol.symbol)
            }.onSuccess {
                if(it.status == HttpStatusCode.OK){
                    _loaded.add(Stock(
                        Symbol(symbol.description,symbol.display,symbol.symbol),
                        symbol.exchange
                        ,it.data.price,it.data.highPrice,it.data.lowPrice, it.data.openPrice))
                    loadedStocks.value = _loaded
                    Log.i("FINNHUNB", "Added new quote: ${symbol.symbol}")
                    callback(true)
                }else{
                    callback(false)
                }
            }.onFailure {
                callback(false)
            }
        }
    }

    fun loadStockData() {
        viewModelScope.launch {
            socket.listen { data, isClosed ->

                if(data.type=="trade" && data.data!=null){

                    for(item in data.data){
                        if(_loaded.map{it.getSymbol().symbol}.contains(item.symbol)) {
                            val temp =  _loaded.find { it.getSymbol().symbol == item.symbol }
                            if(temp!=null) {
                                Log.i("FINNHUNB", "update ${item.symbol}: ${item.price}")
                                val stock = Stock(temp.getSymbol(),temp.getExchange(),temp.getPrice(),temp.getHighestPrice(),temp.getLowestPrice(),temp.getOpenPrice())
                                _loaded.remove(temp)
                                stock.updatePrice(item.price)
                                _loaded.add(stock)
                            }
                        }else{

                        }
                    }
                    loadedStocks.value = _loaded

                }
            }
        }
    }

    fun getStocks():List<StockEntity>{
        return database.stockDao().getAll()
    }

    fun addStocks(symbols: List<Symbol>, exchange: String){
        for(symbol in symbols) addStock(StockEntity(
            symbol.symbol,
            symbol.displaySymbol,
            exchange,
            symbol.description
        ))
    }

    fun addStock(stock: StockEntity){
        database.stockDao().insert(stock)
    }

    fun removeStock(stock: StockEntity){
        database.stockDao().delete(stock)
    }
}