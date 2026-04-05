package com.masmultimedia.sospechapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = androidx.compose.material3.darkColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    error = BrandDanger,
    background = BrandBg,
    surface = BrandSurface,
    surfaceVariant = BrandSurface2,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onError = Color.White,
    onBackground = BrandText,
    onSurface = BrandText
)

@Composable
fun SospechAppTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = DarkScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}