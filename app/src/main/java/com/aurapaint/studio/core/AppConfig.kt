package com.aurapaint.studio.core

import android.content.Context
import java.io.File

/**
 * Centralized application configuration, identity constants, and defaults.
 */
object AppConfig {
    const val APP_NAME = "AuraPaint Studio"
    const val APP_VERSION = "1.0.0"
    const val APP_TAGLINE = "Professional Mobile Digital Art Studio"

    // Storage folders
    const val BASE_PROJECTS_DIR = "/storage/emulated/0/test-folder/projects"
    const val BASE_EXPORTS_DIR = "/storage/emulated/0/test-folder/exports"
    const val BASE_BACKUP_DIR = "/storage/emulated/0/test-folder/backups"

    fun resolveProjectsDir(context: Context?): File {
        return resolveSafeDir(BASE_PROJECTS_DIR, "projects", context)
    }

    fun resolveExportsDir(context: Context?): File {
        return resolveSafeDir(BASE_EXPORTS_DIR, "exports", context)
    }

    fun resolveBackupsDir(context: Context?): File {
        return resolveSafeDir(BASE_BACKUP_DIR, "backups", context)
    }

    private fun resolveSafeDir(preferredPath: String, fallbackSubDir: String, context: Context?): File {
        val target = File(preferredPath)
        try {
            if (target.exists() || target.mkdirs()) {
                val testFile = File(target, ".test_perm")
                testFile.writeText("ok")
                testFile.delete()
                return target
            }
        } catch (_: Exception) {
            // Permission restricted, gracefully fall back to app-specific directory
        }

        val appDir = context?.getExternalFilesDir(null) ?: context?.filesDir
        val fallback = File(appDir ?: File("/data/data/com.aurapaint.studio/files"), fallbackSubDir)
        fallback.mkdirs()
        return fallback
    }

    // Engine Constants
    const val DEFAULT_CANVAS_WIDTH = 1920
    const val DEFAULT_CANVAS_HEIGHT = 1080
    const val DEFAULT_CANVAS_DPI = 300
    const val MAX_CANVAS_DIMENSION = 4096
    const val MIN_CANVAS_DIMENSION = 256

    // History limit
    const val MAX_UNDO_STACK_SIZE = 30

    // Animation defaults
    const val DEFAULT_ANIMATION_FPS = 12
    const val MAX_ANIMATION_FPS = 30
    const val MIN_ANIMATION_FPS = 1

    // Autosave interval (milliseconds)
    const val AUTOSAVE_INTERVAL_MS = 60_000L // 1 minute
}
