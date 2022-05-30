package ru.kettuproj.stocks.ui.fragment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.kettuproj.stocks.common.isInternetAvailable
import ru.kettuproj.stocks.viewmodel.SettingsViewModel
import ru.kettuproj.stocks.viewmodel.StockViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
fun TokenFragment(navController: NavController) {

    val context = LocalContext.current
    val internet = remember { mutableStateOf( isInternetAvailable(context)) }

    val viewModelToken: SettingsViewModel = viewModel()

    val token = remember { mutableStateOf(viewModelToken.getToken()) }
    val tokenValid = viewModelToken.tokenValidation.collectAsState()
    val clicked = remember { mutableStateOf(false)}
    val loading = viewModelToken.loadValidation.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = {
                    viewModelToken.setToken(token.value)
                    navController.navigate("stocks")
                }) {
                Icon(Icons.Filled.Done, "Ok")
            }
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddings)
        ) {
            Text(
                text = "Token settings",
                modifier = Modifier
                    .padding(8.dp),
                fontSize = 24.sp
            )

            OutlinedTextField(
                value = token.value,
                onValueChange = {
                    viewModelToken.setToken(it)
                    token.value = it
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                label = {
                    Text("Token")
                }
            )

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        viewModelToken.checkToken(token.value)
                        clicked.value = true
                    },
                    modifier = Modifier.weight(3f)
                ) {
                    Text(
                        text = "Check",
                        modifier = Modifier.padding(4.dp),
                        fontSize = 16.sp
                    )
                }

                val text =
                    if (!loading.value) "Checking token..."
                    else if(!clicked.value) "Check token"
                    else if(tokenValid.value) "Token valid successfully"
                    else "Invalid token"
                val color =
                    if(!loading.value) MaterialTheme.colorScheme.onBackground
                    else if(!clicked.value) MaterialTheme.colorScheme.onBackground
                    else if(tokenValid.value) Color.Green
                    else Color.Red
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(5f),
                    color = color
                )
            }

        }
    }
}

