package ru.kettuproj.stocks

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import ru.kettuproj.stocks.room.AppDatabase
import ru.kettuproj.stocks.ui.component.ExchangesList
import ru.kettuproj.stocks.ui.fragment.ExchangesFragment
import ru.kettuproj.stocks.ui.fragment.StocksFragment
import ru.kettuproj.stocks.ui.fragment.SymbolsFragment
import ru.kettuproj.stocks.ui.theme.StocksTheme
import ru.kettuproj.stocks.viewmodel.ExchangesViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        setContent {
            StocksTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.statusBarColor = Color.rgb(
                        MaterialTheme.colorScheme.tertiary.red,
                        MaterialTheme.colorScheme.tertiary.green,
                        MaterialTheme.colorScheme.tertiary.blue
                    )
                }

                val navController = rememberNavController()
                Scaffold {
                    Navigation(navController)
                }
            }
        }
        splashScreen.setKeepOnScreenCondition { false }
    }
}


@Composable
fun Navigation(navController: NavHostController){
    NavHost(navController = navController, startDestination = "stocks") {
        composable("stocks") { StocksFragment(navController) }
        composable("exchanges") { ExchangesFragment(navController) }

        composable(
            route = "symbols/{exchange}",
            arguments = listOf(navArgument("exchange"){type = NavType.StringType})) { backStackEntry ->
            SymbolsFragment(navController, backStackEntry.arguments?.getString("exchange"))
        }
    }
}