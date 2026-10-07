package com.aurapaint.studio.layers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import com.aurapaint.studio.core.util.BitmapUtils
import java.util.UUID

class RasterLayer(
    override val id: String = UUID.randomUUID().toString(),
    override var name: String = "Layer",
    val width: Int,
    val height: Int,
    initialBitmap: Bitmap? = null
) : Layer {

    override var isVisible: Boolean = true
    override var opacity: Float = 1.0f
    override var isLocked: Boolean = false
    override var isAlphaLocked: Boolean = false
    override var isClippingMask: Boolean = false
    override var blendMode: BlendMode = BlendMode.NORMAL

    var bitmap: Bitmap = initialBitmap ?: BitmapUtils.createEmptyBitmap(width, height)
        private set

    var canvas: Canvas = Canvas(bitmap)
        private set

    private var thumbnail: Bitmap? = null
    private var isThumbnailDirty: Boolean = true

    fun markDirty() {
        isThumbnailDirty = true
    }

    override fun getThumbnail(): Bitmap {
        if (thumbnail == null || isThumbnailDirty) {
            thumbnail?.recycle()
            thumbnail = BitmapUtils.createThumbnail(bitmap, 128)
            isThumbnailDirty = false
        }
        return thumbnail!!
    }

    override fun clear() {
        bitmap.eraseColor(Color.TRANSPARENT)
        markDirty()
    }

    fun replaceBitmap(newBitmap: Bitmap) {
        if (bitmap != newBitmap) {
            bitmap.recycle()
            bitmap = newBitmap
            canvas = Canvas(bitmap)
            markDirty()
        }
    }

    override fun duplicate(): RasterLayer {
        val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        return RasterLayer(
            name = "$name (Copy)",
            width = width,
            height = height,
            initialBitmap = copyBitmap
        ).also {
            it.isVisible = isVisible
            it.opacity = opacity
            it.blendMode = blendMode
            it.isAlphaLocked = isAlphaLocked
            it.isClippingMask = isClippingMask
        }
    }

    override fun recycle() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
        thumbnail?.let {
            if (!it.isRecycled) it.recycle()
        }
        thumbnail = null
    }
}
