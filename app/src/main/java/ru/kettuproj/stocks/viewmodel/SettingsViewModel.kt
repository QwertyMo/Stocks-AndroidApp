package ru.kettuproj.stocks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.repository.FinnhubRepository
import ru.kettuproj.stocks.room.AppDatabase
import ru.kettuproj.stocks.room.SettingEntity

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase
    private var context   = application
    val tokenValidation = MutableStateFlow(false)
    val loadValidation  = MutableStateFlow(true)

    init{
        database = AppDatabase.getDatabase(application)
        if(isInternetAvailable(context)) checkToken(getToken())
    }

    fun checkToken(token: String){
        loadValidation.value = false
        if(isInternetAvailable(context))
            viewModelScope.launch {
                tokenValidation.value = FinnhubRepository.getExchanges(token).status!=HttpStatusCode.Unauthorized
                loadValidation.value = true
            }
    }

    fun getToken():String{
        database.stockDao().getSettings().find { it.setting == "token" }.let {
            if(it == null) return ""
            else return it.value
        }
    }

    fun setToken(token: String){
        database.stockDao().update(SettingEntity("token",token))
    }
}