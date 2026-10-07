package com.aurapaint.studio.project

import com.aurapaint.studio.core.AppConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

class ProjectRepository(
    private val baseDir: File = File(AppConfig.BASE_PROJECTS_DIR)
) {
    private val gson = Gson()

    private val _projects = MutableStateFlow<List<ProjectMetadata>>(emptyList())
    val projects: StateFlow<List<ProjectMetadata>> = _projects.asStateFlow()

    init {
        baseDir.mkdirs()
    }

    suspend fun refreshProjects() = withContext(Dispatchers.IO) {
        val list = mutableListOf<ProjectMetadata>()
        val projectFolders = baseDir.listFiles { file -> file.isDirectory } ?: emptyArray()

        for (folder in projectFolders) {
            val jsonFile = File(folder, "project.json")
            if (jsonFile.exists()) {
                try {
                    val header = gson.fromJson(jsonFile.readText(), ProjectFileHeader::class.java)
                    list.add(header.metadata)
                } catch (_: Exception) {}
            }
        }

        // Sort by last modified descending
        _projects.value = list.sortedByDescending { it.modifiedAt }
    }

    suspend fun createNewProject(
        name: String,
        width: Int,
        height: Int,
        dpi: Int = 300,
        isAnimation: Boolean = false
    ): ProjectMetadata = withContext(Dispatchers.IO) {
        val meta = ProjectMetadata(
            name = name.ifBlank { "Untitled Artwork" },
            width = width,
            height = height,
            dpi = dpi,
            isAnimation = isAnimation
        )
        val projectDir = File(baseDir, meta.id)
        projectDir.mkdirs()

        // Create empty header
        val header = ProjectFileHeader(meta, emptyList())
        File(projectDir, "project.json").writeText(gson.toJson(header))

        refreshProjects()
        meta
    }

    suspend fun deleteProject(projectId: String): Boolean = withContext(Dispatchers.IO) {
        val projectDir = File(baseDir, projectId)
        val deleted = projectDir.deleteRecursively()
        if (deleted) refreshProjects()
        deleted
    }

    suspend fun duplicateProject(projectId: String): ProjectMetadata? = withContext(Dispatchers.IO) {
        val sourceDir = File(baseDir, projectId)
        if (!sourceDir.exists()) return@withContext null

        val jsonFile = File(sourceDir, "project.json")
        if (!jsonFile.exists()) return@withContext null

        val header = gson.fromJson(jsonFile.readText(), ProjectFileHeader::class.java)
        val newMeta = header.metadata.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${header.metadata.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )

        val targetDir = File(baseDir, newMeta.id)
        sourceDir.copyRecursively(targetDir, overwrite = true)

        val newHeader = header.copy(metadata = newMeta)
        File(targetDir, "project.json").writeText(gson.toJson(newHeader))

        refreshProjects()
        newMeta
    }

    fun getProjectDir(projectId: String): File {
        return File(baseDir, projectId)
    }
}
