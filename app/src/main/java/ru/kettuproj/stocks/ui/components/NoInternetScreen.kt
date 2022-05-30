package ru.kettuproj.stocks.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoInternetScreen(paddings: PaddingValues, onRefresh:()->Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = paddings.calculateBottomPadding()),
        contentAlignment = Alignment.Center
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "internet",
                modifier = Modifier
                    .padding(8.dp)
                    .size(64.dp)
            )
            Text(
                text = "No internet connection",
                fontSize = 24.sp
            )
            FloatingActionButton(
                modifier = Modifier.padding(4.dp),
                onClick = {
                    onRefresh()
                }) {
                    Icon(Icons.Filled.Refresh,"refresh")
            }
        }
    }
}