package ru.kettuproj.stocks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.repository.FinnhubRepository

class SymbolsViewModel(application: Application) : AndroidViewModel(application)  {

    private val tokenSettings = SettingsViewModel(application)
    val symbols   = MutableStateFlow<List<Symbol>>(listOf())
    val status    = MutableStateFlow(HttpStatusCode.OK)
    val loading   = MutableStateFlow(false)
    private var context   = application


    fun start(exchange: String) {
        if(isInternetAvailable(context))
        viewModelScope.launch {
            kotlin.runCatching {
                FinnhubRepository.getSymbols(exchange,tokenSettings.getToken())
            }.onSuccess {
                if(it.status == HttpStatusCode.OK){
                    symbols.value = it.data
                }else{
                    symbols.value = listOf()
                    status.value = it.status
                }
                loading.value = true
            }.onFailure {
                status.value = HttpStatusCode.BadRequest
                symbols.value = listOf()
                loading.value = true
            }
        }
    }
}