package ru.kettuproj.stocks.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.ui.components.LoadingScreen
import ru.kettuproj.stocks.ui.components.NoInternetScreen
import ru.kettuproj.stocks.viewmodel.ExchangesViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangesFragment(navController: NavController, window: Window) {
    val context = LocalContext.current
    val internet = remember { mutableStateOf( isInternetAvailable(context)) }

    val viewModel: ExchangesViewModel = viewModel()
    val data = viewModel.exchanges.collectAsState()
    val loading = viewModel.loading.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ){
        if(!internet.value){
            NoInternetScreen(PaddingValues()) {
                internet.value = isInternetAvailable(context)
                viewModel.load()
            }
        }
        else if(loading.value){
            ExchangesList(
                data.value,
                onItemClick = { exchange ->
                    navController.navigate("symbols/${exchange}")
                }
            )
        }else{
            LoadingScreen()
        }
    }
}

@Composable
fun ExchangesList(
    items: List<String>,
    onItemClick: (path: String) -> Unit,
){
    LazyColumn(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()){
        items(items = items, itemContent = { name->
            ExchangeItem(
                name = name,
                onClick = onItemClick
            )
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExchangeItem(
    name: String,
    onClick: (path: String) -> Unit,
){
    androidx.compose.material.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = {onClick(name)},
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp),
            text = name,
            fontSize = 30.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeItemPreview(){
    ExchangeItem(name = "Forex", onClick = {})
}

@Preview(showBackground = true)
@Composable
fun ExchangesListPreview(){
    ExchangesList(items = listOf("Forex", "Forex", "Forex"), onItemClick = {})
}