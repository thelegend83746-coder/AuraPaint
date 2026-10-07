package com.aurapaint.studio.shapes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

class ShapeTool {
    var currentShape: ShapeType = ShapeType.RECTANGLE
    var strokeWidth: Float = 6.0f
    var strokeColor: Int = Color.BLACK
    var fillColor: Int = Color.TRANSPARENT
    var isFilled: Boolean = false

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val shapePath = Path()

    fun render(
        canvas: Canvas,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ) {
        shapePath.reset()
        val left = minOf(startX, endX)
        val top = minOf(startY, endY)
        val right = maxOf(startX, endX)
        val bottom = maxOf(startY, endY)
        val width = right - left
        val height = bottom - top

        when (currentShape) {
            ShapeType.LINE -> {
                shapePath.moveTo(startX, startY)
                shapePath.lineTo(endX, endY)
            }
            ShapeType.RECTANGLE -> {
                shapePath.addRect(left, top, right, bottom, Path.Direction.CW)
            }
            ShapeType.ROUNDED_RECT -> {
                val rx = (width * 0.15f).coerceAtMost(32f)
                val ry = (height * 0.15f).coerceAtMost(32f)
                shapePath.addRoundRect(RectF(left, top, right, bottom), rx, ry, Path.Direction.CW)
            }
            ShapeType.ELLIPSE -> {
                shapePath.addOval(RectF(left, top, right, bottom), Path.Direction.CW)
            }
            ShapeType.POLYGON -> {
                // Regular hexagon
                val cx = (left + right) / 2f
                val cy = (top + bottom) / 2f
                val radius = minOf(width, height) / 2f
                for (i in 0 until 6) {
                    val angle = (i * 60 - 30) * (Math.PI / 180.0)
                    val px = (cx + radius * cos(angle)).toFloat()
                    val py = (cy + radius * sin(angle)).toFloat()
                    if (i == 0) shapePath.moveTo(px, py) else shapePath.lineTo(px, py)
                }
                shapePath.close()
            }
            ShapeType.STAR -> {
                // 5-point star
                val cx = (left + right) / 2f
                val cy = (top + bottom) / 2f
                val outerR = minOf(width, height) / 2f
                val innerR = outerR * 0.45f
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) outerR else innerR
                    val angle = (i * 36 - 90) * (Math.PI / 180.0)
                    val px = (cx + r * cos(angle)).toFloat()
                    val py = (cy + r * sin(angle)).toFloat()
                    if (i == 0) shapePath.moveTo(px, py) else shapePath.lineTo(px, py)
                }
                shapePath.close()
            }
            ShapeType.ARROW -> {
                // Directed arrow from start to end
                val dx = endX - startX
                val dy = endY - startY
                val angle = kotlin.math.atan2(dy, dx)
                val arrowHeadLen = (strokeWidth * 3.5f).coerceAtLeast(24f)

                shapePath.moveTo(startX, startY)
                shapePath.lineTo(endX, endY)

                val x1 = (endX - arrowHeadLen * cos(angle - Math.PI / 6)).toFloat()
                val y1 = (endY - arrowHeadLen * sin(angle - Math.PI / 6)).toFloat()
                val x2 = (endX - arrowHeadLen * cos(angle + Math.PI / 6)).toFloat()
                val y2 = (endY - arrowHeadLen * sin(angle + Math.PI / 6)).toFloat()

                shapePath.moveTo(x1, y1)
                shapePath.lineTo(endX, endY)
                shapePath.lineTo(x2, y2)
            }
        }

        // Draw fill if active
        if (isFilled && currentShape != ShapeType.LINE && currentShape != ShapeType.ARROW) {
            fillPaint.color = fillColor
            canvas.drawPath(shapePath, fillPaint)
        }

        // Draw outline stroke
        if (strokeWidth > 0f) {
            strokePaint.color = strokeColor
            strokePaint.strokeWidth = strokeWidth
            canvas.drawPath(shapePath, strokePaint)
        }
    }
}
