package ru.kettuproj.stocks.ui.fragment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.ui.component.ExchangeItem
import ru.kettuproj.stocks.ui.component.ExchangesList
import ru.kettuproj.stocks.viewmodel.ExchangesViewModel
import ru.kettuproj.stocks.viewmodel.StockViewModel
import ru.kettuproj.stocks.viewmodel.SymbolsViewModel

@Composable
fun SymbolsFragment(navController: NavController, exchange: String?){
    val viewModel: SymbolsViewModel = viewModel()
    val viewModelStock: StockViewModel = viewModel()
    viewModel.start(exchange!!) //TODO: Error handle
    val data = viewModel.symbols.collectAsState()

    val selected = mutableListOf<Symbol>()

    Scaffold() {
        LazyColumn(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()){
            items(items = data.value, itemContent = { item->
                SymbolItem(
                    item = item,
                    isSelected = selected.contains(item),
                    onSelect = {
                        if(it) selected.add(item)
                        else selected.remove(item)
                })
            })
        }
    }
    FloatingActionButton(onClick = {
        viewModelStock.addStocks(selected,exchange)
        navController.navigate("stocks")
    }) {

    }
}

@Composable
fun SymbolItem(
    item: Symbol,
    isSelected: Boolean,
    onSelect: (isSelected:Boolean)->Unit
){
    val checkedState = remember { mutableStateOf(isSelected) }

    Card(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()
    ){
        Row(modifier = Modifier.padding(8.dp)){
            Box(modifier = Modifier.weight(4f)){
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = item.displaySymbol, fontSize = 30.sp)
                    Text(item.description)
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Checkbox(checked = checkedState.value, onCheckedChange = {
                    checkedState.value = it
                    onSelect(it)
                })
            }
        }
    }
}