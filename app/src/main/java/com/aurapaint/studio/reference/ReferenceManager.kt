package com.aurapaint.studio.reference

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReferenceManager {
    var referenceBitmap: Bitmap? = null

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    var posX: Float = 60f
    var posY: Float = 140f
    var windowWidth: Float = 340f
    var windowHeight: Float = 260f

    var zoomScale: Float = 1.0f
    var panOffsetX: Float = 0f
    var panOffsetY: Float = 0f
    var opacity: Float = 0.9f

    fun setImage(bitmap: Bitmap) {
        referenceBitmap?.recycle()
        referenceBitmap = bitmap
        _isVisible.value = true
    }

    fun toggleVisibility() {
        if (referenceBitmap != null) {
            _isVisible.value = !_isVisible.value
        }
    }

    fun close() {
        _isVisible.value = false
        referenceBitmap?.recycle()
        referenceBitmap = null
    }
}
