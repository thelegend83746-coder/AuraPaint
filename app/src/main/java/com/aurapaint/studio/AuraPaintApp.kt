package com.aurapaint.studio

import android.app.Application
import java.io.File

class AuraPaintApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ensure root workspace directories exist inside /storage/emulated/0/test-folder
        File("/storage/emulated/0/test-folder/projects").mkdirs()
        File("/storage/emulated/0/test-folder/exports").mkdirs()
        File("/storage/emulated/0/test-folder/backups").mkdirs()
    }
}
