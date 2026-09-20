package com.example.puzzlegame.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.puzzlegame.model.BlockColor
import com.example.puzzlegame.model.ColorPalette

val BackgroundDark = Color(0xFF090E17)
val GlowBlue = Color(0xFF0C4A6E)
val GlowPurple = Color(0xFF1E1B4B)
val GlassSurface = Color(0x1AFFFFFF)
val GlassBorder = Color(0x33FFFFFF)
val GridLine = Color(0x14FFFFFF)
val CellEmpty = Color(0x0AFFFFFF)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val AccentPrimary = Color(0xFF0EA5E9)
val AccentSecondary = Color(0xFF8B5CF6)
val WarningRed = Color(0xFFEF4444)
val GhostValid = Color(0x4434D399)
val GhostInvalid = Color(0x44EF4444)

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
    red    = Color(0xFFF43F5E),
    blue   = Color(0xFF3B82F6),
    green  = Color(0xFF10B981),
    purple = Color(0xFF8B5CF6),
    orange = Color(0xFFF59E0B)
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
        BlockColor.NONE   -> CellEmpty
        BlockColor.RED    -> p.red
        BlockColor.BLUE   -> p.blue
        BlockColor.GREEN  -> p.green
        BlockColor.PURPLE -> p.purple
        BlockColor.ORANGE -> p.orange
    }
}