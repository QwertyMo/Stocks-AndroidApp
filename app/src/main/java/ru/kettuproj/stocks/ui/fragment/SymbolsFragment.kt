package ru.kettuproj.stocks.ui.fragment

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.ui.components.LoadingScreen
import ru.kettuproj.stocks.ui.components.NoInternetScreen
import ru.kettuproj.stocks.viewmodel.StockViewModel
import ru.kettuproj.stocks.viewmodel.SymbolsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolsFragment(navController: NavController, exchange: String?){

    val context = LocalContext.current
    val internet = remember { mutableStateOf( isInternetAvailable(context)) }

    val viewModelSymbol: SymbolsViewModel = viewModel()
    val viewModelStock: StockViewModel = viewModel()

    viewModelSymbol.start(exchange!!)
    val data = viewModelSymbol.symbols.collectAsState()
    val loading = viewModelSymbol.loading.collectAsState()

    val selected = remember { mutableStateListOf<Symbol>()}

    androidx.compose.material3.Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {SymbolsBottomBar(viewModelStock,selected,exchange,navController)}
    ) { paddings ->

        if(!internet.value){
            NoInternetScreen(PaddingValues()) {
                internet.value = isInternetAvailable(context)
                viewModelSymbol.start(exchange)
            }
        }
        else if(loading.value){
            SymbolsList(paddings,data.value,selected)
        }
        else{
            LoadingScreen(paddings)
        }

    }

}

@Composable
fun SymbolsList(
    paddings: PaddingValues,
    data: List<Symbol>,
    selected: SnapshotStateList<Symbol>){
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(paddings)
    ) {
        items(items = data, itemContent = { item ->
            SymbolItem(
                item = item,
                isSelected = selected.contains(item),
                onSelect = {
                    if (it) selected.add(item)
                    else selected.remove(item)
                })
        })
    }
}

@Composable
fun SymbolsBottomBar(
    viewModelStock: StockViewModel,
    selected: SnapshotStateList<Symbol>,
    exchange: String,
    navController: NavController
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(32f, 32f, 0f, 0f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Box{
                val text =  if(selected.isEmpty()) "Select elements"
                else "Selected ${selected.size} items"
                Text(
                    text = text,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !selected.isEmpty(),
                    exit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 300,
                        )
                    ),
                    enter = expandVertically(
                        animationSpec = tween(
                            durationMillis = 300
                        )
                    )
                ) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {
                            viewModelStock.addStocks(selected, exchange)
                            navController.navigate("stocks"){
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer) {
                        androidx.compose.material3.Icon(Icons.Filled.Done, "Add stock button")
                    }
                }

            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SymbolItem(
    item: Symbol,
    isSelected: Boolean,
    onSelect: (isSelected:Boolean)->Unit
) {
    val checkedState = remember { mutableStateOf(isSelected) }
    val color = if(checkedState.value) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.secondaryContainer

    val colorOn = if(checkedState.value) MaterialTheme.colorScheme.onSecondary
                else MaterialTheme.colorScheme.onSecondaryContainer
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        backgroundColor = color,
        contentColor = colorOn,
        onClick = {
            checkedState.value=!checkedState.value
            onSelect(checkedState.value)
        }
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(4.dp)) {
            Text(text = item.displaySymbol, fontSize = 30.sp)
            Text(text = item.description + "\n", maxLines = 2, modifier = Modifier.padding(4.dp))
        }
    }
}

@Preview
@Composable
fun PreviewSymbolItem(){
    Column{
        SymbolItem(item = Symbol("EUR/USD description", "EUR/USD", "EUR/USD"), isSelected = false, onSelect = {})
        SymbolItem(item = Symbol("EUR/USD description", "EUR/USD", "EUR/USD"), isSelected = true, onSelect = {})
    }
}