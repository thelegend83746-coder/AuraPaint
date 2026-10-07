package com.aurapaint.studio.project

data class PresetCanvas(
    val name: String,
    val width: Int,
    val height: Int,
    val dpi: Int = 300,
    val description: String = ""
) {
    companion object {
        val defaultPresets = listOf(
            PresetCanvas("Square (Instagram / Avatar)", 2048, 2048, 300, "1:1 format for social avatars and posts"),
            PresetCanvas("FHD Portrait", 1080, 1920, 300, "9:16 mobile portrait format"),
            PresetCanvas("FHD Landscape", 1920, 1080, 300, "16:9 standard landscape widescreen"),
            PresetCanvas("4K Ultra HD", 3840, 2160, 300, "Ultra high resolution for fine detail"),
            PresetCanvas("Manga Page (B5)", 2400, 3400, 350, "Standard Japanese comic publication scale"),
            PresetCanvas("Animation (FHD)", 1920, 1080, 144, "Standard frame-by-frame animation canvas"),
            PresetCanvas("Phone Wallpaper", 1440, 3200, 400, "Tall aspect ratio AMOLED wallpaper")
        )
    }
}
