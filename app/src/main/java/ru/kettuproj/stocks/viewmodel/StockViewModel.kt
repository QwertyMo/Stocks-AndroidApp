package ru.kettuproj.stocks.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.model.Stock
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.repository.FinnhubRepository
import ru.kettuproj.stocks.repository.FinnhubWebSocket
import ru.kettuproj.stocks.room.AppDatabase
import ru.kettuproj.stocks.room.SettingEntity
import ru.kettuproj.stocks.room.StockEntity

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenSettings = SettingsViewModel(application)
    private var context   = application

    val storedStock  = mutableStateListOf<StockEntity>()
    val loadedStocks = mutableStateListOf<Stock>()

    private val database: AppDatabase
    private val socket = FinnhubWebSocket()
    private val loadedItems: MutableList<StockEntity> = mutableListOf()

    private var listening = false

    init{
        database = AppDatabase.getDatabase(application)
        if(isInternetAvailable(context)) updateStock()
    }

    fun loadItems(items: List<StockEntity>){
        val iterator = loadedItems.iterator()
        while (iterator.hasNext()){
            val item = iterator.next()
            if(!items.map { it.symbol }.contains(item.symbol)) {
                socket.unsubscribe(item.symbol)
                Log.i("FINNHUNB", "Add command to unsub: ${item.symbol}")
                iterator.remove()
            }
        }

        for(item in items) {
            if(!loadedItems.map { it.symbol }.contains(item.symbol)){
                loadedItems.add(item)
                if(!loadedStocks.map { it.getSymbol().symbol }.contains(item.symbol))
                    addNewQuote(item){
                        if(it) {
                            socket.subscribe(item.symbol)
                            Log.i("FINNHUNB", "Add command to sub: ${item.symbol}")
                        }
                    }
            }
        }
    }

    fun loadItem(stock: StockEntity){
        if(!loadedItems.contains(stock)) {
            loadedItems.add(stock)
            addNewQuote(stock){}
            socket.subscribe(stock.symbol)
            Log.i("FINNHUNB", "Add command to sub: ${stock.symbol}")
        }
    }

    fun addNewQuote(symbol: StockEntity,  retry:Boolean = false, callback: (added:Boolean)->Unit){
        viewModelScope.launch {
            kotlin.runCatching {
                FinnhubRepository.getQuote(symbol.symbol, tokenSettings.getToken())
            }.onSuccess {
                if(it.status == HttpStatusCode.OK){
                    loadedStocks.add(ru.kettuproj.stocks.model.Stock(
                        ru.kettuproj.stocks.model.Symbol(
                            symbol.description,
                            symbol.display,
                            symbol.symbol
                        ),
                        symbol.exchange,
                        it.data.price,
                        it.data.highPrice,
                        it.data.lowPrice,
                        it.data.openPrice
                    ))
                    Log.i("FINNHUNB", "Added new quote: ${symbol.symbol}")
                    callback(true)
                }else{
                    Log.i("FINNHUNB", "Error while adding quote: ${symbol.symbol}. Status code: ${it.status}")
                    if(it.status == HttpStatusCode.TooManyRequests) delay(5000)
                    Log.i("FINNHUNB", "Retry to get quote for: ${symbol.symbol}")
                    addNewQuote(symbol){callback(it)}
                }
            }.onFailure {
                Log.i("FINNHUNB", "Error while adding quote: ${symbol.symbol}. Error: ${it.message}")
                Log.i("FINNHUNB", "Retry to get quote for: ${symbol.symbol}")
                addNewQuote(symbol){callback(it)}
            }
        }
    }

    fun loadStockData() {
        if(listening) return
        else listening = true
        viewModelScope.launch (Dispatchers.IO){
                socket.listen(tokenSettings.getToken()) {data, isClosed ->
                    if(data.type=="trade" && data.data!=null){
                        for(item in data.data){
                            if(loadedStocks.map{it.getSymbol().symbol}.contains(item.symbol)) {
                                val temp =  loadedStocks.find { it.getSymbol().symbol == item.symbol }
                                if(temp!=null) {
                                    Log.i("FINNHUNB", "update ${item.symbol}: ${item.price}")
                                    val stock = Stock(temp.getSymbol(),temp.getExchange(),temp.getPrice(),temp.getHighestPrice(),temp.getLowestPrice(),temp.getOpenPrice())
                                    loadedStocks.remove(temp)
                                    stock.updatePrice(item.price)
                                    loadedStocks.add(stock)
                                }
                            }
                        }

                    }
                }
        }
    }

    fun updateStock(){
        storedStock.clear()
        storedStock.addAll(database.stockDao().getAll())
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