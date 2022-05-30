package ru.kettuproj.stocks.repository

import android.util.Log
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.kettuproj.stocks.common.Constant.REST_HOST
import ru.kettuproj.stocks.model.Quote
import ru.kettuproj.stocks.model.RepositoryResponse
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.net.Client

object FinnhubRepository {

    suspend fun getExchanges(token: String):RepositoryResponse<List<String>>{
        Log.i("StockRequest", "Try to request exchanges")
        val data = Client.client.get("$REST_HOST/forex/exchange"){
            parameter("token", token)
        }
        return if(data.status == HttpStatusCode.OK)
            RepositoryResponse(data.status, data.body())

        else RepositoryResponse(data.status, listOf())
    }

    suspend fun getSymbols(exchange: String, token: String):RepositoryResponse<List<Symbol>>{

        val data = Client.client.get("$REST_HOST/forex/symbol"){
            parameter("token", token)
            parameter("exchange", exchange)
        }
        return if(data.status == HttpStatusCode.OK)
            RepositoryResponse(data.status,data.body())
        else RepositoryResponse(data.status, listOf())
    }

    suspend fun getQuote(symbol: String, token: String):RepositoryResponse<Quote>{

        val data = Client.client.get("$REST_HOST/quote"){
            parameter("token", token)
            parameter("symbol", symbol)
        }
        return if(data.status == HttpStatusCode.OK)
            RepositoryResponse(data.status,data.body())
        else RepositoryResponse(data.status,  Quote(0f,0f,0f,0f,0f,0f,""))

    }
}