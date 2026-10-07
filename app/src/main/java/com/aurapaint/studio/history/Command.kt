package com.aurapaint.studio.history

import android.graphics.Bitmap
import com.aurapaint.studio.layers.RasterLayer

interface Command {
    fun execute()
    fun undo()
    fun redo() = execute()
    fun release()
}

/**
 * High-performance command that records the state of a raster layer before an edit.
 */
class LayerBitmapCommand(
    private val layer: RasterLayer,
    private val beforeBitmap: Bitmap,
    private val afterBitmap: Bitmap
) : Command {

    override fun execute() {
        layer.replaceBitmap(afterBitmap.copy(Bitmap.Config.ARGB_8888, true))
    }

    override fun undo() {
        layer.replaceBitmap(beforeBitmap.copy(Bitmap.Config.ARGB_8888, true))
    }

    override fun release() {
        if (!beforeBitmap.isRecycled) beforeBitmap.recycle()
        if (!afterBitmap.isRecycled) afterBitmap.recycle()
    }
}
