package com.example.puzzlegame.model

data class Cell(
    val filled: Boolean = false,
    val color: BlockColor = BlockColor.NONE
)