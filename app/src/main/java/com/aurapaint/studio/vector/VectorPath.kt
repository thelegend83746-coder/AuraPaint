package com.aurapaint.studio.vector

import android.graphics.Color
import java.util.UUID

data class VectorPath(
    val id: String = UUID.randomUUID().toString(),
    val points: MutableList<VectorPoint> = mutableListOf(),
    var strokeColor: Int = Color.BLACK,
    var strokeWidth: Float = 6.0f,
    var fillColor: Int = Color.TRANSPARENT,
    var isClosed: Boolean = false
) {
    fun duplicate(): VectorPath {
        return VectorPath(
            points = points.map { it.copy() }.toMutableList(),
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
            fillColor = fillColor,
            isClosed = isClosed
        )
    }
}
