package com.aurapaint.studio.project

import com.aurapaint.studio.core.AppConfig
import java.util.UUID

data class ProjectMetadata(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Untitled Artwork",
    val width: Int = AppConfig.DEFAULT_CANVAS_WIDTH,
    val height: Int = AppConfig.DEFAULT_CANVAS_HEIGHT,
    val dpi: Int = AppConfig.DEFAULT_CANVAS_DPI,
    val createdAt: Long = System.currentTimeMillis(),
    var modifiedAt: Long = System.currentTimeMillis(),
    var isFavorite: Boolean = false,
    var layerCount: Int = 1,
    var isAnimation: Boolean = false
)
