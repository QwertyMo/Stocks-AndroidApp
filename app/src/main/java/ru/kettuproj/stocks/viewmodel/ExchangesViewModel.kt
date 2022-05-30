package ru.kettuproj.stocks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.repository.FinnhubRepository

class ExchangesViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenSettings = SettingsViewModel(application)
    private var context   = application
    val exchanges = MutableStateFlow<List<String>>(listOf())
    val status    = MutableStateFlow(HttpStatusCode.OK)
    val loading   = MutableStateFlow(false)

    fun load(){
        if(isInternetAvailable(context))
            viewModelScope.launch {
                kotlin.runCatching {
                    FinnhubRepository.getExchanges(tokenSettings.getToken())
                }.onSuccess {
                    if(it.status == HttpStatusCode.OK){
                        exchanges.value = it.data
                    }else{
                        exchanges.value = listOf()
                        status.value = it.status
                    }
                    loading.value = true
                }.onFailure {
                    status.value = HttpStatusCode.BadRequest
                    exchanges.value = listOf()
                    loading.value = true
                }
            }
    }

    init {
        load()
    }
}