package com.example.puzzlegame.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import com.example.puzzlegame.model.BlockColor
import com.example.puzzlegame.model.Cell
import com.example.puzzlegame.model.CellOffset
import com.example.puzzlegame.model.GameState
import com.example.puzzlegame.model.GameState.Companion.GRID_SIZE
import com.example.puzzlegame.model.Grid
import com.example.puzzlegame.model.Shape
import com.example.puzzlegame.ui.theme.PuzzleGameTheme
import com.example.puzzlegame.ui.theme.BackgroundDark
import com.example.puzzlegame.ui.theme.GlassSurface
import com.example.puzzlegame.ui.theme.GlassBorder
import com.example.puzzlegame.ui.theme.GridLine
import com.example.puzzlegame.ui.theme.CellEmpty
import com.example.puzzlegame.ui.theme.toComposeColor

private const val CORNER_BOARD = 0.08f
private const val CORNER_LARGE = 0.15f
private const val CORNER_MEDIUM = 0.12f
private const val CORNER_SMALL = 0.06f

@Composable
fun GameGrid(
    grid: Grid,
    clearingCells: Set<CellOffset> = emptySet(),
    highlightCells: Set<CellOffset> = emptySet(),
    ghostShape: Shape? = null,
    ghostRow: Int = -1,
    ghostCol: Int = -1,
    ghostValid: Boolean = false,
    modifier: Modifier = Modifier,
    onGridLayout: ((gridOffset: Offset, cellSizePx: Float) -> Unit)? = null
) {
    val clearAnim = remember { Animatable(0f) }

    LaunchedEffect(clearingCells) {
        if (clearingCells.isNotEmpty()) {
            clearAnim.snapTo(0f)
            clearAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 550, easing = LinearEasing)
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val animProgress = clearAnim.value
        val padding = size.width * 0.02f
        val boardSize = size.width - padding * 2
        val cellSize = boardSize / GRID_SIZE

        onGridLayout?.invoke(Offset(padding, padding), cellSize)

        drawRoundRect(
            color = GlassSurface,
            topLeft = Offset(padding, padding),
            size = Size(boardSize, boardSize),
            cornerRadius = CornerRadius(cellSize * CORNER_BOARD)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.05f),
            topLeft = Offset(padding + 2f, padding + 2f),
            size = Size(boardSize - 4f, boardSize * 0.2f),
            cornerRadius = CornerRadius(cellSize * CORNER_BOARD)
        )
        drawRoundRect(
            color = GlassBorder,
            topLeft = Offset(padding, padding),
            size = Size(boardSize, boardSize),
            cornerRadius = CornerRadius(cellSize * CORNER_BOARD),
            style = Stroke(width = 1.5f)
        )

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                val x = padding + col * cellSize
                val y = padding + row * cellSize
                val cell = grid[row][col]
                val isClearing = CellOffset(row, col) in clearingCells

                if (isClearing && cell.filled) {
                    if (animProgress <= 150f / 550f) {
                        val flashFraction = animProgress / (150f / 550f)
                        val baseColor = cell.color.toComposeColor()
                        val flashColor = lerp(baseColor, Color.White, flashFraction)
                        drawFilledCell(x, y, cellSize, flashColor)
                    } else {
                        val fadeFraction = (animProgress - 150f / 550f) / (1f - 150f / 550f)
                        val alpha = 1f - fadeFraction
                        drawFilledCell(x, y, cellSize, Color.White.copy(alpha = alpha))
                    }
                } else if (cell.filled) {
                    val isHighlighted = CellOffset(row, col) in highlightCells
                    val color = if (isHighlighted) Color.White else cell.color.toComposeColor()
                    drawFilledCell(x, y, cellSize, color)
                } else if (CellOffset(row, col) in highlightCells) {
                    drawFilledCell(x, y, cellSize, Color.White.copy(alpha = 0.5f))
                } else {
                    drawEmptyCell(x, y, cellSize)
                }
            }
        }

        if (ghostShape != null && ghostRow >= 0 && ghostCol >= 0 && ghostValid) {
            val outlineColor = ghostShape.color.toComposeColor().copy(alpha = 0.8f)
            for (offset in ghostShape.cells) {
                val r = ghostRow + offset.row
                val c = ghostCol + offset.col
                if (r in 0 until GRID_SIZE && c in 0 until GRID_SIZE) {
                    val x = padding + c * cellSize
                    val y = padding + r * cellSize
                    drawGhostCell(x, y, cellSize, outlineColor)
                }
            }
        }

        for (i in 0..GRID_SIZE) {
            val pos = padding + i * cellSize
            drawLine(
                color = GridLine,
                start = Offset(padding, pos),
                end = Offset(padding + boardSize, pos),
                strokeWidth = if (i % GRID_SIZE == 0) 2f else 1f
            )
            drawLine(
                color = GridLine,
                start = Offset(pos, padding),
                end = Offset(pos, padding + boardSize),
                strokeWidth = if (i % GRID_SIZE == 0) 2f else 1f
            )
        }
    }
}

