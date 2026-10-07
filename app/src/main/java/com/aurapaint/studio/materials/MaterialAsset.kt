package com.aurapaint.studio.materials

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

enum class MaterialCategory(val title: String) {
    SCREENTONE("Manga Screentones"),
    TEXTURE("Canvas & Paper"),
    PATTERNS("Decorative Patterns")
}

data class MaterialAsset(
    val id: String,
    val name: String,
    val category: MaterialCategory,
    val renderPattern: (width: Int, height: Int, scale: Float) -> Bitmap
)

object MaterialManager {
    val presets: List<MaterialAsset> = listOf(
        MaterialAsset(
            id = "mat_screentone_dot",
            name = "Halftone Screen (40L)",
            category = MaterialCategory.SCREENTONE
        ) { w, h, scale ->
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                style = Paint.Style.FILL
            }
            val step = (24f * scale).coerceAtLeast(6f)
            val radius = step * 0.28f
            for (y in 0 until (h / step).toInt() + 1) {
                for (x in 0 until (w / step).toInt() + 1) {
                    val offsetX = if (y % 2 == 1) step / 2f else 0f
                    canvas.drawCircle(x * step + offsetX, y * step, radius, paint)
                }
            }
            bmp
        },
        MaterialAsset(
            id = "mat_paper_grain",
            name = "Fine Cold-Press Paper",
            category = MaterialCategory.TEXTURE
        ) { w, h, _ ->
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val pixels = IntArray(w * h)
            val rand = java.util.Random(42)
            for (i in pixels.indices) {
                val noise = 230 + rand.nextInt(26)
                pixels[i] = (0x33 shl 24) or (noise shl 16) or (noise shl 8) or noise
            }
            bmp.setPixels(pixels, 0, w, 0, 0, w, h)
            bmp
        },
        MaterialAsset(
            id = "mat_canvas_weave",
            name = "Linen Canvas Weave",
            category = MaterialCategory.TEXTURE
        ) { w, h, scale ->
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint().apply {
                color = 0x44888888.toInt()
                strokeWidth = (2f * scale).coerceAtLeast(1f)
            }
            val step = (12f * scale).coerceAtLeast(4f)
            for (x in 0 until (w / step).toInt() + 1) {
                canvas.drawLine(x * step, 0f, x * step, h.toFloat(), paint)
            }
            for (y in 0 until (h / step).toInt() + 1) {
                canvas.drawLine(0f, y * step, w.toFloat(), y * step, paint)
            }
            bmp
        },
        MaterialAsset(
            id = "mat_manga_speedlines",
            name = "Dynamic Focus Lines",
            category = MaterialCategory.PATTERNS
        ) { w, h, _ ->
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val cx = w / 2f
            val cy = h / 2f
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                strokeWidth = 2.5f
            }
            val count = 90
            val innerR = minOf(w, h) * 0.35f
            val outerR = maxOf(w, h) * 0.9f
            for (i in 0 until count) {
                val angle = (i * (360.0 / count)) * (Math.PI / 180.0)
                val x1 = (cx + innerR * kotlin.math.cos(angle)).toFloat()
                val y1 = (cy + innerR * kotlin.math.sin(angle)).toFloat()
                val x2 = (cx + outerR * kotlin.math.cos(angle)).toFloat()
                val y2 = (cy + outerR * kotlin.math.sin(angle)).toFloat()
                canvas.drawLine(x1, y1, x2, y2, paint)
            }
            bmp
        }
    )
}
