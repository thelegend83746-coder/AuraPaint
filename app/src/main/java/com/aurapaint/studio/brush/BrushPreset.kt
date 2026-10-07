package com.aurapaint.studio.brush

data class BrushPreset(
    val id: String,
    val name: String,
    val category: BrushCategory,
    val properties: BrushProperties,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val description: String = ""
)
