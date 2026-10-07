package com.aurapaint.studio

import android.app.Application
import com.aurapaint.studio.core.AppConfig

class AuraPaintApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            AppConfig.resolveProjectsDir(this)
            AppConfig.resolveExportsDir(this)
            AppConfig.resolveBackupsDir(this)
        } catch (_: Exception) {}
    }
}
