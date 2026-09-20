package com.example.puzzlegame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlegame.ui.theme.PuzzleGameTheme
import com.example.puzzlegame.ui.theme.TextPrimary
import com.example.puzzlegame.ui.theme.WarningRed

@Composable
fun GameOverWarningOverlay(
    secondsRemaining: Int,
    dimmed: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val overlayAlpha by animateFloatAsState(
        targetValue = if (dimmed) 0.15f else 1f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "warningAlpha"
    )

    val bannerPulse = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        bannerPulse.animateTo(
            targetValue = 0.45f,
            animationSpec = repeatable(
                iterations = 12,
                animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val punch = remember { Animatable(1.35f) }
    LaunchedEffect(secondsRemaining) {
        punch.snapTo(1.35f)
        punch.animateTo(1f, tween(durationMillis = 350, easing = FastOutSlowInEasing))
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("game_over_warning"),
        contentAlignment = Alignment.Center
    ) {
        val digitDp = minOf(maxWidth.value * 1.05f, maxHeight.value * 0.6f)
        val digitFontSize = with(density) { digitDp.dp.toSp() }

        val digitStyle = TextStyle(
            fontSize = digitFontSize,
            lineHeight = digitFontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier.graphicsLayer {
                scaleX = punch.value
                scaleY = punch.value
                alpha = overlayAlpha
            }
        ) {
            Text(
                text = "$secondsRemaining",
                style = digitStyle.copy(color = WarningRed.copy(alpha = 0.30f))
            )
            Text(
                text = "$secondsRemaining",
                style = digitStyle.copy(
                    color = TextPrimary.copy(alpha = 0.55f),
                    drawStyle = Stroke(width = 6f, join = StrokeJoin.Round)
                )
            )
        }

        Text(
            text = WARNING_TEXT,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = maxHeight * 0.12f)
                .graphicsLayer { alpha = overlayAlpha * bannerPulse.value }
                .background(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
            style = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = WarningRed,
                letterSpacing = 1.sp,
                shadow = Shadow(
                    color = Color(0xCC000000),
                    offset = Offset(2f, 3f),
                    blurRadius = 4f
                )
            ),
            textAlign = TextAlign.Center
        )
    }
}

const val WARNING_TEXT = "Game about to END!"

@Preview(showBackground = true, backgroundColor = 0xFF3E2723)
@Composable
private fun GameOverWarningPreview() {
    PuzzleGameTheme {
        GameOverWarningOverlay(secondsRemaining = 3, dimmed = false)
    }
}