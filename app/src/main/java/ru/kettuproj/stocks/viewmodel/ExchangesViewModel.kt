package ru.kettuproj.stocks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.kettuproj.stocks.repository.FinnhubRepository

class ExchangesViewModel : ViewModel() {
    val exchanges = MutableStateFlow<List<String>>(listOf())
    val status    = MutableStateFlow<HttpStatusCode>(HttpStatusCode.OK)

    init {
        viewModelScope.launch {
            kotlin.runCatching {
                FinnhubRepository.getExchanges()
            }.onSuccess {
                if(it.status == HttpStatusCode.OK){
                    exchanges.value = it.data
                }else{
                    exchanges.value = listOf()
                    status.value = it.status
                }
            }.onFailure {
                status.value = HttpStatusCode.BadRequest
                exchanges.value = listOf()
            }
        }
    }
}