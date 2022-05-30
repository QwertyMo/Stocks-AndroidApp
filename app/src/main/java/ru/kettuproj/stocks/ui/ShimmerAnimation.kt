package ru.kettuproj.stocks.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp


@Composable
fun AnimatedShimmer(heighth: TextUnit) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.4f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    val h = with(LocalDensity.current) {
        heighth.toDp()
    }

    Box(modifier = Modifier) {
        Spacer(
            modifier = Modifier
                .height(h)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth(fraction = 0.9f)
                .background(brush)
        )
    }
}
