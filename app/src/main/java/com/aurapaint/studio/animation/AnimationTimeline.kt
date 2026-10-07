package com.aurapaint.studio.animation

import android.graphics.Bitmap
import com.aurapaint.studio.core.AppConfig
import com.aurapaint.studio.core.util.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AnimationTimeline(
    val canvasWidth: Int,
    val canvasHeight: Int,
    private val scope: CoroutineScope
) {
    private val _frames = mutableListOf<AnimationFrame>()
    val frames: List<AnimationFrame> get() = _frames

    private val _framesState = MutableStateFlow<List<AnimationFrame>>(emptyList())
    val framesState: StateFlow<List<AnimationFrame>> = _framesState.asStateFlow()

    private val _currentFrameIndex = MutableStateFlow(0)
    val currentFrameIndex: StateFlow<Int> = _currentFrameIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _fps = MutableStateFlow(AppConfig.DEFAULT_ANIMATION_FPS)
    val fps: StateFlow<Int> = _fps.asStateFlow()

    val onionSkin = OnionSkin()
    private var playbackJob: Job? = null

    init {
        // Create initial frame
        addFrame()
    }

    private fun notifyChanged() {
        _framesState.value = _frames.toList()
    }

    fun getCurrentFrame(): AnimationFrame? {
        val idx = _currentFrameIndex.value
        return if (idx in _frames.indices) _frames[idx] else null
    }

    fun setFps(newFps: Int) {
        _fps.value = newFps.coerceIn(AppConfig.MIN_ANIMATION_FPS, AppConfig.MAX_ANIMATION_FPS)
    }

    fun addFrame(): AnimationFrame {
        val bmp = BitmapUtils.createEmptyBitmap(canvasWidth, canvasHeight)
        val frame = AnimationFrame(bitmap = bmp)
        val targetIdx = (_currentFrameIndex.value + 1).coerceAtMost(_frames.size)
        _frames.add(targetIdx, frame)
        _currentFrameIndex.value = targetIdx
        notifyChanged()
        return frame
    }

    fun duplicateCurrentFrame(): AnimationFrame? {
        val current = getCurrentFrame() ?: return null
        val dup = current.duplicate()
        val targetIdx = (_currentFrameIndex.value + 1).coerceAtMost(_frames.size)
        _frames.add(targetIdx, dup)
        _currentFrameIndex.value = targetIdx
        notifyChanged()
        return dup
    }

    fun deleteCurrentFrame(): Boolean {
        if (_frames.size <= 1) return false
        val idx = _currentFrameIndex.value
        val removed = _frames.removeAt(idx)
        removed.recycle()
        _currentFrameIndex.value = (idx - 1).coerceAtLeast(0)
        notifyChanged()
        return true
    }

    fun selectFrame(index: Int) {
        if (index in _frames.indices) {
            _currentFrameIndex.value = index
        }
    }

    fun nextFrame() {
        if (_frames.isNotEmpty()) {
            _currentFrameIndex.value = (_currentFrameIndex.value + 1) % _frames.size
        }
    }

    fun prevFrame() {
        if (_frames.isNotEmpty()) {
            _currentFrameIndex.value = if (_currentFrameIndex.value > 0) _currentFrameIndex.value - 1 else _frames.size - 1
        }
    }

    fun play() {
        if (_isPlaying.value || _frames.size <= 1) return
        _isPlaying.value = true
        playbackJob = scope.launch(Dispatchers.Default) {
            while (isActive && _isPlaying.value) {
                val delayTime = (1000L / _fps.value).coerceAtLeast(16L)
                delay(delayTime)
                nextFrame()
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun release() {
        pause()
        for (f in _frames) f.recycle()
        _frames.clear()
    }
}
