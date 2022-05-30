package ru.kettuproj.stocks.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(paddings: PaddingValues = PaddingValues(0.dp)){
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = paddings.calculateBottomPadding()),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}