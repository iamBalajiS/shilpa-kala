package com.example.shilpakala.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFA92B2B),
    secondary = Color(0xFFD6A039),
    background = Color(0xFFFFF9F0),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color(0xFF241A12),
    onBackground = Color(0xFF1F1A17)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4A8),
    secondary = Color(0xFFE9C16D),
    background = Color(0xFF17120F),
    surface = Color(0xFF241A12),
    onPrimary = Color(0xFF4F0707),
    onSecondary = Color(0xFF2D2105),
    onBackground = Color(0xFFFFF4EA)
)

@Composable
fun ShilpaKalaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = ShilpaKalaShapes,
        typography = ShilpaKalaTypography,
        content = content
    )
}

object ShilpaKalaMotion {
    val standardEnter = tween<Int>(durationMillis = 350, easing = FastOutSlowInEasing)
}
