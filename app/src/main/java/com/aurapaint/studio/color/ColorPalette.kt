package com.aurapaint.studio.color

data class ColorPalette(
    val id: String,
    val name: String,
    val colors: List<Int>,
    val isCustom: Boolean = false
)
