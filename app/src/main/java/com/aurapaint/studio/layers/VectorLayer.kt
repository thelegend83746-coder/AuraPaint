package com.aurapaint.studio.layers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.aurapaint.studio.core.util.BitmapUtils
import com.aurapaint.studio.vector.VectorPath
import com.aurapaint.studio.vector.VectorRenderer
import java.util.UUID

class VectorLayer(
    override val id: String = UUID.randomUUID().toString(),
    override var name: String = "Vector Layer",
    val width: Int,
    val height: Int
) : Layer {

    override var isVisible: Boolean = true
    override var opacity: Float = 1.0f
    override var isLocked: Boolean = false
    override var isAlphaLocked: Boolean = false
    override var isClippingMask: Boolean = false
    override var blendMode: BlendMode = BlendMode.NORMAL

    val paths: MutableList<VectorPath> = mutableListOf()
    private val vectorRenderer = VectorRenderer()
    private var thumbnail: Bitmap? = null
    private var isThumbnailDirty: Boolean = true

    fun addPath(path: VectorPath) {
        paths.add(path)
        isThumbnailDirty = true
    }

    fun removePath(path: VectorPath) {
        paths.remove(path)
        isThumbnailDirty = true
    }

    fun renderToCanvas(canvas: Canvas) {
        if (!isVisible) return
        for (path in paths) {
            vectorRenderer.render(canvas, path, opacity)
        }
    }

    override fun getThumbnail(): Bitmap {
        if (thumbnail == null || isThumbnailDirty) {
            thumbnail?.recycle()
            val thumb = BitmapUtils.createEmptyBitmap(128, 128)
            val thumbCanvas = Canvas(thumb)
            val scale = 128f / maxOf(width, height)
            thumbCanvas.scale(scale, scale)
            renderToCanvas(thumbCanvas)
            thumbnail = thumb
            isThumbnailDirty = false
        }
        return thumbnail!!
    }

    override fun clear() {
        paths.clear()
        isThumbnailDirty = true
    }

    override fun duplicate(): VectorLayer {
        val dup = VectorLayer(
            name = "$name (Copy)",
            width = width,
            height = height
        )
        dup.isVisible = isVisible
        dup.opacity = opacity
        dup.blendMode = blendMode
        for (p in paths) {
            dup.addPath(p.duplicate())
        }
        return dup
    }

    override fun recycle() {
        thumbnail?.let {
            if (!it.isRecycled) it.recycle()
        }
        thumbnail = null
        paths.clear()
    }
}
