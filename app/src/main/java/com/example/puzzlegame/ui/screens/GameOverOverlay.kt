package com.example.puzzlegame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.ui.theme.PuzzleGameTheme
import androidx.compose.foundation.border
import com.example.puzzlegame.ui.theme.TextPrimary
import com.example.puzzlegame.ui.theme.TextSecondary
import com.example.puzzlegame.ui.theme.AccentPrimary
import com.example.puzzlegame.ui.theme.GlassSurface
import com.example.puzzlegame.ui.theme.GlassBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GameOverOverlay(
    score: Int,
    highScore: Int,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNewHighScore = score >= highScore && score > 0

    val scrimAlpha = remember { Animatable(0f) }
    val cardAlpha = remember { Animatable(0f) }
    val cardOffsetY = remember { Animatable(60f) }
    val scoreProgress = remember { Animatable(0f) }
    val highScorePulse = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        suspend fun Animatable<Float, *>.slideTo(
            target: Float, duration: Int = 400
        ) = animateTo(target, tween(duration, easing = FastOutSlowInEasing))

        launch { scrimAlpha.slideTo(0.85f) }
        launch {
            delay(100)
            launch { cardAlpha.slideTo(1f) }
            launch { cardOffsetY.slideTo(0f) }
        }
        launch {
            delay(500)
            scoreProgress.slideTo(1f, duration = 1000)
            if (isNewHighScore) {
                highScorePulse.animateTo(
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    val displayedScore = (score * scoreProgress.value).roundToInt()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = scrimAlpha.value))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isNewHighScore && scoreProgress.value >= 1f) {
            ConfettiEffect()
        }

        Column(
            modifier = Modifier
                .graphicsLayer {
                    alpha = cardAlpha.value
                    translationY = cardOffsetY.value * density
                }
                .background(GlassSurface, RoundedCornerShape(24.dp))
                .border(1.5.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Game Over",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Score: $displayedScore",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )

            if (isNewHighScore) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "New High Score!",
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentPrimary,
                    modifier = Modifier.graphicsLayer {
                        scaleX = highScorePulse.value
                        scaleY = highScorePulse.value
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPrimary,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Play Again",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }
        }
    }
}


internal data class ConfettiParticle(
    val x: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val wobbleSpeed: Float,
    val wobbleAmp: Float,
    val rotation: Float,
    val landingY: Float = 1f
)

internal val confettiColors = listOf(
    Color(0xFF38BDF8),
    Color(0xFF8B5CF6),
    Color(0xFFF43F5E),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
    Color(0xFF0EA5E9),
    Color(0xFFFFFFFF),
)

@Composable
internal fun ConfettiEffect() {
    val density = LocalDensity.current
    val particles = remember {
        val rng = Random(System.nanoTime())
        val numColumns = 25
        val avgSizeDp = 7f
        val slotHeight = (avgSizeDp * 1.4f) / 800f

        val raw = List(50) {
            ConfettiParticle(
                x = rng.nextFloat(),
                speed = 0.5f + rng.nextFloat() * 0.8f,
                size = 4f + rng.nextFloat() * 6f,
                color = confettiColors[rng.nextInt(confettiColors.size)],
                wobbleSpeed = 1.5f + rng.nextFloat() * 2f,
                wobbleAmp = 0.01f + rng.nextFloat() * 0.03f,
                rotation = rng.nextFloat() * 360f
            )
        }

        val sorted = raw.sortedByDescending { it.speed }

        val columnCounts = IntArray(numColumns)
        sorted.map { p ->
            val col = (p.x * numColumns).toInt().coerceIn(0, numColumns - 1)
            val slot = columnCounts[col]
            columnCounts[col]++
            p.copy(landingY = 1f - slot * slotHeight)
        }
    }

    val fallProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fallProgress.animateTo(
            targetValue = 2f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val t = fallProgress.value
        for (p in particles) {
            val particleT = t * p.speed
            val fallingY = -0.1f + particleT * 1.5f
            val landed = fallingY >= p.landingY
            val y = fallingY.coerceAtMost(p.landingY)
            if (y < -0.1f) continue
            val wobble = if (landed) 0f
            else kotlin.math.sin(particleT * p.wobbleSpeed * Math.PI.toFloat() * 2f) * p.wobbleAmp
            val x = p.x + wobble

            val px = x * size.width
            val py = y * size.height
            val sizePx = with(density) { p.size.dp.toPx() }

            drawRect(
                color = p.color,
                topLeft = Offset(px - sizePx / 2, py - sizePx / 2),
                size = Size(sizePx, sizePx * 1.4f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameOverPreview() {
    PuzzleGameTheme {
        GameOverOverlay(score = 1250, highScore = 3400, onNewGame = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun GameOverHighScorePreview() {
    PuzzleGameTheme {
        GameOverOverlay(score = 3500, highScore = 3400, onNewGame = {})
    }
}