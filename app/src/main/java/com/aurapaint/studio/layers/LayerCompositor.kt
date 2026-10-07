package com.aurapaint.studio.layers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect

class LayerCompositor(
    private val width: Int,
    private val height: Int
) {
    private val layerPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val clipPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }
    private val srcRect = Rect(0, 0, width, height)
    private val dstRect = Rect(0, 0, width, height)
    private val bgPaint = Paint().apply { style = Paint.Style.FILL }

    // Temporary clip buffer to support nested clipping masks
    private var clipBuffer: Bitmap? = null
    private var clipCanvas: Canvas? = null

    private fun ensureClipBuffer(): Pair<Bitmap, Canvas> {
        if (clipBuffer == null || clipBuffer?.isRecycled == true) {
            clipBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            clipCanvas = Canvas(clipBuffer!!)
        }
        return Pair(clipBuffer!!, clipCanvas!!)
    }

    /**
     * Composites all visible layers onto the destination target canvas.
     */
    fun composite(
        layers: List<Layer>,
        targetCanvas: Canvas,
        backgroundColor: Int = Color.WHITE
    ) {
        // Draw background base within canvas bounds
        if (backgroundColor != Color.TRANSPARENT) {
            bgPaint.color = backgroundColor
            targetCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        } else {
            bgPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            targetCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            bgPaint.xfermode = null
        }

        var previousRasterLayer: RasterLayer? = null

        for (layer in layers) {
            if (!layer.isVisible || layer.opacity <= 0.001f) continue

            layerPaint.alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
            layerPaint.xfermode = layer.blendMode.toXfermode()

            if (layer.isClippingMask && previousRasterLayer != null) {
                // Render clipping mask into temporary buffer
                val (buffer, cCanvas) = ensureClipBuffer()
                buffer.eraseColor(Color.TRANSPARENT)

                when (layer) {
                    is RasterLayer -> {
                        cCanvas.drawBitmap(layer.bitmap, null, dstRect, null)
                    }
                    is VectorLayer -> {
                        layer.renderToCanvas(cCanvas)
                    }
                }

                // Mask with previous layer's alpha
                cCanvas.drawBitmap(previousRasterLayer.bitmap, null, dstRect, clipPaint)

                // Draw clipped result to target
                targetCanvas.drawBitmap(buffer, null, dstRect, layerPaint)
            } else {
                when (layer) {
                    is RasterLayer -> {
                        targetCanvas.drawBitmap(layer.bitmap, null, dstRect, layerPaint)
                        previousRasterLayer = layer
                    }
                    is VectorLayer -> {
                        targetCanvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), layerPaint)
                        layer.renderToCanvas(targetCanvas)
                        targetCanvas.restore()
                        previousRasterLayer = null
                    }
                }
            }
        }
    }

    fun release() {
        clipBuffer?.recycle()
        clipBuffer = null
        clipCanvas = null
    }
}
