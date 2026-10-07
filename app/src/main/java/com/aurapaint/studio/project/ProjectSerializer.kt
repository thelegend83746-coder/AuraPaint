package com.aurapaint.studio.project

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.aurapaint.studio.core.util.BitmapUtils
import com.aurapaint.studio.layers.BlendMode
import com.aurapaint.studio.layers.LayerManager
import com.aurapaint.studio.layers.RasterLayer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

data class LayerMetadata(
    val id: String,
    val name: String,
    val isVisible: Boolean,
    val opacity: Float,
    val isLocked: Boolean,
    val isAlphaLocked: Boolean,
    val isClippingMask: Boolean,
    val blendMode: String,
    val imageFileName: String
)

data class ProjectFileHeader(
    val metadata: ProjectMetadata,
    val layers: List<LayerMetadata>
)

object ProjectSerializer {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun saveProject(
        projectDir: File,
        metadata: ProjectMetadata,
        layerManager: LayerManager
    ): Boolean {
        return try {
            projectDir.mkdirs()

            val layerMetaList = mutableListOf<LayerMetadata>()
            val layers = layerManager.layers

            for ((index, layer) in layers.withIndex()) {
                val fileName = "layer_${index}_${layer.id}.png"
                val layerFile = File(projectDir, fileName)

                if (layer is RasterLayer) {
                    BitmapUtils.saveBitmapAsPng(layer.bitmap, layerFile)
                } else {
                    // Render vector or other layer to PNG for safe persistence
                    val temp = BitmapUtils.createEmptyBitmap(layerManager.canvasWidth, layerManager.canvasHeight)
                    val c = Canvas(temp)
                    if (layer is com.aurapaint.studio.layers.VectorLayer) {
                        layer.renderToCanvas(c)
                    }
                    BitmapUtils.saveBitmapAsPng(temp, layerFile)
                    temp.recycle()
                }

                layerMetaList.add(
                    LayerMetadata(
                        id = layer.id,
                        name = layer.name,
                        isVisible = layer.isVisible,
                        opacity = layer.opacity,
                        isLocked = layer.isLocked,
                        isAlphaLocked = layer.isAlphaLocked,
                        isClippingMask = layer.isClippingMask,
                        blendMode = layer.blendMode.name,
                        imageFileName = fileName
                    )
                )
            }

            metadata.layerCount = layers.size
            metadata.modifiedAt = System.currentTimeMillis()

            // Save composite thumbnail
            val compBmp = BitmapUtils.createEmptyBitmap(layerManager.canvasWidth, layerManager.canvasHeight)
            val compCanvas = Canvas(compBmp)
            layerManager.compositor.composite(layers, compCanvas, Color.WHITE)
            val thumb = BitmapUtils.createThumbnail(compBmp, 320)
            BitmapUtils.saveBitmapAsPng(thumb, File(projectDir, "thumbnail.png"))
            compBmp.recycle()
            thumb.recycle()

            // Write header JSON
            val header = ProjectFileHeader(metadata, layerMetaList)
            val jsonString = gson.toJson(header)
            File(projectDir, "project.json").writeText(jsonString)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadProject(
        projectDir: File
    ): Pair<ProjectMetadata, List<RasterLayer>>? {
        return try {
            val jsonFile = File(projectDir, "project.json")
            if (!jsonFile.exists()) return null

            val header = gson.fromJson(jsonFile.readText(), ProjectFileHeader::class.java)
            val rasterLayers = mutableListOf<RasterLayer>()

            for (lMeta in header.layers) {
                val imgFile = File(projectDir, lMeta.imageFileName)
                val bmp = BitmapUtils.loadBitmapFromFile(imgFile)
                    ?: BitmapUtils.createEmptyBitmap(header.metadata.width, header.metadata.height)

                val layer = RasterLayer(
                    id = lMeta.id,
                    name = lMeta.name,
                    width = header.metadata.width,
                    height = header.metadata.height,
                    initialBitmap = bmp
                ).apply {
                    isVisible = lMeta.isVisible
                    opacity = lMeta.opacity
                    isLocked = lMeta.isLocked
                    isAlphaLocked = lMeta.isAlphaLocked
                    isClippingMask = lMeta.isClippingMask
                    blendMode = try { BlendMode.valueOf(lMeta.blendMode) } catch (_: Exception) { BlendMode.NORMAL }
                }
                rasterLayers.add(layer)
            }

            Pair(header.metadata, rasterLayers)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
