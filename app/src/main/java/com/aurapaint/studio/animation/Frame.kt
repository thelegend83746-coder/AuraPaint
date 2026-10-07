package com.aurapaint.studio.animation

import android.graphics.Bitmap
import com.aurapaint.studio.core.util.BitmapUtils
import java.util.UUID

data class AnimationFrame(
    val id: String = UUID.randomUUID().toString(),
    var bitmap: Bitmap,
    var durationFrames: Int = 1
) {
    private var thumbnail: Bitmap? = null

    fun getThumbnail(): Bitmap {
        if (thumbnail == null || thumbnail?.isRecycled == true) {
            thumbnail = BitmapUtils.createThumbnail(bitmap, 96)
        }
        return thumbnail!!
    }

    fun invalidateThumbnail() {
        thumbnail?.recycle()
        thumbnail = null
    }

    fun duplicate(): AnimationFrame {
        val copy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        return AnimationFrame(
            bitmap = copy,
            durationFrames = durationFrames
        )
    }

    fun recycle() {
        if (!bitmap.isRecycled) bitmap.recycle()
        thumbnail?.recycle()
        thumbnail = null
    }
}
