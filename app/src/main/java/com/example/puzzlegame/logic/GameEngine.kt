package com.example.puzzlegame.logic

import com.duddletech.blockpuzzlegame.model.Cell
import com.duddletech.blockpuzzlegame.model.CellOffset
import com.duddletech.blockpuzzlegame.model.GameState.Companion.GRID_SIZE
import com.duddletech.blockpuzzlegame.model.Grid
import com.duddletech.blockpuzzlegame.model.Shape
import com.duddletech.blockpuzzlegame.model.ShapeTemplates
import kotlin.random.Random

object GameEngine {

    private inline fun Grid.mutated(
        mutate: (MutableList<MutableList<Cell>>) -> Unit
    ): Grid {
        val copy = map { it.toMutableList() }.toMutableList()
        mutate(copy)
        return copy.map { it.toList() }
    }

    fun canPlace(grid: Grid, shape: Shape, row: Int, col: Int): Boolean =
        shape.cells.all { offset ->
            val r = row + offset.row
            val c = col + offset.col
            r in 0 until GRID_SIZE && c in 0 until GRID_SIZE && !grid[r][c].filled
        }

    fun placeShape(grid: Grid, shape: Shape, row: Int, col: Int): Grid =
        grid.mutated { mutable ->
            for (offset in shape.cells) {
                mutable[row + offset.row][col + offset.col] = Cell(filled = true, color = shape.color)
            }
        }

    fun findCompleteLines(grid: Grid): CompleteLines {
        val rows = grid.indices.filter { r ->
            grid[r].all { it.filled }
        }.toSet()
        val cols = grid[0].indices.filter { c ->
            grid.all { row -> row[c].filled }
        }.toSet()
        return CompleteLines(rows, cols)
    }

    fun clearLines(grid: Grid, lines: CompleteLines): ClearResult {
        if (lines.isEmpty) return ClearResult(grid, 0)

        val clearedCells = lines.toCellSet(GRID_SIZE)

        val newGrid = grid.mutated { mutable ->
            for ((r, c) in clearedCells) {
                mutable[r][c] = Cell()
            }
        }

        val lineCount = lines.rows.size + lines.cols.size
        val multiplier = lineCount * (lineCount + 1) / 2  // triangular number
        val points = clearedCells.size * 10 * multiplier

        return ClearResult(newGrid, points)
    }

    fun canFitAnywhere(grid: Grid, shape: Shape): Boolean =
        grid.indices.any { r ->
            grid[0].indices.any { c ->
                canPlace(grid, shape, r, c)
            }
        }

    fun isGameOver(grid: Grid, shapes: List<Shape?>): Boolean =
        shapes.none { it != null && canFitAnywhere(grid, it) }

    fun generateShapeTriple(
        grid: Grid,
        ensureFit: Boolean = true,
        random: Random = Random,
        maxAttempts: Int = 100
    ): List<Shape> =
        List(3) {
            var shape: Shape
            var attempts = 0
            do {
                val template = ShapeTemplates.ALL.random(random)
                val color = ShapeTemplates.COLORS.random(random)
                shape = (0 until random.nextInt(template.rotations))
                    .fold(template.toShape(color)) { s, _ -> s.rotateCW() }
                attempts++
            } while (ensureFit && !canFitAnywhere(grid, shape) && attempts < maxAttempts)
            shape
        }

    fun placementPoints(shape: Shape): Int = shape.cells.size
}

data class CompleteLines(
    val rows: Set<Int> = emptySet(),
    val cols: Set<Int> = emptySet()
) {
    val isEmpty: Boolean get() = rows.isEmpty() && cols.isEmpty()
    val isNotEmpty: Boolean get() = !isEmpty

    fun toCellSet(gridSize: Int): Set<CellOffset> = buildSet {
        for (r in rows) {
            for (c in 0 until gridSize) add(CellOffset(r, c))
        }
        for (c in cols) {
            for (r in 0 until gridSize) add(CellOffset(r, c))
        }
    }
}

data class ClearResult(
    val grid: Grid,
    val points: Int
)