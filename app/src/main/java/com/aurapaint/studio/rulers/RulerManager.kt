package com.aurapaint.studio.rulers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.aurapaint.studio.brush.StrokePoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class RulerManager(
    var canvasWidth: Float,
    var canvasHeight: Float
) {
    private val _type = MutableStateFlow(RulerType.NONE)
    val type: StateFlow<RulerType> = _type.asStateFlow()

    // Straight ruler points
    var lineStartX = 100f
    var lineStartY = canvasHeight / 2f
    var lineEndX = canvasWidth - 100f
    var lineEndY = canvasHeight / 2f

    // Circle guide parameters
    var circleCenterX = canvasWidth / 2f
    var circleCenterY = canvasHeight / 2f
    var circleRadius = 250f

    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF8A5CFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
    }

    private val handlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00E5FF.toInt()
        style = Paint.Style.FILL
    }

    fun setType(newType: RulerType) {
        _type.value = newType
    }

    /**
     * Snaps stroke point to the active ruler constraint.
     */
    fun snap(point: StrokePoint): StrokePoint {
        return when (_type.value) {
            RulerType.STRAIGHT_LINE -> {
                // Project point onto line
                val dx = lineEndX - lineStartX
                val dy = lineEndY - lineStartY
                val magSq = dx * dx + dy * dy
                if (magSq == 0f) return point

                val u = ((point.x - lineStartX) * dx + (point.y - lineStartY) * dy) / magSq
                val snappedX = lineStartX + u * dx
                val snappedY = lineStartY + u * dy
                point.copy(x = snappedX, y = snappedY)
            }
            RulerType.CIRCLE -> {
                // Project point onto circle circumference
                val dx = point.x - circleCenterX
                val dy = point.y - circleCenterY
                val angle = atan2(dy.toDouble(), dx.toDouble())
                val snappedX = (circleCenterX + circleRadius * cos(angle)).toFloat()
                val snappedY = (circleCenterY + circleRadius * sin(angle)).toFloat()
                point.copy(x = snappedX, y = snappedY)
            }
            else -> point
        }
    }

    fun drawGuides(canvas: Canvas) {
        when (_type.value) {
            RulerType.STRAIGHT_LINE -> {
                canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, guidePaint)
                canvas.drawCircle(lineStartX, lineStartY, 10f, handlePaint)
                canvas.drawCircle(lineEndX, lineEndY, 10f, handlePaint)
            }
            RulerType.CIRCLE -> {
                canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, guidePaint)
                canvas.drawCircle(circleCenterX, circleCenterY, 6f, handlePaint)
                canvas.drawCircle(circleCenterX + circleRadius, circleCenterY, 10f, handlePaint)
            }
            RulerType.PERSPECTIVE -> {
                // Horizon line and 2 vanishing points
                val horizonY = canvasHeight * 0.45f
                canvas.drawLine(0f, horizonY, canvasWidth, horizonY, guidePaint)
                val vp1X = canvasWidth * 0.15f
                val vp2X = canvasWidth * 0.85f
                canvas.drawCircle(vp1X, horizonY, 8f, handlePaint)
                canvas.drawCircle(vp2X, horizonY, 8f, handlePaint)
            }
            RulerType.NONE -> {}
        }
    }
}
