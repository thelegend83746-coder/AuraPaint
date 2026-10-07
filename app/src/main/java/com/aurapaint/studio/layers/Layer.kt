package com.aurapaint.studio.layers

import android.graphics.Bitmap

interface Layer {
    val id: String
    var name: String
    var isVisible: Boolean
    var opacity: Float // 0.0f .. 1.0f
    var isLocked: Boolean
    var isAlphaLocked: Boolean
    var isClippingMask: Boolean
    var blendMode: BlendMode

    fun getThumbnail(): Bitmap?
    fun clear()
    fun duplicate(): Layer
    fun recycle()
}
