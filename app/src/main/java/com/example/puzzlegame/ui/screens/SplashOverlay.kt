package com.example.puzzlegame.ui.screens

import androidx.compose.ui.draw.shadow

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlegame.R
import com.example.puzzlegame.ui.theme.BackgroundDark
import kotlinx.coroutines.delay

@Composable
fun SplashOverlay(onFinished: () -> Unit) {
    val contentAlpha = remember { Animatable(0f) }
    val overlayAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        delay(1500)
        overlayAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(overlayAlpha.value)
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(contentAlpha.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_block_puzzle),
                contentDescription = "PuzzleNest Logo",
                modifier = Modifier
                    .size(96.dp)
                    .shadow(
                        elevation = 32.dp,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        spotColor = com.example.puzzlegame.ui.theme.AccentPrimary,
                        ambientColor = com.example.puzzlegame.ui.theme.AccentSecondary
                    ),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(com.example.puzzlegame.ui.theme.AccentPrimary)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "PuzzleNest",
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = com.example.puzzlegame.ui.theme.TextPrimary,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PUZZLE GAME",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    color = com.example.puzzlegame.ui.theme.TextSecondary,
                    letterSpacing = 4.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}