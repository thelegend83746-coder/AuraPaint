package com.aurapaint.studio.selection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectionManager(
    val canvasWidth: Int,
    val canvasHeight: Int
) {
    val mask = SelectionMask(canvasWidth, canvasHeight)

    private val _currentMode = MutableStateFlow(SelectionMode.RECTANGLE)
    val currentMode: StateFlow<SelectionMode> = _currentMode.asStateFlow()

    private val _hasActiveSelection = MutableStateFlow(false)
    val hasActiveSelection: StateFlow<Boolean> = _hasActiveSelection.asStateFlow()

    fun setMode(mode: SelectionMode) {
        _currentMode.value = mode
    }

    fun notifyUpdated() {
        _hasActiveSelection.value = mask.isActive
    }

    fun clear() {
        mask.clear()
        _hasActiveSelection.value = false
    }

    fun invert() {
        mask.invert()
        _hasActiveSelection.value = mask.isActive
    }
}
