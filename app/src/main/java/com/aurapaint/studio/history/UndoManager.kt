package com.aurapaint.studio.history

import com.aurapaint.studio.core.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.LinkedList

class UndoManager(
    private val maxHistory: Int = AppConfig.MAX_UNDO_STACK_SIZE
) {
    private val undoStack = LinkedList<Command>()
    private val redoStack = LinkedList<Command>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    fun pushCommand(command: Command) {
        undoStack.addLast(command)
        if (undoStack.size > maxHistory) {
            val oldest = undoStack.removeFirst()
            oldest.release()
        }
        // Clear redo stack on new operation
        clearRedoStack()
        updateFlows()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val command = undoStack.removeLast()
            command.undo()
            redoStack.addLast(command)
            updateFlows()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val command = redoStack.removeLast()
            command.redo()
            undoStack.addLast(command)
            updateFlows()
        }
    }

    private fun clearRedoStack() {
        while (redoStack.isNotEmpty()) {
            redoStack.removeLast().release()
        }
    }

    private fun updateFlows() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    fun clear() {
        while (undoStack.isNotEmpty()) undoStack.removeLast().release()
        clearRedoStack()
        updateFlows()
    }
}
