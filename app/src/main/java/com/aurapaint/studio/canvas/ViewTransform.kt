package com.aurapaint.studio.canvas

import android.graphics.Matrix
import android.graphics.PointF

class ViewTransform(
    val canvasWidth: Int,
    val canvasHeight: Int
) {
    var viewWidth: Int = 0
    var viewHeight: Int = 0

    var zoom: Float = 1.0f
    var panX: Float = 0f
    var panY: Float = 0f
    var rotationDegrees: Float = 0f

    val matrix = Matrix()
    private val inverseMatrix = Matrix()

    fun updateMatrix() {
        matrix.reset()
        val cx = viewWidth / 2f
        val cy = viewHeight / 2f

        matrix.postTranslate(-canvasWidth / 2f, -canvasHeight / 2f)
        matrix.postScale(zoom, zoom)
        matrix.postRotate(rotationDegrees)
        matrix.postTranslate(cx + panX, cy + panY)

        matrix.invert(inverseMatrix)
    }

    /**
     * Converts a screen touch coordinate to canvas space.
     */
    fun screenToCanvas(screenX: Float, screenY: Float): PointF {
        val pts = floatArrayOf(screenX, screenY)
        inverseMatrix.mapPoints(pts)
        return PointF(pts[0], pts[1])
    }

    /**
     * Converts a canvas coordinate to screen space.
     */
    fun canvasToScreen(canvasX: Float, canvasY: Float): PointF {
        val pts = floatArrayOf(canvasX, canvasY)
        matrix.mapPoints(pts)
        return PointF(pts[0], pts[1])
    }

    /**
     * Fits the canvas centered inside current view dimensions.
     */
    fun fitToScreen() {
        if (viewWidth <= 0 || viewHeight <= 0) return
        val padding = 48f
        val availableW = (viewWidth - padding * 2).coerceAtLeast(100f)
        val availableH = (viewHeight - padding * 2).coerceAtLeast(100f)

        val scaleX = availableW / canvasWidth
        val scaleY = availableH / canvasHeight
        zoom = minOf(scaleX, scaleY).coerceIn(0.1f, 10f)

        panX = 0f
        panY = 0f
        rotationDegrees = 0f
        updateMatrix()
    }

    fun resetRotation() {
        rotationDegrees = 0f
        updateMatrix()
    }
}
