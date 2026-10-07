package com.aurapaint.studio.canvas

import android.view.MotionEvent
import com.aurapaint.studio.brush.StrokePoint
import com.aurapaint.studio.core.util.MathUtils
import kotlin.math.atan2
import kotlin.math.hypot

interface InputEventListener {
    fun onDrawStart(point: StrokePoint)
    fun onDrawMove(point: StrokePoint)
    fun onDrawEnd()
    fun onTransformGesture(deltaPanX: Float, deltaPanY: Float, scaleFactor: Float, deltaRotation: Float)
    fun onDoubleTap()
}

class InputTracker(
    private val transform: ViewTransform,
    private val listener: InputEventListener
) {
    private var isDrawing = false
    private var isNavigating = false

    // Two finger navigation tracking
    private var prevMidX = 0f
    private var prevMidY = 0f
    private var prevSpan = 0f
    private var prevAngle = 0f

    // Double tap detection
    private var lastTapTime: Long = 0
    private var lastTapX: Float = 0f
    private var lastTapY: Float = 0f

    fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerCount = event.pointerCount

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val now = System.currentTimeMillis()
                if (now - lastTapTime < 300 && MathUtils.distance(event.x, event.y, lastTapX, lastTapY) < 50f) {
                    listener.onDoubleTap()
                    lastTapTime = 0
                    return true
                }
                lastTapTime = now
                lastTapX = event.x
                lastTapY = event.y

                if (pointerCount == 1) {
                    isDrawing = true
                    val canvasPt = transform.screenToCanvas(event.x, event.y)
                    val pressure = getPressure(event, 0)
                    listener.onDrawStart(StrokePoint(canvasPt.x, canvasPt.y, pressure))
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (pointerCount == 2) {
                    if (isDrawing) {
                        listener.onDrawEnd()
                        isDrawing = false
                    }
                    isNavigating = true
                    initTwoFingerState(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isNavigating && pointerCount >= 2) {
                    handleTwoFingerMove(event)
                } else if (isDrawing && pointerCount == 1) {
                    val canvasPt = transform.screenToCanvas(event.x, event.y)
                    val pressure = getPressure(event, 0)
                    listener.onDrawMove(StrokePoint(canvasPt.x, canvasPt.y, pressure))
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (pointerCount <= 2 && isNavigating) {
                    isNavigating = false
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDrawing) {
                    listener.onDrawEnd()
                    isDrawing = false
                }
                isNavigating = false
            }
        }
        return true
    }

    private fun initTwoFingerState(event: MotionEvent) {
        val x0 = event.getX(0); val y0 = event.getY(0)
        val x1 = event.getX(1); val y1 = event.getY(1)

        prevMidX = (x0 + x1) / 2f
        prevMidY = (y0 + y1) / 2f
        prevSpan = hypot(x1 - x0, y1 - y0).coerceAtLeast(10f)
        prevAngle = Math.toDegrees(atan2((y1 - y0).toDouble(), (x1 - x0).toDouble())).toFloat()
    }

    private fun handleTwoFingerMove(event: MotionEvent) {
        val x0 = event.getX(0); val y0 = event.getY(0)
        val x1 = event.getX(1); val y1 = event.getY(1)

        val midX = (x0 + x1) / 2f
        val midY = (y0 + y1) / 2f
        val span = hypot(x1 - x0, y1 - y0).coerceAtLeast(10f)
        val angle = Math.toDegrees(atan2((y1 - y0).toDouble(), (x1 - x0).toDouble())).toFloat()

        val deltaPanX = midX - prevMidX
        val deltaPanY = midY - prevMidY
        val scaleFactor = span / prevSpan
        val deltaRotation = angle - prevAngle

        prevMidX = midX
        prevMidY = midY
        prevSpan = span
        prevAngle = angle

        listener.onTransformGesture(deltaPanX, deltaPanY, scaleFactor, deltaRotation)
    }

    private fun getPressure(event: MotionEvent, index: Int): Float {
        val p = event.getPressure(index)
        return if (p > 0f) p.coerceIn(0.05f, 1.0f) else 1.0f
    }
}
