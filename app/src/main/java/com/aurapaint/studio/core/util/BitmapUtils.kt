package com.aurapaint.studio.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object BitmapUtils {
    /**
     * Creates a safe mutable ARGB_8888 bitmap with optional background fill.
     */
    fun createEmptyBitmap(width: Int, height: Int, fillColor: Int = Color.TRANSPARENT): Bitmap {
        val bitmap = Bitmap.createBitmap(
            width.coerceAtLeast(1),
            height.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        if (fillColor != Color.TRANSPARENT) {
            val canvas = Canvas(bitmap)
            canvas.drawColor(fillColor)
        }
        return bitmap
    }

    /**
     * Saves bitmap as PNG to given file path.
     */
    @Throws(IOException::class)
    fun saveBitmapAsPng(bitmap: Bitmap, targetFile: File) {
        targetFile.parentFile?.mkdirs()
        FileOutputStream(targetFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
    }

    /**
     * Saves bitmap as JPEG with specified quality.
     */
    @Throws(IOException::class)
    fun saveBitmapAsJpeg(bitmap: Bitmap, targetFile: File, quality: Int = 90) {
        targetFile.parentFile?.mkdirs()
        FileOutputStream(targetFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), out)
            out.flush()
        }
    }

    /**
     * Loads a mutable bitmap from file, scaling down if dimensions exceed bounds to prevent OOM.
     */
    fun loadBitmapFromFile(file: File, maxWidth: Int = 4096, maxHeight: Int = 4096): Bitmap? {
        if (!file.exists()) return null

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)

        var sampleSize = 1
        while (options.outWidth / sampleSize > maxWidth || options.outHeight / sampleSize > maxHeight) {
            sampleSize *= 2
        }

        val loadOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inMutable = true
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        return BitmapFactory.decodeFile(file.absolutePath, loadOptions)
    }

    /**
     * Generates a square thumbnail from a source bitmap.
     */
    fun createThumbnail(source: Bitmap, size: Int = 256): Bitmap {
        val thumb = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(thumb)
        val srcWidth = source.width.toFloat()
        val srcHeight = source.height.toFloat()
        val scale = minOf(size / srcWidth, size / srcHeight)
        val dx = (size - srcWidth * scale) / 2f
        val dy = (size - srcHeight * scale) / 2f

        canvas.save()
        canvas.translate(dx, dy)
        canvas.scale(scale, scale)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(source, 0f, 0f, paint)
        canvas.restore()
        return thumb
    }
}
