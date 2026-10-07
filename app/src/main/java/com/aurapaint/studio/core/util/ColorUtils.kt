package com.aurapaint.studio.core.util

import android.graphics.Color
import java.util.Locale

object ColorUtils {
    /**
     * Converts RGB + Alpha into 32-bit ARGB integer.
     */
    fun toArgb(alpha: Int, red: Int, green: Int, blue: Int): Int {
        return (alpha and 0xFF shl 24) or
               (red and 0xFF shl 16) or
               (green and 0xFF shl 8) or
               (blue and 0xFF)
    }

    /**
     * Formats ARGB to Hex string (e.g. #FF3D7BFF or #3D7BFF).
     */
    fun toHexString(color: Int, includeAlpha: Boolean = false): String {
        return if (includeAlpha) {
            String.format(Locale.ROOT, "#%08X", color)
        } else {
            String.format(Locale.ROOT, "#%06X", 0xFFFFFF and color)
        }
    }

    /**
     * Parses Hex string into ARGB color integer.
     */
    fun parseColor(hex: String, defaultColor: Int = Color.BLACK): Int {
        return try {
            val cleanHex = if (!hex.startsWith("#")) "#$hex" else hex
            Color.parseColor(cleanHex)
        } catch (_: Exception) {
            defaultColor
        }
    }

    /**
     * Converts HSV components to ARGB color integer.
     * h: 0..360, s: 0..1, v: 0..1
     */
    fun hsvToColor(h: Float, s: Float, v: Float, alpha: Float = 1.0f): Int {
        val hsv = floatArrayOf(h, s, v)
        val argb = Color.HSVToColor(hsv)
        val alphaInt = (alpha.coerceIn(0f, 1f) * 255).toInt()
        return (argb and 0x00FFFFFF) or (alphaInt shl 24)
    }

    /**
     * Extracts HSV from ARGB color integer.
     */
    fun colorToHsv(color: Int): FloatArray {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv
    }
}
