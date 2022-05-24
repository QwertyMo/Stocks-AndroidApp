package ru.kettuproj.stocks.ui.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeItem(
    name: String,
    onClick: (path: String) -> Unit,
){
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        onClick = {onClick(name)}
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp),
            text = name
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