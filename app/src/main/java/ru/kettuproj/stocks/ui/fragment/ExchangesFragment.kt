package ru.kettuproj.stocks.ui.fragment

import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.kettuproj.stocks.ui.component.ExchangesList
import ru.kettuproj.stocks.viewmodel.ExchangesViewModel


    @Composable
    fun ExchangesFragment(navController: NavController) {
        val viewModel: ExchangesViewModel = viewModel()
        val data = viewModel.exchanges.collectAsState()
    Scaffold() {
        ExchangesList(
            data.value,
            onItemClick = { exchange ->
                navController.navigate("symbols/${exchange}") }
        )
    }
}