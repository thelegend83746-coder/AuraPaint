package com.aurapaint.studio.brush

data class BrushProperties(
    val size: Float = 24.0f,
    val opacity: Float = 1.0f,
    val flow: Float = 1.0f,
    val spacing: Float = 0.15f,
    val hardness: Float = 0.8f,
    val pressureSize: Boolean = true,
    val pressureOpacity: Boolean = true,
    val pressureSizeAmount: Float = 1.0f,
    val pressureOpacityAmount: Float = 1.0f,
    val scatter: Float = 0.0f,
    val stabilization: Float = 3.0f,
    val grain: Float = 0.0f
)
