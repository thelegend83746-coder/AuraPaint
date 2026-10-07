package com.aurapaint.studio.animation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

class OnionSkin {
    var isEnabled: Boolean = true
    var prevFramesCount: Int = 2
    var nextFramesCount: Int = 1
    var opacity: Float = 0.35f

    // Standard animation tints: preceding = red/orange, succeeding = blue/green
    private val prevTintFilter: ColorFilter = PorterDuffColorFilter(0xFFFF4565.toInt(), PorterDuff.Mode.SRC_ATOP)
    private val nextTintFilter: ColorFilter = PorterDuffColorFilter(0xFF00E5FF.toInt(), PorterDuff.Mode.SRC_ATOP)

    private val prevPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        colorFilter = prevTintFilter
    }

    private val nextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        colorFilter = nextTintFilter
    }

    fun render(
        canvas: Canvas,
        frames: List<AnimationFrame>,
        currentIndex: Int
    ) {
        if (!isEnabled || frames.isEmpty()) return

        // Render preceding frames
        for (offset in prevFramesCount downTo 1) {
            val idx = currentIndex - offset
            if (idx in frames.indices) {
                val frame = frames[idx]
                val fade = (1.0f - (offset - 1) * 0.35f).coerceAtLeast(0.2f)
                prevPaint.alpha = ((opacity * fade) * 255).toInt()
                canvas.drawBitmap(frame.bitmap, 0f, 0f, prevPaint)
            }
        }

        // Render succeeding frames
        for (offset in 1..nextFramesCount) {
            val idx = currentIndex + offset
            if (idx in frames.indices) {
                val frame = frames[idx]
                val fade = (1.0f - (offset - 1) * 0.35f).coerceAtLeast(0.2f)
                nextPaint.alpha = ((opacity * fade) * 255).toInt()
                canvas.drawBitmap(frame.bitmap, 0f, 0f, nextPaint)
            }
        }
    }
}
