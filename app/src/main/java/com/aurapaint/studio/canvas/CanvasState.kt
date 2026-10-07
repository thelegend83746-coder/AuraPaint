package com.aurapaint.studio.canvas

enum class DrawingTool(val displayName: String) {
    BRUSH("Brush"),
    ERASER("Eraser"),
    FILL("Bucket Fill"),
    SHAPE("Shapes"),
    TEXT("Text"),
    EYEDROPPER("Eyedropper"),
    TRANSFORM("Transform"),
    SELECTION("Selection"),
    VECTOR("Vector Pen")
}

data class CanvasState(
    var activeTool: DrawingTool = DrawingTool.BRUSH,
    var zoom: Float = 1.0f,
    var panX: Float = 0f,
    var panY: Float = 0f,
    var rotationDegrees: Float = 0f,
    var isFingerDrawingEnabled: Boolean = true,
    var isStylusPressureEnabled: Boolean = true,
    var backgroundColor: Int = android.graphics.Color.WHITE
)
