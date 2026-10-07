package com.aurapaint.studio.shapes

import android.graphics.Bitmap
import android.graphics.Color
import java.util.LinkedList
import kotlin.math.abs

object FloodFill {

    /**
     * Executes flood fill on a mutable bitmap starting at (startX, startY).
     */
    fun fill(
        bitmap: Bitmap,
        startX: Int,
        startY: Int,
        fillColor: Int,
        tolerance: Int = 20
    ): Boolean {
        val width = bitmap.width
        val height = bitmap.height

        if (startX !in 0 until width || startY !in 0 until height) return false

        val targetColor = bitmap.getPixel(startX, startY)
        if (colorMatch(targetColor, fillColor, 0)) return false

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val queue = LinkedList<Int>()
        val visited = BooleanArray(width * height)

        val startIdx = startY * width + startX
        queue.add(startIdx)
        visited[startIdx] = true

        val tr = Color.red(targetColor)
        val tg = Color.green(targetColor)
        val tb = Color.blue(targetColor)
        val ta = Color.alpha(targetColor)

        while (!queue.isEmpty()) {
            val idx = queue.poll() ?: break
            val x = idx % width
            val y = idx / width

            pixels[idx] = fillColor

            // Check 4 neighbors
            // West
            if (x > 0) {
                val nIdx = idx - 1
                if (!visited[nIdx] && match(pixels[nIdx], tr, tg, tb, ta, tolerance)) {
                    visited[nIdx] = true
                    queue.add(nIdx)
                }
            }
            // East
            if (x < width - 1) {
                val nIdx = idx + 1
                if (!visited[nIdx] && match(pixels[nIdx], tr, tg, tb, ta, tolerance)) {
                    visited[nIdx] = true
                    queue.add(nIdx)
                }
            }
            // North
            if (y > 0) {
                val nIdx = idx - width
                if (!visited[nIdx] && match(pixels[nIdx], tr, tg, tb, ta, tolerance)) {
                    visited[nIdx] = true
                    queue.add(nIdx)
                }
            }
            // South
            if (y < height - 1) {
                val nIdx = idx + width
                if (!visited[nIdx] && match(pixels[nIdx], tr, tg, tb, ta, tolerance)) {
                    visited[nIdx] = true
                    queue.add(nIdx)
                }
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return true
    }

    private fun match(color: Int, tr: Int, tg: Int, tb: Int, ta: Int, tolerance: Int): Boolean {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val a = Color.alpha(color)
        return abs(r - tr) <= tolerance &&
               abs(g - tg) <= tolerance &&
               abs(b - tb) <= tolerance &&
               abs(a - ta) <= tolerance
    }

    private fun colorMatch(c1: Int, c2: Int, tolerance: Int): Boolean {
        return abs(Color.red(c1) - Color.red(c2)) <= tolerance &&
               abs(Color.green(c1) - Color.green(c2)) <= tolerance &&
               abs(Color.blue(c1) - Color.blue(c2)) <= tolerance &&
               abs(Color.alpha(c1) - Color.alpha(c2)) <= tolerance
    }
}
