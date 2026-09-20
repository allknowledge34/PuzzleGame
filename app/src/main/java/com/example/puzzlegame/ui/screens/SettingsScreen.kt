package com.example.puzzlegame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.model.ColorPalette
import com.example.puzzlegame.ui.theme.BackgroundDark
import com.example.puzzlegame.ui.theme.TextPrimary
import com.example.puzzlegame.ui.theme.AccentPrimary
import com.example.puzzlegame.ui.theme.paletteOf

import androidx.compose.foundation.shape.CircleShape
import com.example.puzzlegame.ui.theme.GlassSurface
import com.example.puzzlegame.ui.theme.GlassBorder

@Composable
fun SettingsScreen(
    hapticEnabled: Boolean,
    easyShapes: Boolean,
    selectedPalette: ColorPalette,
    onHapticToggle: (Boolean) -> Unit,
    onEasyShapesToggle: (Boolean) -> Unit,
    onPaletteSelect: (ColorPalette) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(GlassSurface, CircleShape)
                        .border(1.dp, GlassBorder, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            GlassRow(
                label = "Haptic Feedback",
                content = {
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = onHapticToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AccentPrimary,
                            checkedTrackColor = AccentPrimary.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextPrimary.copy(alpha = 0.6f),
                            uncheckedTrackColor = TextPrimary.copy(alpha = 0.2f)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlassRow(
                label = "Fair Shapes",
                content = {
                    Switch(
                        checked = easyShapes,
                        onCheckedChange = onEasyShapesToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AccentPrimary,
                            checkedTrackColor = AccentPrimary.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextPrimary.copy(alpha = 0.6f),
                            uncheckedTrackColor = TextPrimary.copy(alpha = 0.2f)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Color Palette",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ColorPalette.entries.forEach { palette ->
                PaletteRow(
                    palette = palette,
                    selected = palette == selectedPalette,
                    onClick = { onPaletteSelect(palette) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun GlassRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassSurface, RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        content()
    }
}

@Composable
private fun PaletteRow(
    palette: ColorPalette,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = paletteOf(palette).toList()
    val label = when (palette) {
        ColorPalette.JEWEL -> "Jewel"
        ColorPalette.EARTHY -> "Earthy"
        ColorPalette.PASTEL -> "Pastel"
        ColorPalette.NEON -> "Neon"
        ColorPalette.WOOD -> "Wood"
    }

    val bgColor = if (selected) GlassSurface.copy(alpha = 0.25f) else GlassSurface.copy(alpha = 0.1f)
    val borderColor = if (selected) AccentPrimary else GlassBorder

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(if (selected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = AccentPrimary,
                unselectedColor = TextPrimary.copy(alpha = 0.6f)
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) TextPrimary else TextPrimary.copy(alpha = 0.8f),
            modifier = Modifier.width(68.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            colors.forEach { color ->
                ColorSwatch(color = color, selected = selected)
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, selected: Boolean) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .then(
                if (selected) {
                    Modifier.border(1.5.dp, TextPrimary, RoundedCornerShape(6.dp))
                } else {
                    Modifier
                }
            )
    )
}