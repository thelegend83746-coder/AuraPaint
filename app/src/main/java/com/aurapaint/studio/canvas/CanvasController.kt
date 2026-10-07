package com.aurapaint.studio.canvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.aurapaint.studio.animation.AnimationTimeline
import com.aurapaint.studio.brush.BrushEngine
import com.aurapaint.studio.brush.BrushProperties
import com.aurapaint.studio.brush.Stabilizer
import com.aurapaint.studio.brush.StrokePoint
import com.aurapaint.studio.color.ColorManager
import com.aurapaint.studio.history.LayerBitmapCommand
import com.aurapaint.studio.history.UndoManager
import com.aurapaint.studio.layers.LayerManager
import com.aurapaint.studio.layers.RasterLayer
import com.aurapaint.studio.rulers.RulerManager
import com.aurapaint.studio.selection.SelectionManager
import com.aurapaint.studio.shapes.FloodFill
import com.aurapaint.studio.shapes.ShapeTool
import com.aurapaint.studio.symmetry.SymmetryManager
import com.aurapaint.studio.timelapse.CreativeAction
import com.aurapaint.studio.timelapse.TimelapseRecorder
import com.aurapaint.studio.transform.TransformManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class CanvasController(
    val canvasWidth: Int,
    val canvasHeight: Int,
    sessionDir: File
) : InputEventListener {

    val viewTransform = ViewTransform(canvasWidth, canvasHeight)
    val layerManager = LayerManager(canvasWidth, canvasHeight)
    val colorManager = ColorManager()
    val brushEngine = BrushEngine()
    val stabilizer = Stabilizer()
    val selectionManager = SelectionManager(canvasWidth, canvasHeight)
    val transformManager = TransformManager()
    val shapeTool = ShapeTool()
    val symmetryManager = SymmetryManager(canvasWidth / 2f, canvasHeight / 2f)
    val rulerManager = RulerManager(canvasWidth.toFloat(), canvasHeight.toFloat())
    val undoManager = UndoManager()
    val timelapseRecorder = TimelapseRecorder(sessionDir)

    var animationTimeline: AnimationTimeline? = null

    var brushProperties: BrushProperties = BrushProperties()
        set(value) {
            field = value
            stabilizer.setLevel(value.stabilization)
        }

    private val _activeTool = MutableStateFlow(DrawingTool.BRUSH)
    val activeTool: StateFlow<DrawingTool> = _activeTool.asStateFlow()

    // Transient drawing state
    private var beforeStrokeBitmap: Bitmap? = null
    private var strokeStartX = 0f
    private var strokeStartY = 0f
    private var lastStrokePoint: StrokePoint? = null
    var isCurrentlyInteracting: Boolean = false
        private set

    // Callback to invalidate CanvasView
    var onInvalidateView: (() -> Unit)? = null

    fun setTool(tool: DrawingTool) {
        if (_activeTool.value == DrawingTool.TRANSFORM && tool != DrawingTool.TRANSFORM) {
            transformManager.apply()
        }
        _activeTool.value = tool
        if (tool == DrawingTool.TRANSFORM) {
            layerManager.getActiveRasterLayer()?.let {
                transformManager.startTransform(it)
            }
        }
        onInvalidateView?.invoke()
    }

    override fun onDrawStart(point: StrokePoint) {
        isCurrentlyInteracting = true
        stabilizer.reset()
        val smoothed = stabilizer.filter(point)
        val snapped = rulerManager.snap(smoothed)
        strokeStartX = snapped.x
        strokeStartY = snapped.y
        lastStrokePoint = snapped

        val activeLayer = layerManager.getActiveRasterLayer()

        when (_activeTool.value) {
            DrawingTool.BRUSH, DrawingTool.ERASER -> {
                if (activeLayer != null && !activeLayer.isLocked) {
                    beforeStrokeBitmap = activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    brushEngine.startStroke(snapped)

                    // Draw first dab for all symmetry instances
                    val symPoints = symmetryManager.getSymmetricPoints(snapped)
                    for (sp in symPoints) {
                        brushEngine.renderSegment(
                            canvas = activeLayer.canvas,
                            currentPoint = sp,
                            properties = brushProperties,
                            brushColor = colorManager.currentColor.value,
                            isEraser = _activeTool.value == DrawingTool.ERASER,
                            zoomScale = viewTransform.zoom
                        )
                    }
                    activeLayer.markDirty()
                }
            }

            DrawingTool.FILL -> {
                if (activeLayer != null && !activeLayer.isLocked) {
                    val before = activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    val filled = FloodFill.fill(
                        bitmap = activeLayer.bitmap,
                        startX = snapped.x.toInt(),
                        startY = snapped.y.toInt(),
                        fillColor = colorManager.currentColor.value
                    )
                    if (filled) {
                        activeLayer.markDirty()
                        val after = activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                        undoManager.pushCommand(LayerBitmapCommand(activeLayer, before, after))
                        timelapseRecorder.recordAction(CreativeAction.FloodFill(snapped.x.toInt(), snapped.y.toInt(), colorManager.currentColor.value))
                    } else {
                        before.recycle()
                    }
                }
            }

            DrawingTool.EYEDROPPER -> {
                if (activeLayer != null) {
                    colorManager.sampleFromBitmap(activeLayer.bitmap, snapped.x.toInt(), snapped.y.toInt())
                }
            }

            DrawingTool.SELECTION -> {
                selectionManager.mask.setRectangle(snapped.x, snapped.y, snapped.x, snapped.y)
                selectionManager.notifyUpdated()
            }

            DrawingTool.SHAPE -> {
                if (activeLayer != null && !activeLayer.isLocked) {
                    beforeStrokeBitmap = activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                }
            }

            else -> {}
        }

        onInvalidateView?.invoke()
    }

    override fun onDrawMove(point: StrokePoint) {
        val smoothed = stabilizer.filter(point)
        val snapped = rulerManager.snap(smoothed)
        val prev = lastStrokePoint ?: snapped
        lastStrokePoint = snapped

        val activeLayer = layerManager.getActiveRasterLayer()

        when (_activeTool.value) {
            DrawingTool.BRUSH, DrawingTool.ERASER -> {
                if (activeLayer != null && !activeLayer.isLocked) {
                    val symPrev = symmetryManager.getSymmetricPoints(prev)
                    val symCurr = symmetryManager.getSymmetricPoints(snapped)

                    for (i in symCurr.indices) {
                        val p = if (i in symPrev.indices) symPrev[i] else symCurr[i]
                        val c = symCurr[i]
                        brushEngine.startStroke(p)
                        brushEngine.renderSegment(
                            canvas = activeLayer.canvas,
                            currentPoint = c,
                            properties = brushProperties,
                            brushColor = colorManager.currentColor.value,
                            isEraser = _activeTool.value == DrawingTool.ERASER,
                            zoomScale = viewTransform.zoom
                        )
                    }
                    activeLayer.markDirty()
                }
            }

            DrawingTool.SELECTION -> {
                selectionManager.mask.setRectangle(strokeStartX, strokeStartY, snapped.x, snapped.y)
                selectionManager.notifyUpdated()
            }

            DrawingTool.EYEDROPPER -> {
                if (activeLayer != null) {
                    colorManager.sampleFromBitmap(activeLayer.bitmap, snapped.x.toInt(), snapped.y.toInt())
                }
            }

            else -> {}
        }

        onInvalidateView?.invoke()
    }

    override fun onDrawEnd() {
        val activeLayer = layerManager.getActiveRasterLayer()

        when (_activeTool.value) {
            DrawingTool.BRUSH, DrawingTool.ERASER -> {
                brushEngine.endStroke()
                if (activeLayer != null && beforeStrokeBitmap != null) {
                    val after = activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    undoManager.pushCommand(LayerBitmapCommand(activeLayer, beforeStrokeBitmap!!, after))
                    timelapseRecorder.recordAction(
                        CreativeAction.Stroke(_activeTool.value.displayName, 10, colorManager.currentColor.value),
                        activeLayer.bitmap
                    )
                    beforeStrokeBitmap = null
                }
            }

            DrawingTool.SHAPE -> {
                if (activeLayer != null && beforeStrokeBitmap != null && lastStrokePoint != null) {
                    shapeTool.strokeColor = colorManager.currentColor.value
                    shapeTool.fillColor = colorManager.previousColor.value
                    shapeTool.render(
                        canvas = activeLayer.canvas,
                        startX = strokeStartX,
                        startY = strokeStartY,
                        endX = lastStrokePoint!!.x,
                        endY = lastStrokePoint!!.y
                    )
                    activeLayer.markDirty()
                    val after = activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    undoManager.pushCommand(LayerBitmapCommand(activeLayer, beforeStrokeBitmap!!, after))
                    timelapseRecorder.recordAction(CreativeAction.Shape(shapeTool.currentShape.name, shapeTool.strokeColor, shapeTool.fillColor))
                    beforeStrokeBitmap = null
                }
            }

            else -> {}
        }

        isCurrentlyInteracting = false
        onInvalidateView?.invoke()
    }

    override fun onTransformGesture(deltaPanX: Float, deltaPanY: Float, scaleFactor: Float, deltaRotation: Float) {
        viewTransform.panX += deltaPanX
        viewTransform.panY += deltaPanY
        viewTransform.zoom = (viewTransform.zoom * scaleFactor).coerceIn(0.1f, 30.0f)
        viewTransform.rotationDegrees = (viewTransform.rotationDegrees + deltaRotation) % 360f
        viewTransform.updateMatrix()
        onInvalidateView?.invoke()
    }

    override fun onDoubleTap() {
        viewTransform.fitToScreen()
        onInvalidateView?.invoke()
    }
}
