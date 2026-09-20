package com.example.puzzlegame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.model.BlockColor
import com.example.puzzlegame.model.CellOffset
import com.example.puzzlegame.model.Shape
import com.example.puzzlegame.ui.theme.PuzzleGameTheme
import com.example.puzzlegame.ui.theme.toComposeColor

@Composable
fun ShapePreview(
    shape: Shape?,
    modifier: Modifier = Modifier,
    cellSize: Dp = 28.dp,
    dimmed: Boolean = false
) {
    // Fixed canvas size so the Row height doesn't change when a shape is placed
    val maxDim = 5
    val canvasSize = cellSize * maxDim

    if (shape == null) {
        Canvas(modifier = modifier.size(canvasSize)) {}
        return
    }

    val width = shape.width
    val height = shape.height

    Canvas(modifier = modifier.size(canvasSize)) {
        val cellPx = canvasSize.toPx() / maxDim
        // Center the shape within the canvas
        val offsetX = (size.width - width * cellPx) / 2
        val offsetY = (size.height - height * cellPx) / 2
        val color = if (dimmed) {
            shape.color.toComposeColor().copy(alpha = 0.15f)
        } else {
            shape.color.toComposeColor()
        }

        for (cell in shape.cells) {
            val x = offsetX + cell.col * cellPx
            val y = offsetY + cell.row * cellPx
            drawFilledCell(x, y, cellPx, color, insetFraction = 0.08f)
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF3E2723)
@Composable
private fun ShapePreviewDemo() {
    val lShape = Shape(
        cells = listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(2, 1)),
        color = BlockColor.RED
    )
    PuzzleGameTheme {
        ShapePreview(shape = lShape)
    }
}