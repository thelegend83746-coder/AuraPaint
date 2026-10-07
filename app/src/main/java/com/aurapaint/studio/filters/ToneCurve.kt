package com.aurapaint.studio.filters

import com.aurapaint.studio.core.util.MathUtils

data class CurveControlPoint(
    var x: Float, // 0..255
    var y: Float  // 0..255
)

class ToneCurve {
    val masterPoints = mutableListOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f))
    val redPoints = mutableListOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f))
    val greenPoints = mutableListOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f))
    val bluePoints = mutableListOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f))

    private val masterLut = IntArray(256)
    private val redLut = IntArray(256)
    private val greenLut = IntArray(256)
    private val blueLut = IntArray(256)

    init {
        buildAllLuts()
    }

    fun reset() {
        masterPoints.clear()
        masterPoints.addAll(listOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f)))
        redPoints.clear()
        redPoints.addAll(listOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f)))
        greenPoints.clear()
        greenPoints.addAll(listOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f)))
        bluePoints.clear()
        bluePoints.addAll(listOf(CurveControlPoint(0f, 0f), CurveControlPoint(255f, 255f)))
        buildAllLuts()
    }

    fun buildAllLuts() {
        buildLut(masterPoints, masterLut)
        buildLut(redPoints, redLut)
        buildLut(greenPoints, greenLut)
        buildLut(bluePoints, blueLut)
    }

    private fun buildLut(points: List<CurveControlPoint>, lut: IntArray) {
        val sorted = points.sortedBy { it.x }
        if (sorted.isEmpty()) {
            for (i in 0..255) lut[i] = i
            return
        }

        for (i in 0..255) {
            val x = i.toFloat()
            if (x <= sorted.first().x) {
                lut[i] = MathUtils.clamp(sorted.first().y.toInt(), 0, 255)
            } else if (x >= sorted.last().x) {
                lut[i] = MathUtils.clamp(sorted.last().y.toInt(), 0, 255)
            } else {
                // Find bounding segments
                var p1 = sorted[0]
                var p2 = sorted[1]
                for (j in 0 until sorted.size - 1) {
                    if (x >= sorted[j].x && x <= sorted[j + 1].x) {
                        p1 = sorted[j]
                        p2 = sorted[j + 1]
                        break
                    }
                }
                val t = (x - p1.x) / (p2.x - p1.x).coerceAtLeast(0.001f)
                val y = MathUtils.lerp(p1.y, p2.y, t)
                lut[i] = MathUtils.clamp(y.toInt(), 0, 255)
            }
        }
    }

    fun mapPixel(color: Int): Int {
        val a = (color ushr 24) and 0xFF
        var r = (color ushr 16) and 0xFF
        var g = (color ushr 8) and 0xFF
        var b = color and 0xFF

        // Master LUT
        r = masterLut[r]
        g = masterLut[g]
        b = masterLut[b]

        // Channel LUTs
        r = redLut[r]
        g = greenLut[g]
        b = blueLut[b]

        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
