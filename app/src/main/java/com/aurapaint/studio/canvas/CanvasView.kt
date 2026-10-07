package com.aurapaint.studio.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View

class CanvasView(
    context: Context,
    val controller: CanvasController
) : View(context) {

    private val inputTracker: InputTracker
    private var isFirstLayout = true

    // Background paints
    private val canvasShadowPaint = Paint().apply {
        color = 0x55000000
        style = Paint.Style.FILL
    }
    private val canvasBorderPaint = Paint().apply {
        color = 0xFF323642.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
    }
    private val canvasBounds = RectF(0f, 0f, controller.canvasWidth.toFloat(), controller.canvasHeight.toFloat())

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        inputTracker = InputTracker(controller.viewTransform, controller)
        controller.onInvalidateView = {
            postInvalidateOnAnimation()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        controller.viewTransform.viewWidth = w
        controller.viewTransform.viewHeight = h

        if (isFirstLayout && w > 0 && h > 0) {
            controller.viewTransform.fitToScreen()
            isFirstLayout = false
        } else {
            controller.viewTransform.updateMatrix()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = inputTracker.onTouchEvent(event)
        invalidate()
        return handled
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Fill workspace area with dark background
        canvas.drawColor(0xFF181A1F.toInt())

        canvas.save()
        canvas.concat(controller.viewTransform.matrix)

        // Draw canvas paper drop shadow and base
        val shadowRect = RectF(canvasBounds).apply { offset(4f, 8f) }
        canvas.drawRect(shadowRect, canvasShadowPaint)

        // Draw Onion Skins if in animation mode
        controller.animationTimeline?.let { timeline ->
            timeline.onionSkin.render(canvas, timeline.frames, timeline.currentFrameIndex.value)
        }

        // Composite all layers onto canvas
        controller.layerManager.compositor.composite(
            layers = controller.layerManager.layers,
            targetCanvas = canvas,
            backgroundColor = Color.WHITE
        )

        // Draw active transform preview if transforming
        if (controller.transformManager.isTransformActive) {
            controller.transformManager.renderPreview(canvas)
        }

        // Draw Selection Mask (marching ants)
        controller.selectionManager.mask.drawSelectionOverlay(canvas)

        // Draw Symmetry and Ruler Guidelines
        controller.symmetryManager.drawGuides(canvas, controller.canvasWidth.toFloat(), controller.canvasHeight.toFloat())
        controller.rulerManager.drawGuides(canvas)

        // Draw canvas outer border
        canvas.drawRect(canvasBounds, canvasBorderPaint)

        canvas.restore()
    }
}
