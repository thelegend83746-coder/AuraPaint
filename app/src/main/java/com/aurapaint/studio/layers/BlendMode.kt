package com.aurapaint.studio.layers

import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Xfermode

enum class BlendMode(val displayName: String) {
    NORMAL("Normal"),
    MULTIPLY("Multiply"),
    SCREEN("Screen"),
    OVERLAY("Overlay"),
    SOFT_LIGHT("Soft Light"),
    HARD_LIGHT("Hard Light"),
    ADD("Add (Linear Dodge)"),
    COLOR_DODGE("Color Dodge"),
    DARKEN("Darken"),
    LIGHTEN("Lighten"),
    DIFFERENCE("Difference"),
    EXCLUSION("Exclusion");

    /**
     * Maps to standard PorterDuff mode or returns fallback for custom compositing.
     */
    fun toPorterDuff(): PorterDuff.Mode {
        return when (this) {
            NORMAL -> PorterDuff.Mode.SRC_OVER
            MULTIPLY -> PorterDuff.Mode.MULTIPLY
            SCREEN -> PorterDuff.Mode.SCREEN
            ADD -> PorterDuff.Mode.ADD
            DARKEN -> PorterDuff.Mode.DARKEN
            LIGHTEN -> PorterDuff.Mode.LIGHTEN
            OVERLAY -> PorterDuff.Mode.OVERLAY
            else -> PorterDuff.Mode.SRC_OVER
        }
    }

    fun toXfermode(): Xfermode {
        return PorterDuffXfermode(toPorterDuff())
    }
}
