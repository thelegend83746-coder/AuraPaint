package com.aurapaint.studio.selection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

class SelectionMask(
    val canvasWidth: Int,
    val canvasHeight: Int
) {
    val selectionPath = Path()
    val bounds = RectF()
    var isActive: Boolean = false
        private set

    private val marchingAntsPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
        color = Color.WHITE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val marchingAntsBlackPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
        color = Color.BLACK
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 10f)
    }

    fun setRectangle(left: Float, top: Float, right: Float, bottom: Float) {
        selectionPath.reset()
        selectionPath.addRect(
            minOf(left, right),
            minOf(top, bottom),
            maxOf(left, right),
            maxOf(top, bottom),
            Path.Direction.CW
        )
        updateBounds()
        isActive = true
    }

    fun setEllipse(left: Float, top: Float, right: Float, bottom: Float) {
        selectionPath.reset()
        val rect = RectF(
            minOf(left, right),
            minOf(top, bottom),
            maxOf(left, right),
            maxOf(top, bottom)
        )
        selectionPath.addOval(rect, Path.Direction.CW)
        updateBounds()
        isActive = true
    }

    fun setLassoPath(path: Path) {
        selectionPath.reset()
        selectionPath.set(path)
        selectionPath.close()
        updateBounds()
        isActive = true
    }

    private fun updateBounds() {
        selectionPath.computeBounds(bounds, true)
    }

    fun clear() {
        selectionPath.reset()
        bounds.setEmpty()
        isActive = false
    }

    fun invert() {
        if (!isActive) return
        val fullCanvasPath = Path().apply {
            addRect(0f, 0f, canvasWidth.toFloat(), canvasHeight.toFloat(), Path.Direction.CW)
        }
        val invertedPath = Path()
        invertedPath.op(fullCanvasPath, selectionPath, Path.Op.DIFFERENCE)
        selectionPath.set(invertedPath)
        updateBounds()
    }

    fun drawSelectionOverlay(canvas: Canvas, dashPhase: Float = 0f) {
        if (!isActive) return
        marchingAntsPaint.pathEffect = DashPathEffect(floatArrayOf(12f, 12f), dashPhase)
        marchingAntsBlackPaint.pathEffect = DashPathEffect(floatArrayOf(12f, 12f), dashPhase + 12f)
        canvas.drawPath(selectionPath, marchingAntsBlackPaint)
        canvas.drawPath(selectionPath, marchingAntsPaint)
    }
}
