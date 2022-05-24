package ru.kettuproj.stocks.ui.fragment

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.map
import ru.kettuproj.stocks.room.StockEntity
import ru.kettuproj.stocks.viewmodel.StockViewModel

@Composable
fun StocksFragment(navController: NavController) {
    Scaffold(
        floatingActionButton = { AddStockButton(onClick = {navController.navigate("exchanges")})},
        floatingActionButtonPosition = FabPosition.End
    ) {
        val viewModelStock: StockViewModel = viewModel()

        val storedStocks = viewModelStock.getStocks()
        viewModelStock.loadItems(storedStocks)
        viewModelStock.loadStockData()

        val loadedStocks = viewModelStock.loadedStocks.collectAsState().value

        LazyColumn(){
            items(items = loadedStocks, itemContent = { item ->
                Text(item.getPrice().toString())
            })
        }
    }
}

@Composable
fun StockScreen(){
    StockList()

    //NoItems()
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>){
    clear()
    addAll(newList)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockList(){
    val viewModelStock: StockViewModel = viewModel()
    val stocks = viewModelStock.getStocks()

    val lazyListState = rememberLazyListState()
    val visibleSymbols = remember { mutableStateListOf<StockEntity>()}
    viewModelStock.loadStockData()
    val loaded = viewModelStock.loadedStocks.collectAsState()

    viewModelStock.loadItems(visibleSymbols)
    LazyColumn(
        state = lazyListState
    ){

        visibleSymbols.swapList(lazyListState.layoutInfo.visibleItemsInfo.map { stocks[it.index] })

        items(items = stocks, itemContent = { item->

            Card(modifier = Modifier.fillMaxWidth()){
                Column() {
                    Text(item.display)
                    Text(item.exchange)
                    Text(item.symbol)
                    loaded.value.map { it.getSymbol().symbol }.indexOf(item.symbol).let { index->
                        if(index!=-1){
                            Text(loaded.value[index].getPrice().toString());
                        }
                    }

                }


            }


        })
    }
}

@Composable
fun StockItem(){

}

@Composable
fun NoItems(){
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Text("No boba")
    }
}

@Composable
fun AddStockButton(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
    ) {
        Icon(Icons.Filled.Add, "Add stock button")
    }
}