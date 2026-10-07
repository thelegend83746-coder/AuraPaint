package com.aurapaint.studio.filters

import android.graphics.Bitmap
import android.graphics.Color
import com.aurapaint.studio.core.util.MathUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FilterProcessor {

    suspend fun applyFilter(
        source: Bitmap,
        type: FilterType,
        param1: Float = 0f, // e.g. brightness or blur radius
        param2: Float = 0f, // e.g. contrast
        curve: ToneCurve? = null
    ): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        when (type) {
            FilterType.BRIGHTNESS_CONTRAST -> {
                val brightness = param1 // -100 .. 100
                val contrast = (param2 + 100f) / 100f // 0 .. 2
                val factor = contrast * contrast
                for (i in pixels.indices) {
                    val a = (pixels[i] ushr 24) and 0xFF
                    if (a == 0) continue
                    var r = (pixels[i] ushr 16) and 0xFF
                    var g = (pixels[i] ushr 8) and 0xFF
                    var b = pixels[i] and 0xFF

                    r = MathUtils.clamp((((r - 128) * factor) + 128 + brightness).toInt(), 0, 255)
                    g = MathUtils.clamp((((g - 128) * factor) + 128 + brightness).toInt(), 0, 255)
                    b = MathUtils.clamp((((b - 128) * factor) + 128 + brightness).toInt(), 0, 255)
                    pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
                }
            }

            FilterType.GRAYSCALE -> {
                for (i in pixels.indices) {
                    val a = (pixels[i] ushr 24) and 0xFF
                    if (a == 0) continue
                    val r = (pixels[i] ushr 16) and 0xFF
                    val g = (pixels[i] ushr 8) and 0xFF
                    val b = pixels[i] and 0xFF
                    val lum = (0.299f * r + 0.587f * g + 0.114f * b).toInt()
                    pixels[i] = (a shl 24) or (lum shl 16) or (lum shl 8) or lum
                }
            }

            FilterType.SEPIA -> {
                for (i in pixels.indices) {
                    val a = (pixels[i] ushr 24) and 0xFF
                    if (a == 0) continue
                    val r = (pixels[i] ushr 16) and 0xFF
                    val g = (pixels[i] ushr 8) and 0xFF
                    val b = pixels[i] and 0xFF

                    val tr = MathUtils.clamp((0.393 * r + 0.769 * g + 0.189 * b).toInt(), 0, 255)
                    val tg = MathUtils.clamp((0.349 * r + 0.686 * g + 0.168 * b).toInt(), 0, 255)
                    val tb = MathUtils.clamp((0.272 * r + 0.534 * g + 0.131 * b).toInt(), 0, 255)
                    pixels[i] = (a shl 24) or (tr shl 16) or (tg shl 8) or tb
                }
            }

            FilterType.INVERT -> {
                for (i in pixels.indices) {
                    val a = (pixels[i] ushr 24) and 0xFF
                    val r = 255 - ((pixels[i] ushr 16) and 0xFF)
                    val g = 255 - ((pixels[i] ushr 8) and 0xFF)
                    val b = 255 - (pixels[i] and 0xFF)
                    pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
                }
            }

            FilterType.THRESHOLD -> {
                val thresh = param1.toInt().coerceIn(1, 254)
                for (i in pixels.indices) {
                    val a = (pixels[i] ushr 24) and 0xFF
                    if (a == 0) continue
                    val r = (pixels[i] ushr 16) and 0xFF
                    val g = (pixels[i] ushr 8) and 0xFF
                    val b = pixels[i] and 0xFF
                    val lum = (0.299f * r + 0.587f * g + 0.114f * b).toInt()
                    val result = if (lum >= thresh) 255 else 0
                    pixels[i] = (a shl 24) or (result shl 16) or (result shl 8) or result
                }
            }

            FilterType.PIXELATE -> {
                val blockSize = param1.toInt().coerceIn(2, 64)
                for (y in 0 until height step blockSize) {
                    for (x in 0 until width step blockSize) {
                        val sampleColor = pixels[y * width + x]
                        for (by in 0 until blockSize) {
                            val py = y + by
                            if (py >= height) break
                            for (bx in 0 until blockSize) {
                                val px = x + bx
                                if (px >= width) break
                                pixels[py * width + px] = sampleColor
                            }
                        }
                    }
                }
            }

            FilterType.GAUSSIAN_BLUR -> {
                val radius = param1.toInt().coerceIn(1, 25)
                boxBlur(pixels, width, height, radius)
            }

            FilterType.TONE_CURVES -> {
                if (curve != null) {
                    for (i in pixels.indices) {
                        pixels[i] = curve.mapPixel(pixels[i])
                    }
                }
            }

            else -> {
                // Hue / Saturation fallback
                val satFactor = (param1 + 100f) / 100f
                val hsv = FloatArray(3)
                for (i in pixels.indices) {
                    val a = (pixels[i] ushr 24) and 0xFF
                    if (a == 0) continue
                    Color.colorToHSV(pixels[i], hsv)
                    hsv[1] = (hsv[1] * satFactor).coerceIn(0f, 1f)
                    val newCol = Color.HSVToColor(hsv)
                    pixels[i] = (a shl 24) or (newCol and 0x00FFFFFF)
                }
            }
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        output
    }

    private fun boxBlur(pixels: IntArray, w: Int, h: Int, radius: Int) {
        val temp = IntArray(w * h)
        // Horizontal pass
        for (y in 0 until h) {
            for (x in 0 until w) {
                var r = 0; var g = 0; var b = 0; var a = 0; var count = 0
                for (dx in -radius..radius) {
                    val px = (x + dx).coerceIn(0, w - 1)
                    val c = pixels[y * w + px]
                    a += (c ushr 24) and 0xFF
                    r += (c ushr 16) and 0xFF
                    g += (c ushr 8) and 0xFF
                    b += c and 0xFF
                    count++
                }
                temp[y * w + x] = ((a / count) shl 24) or ((r / count) shl 16) or ((g / count) shl 8) or (b / count)
            }
        }
        // Vertical pass
        for (y in 0 until h) {
            for (x in 0 until w) {
                var r = 0; var g = 0; var b = 0; var a = 0; var count = 0
                for (dy in -radius..radius) {
                    val py = (y + dy).coerceIn(0, h - 1)
                    val c = temp[py * w + x]
                    a += (c ushr 24) and 0xFF
                    r += (c ushr 16) and 0xFF
                    g += (c ushr 8) and 0xFF
                    b += c and 0xFF
                    count++
                }
                pixels[y * w + x] = ((a / count) shl 24) or ((r / count) shl 16) or ((g / count) shl 8) or (b / count)
            }
        }
    }
}
