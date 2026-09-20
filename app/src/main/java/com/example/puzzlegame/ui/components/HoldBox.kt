package com.example.puzzlegame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlegame.model.Shape
import com.example.puzzlegame.ui.theme.AccentPrimary
import com.example.puzzlegame.ui.theme.GlassSurface
import com.example.puzzlegame.ui.theme.GlassBorder
import com.example.puzzlegame.ui.theme.TextSecondary

@Composable
fun HoldBox(
    holdShape: Shape?,
    isDragging: Boolean,
    dimmed: Boolean,
    onDragStart: (Shape) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDoubleTap: () -> Unit,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
    modifier: Modifier = Modifier
) {
    var positionInRoot by remember { mutableStateOf(Offset.Zero) }
    val boxShape = RoundedCornerShape(16.dp)
    val borderColor = if (isDragging) AccentPrimary else GlassBorder

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .background(GlassSurface, boxShape)
            .border(1.dp, borderColor, boxShape)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .onGloballyPositioned { coords ->
                positionInRoot = coords.positionInRoot()
                onGloballyPositioned(coords)
            }
            .pointerInput(holdShape) {
                if (holdShape == null) return@pointerInput
                detectTapGestures(onDoubleTap = { onDoubleTap() })
            }
            .pointerInput(holdShape) {
                if (holdShape == null) return@pointerInput
                detectDragGestures(
                    onDragStart = { startOffset ->
                        onDragStart(holdShape)
                        onDrag(positionInRoot + startOffset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onDrag(positionInRoot + change.position)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HOLD",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer {
                    alpha = if (isDragging) 0f else 1f
                }
        ) {
            if (holdShape != null) {
                ShapePreview(
                    shape = holdShape,
                    dimmed = dimmed
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}
