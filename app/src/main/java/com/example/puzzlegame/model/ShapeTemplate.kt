package com.example.puzzlegame.model

data class ShapeTemplate(
    val name: String,
    val cells: List<CellOffset>,
    val rotations: Int,
    val weight: Float = 1f
) {
    fun toShape(color: BlockColor): Shape = Shape(cells = cells, color = color)
}