package com.example.puzzlegame.model

typealias Grid = List<List<Cell>>

data class GameState(
    val grid: Grid = emptyGrid(),
    val currentShapes: List<Shape?> = listOf(null, null, null),
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false,
    val clearingCells: Set<CellOffset> = emptySet(),
    val holdShape: Shape? = null
) {
    companion object {
        const val GRID_SIZE = 8

        fun emptyGrid(): Grid =
            List(GRID_SIZE) { List(GRID_SIZE) { Cell() } }
    }
}