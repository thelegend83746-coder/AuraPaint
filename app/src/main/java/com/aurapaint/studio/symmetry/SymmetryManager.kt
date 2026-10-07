package com.aurapaint.studio.symmetry

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.aurapaint.studio.brush.StrokePoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin

class SymmetryManager(
    var centerX: Float,
    var centerY: Float
) {
    private val _mode = MutableStateFlow(SymmetryMode.NONE)
    val mode: StateFlow<SymmetryMode> = _mode.asStateFlow()

    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF3D7BFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
        pathEffect = DashPathEffect(floatArrayOf(12f, 8f), 0f)
    }

    private val centerPointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00E5FF.toInt()
        style = Paint.Style.FILL
    }

    fun setMode(newMode: SymmetryMode) {
        _mode.value = newMode
    }

    /**
     * Given a primary stroke point, returns all mirrored points (including the primary one).
     */
    fun getSymmetricPoints(point: StrokePoint): List<StrokePoint> {
        val currentMode = _mode.value
        if (currentMode == SymmetryMode.NONE) return listOf(point)

        val result = mutableListOf<StrokePoint>()
        result.add(point)

        val dx = point.x - centerX
        val dy = point.y - centerY

        when (currentMode) {
            SymmetryMode.VERTICAL -> {
                result.add(point.copy(x = centerX - dx, y = point.y))
            }
            SymmetryMode.HORIZONTAL -> {
                result.add(point.copy(x = point.x, y = centerY - dy))
            }
            SymmetryMode.FOUR_WAY -> {
                result.add(point.copy(x = centerX - dx, y = point.y))
                result.add(point.copy(x = point.x, y = centerY - dy))
                result.add(point.copy(x = centerX - dx, y = centerY - dy))
            }
            SymmetryMode.RADIAL_6, SymmetryMode.RADIAL_8 -> {
                val segments = currentMode.segments
                val angleStep = (2.0 * Math.PI / segments)
                val baseAngle = kotlin.math.atan2(dy.toDouble(), dx.toDouble())
                val radius = kotlin.math.hypot(dx.toDouble(), dy.toDouble())

                for (i in 1 until segments) {
                    val angle = baseAngle + i * angleStep
                    val sx = (centerX + radius * cos(angle)).toFloat()
                    val sy = (centerY + radius * sin(angle)).toFloat()
                    result.add(point.copy(x = sx, y = sy))
                }
            }
            else -> {}
        }
        return result
    }

    fun drawGuides(canvas: Canvas, canvasWidth: Float, canvasHeight: Float) {
        val currentMode = _mode.value
        if (currentMode == SymmetryMode.NONE) return

        when (currentMode) {
            SymmetryMode.VERTICAL -> {
                canvas.drawLine(centerX, 0f, centerX, canvasHeight, guidePaint)
            }
            SymmetryMode.HORIZONTAL -> {
                canvas.drawLine(0f, centerY, canvasWidth, centerY, guidePaint)
            }
            SymmetryMode.FOUR_WAY -> {
                canvas.drawLine(centerX, 0f, centerX, canvasHeight, guidePaint)
                canvas.drawLine(0f, centerY, canvasWidth, centerY, guidePaint)
            }
            SymmetryMode.RADIAL_6, SymmetryMode.RADIAL_8 -> {
                val segments = currentMode.segments
                val angleStep = (2.0 * Math.PI / segments)
                val lineLength = maxOf(canvasWidth, canvasHeight) * 1.5f

                for (i in 0 until segments) {
                    val angle = i * angleStep
                    val x2 = (centerX + lineLength * cos(angle)).toFloat()
                    val y2 = (centerY + lineLength * sin(angle)).toFloat()
                    canvas.drawLine(centerX, centerY, x2, y2, guidePaint)
                }
            }
            else -> {}
        }

        // Draw center pivot knob
        canvas.drawCircle(centerX, centerY, 8f, centerPointPaint)
    }
}
