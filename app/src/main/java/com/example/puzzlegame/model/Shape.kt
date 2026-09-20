package com.example.puzzlegame.model

data class CellOffset(
    val row: Int,
    val col: Int,
)

data class Shape(
    val cells: List<CellOffset>,
    val color: BlockColor,
) {
    val width: Int get() = (cells.maxOfOrNull { it.col } ?: 0) + 1
    val height: Int get() = (cells.maxOfOrNull { it.row } ?: 0) + 1

    fun rotateCW(): Shape {
        val rotated = cells.map { CellOffset(row = it.col, col = -it.row) }
        val minRow = rotated.minOf { it.row }
        val minCol = rotated.minOf { it.col }
        val normalized = rotated.map { CellOffset(row = it.row - minRow, col = it.col - minCol) }
        return copy(cells = normalized)
    }
}