package com.aurapaint.studio.color

import android.graphics.Bitmap
import android.graphics.Color
import com.aurapaint.studio.core.util.ColorUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.LinkedList

class ColorManager {
    private val _currentColor = MutableStateFlow(0xFF0F1115.toInt())
    val currentColor: StateFlow<Int> = _currentColor.asStateFlow()

    private val _previousColor = MutableStateFlow(0xFFFFFFFF.toInt())
    val previousColor: StateFlow<Int> = _previousColor.asStateFlow()

    private val recentColorsList = LinkedList<Int>().apply {
        addAll(listOf(
            0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF3D7BFF.toInt(),
            0xFF8A5CFF.toInt(), 0xFFFF4565.toInt(), 0xFF00E5FF.toInt()
        ))
    }

    private val _recentColors = MutableStateFlow<List<Int>>(recentColorsList.toList())
    val recentColors: StateFlow<List<Int>> = _recentColors.asStateFlow()

    private val _palettes = MutableStateFlow(PalettePresets.defaultPalettes)
    val palettes: StateFlow<List<ColorPalette>> = _palettes.asStateFlow()

    private val _activePaletteIndex = MutableStateFlow(0)
    val activePaletteIndex: StateFlow<Int> = _activePaletteIndex.asStateFlow()

    fun setColor(color: Int) {
        if (_currentColor.value != color) {
            _previousColor.value = _currentColor.value
            _currentColor.value = color
            addToRecent(color)
        }
    }

    private fun addToRecent(color: Int) {
        recentColorsList.remove(color)
        recentColorsList.addFirst(color)
        while (recentColorsList.size > 20) {
            recentColorsList.removeLast()
        }
        _recentColors.value = recentColorsList.toList()
    }

    fun swapCurrentAndPrevious() {
        val temp = _currentColor.value
        _currentColor.value = _previousColor.value
        _previousColor.value = temp
    }

    fun sampleFromBitmap(bitmap: Bitmap, x: Int, y: Int): Int? {
        if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
            val sampled = bitmap.getPixel(x, y)
            setColor(sampled)
            return sampled
        }
        return null
    }

    fun selectPalette(index: Int) {
        if (index in _palettes.value.indices) {
            _activePaletteIndex.value = index
        }
    }
}
