package com.example.puzzlegame.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.puzzlegame.model.BlockColor
import com.example.puzzlegame.model.ColorPalette

val BoardDark = Color(0xFF3E2723)
val BoardMedium = Color(0xFF5D4037)
val BoardLight = Color(0xFF795548)
val GridLine = Color(0xFF4E342E)
val CellInset = Color(0xFF4A3228)
val TextCream = Color(0xFFFFF8E1)
val TextGold = Color(0xFFFFD54F)
val WarningRed = Color(0xFFFF5252)
val GhostValid = Color(0x4400C853)
val GhostInvalid = Color(0x44FF1744)
val Base = Color(0xFFCDBFB0)
val Trim = Color(0xFFAC9A8A)

var activePalette by mutableStateOf(ColorPalette.JEWEL)

data class PaletteColors(
    val red: Color,
    val blue: Color,
    val green: Color,
    val purple: Color,
    val orange: Color
) {
    fun toList(): List<Color> = listOf(red, blue, green, purple, orange)
}

val JewelPalette = PaletteColors(
    red    = Color(0xFFE53935),
    blue   = Color(0xFF1E88E5),
    green  = Color(0xFF43A047),
    purple = Color(0xFF8E24AA),
    orange = Color(0xFFFB8C00)
)

val EarthyPalette = PaletteColors(
    red    = Color(0xFFCB997E),
    blue   = Color(0xFFB7B7A4),
    green  = Color(0xFF6B705C),
    purple = Color(0xFFA5A58D),
    orange = Color(0xFFFFE8D6)
)

val PastelPalette = PaletteColors(
    red    = Color(0xFFF48FB1),
    blue   = Color(0xFF81D4FA),
    green  = Color(0xFFA5D6A7),
    purple = Color(0xFFCE93D8),
    orange = Color(0xFFFFCC80)
)

val NeonPalette = PaletteColors(
    red    = Color(0xFFFF2E63),
    blue   = Color(0xFF08D9D6),
    green  = Color(0xFF39FF14),
    purple = Color(0xFFFCE38A),
    orange = Color(0xFFFF9F1C)
)

val WoodPalette = PaletteColors(
    red    = Color(0xFFEED9C4),
    blue   = Color(0xFFD4B896),
    green  = Color(0xFFBA9768),
    purple = Color(0xFF92734E),
    orange = Color(0xFF6B4F3A)
)

fun paletteOf(palette: ColorPalette): PaletteColors = when (palette) {
    ColorPalette.JEWEL -> JewelPalette
    ColorPalette.EARTHY -> EarthyPalette
    ColorPalette.PASTEL -> PastelPalette
    ColorPalette.NEON -> NeonPalette
    ColorPalette.WOOD -> WoodPalette
}

private fun currentPalette(): PaletteColors = paletteOf(activePalette)

fun BlockColor.toComposeColor(): Color {
    val p = currentPalette()
    return when (this) {
        BlockColor.NONE   -> BoardLight
        BlockColor.RED    -> p.red
        BlockColor.BLUE   -> p.blue
        BlockColor.GREEN  -> p.green
        BlockColor.PURPLE -> p.purple
        BlockColor.ORANGE -> p.orange
    }
}