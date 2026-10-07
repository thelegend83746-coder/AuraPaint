package com.aurapaint.studio.timelapse

import android.graphics.Bitmap
import com.aurapaint.studio.core.util.BitmapUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class TimelapseRecorder(
    val sessionDir: File
) {
    private val _isRecording = MutableStateFlow(true)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val actions = mutableListOf<CreativeAction>()
    private val keyframes = mutableListOf<File>()
    private var actionCounter: Int = 0

    init {
        sessionDir.mkdirs()
    }

    fun recordAction(action: CreativeAction, currentCanvasBitmap: Bitmap? = null) {
        if (!_isRecording.value) return
        actions.add(action)
        actionCounter++

        // Periodically capture a keyframe every 10 meaningful actions for fast timelapse export
        if (actionCounter % 10 == 0 && currentCanvasBitmap != null) {
            val keyframeFile = File(sessionDir, "timelapse_frame_${keyframes.size.toString().padStart(4, '0')}.jpg")
            try {
                BitmapUtils.saveBitmapAsJpeg(currentCanvasBitmap, keyframeFile, 85)
                keyframes.add(keyframeFile)
            } catch (_: Exception) {}
        }
    }

    fun pause() {
        _isRecording.value = false
    }

    fun resume() {
        _isRecording.value = true
    }

    fun getKeyframes(): List<File> = keyframes.toList()

    fun clear() {
        actions.clear()
        for (f in keyframes) f.delete()
        keyframes.clear()
        actionCounter = 0
    }
}
