package ru.kettuproj.stocks.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff1e40ff),
    secondary = Color(0xFF1E3BDD),
    tertiary = Color(0xFFFF9800),
)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0D2BD1),
    secondary = Color(0xFF071B8F),
    tertiary = Color(0xFFFFC107),
)

@Composable
fun StocksTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    MaterialTheme(
        colorScheme  = colorScheme,
        content = content
    )
}