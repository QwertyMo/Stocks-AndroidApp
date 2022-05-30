package ru.kettuproj.stocks.ui.fragment

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberDismissState
import androidx.compose.material.Card
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.model.Stock
import ru.kettuproj.stocks.model.Symbol
import ru.kettuproj.stocks.room.StockEntity
import ru.kettuproj.stocks.ui.AnimatedShimmer
import ru.kettuproj.stocks.ui.components.LoadingScreen
import ru.kettuproj.stocks.ui.components.NoInternetScreen
import ru.kettuproj.stocks.viewmodel.SettingsViewModel
import ru.kettuproj.stocks.viewmodel.StockViewModel
import kotlin.math.abs

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StocksFragment(navController: NavController) {

    val context = LocalContext.current

    val viewModelSettings:  SettingsViewModel = viewModel()
    val internet = remember { mutableStateOf( isInternetAvailable(context))}
    val viewModelStock: StockViewModel = viewModel()
    val storedStocks = viewModelStock.storedStock
    val deleted = remember { mutableStateOf(0) }

    val tokenValid = viewModelSettings.tokenValidation.collectAsState()
    val loadValid  = viewModelSettings.loadValidation.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {BottomBar(navController)}
    ) { paddings ->

                if (!internet.value) {
                    NoInternetScreen(paddings){
                        internet.value = isInternetAvailable(context)
                        viewModelSettings.checkToken(viewModelSettings.getToken())
                        viewModelStock.updateStock()
                    }
                } else if (!loadValid.value) {
                    LoadingScreen(paddings)
                } else if (viewModelSettings.getToken() == "" || !tokenValid.value) {
                    InvalidToken(paddings)
                } else if (storedStocks.isEmpty() || storedStocks.size - deleted.value == 0) {
                    NoItems(paddings)
                } else {
                    viewModelStock.loadStockData()
                    ListItems(paddings, storedStocks, viewModelStock, deleted)
                }

        }
    }





@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ListItems(
    paddings:       PaddingValues,
    storedStocks:   List<StockEntity>,
    viewModelStock: StockViewModel,
    deleted:        MutableState<Int>){

    val loadedStocks = viewModelStock.loadedStocks

    LazyColumn(modifier = Modifier.padding(bottom = paddings.calculateBottomPadding())) {
        items(items = storedStocks, itemContent = { item ->
            viewModelStock.loadItem(item)
            val dismissState = rememberDismissState()
            val dismissDirection = dismissState.dismissDirection
            val isDismissed = dismissState.isDismissed(DismissDirection.StartToEnd)
            if (isDismissed && dismissDirection == DismissDirection.StartToEnd) {
                LaunchedEffect(Unit) {
                    delay(300)
                    viewModelStock.removeStock(item)
                    deleted.value +=1
                }
            }
            AnimatedVisibility(
                visible = !isDismissed,
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
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.StartToEnd),
                    background = { DeleteBackground() }
                ) {
                    StockCard(
                        item = item,
                        data = loadedStocks.find { it.getSymbol().symbol == item.symbol })
                }
            }
        })
    }
}

@Composable
fun DeleteBackground(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row {
            Icon(Icons.Filled.Delete, "Delete")
            Text("Remove from list")
        }
    }
}

@Composable
fun StockCard(
    item: StockEntity,
    data: Stock?
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { if (data != null) expanded.value = !expanded.value },
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Column {
            Row(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = item.display + "\n",
                    fontSize = 24.sp,
                    maxLines = 2,
                    modifier = Modifier.weight(6f)
                )

                Box(modifier = Modifier.weight(2.5f)) {
                    if (data != null) {

                        val isUp = data.getPercent() >= 0
                        val color = if (isUp) Color.Green else Color.Red
                        val icon =
                            if (isUp) Icons.Filled.KeyboardArrowUp
                            else Icons.Filled.KeyboardArrowDown

                        Column {
                            Text(
                                text = data.getPrice().toString(),
                                fontSize = 20.sp
                            )
                            Row{
                                Icon(
                                    imageVector = icon,
                                    tint = color,
                                    contentDescription = "Icon shows up or down"
                                )
                                Text(
                                    text = String.format("%3.2f", abs(data.getPercent())) + "%",
                                    color = color,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    } else {
                        Column{
                            Row(modifier = Modifier.padding(4.dp)) {
                                AnimatedShimmer(20.sp)
                            }
                            Row(modifier = Modifier.padding(4.dp)) {
                                AnimatedShimmer(16.sp)
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(expanded.value) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Divider(
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }

                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        UnderlineData("Min price", data?.getLowestPrice() ?: 0f)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        UnderlineData("Max price", data?.getHighestPrice() ?: 0f)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        UnderlineData("Open price", data?.getOpenPrice() ?: 0f)
                    }
                }
            }

        }
    }
}

@Composable
fun UnderlineData(text: String, data: Float){
    Text(text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 16.sp)
    Text("$data", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 16.sp)
}

@Composable
fun NoItems(paddings: PaddingValues){
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = paddings.calculateBottomPadding()),
        contentAlignment = Alignment.Center
    ){
        Text(
            textAlign = TextAlign.Center,
            text = "There is no stocks. Add new now!",
            fontSize = 24.sp,
            modifier = Modifier.padding(32.dp))
    }
}

@Composable
fun InvalidToken(paddings: PaddingValues){
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = paddings.calculateBottomPadding()),
        contentAlignment = Alignment.Center
    ){
        Text(
            textAlign = TextAlign.Center,
            text = "Invalid token. Add new token at token settings",
            fontSize = 24.sp,
            modifier = Modifier.padding(32.dp))
    }
}

@Composable
fun BottomBar(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(32f, 32f, 0f, 0f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box{
                TextButton(
                    onClick = {
                        navController.navigate("tokenSettings")
                    },
                ) {
                    Icon(Icons.Filled.Settings, "settings")
                    Text(
                        text = "Token settings",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(4.dp)
                    )
                }
            }
            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.weight(1f)) {
                FloatingActionButton(
                    onClick = { navController.navigate("exchanges") },
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Icon(Icons.Filled.Add, "Add stock button")
                }
            }
        }
    }
}


@Preview
@Composable
fun Preview(){
    StockCard(
        item = StockEntity("EUR/USD", "EUR/USD", "Forex", "test"),
        data = Stock(Symbol("EUR/USD", "EUR/USD", "EUR/USD"),"forex",1.0432f,1.4000f,1.0000f, 1.2100f)
    )
}