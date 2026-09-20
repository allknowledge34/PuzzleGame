package com.example.puzzlegame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WoodColorScheme = darkColorScheme(
    primary = TextGold,
    secondary = TextCream,
    tertiary = TextGold,
    background = BoardDark,
    surface = BoardMedium,
    onPrimary = BoardDark,
    onSecondary = TextCream,
    onTertiary = BoardDark,
    onBackground = TextCream,
    onSurface = TextCream
)
@Composable
fun PuzzleGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WoodColorScheme,
        typography = Typography,
        content = content
    )
}