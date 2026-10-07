package com.aurapaint.studio.color

object PalettePresets {
    val defaultPalettes: List<ColorPalette> = listOf(
        ColorPalette(
            id = "palette_studio_essentials",
            name = "Studio Essentials",
            colors = listOf(
                0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF7A7E85.toInt(),
                0xFFFF334B.toInt(), 0xFFFF7B25.toInt(), 0xFFFFD23F.toInt(),
                0xFF2EC4B6.toInt(), 0xFF3D7BFF.toInt(), 0xFF8A5CFF.toInt(),
                0xFFFF4D8D.toInt(), 0xFF6D4C41.toInt(), 0xFF3E2723.toInt()
            )
        ),
        ColorPalette(
            id = "palette_manga_ink",
            name = "Manga & Screentone",
            colors = listOf(
                0xFF0F1115.toInt(), 0xFF2A2D34.toInt(), 0xFF4A4E58.toInt(),
                0xFF727785.toInt(), 0xFFA0A6B6.toInt(), 0xFFD0D5E0.toInt(),
                0xFFF4F6FB.toInt(), 0xFFE53935.toInt(), 0xFF1E88E5.toInt()
            )
        ),
        ColorPalette(
            id = "palette_cyberpunk",
            name = "Cyberpunk Neon",
            colors = listOf(
                0xFF0D0221.toInt(), 0xFF0F084B.toInt(), 0xFF26408B.toInt(),
                0xFF00F0FF.toInt(), 0xFF7000FF.toInt(), 0xFFFF007F.toInt(),
                0xFFFFE600.toInt(), 0xFF00FFA3.toInt()
            )
        ),
        ColorPalette(
            id = "palette_skin_tones",
            name = "Portrait Skin Tones",
            colors = listOf(
                0xFFFFDFC4.toInt(), 0xFFF0D5BE.toInt(), 0xFFEECEB3.toInt(),
                0xFFE1B899.toInt(), 0xFFCE967C.toInt(), 0xFFB47556.toInt(),
                0xFF8D5524.toInt(), 0xFF5C3818.toInt(), 0xFF3B1E08.toInt()
            )
        ),
        ColorPalette(
            id = "palette_anime_pastel",
            name = "Anime Pastel",
            colors = listOf(
                0xFFFFB5E8.toInt(), 0xFFFFC9DE.toInt(), 0xFFFFD6A5.toInt(),
                0xFFFDFFB6.toInt(), 0xFFCAFFBF.toInt(), 0xFF9BF6FF.toInt(),
                0xFFA0C4FF.toInt(), 0xFFBDB2FF.toInt(), 0xFFFFC6FF.toInt()
            )
        )
    )
}
