package com.aurapaint.studio.vector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

class VectorRenderer {
    private val strokePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val androidPath = Path()

    fun render(canvas: Canvas, vectorPath: VectorPath, alphaMultiplier: Float = 1.0f) {
        if (vectorPath.points.isEmpty()) return

        androidPath.reset()
        val first = vectorPath.points[0]
        androidPath.moveTo(first.x, first.y)

        for (i in 1 until vectorPath.points.size) {
            val prev = vectorPath.points[i - 1]
            val curr = vectorPath.points[i]

            val c1x = prev.handleOutX ?: prev.x
            val c1y = prev.handleOutY ?: prev.y
            val c2x = curr.handleInX ?: curr.x
            val c2y = curr.handleInY ?: curr.y

            if (prev.handleOutX != null || curr.handleInX != null) {
                androidPath.cubicTo(c1x, c1y, c2x, c2y, curr.x, curr.y)
            } else {
                androidPath.lineTo(curr.x, curr.y)
            }
        }

        if (vectorPath.isClosed) {
            androidPath.close()
        }

        // Fill pass
        if (vectorPath.fillColor != Color.TRANSPARENT) {
            fillPaint.color = vectorPath.fillColor
            fillPaint.alpha = ((Color.alpha(vectorPath.fillColor) * alphaMultiplier)).toInt().coerceIn(0, 255)
            canvas.drawPath(androidPath, fillPaint)
        }

        // Stroke pass
        if (vectorPath.strokeWidth > 0f && vectorPath.strokeColor != Color.TRANSPARENT) {
            strokePaint.color = vectorPath.strokeColor
            strokePaint.alpha = ((Color.alpha(vectorPath.strokeColor) * alphaMultiplier)).toInt().coerceIn(0, 255)
            strokePaint.strokeWidth = vectorPath.strokeWidth
            canvas.drawPath(androidPath, strokePaint)
        }
    }
}
