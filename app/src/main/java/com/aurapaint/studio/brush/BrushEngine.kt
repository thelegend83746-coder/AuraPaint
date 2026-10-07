package com.aurapaint.studio.brush

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Shader
import com.aurapaint.studio.core.util.MathUtils
import kotlin.random.Random

class BrushEngine {

    private val dabPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint().apply {
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        style = Paint.Style.STROKE
    }

    private val clearMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private val srcOverMode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

    private var previousPoint: StrokePoint? = null
    private var accumulatedDistance: Float = 0f

    fun startStroke(firstPoint: StrokePoint) {
        previousPoint = firstPoint
        accumulatedDistance = 0f
    }

    fun renderSegment(
        canvas: Canvas,
        currentPoint: StrokePoint,
        properties: BrushProperties,
        brushColor: Int,
        isEraser: Boolean = false,
        zoomScale: Float = 1.0f
    ) {
        val prev = previousPoint ?: currentPoint

        val dist = MathUtils.distance(prev.x, prev.y, currentPoint.x, currentPoint.y)
        val baseSize = properties.size
        val effectivePressure = currentPoint.pressure

        val currentSize = if (properties.pressureSize) {
            val factor = MathUtils.lerp(0.15f, 1.0f, effectivePressure * properties.pressureSizeAmount)
            (baseSize * factor).coerceAtLeast(1.0f)
        } else {
            baseSize
        }

        val stepSpacing = (currentSize * properties.spacing).coerceAtLeast(1.0f)

        // Setup paint modes
        if (isEraser) {
            dabPaint.xfermode = clearMode
            strokePaint.xfermode = clearMode
        } else {
            dabPaint.xfermode = srcOverMode
            strokePaint.xfermode = srcOverMode
        }

        val alphaMultiplier = properties.opacity * properties.flow *
                (if (properties.pressureOpacity) effectivePressure * properties.pressureOpacityAmount else 1.0f)
        val alphaInt = (MathUtils.clamp(alphaMultiplier, 0.01f, 1.0f) * 255).toInt()

        // If spacing is extremely small or brush is hard line, draw connected segments for maximum smoothness
        if (properties.hardness >= 0.85f && properties.scatter <= 0.1f && properties.grain <= 0.1f) {
            strokePaint.color = brushColor
            strokePaint.alpha = alphaInt
            strokePaint.strokeWidth = currentSize
            canvas.drawLine(prev.x, prev.y, currentPoint.x, currentPoint.y, strokePaint)
        } else {
            // Dab interpolation along the trajectory
            val steps = (dist / stepSpacing).toInt().coerceAtLeast(1)
            for (i in 0..steps) {
                val t = i.toFloat() / steps
                var dabX = MathUtils.lerp(prev.x, currentPoint.x, t)
                var dabY = MathUtils.lerp(prev.y, currentPoint.y, t)

                // Scatter calculation
                if (properties.scatter > 0f) {
                    val angle = Random.nextFloat() * 6.28318f
                    val scatterDist = Random.nextFloat() * properties.scatter
                    dabX += kotlin.math.cos(angle) * scatterDist
                    dabY += kotlin.math.sin(angle) * scatterDist
                }

                val dabRadius = currentSize / 2f

                if (properties.hardness < 0.7f && !isEraser) {
                    // Soft edge radial gradient for airbrush / watercolor / charcoal
                    val edgeAlpha = (alphaInt * properties.hardness).toInt()
                    val gradient = RadialGradient(
                        dabX, dabY, dabRadius,
                        intArrayOf(brushColor, (brushColor and 0x00FFFFFF) or (edgeAlpha shl 24), brushColor and 0x00FFFFFF),
                        floatArrayOf(0.0f, properties.hardness, 1.0f),
                        Shader.TileMode.CLAMP
                    )
                    dabPaint.shader = gradient
                    dabPaint.color = brushColor
                    dabPaint.alpha = alphaInt
                    canvas.drawCircle(dabX, dabY, dabRadius, dabPaint)
                    dabPaint.shader = null
                } else {
                    dabPaint.color = brushColor
                    dabPaint.alpha = alphaInt
                    canvas.drawCircle(dabX, dabY, dabRadius, dabPaint)
                }
            }
        }

        previousPoint = currentPoint
    }

    fun endStroke() {
        previousPoint = null
        accumulatedDistance = 0f
    }
}
