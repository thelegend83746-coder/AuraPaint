package com.aurapaint.studio.export

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.aurapaint.studio.core.AppConfig
import com.aurapaint.studio.core.util.BitmapUtils
import com.aurapaint.studio.layers.LayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ExportFormat(val extension: String, val displayName: String) {
    PNG("png", "PNG Image (Solid Background)"),
    PNG_TRANSPARENT("png", "Transparent PNG (No Background)"),
    JPEG("jpg", "Standard JPEG"),
    ANIMATION_FRAMES("zip", "Animation Frame Sequence")
}

class ArtworkExporter(
    private val exportDir: File = File(AppConfig.BASE_EXPORTS_DIR)
) {
    init {
        exportDir.mkdirs()
    }

    suspend fun exportArtwork(
        layerManager: LayerManager,
        projectName: String,
        format: ExportFormat,
        jpegQuality: Int = 95
    ): File = withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
        val cleanName = projectName.replace(Regex("[^a-zA-Z0-9_]"), "_")
        val outFile = File(exportDir, "${cleanName}_$timestamp.${format.extension}")

        val exportBmp = Bitmap.createBitmap(
            layerManager.canvasWidth,
            layerManager.canvasHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(exportBmp)

        val bgColor = if (format == ExportFormat.PNG_TRANSPARENT) {
            Color.TRANSPARENT
        } else {
            Color.WHITE
        }

        layerManager.compositor.composite(layerManager.layers, canvas, bgColor)

        when (format) {
            ExportFormat.PNG, ExportFormat.PNG_TRANSPARENT -> {
                BitmapUtils.saveBitmapAsPng(exportBmp, outFile)
            }
            ExportFormat.JPEG -> {
                BitmapUtils.saveBitmapAsJpeg(exportBmp, outFile, jpegQuality)
            }
            ExportFormat.ANIMATION_FRAMES -> {
                BitmapUtils.saveBitmapAsPng(exportBmp, outFile)
            }
        }

        exportBmp.recycle()
        outFile
    }
}
