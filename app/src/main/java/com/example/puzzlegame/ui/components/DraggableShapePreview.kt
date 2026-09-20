package com.example.puzzlegame.ui.components

import androidx.compose.foundation.layout.aspectRatio

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.model.Shape


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.puzzlegame.ui.theme.GlassSurface
import com.example.puzzlegame.ui.theme.GlassBorder

@Composable
fun DraggableShapePreview(
    shape: Shape?,
    onDragStart: (Shape) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDoubleTap: () -> Unit,
    isDragging: Boolean,
    dimmed: Boolean = false,
    modifier: Modifier = Modifier
) {
    var positionInRoot by remember { mutableStateOf(Offset.Zero) }
    val boxShape = RoundedCornerShape(16.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .aspectRatio(1f)
            .background(GlassSurface, boxShape)
            .border(1.dp, GlassBorder, boxShape)
            .onGloballyPositioned { coords ->
                positionInRoot = coords.positionInRoot()
            }
            .pointerInput(shape) {
                if (shape == null) return@pointerInput
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() }
                )
            }
            .pointerInput(shape) {
                if (shape == null) return@pointerInput
                detectDragGestures(
                    onDragStart = { startOffset ->
                        onDragStart(shape)
                        onDrag(positionInRoot + startOffset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onDrag(positionInRoot + change.position)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            }
            .graphicsLayer {
                alpha = if (isDragging) 0f else 1f
            }
    ) {
        ShapePreview(shape = shape, dimmed = dimmed)
    }
}