package com.aurapaint.studio.brush

import com.aurapaint.studio.core.util.MathUtils
import java.util.LinkedList

data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float = 1.0f,
    val tiltX: Float = 0.0f,
    val tiltY: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis()
)

class Stabilizer(
    private var level: Float = 3.0f
) {
    private val history = LinkedList<StrokePoint>()
    private var lastFilteredPoint: StrokePoint? = null

    fun setLevel(newLevel: Float) {
        this.level = MathUtils.clamp(newLevel, 0.0f, 10.0f)
    }

    fun reset() {
        history.clear()
        lastFilteredPoint = null
    }

    /**
     * Filters an input point using dynamic exponential moving average based on stabilization level.
     */
    fun filter(raw: StrokePoint): StrokePoint {
        if (level <= 0.1f) {
            lastFilteredPoint = raw
            return raw
        }

        history.add(raw)
        val maxHistory = (level * 2).toInt().coerceIn(2, 16)
        while (history.size > maxHistory) {
            history.removeFirst()
        }

        var sumX = 0f
        var sumY = 0f
        var sumPressure = 0f
        var totalWeight = 0f

        var weight = 1.0f
        for (pt in history) {
            sumX += pt.x * weight
            sumY += pt.y * weight
            sumPressure += pt.pressure * weight
            totalWeight += weight
            weight *= 1.35f
        }

        val smoothedX = sumX / totalWeight
        val smoothedY = sumY / totalWeight
        val smoothedPressure = sumPressure / totalWeight

        val result = StrokePoint(
            x = smoothedX,
            y = smoothedY,
            pressure = smoothedPressure,
            tiltX = raw.tiltX,
            tiltY = raw.tiltY,
            timestamp = raw.timestamp
        )
        lastFilteredPoint = result
        return result
    }
}
