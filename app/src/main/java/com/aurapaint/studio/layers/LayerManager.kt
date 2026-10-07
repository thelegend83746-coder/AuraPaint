package com.aurapaint.studio.layers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LayerManager(
    val canvasWidth: Int,
    val canvasHeight: Int
) {
    private val _layers = mutableListOf<Layer>()
    val layers: List<Layer> get() = _layers

    private val _layersState = MutableStateFlow<List<Layer>>(emptyList())
    val layersState: StateFlow<List<Layer>> = _layersState.asStateFlow()

    private val _activeLayerIndex = MutableStateFlow(0)
    val activeLayerIndex: StateFlow<Int> = _activeLayerIndex.asStateFlow()

    val compositor = LayerCompositor(canvasWidth, canvasHeight)

    init {
        // Initialize with default background layer
        addRasterLayer("Layer 1")
    }

    private fun notifyChanged() {
        _layersState.value = _layers.toList()
    }

    fun getActiveLayer(): Layer? {
        val idx = _activeLayerIndex.value
        return if (idx in _layers.indices) _layers[idx] else null
    }

    fun getActiveRasterLayer(): RasterLayer? {
        return getActiveLayer() as? RasterLayer
    }

    fun setActiveLayerIndex(index: Int) {
        if (index in _layers.indices) {
            _activeLayerIndex.value = index
        }
    }

    fun addRasterLayer(name: String = "Layer ${_layers.size + 1}"): RasterLayer {
        val newLayer = RasterLayer(
            name = name,
            width = canvasWidth,
            height = canvasHeight
        )
        val targetIndex = (_activeLayerIndex.value + 1).coerceAtMost(_layers.size)
        _layers.add(targetIndex, newLayer)
        _activeLayerIndex.value = targetIndex
        notifyChanged()
        return newLayer
    }

    fun addVectorLayer(name: String = "Vector ${_layers.size + 1}"): VectorLayer {
        val newLayer = VectorLayer(
            name = name,
            width = canvasWidth,
            height = canvasHeight
        )
        val targetIndex = (_activeLayerIndex.value + 1).coerceAtMost(_layers.size)
        _layers.add(targetIndex, newLayer)
        _activeLayerIndex.value = targetIndex
        notifyChanged()
        return newLayer
    }

    fun duplicateActiveLayer(): Layer? {
        val current = getActiveLayer() ?: return null
        val copy = current.duplicate()
        val targetIndex = (_activeLayerIndex.value + 1).coerceAtMost(_layers.size)
        _layers.add(targetIndex, copy)
        _activeLayerIndex.value = targetIndex
        notifyChanged()
        return copy
    }

    fun deleteActiveLayer(): Boolean {
        if (_layers.size <= 1) return false // Prevent deleting the only remaining layer
        val idx = _activeLayerIndex.value
        val removed = _layers.removeAt(idx)
        removed.recycle()
        _activeLayerIndex.value = (idx - 1).coerceAtLeast(0)
        notifyChanged()
        return true
    }

    fun mergeDown(upperIndex: Int): Boolean {
        if (upperIndex <= 0 || upperIndex >= _layers.size) return false
        val lowerIndex = upperIndex - 1
        val upper = _layers[upperIndex]
        val lower = _layers[lowerIndex]

        if (lower is RasterLayer) {
            val lowerCanvas = lower.canvas
            val mergePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                alpha = (upper.opacity.coerceIn(0f, 1f) * 255).toInt()
                xfermode = upper.blendMode.toXfermode()
            }

            when (upper) {
                is RasterLayer -> {
                    lowerCanvas.drawBitmap(upper.bitmap, 0f, 0f, mergePaint)
                }
                is VectorLayer -> {
                    lowerCanvas.saveLayer(0f, 0f, canvasWidth.toFloat(), canvasHeight.toFloat(), mergePaint)
                    upper.renderToCanvas(lowerCanvas)
                    lowerCanvas.restore()
                }
            }
            lower.markDirty()

            _layers.removeAt(upperIndex)
            upper.recycle()
            _activeLayerIndex.value = lowerIndex
            notifyChanged()
            return true
        }
        return false
    }

    fun moveLayer(fromIndex: Int, toIndex: Int) {
        if (fromIndex in _layers.indices && toIndex in _layers.indices && fromIndex != toIndex) {
            val item = _layers.removeAt(fromIndex)
            _layers.add(toIndex, item)
            _activeLayerIndex.value = toIndex
            notifyChanged()
        }
    }

    fun clearActiveLayer() {
        getActiveLayer()?.clear()
        notifyChanged()
    }

    fun release() {
        for (l in _layers) {
            l.recycle()
        }
        _layers.clear()
        compositor.release()
    }
}
