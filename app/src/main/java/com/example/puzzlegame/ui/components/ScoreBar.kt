package com.example.puzzlegame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlegame.ui.theme.PuzzleGameTheme
import com.example.puzzlegame.ui.theme.TextPrimary
import com.example.puzzlegame.ui.theme.TextSecondary
import com.example.puzzlegame.ui.theme.GlassSurface
import com.example.puzzlegame.ui.theme.GlassBorder
import com.example.puzzlegame.ui.theme.GlowBlue
import com.example.puzzlegame.ui.theme.GlowPurple

@Composable
fun ScoreBar(
    score: Int,
    highScore: Int,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassGradient = Brush.linearGradient(
        colors = listOf(
            GlassSurface,
            GlassSurface.copy(alpha = 0.05f),
            GlowBlue.copy(alpha = 0.15f),
            GlowPurple.copy(alpha = 0.2f)
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black, spotColor = GlowBlue)
            .background(glassGradient, RoundedCornerShape(20.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreColumn(label = "SCORE", value = score, primary = true)
        ScoreColumn(label = "BEST", value = highScore, primary = false)
        
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(44.dp)
                .background(GlassSurface, CircleShape)
                .border(1.dp, GlassBorder, CircleShape)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ScoreColumn(label: String, value: Int, primary: Boolean) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = if (primary) 32.sp else 22.sp,
                fontWeight = if (primary) FontWeight.Black else FontWeight.Bold
            ),
            color = if (primary) TextPrimary else TextSecondary
        )
    }
}

@Preview
@Composable
private fun ScoreBarPreview() {
    PuzzleGameTheme {
        ScoreBar(score = 1250, highScore = 3400, onSettingsClick = {})
    }
}