private fun DrawScope.drawEmptyCell(x: Float, y: Float, cellSize: Float) {
    val inset = cellSize * 0.08f
    drawRoundRect(
        color = CellEmpty,
        topLeft = Offset(x + inset, y + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = CornerRadius(cellSize * CORNER_MEDIUM)
    )
    drawRoundRect(
        color = GridLine,
        topLeft = Offset(x + inset, y + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = CornerRadius(cellSize * CORNER_MEDIUM),
        style = Stroke(width = 1f)
    )
}

internal fun DrawScope.drawFilledCell(
    x: Float, y: Float, cellSize: Float, color: Color, insetFraction: Float = 0.05f
) {
    val inset = cellSize * insetFraction
    
    drawRoundRect(
        color = color.copy(alpha = 0.5f),
        topLeft = Offset(x + inset, y + inset + cellSize * 0.05f),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = CornerRadius(cellSize * CORNER_MEDIUM)
    )
    
    drawRoundRect(
        color = color,
        topLeft = Offset(x + inset, y + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = CornerRadius(cellSize * CORNER_MEDIUM)
    )
    
    drawRoundRect(
        color = Color.White.copy(alpha = 0.25f),
        topLeft = Offset(x + inset + 2f, y + inset + 2f),
        size = Size(cellSize - inset * 2 - 4f, cellSize * 0.2f),
        cornerRadius = CornerRadius(cellSize * CORNER_SMALL)
    )
    
    drawRoundRect(
        color = Color.White.copy(alpha = 0.4f),
        topLeft = Offset(x + inset, y + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = CornerRadius(cellSize * CORNER_MEDIUM),
        style = Stroke(width = 1f)
    )
}

private fun DrawScope.drawGhostCell(x: Float, y: Float, cellSize: Float, color: Color) {
    val inset = cellSize * 0.06f
    val strokeWidth = 3f
    drawRoundRect(
        color = color.copy(alpha = 0.2f),
        topLeft = Offset(x + inset, y + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = CornerRadius(cellSize * CORNER_MEDIUM)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(x + inset, y + inset),
        size = Size(cellSize - inset * 2, cellSize - inset * 2),
        cornerRadius = CornerRadius(cellSize * CORNER_MEDIUM),
        style = Stroke(width = strokeWidth)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF3E2723)
@Composable
private fun GameGridPreview() {
    val grid = GameState.emptyGrid().toMutableList().map { it.toMutableList() }.also { g ->
        g[0][0] = Cell(true, BlockColor.RED)
        g[0][1] = Cell(true, BlockColor.RED)
        g[1][0] = Cell(true, BlockColor.RED)
        g[3][3] = Cell(true, BlockColor.BLUE)
        g[3][4] = Cell(true, BlockColor.BLUE)
        g[3][5] = Cell(true, BlockColor.BLUE)
        g[4][4] = Cell(true, BlockColor.BLUE)
        g[6][1] = Cell(true, BlockColor.ORANGE)
        g[6][2] = Cell(true, BlockColor.ORANGE)
        g[7][1] = Cell(true, BlockColor.ORANGE)
        g[7][2] = Cell(true, BlockColor.ORANGE)
    }
    val ghost = Shape(listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 0)), BlockColor.GREEN)

    PuzzleGameTheme {
        GameGrid(
            grid = grid,
            ghostShape = ghost,
            ghostRow = 5,
            ghostCol = 5,
            ghostValid = true
        )
    }
}