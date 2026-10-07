package com.aurapaint.studio.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import com.aurapaint.studio.layers.RasterLayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin

class TransformManager {

    var isTransformActive: Boolean = false
        private set

    private val _stateFlow = MutableStateFlow(isTransformActive)
    val stateFlow: StateFlow<Boolean> = _stateFlow.asStateFlow()

    private var targetLayer: RasterLayer? = null
    private var sourceBitmap: Bitmap? = null
    private var originalBackupBitmap: Bitmap? = null

    // Transform parameters
    var translationX: Float = 0f
    var translationY: Float = 0f
    var scaleX: Float = 1.0f
    var scaleY: Float = 1.0f
    var rotationDegrees: Float = 0f
    var isFlippedH: Boolean = false
    var isFlippedV: Boolean = false

    private val transformMatrix = Matrix()
    private val inverseMatrix = Matrix()

    private val handlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF3D7BFF.toInt()
        style = Paint.Style.FILL
    }
    private val handleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }
    private val boxBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF3D7BFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
    }

    fun startTransform(layer: RasterLayer, selectionBounds: RectF? = null) {
        targetLayer = layer
        originalBackupBitmap = layer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        sourceBitmap = layer.bitmap.copy(Bitmap.Config.ARGB_8888, true)

        translationX = 0f
        translationY = 0f
        scaleX = 1.0f
        scaleY = 1.0f
        rotationDegrees = 0f
        isFlippedH = false
        isFlippedV = false

        isTransformActive = true
        _stateFlow.value = true
    }

    fun flipHorizontal() {
        isFlippedH = !isFlippedH
    }

    fun flipVertical() {
        isFlippedV = !isFlippedV
    }

    fun getComputedMatrix(cx: Float, cy: Float): Matrix {
        transformMatrix.reset()
        val flipScaleX = if (isFlippedH) -1f else 1f
        val flipScaleY = if (isFlippedV) -1f else 1f

        transformMatrix.postTranslate(-cx, -cy)
        transformMatrix.postScale(scaleX * flipScaleX, scaleY * flipScaleY)
        transformMatrix.postRotate(rotationDegrees)
        transformMatrix.postTranslate(cx + translationX, cy + translationY)
        return transformMatrix
    }

    fun renderPreview(canvas: Canvas) {
        val src = sourceBitmap ?: return
        val cx = src.width / 2f
        val cy = src.height / 2f
        val matrix = getComputedMatrix(cx, cy)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(src, matrix, paint)

        // Draw bounding box and handles
        val points = floatArrayOf(
            0f, 0f,
            src.width.toFloat(), 0f,
            src.width.toFloat(), src.height.toFloat(),
            0f, src.height.toFloat()
        )
        matrix.mapPoints(points)

        // Draw box border
        canvas.drawLine(points[0], points[1], points[2], points[3], boxBorderPaint)
        canvas.drawLine(points[2], points[3], points[4], points[5], boxBorderPaint)
        canvas.drawLine(points[4], points[5], points[6], points[7], boxBorderPaint)
        canvas.drawLine(points[6], points[7], points[0], points[1], boxBorderPaint)

        // Draw 4 corner handles
        val handleRadius = 14f
        for (i in 0 until 8 step 2) {
            canvas.drawCircle(points[i], points[i + 1], handleRadius, handlePaint)
            canvas.drawCircle(points[i], points[i + 1], handleRadius, handleStrokePaint)
        }
    }

    fun apply(): Boolean {
        val layer = targetLayer ?: return false
        val src = sourceBitmap ?: return false

        val cx = src.width / 2f
        val cy = src.height / 2f
        val matrix = getComputedMatrix(cx, cy)

        val resultBitmap = Bitmap.createBitmap(layer.width, layer.height, Bitmap.Config.ARGB_8888)
        val resultCanvas = Canvas(resultBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        resultCanvas.drawBitmap(src, matrix, paint)

        layer.replaceBitmap(resultBitmap)
        cleanup()
        return true
    }

    fun cancel() {
        val layer = targetLayer
        val backup = originalBackupBitmap
        if (layer != null && backup != null) {
            layer.replaceBitmap(backup)
        }
        cleanup()
    }

    private fun cleanup() {
        sourceBitmap?.recycle()
        sourceBitmap = null
        originalBackupBitmap = null
        targetLayer = null
        isTransformActive = false
        _stateFlow.value = false
    }
}
