package ru.kettuproj.stocks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.repository.FinnhubRepository

class SymbolsViewModel : ViewModel() {
    val symbols   = MutableStateFlow<List<Symbol>>(listOf())
    val status    = MutableStateFlow<HttpStatusCode>(HttpStatusCode.OK)

    fun start(exchange: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                FinnhubRepository.getSymbols(exchange)
            }.onSuccess {
                if(it.status == HttpStatusCode.OK){
                    symbols.value = it.data
                }else{
                    symbols.value = listOf()
                    status.value = it.status
                }
            }.onFailure {
                status.value = HttpStatusCode.BadRequest
                symbols.value = listOf()
            }
        }
    }
}