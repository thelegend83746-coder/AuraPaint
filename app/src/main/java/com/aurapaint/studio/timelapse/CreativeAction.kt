package com.aurapaint.studio.timelapse

sealed class CreativeAction {
    data class Stroke(
        val toolName: String,
        val pointCount: Int,
        val color: Int,
        val timestamp: Long = System.currentTimeMillis()
    ) : CreativeAction()

    data class Shape(
        val shapeName: String,
        val strokeColor: Int,
        val fillColor: Int,
        val timestamp: Long = System.currentTimeMillis()
    ) : CreativeAction()

    data class FloodFill(
        val x: Int,
        val y: Int,
        val color: Int,
        val timestamp: Long = System.currentTimeMillis()
    ) : CreativeAction()

    data class LayerAction(
        val actionType: String,
        val layerName: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : CreativeAction()
}
