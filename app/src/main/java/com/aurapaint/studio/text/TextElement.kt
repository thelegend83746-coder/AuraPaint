package com.aurapaint.studio.text

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

data class TextElement(
    var text: String = "Artwork Text",
    var fontSize: Float = 48f,
    var color: Int = Color.BLACK,
    var isBold: Boolean = false,
    var isItalic: Boolean = false,
    var posX: Float = 100f,
    var posY: Float = 200f
)

class TextToolManager {
    var activeElement: TextElement? = null

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    fun render(canvas: Canvas, element: TextElement) {
        textPaint.textSize = element.fontSize
        textPaint.color = element.color

        val style = when {
            element.isBold && element.isItalic -> Typeface.BOLD_ITALIC
            element.isBold -> Typeface.BOLD
            element.isItalic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, style)

        // Support multiline text
        val lines = element.text.split("\n")
        var currentY = element.posY
        val lineSpacing = element.fontSize * 1.25f

        for (line in lines) {
            canvas.drawText(line, element.posX, currentY, textPaint)
            currentY += lineSpacing
        }
    }
}
