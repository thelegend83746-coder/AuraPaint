package com.aurapaint.studio.ui.editor

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aurapaint.studio.animation.AnimationTimeline
import com.aurapaint.studio.brush.BrushPreset
import com.aurapaint.studio.brush.BrushRegistry
import com.aurapaint.studio.canvas.CanvasController
import com.aurapaint.studio.canvas.CanvasView
import com.aurapaint.studio.core.AppConfig
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.export.ArtworkExporter
import com.aurapaint.studio.export.ExportFormat
import com.aurapaint.studio.filters.FilterProcessor
import com.aurapaint.studio.project.ProjectMetadata
import com.aurapaint.studio.project.ProjectRepository
import com.aurapaint.studio.project.ProjectSerializer
import com.aurapaint.studio.reference.ReferenceManager
import com.aurapaint.studio.text.TextToolManager
import com.aurapaint.studio.ui.editor.panels.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class ActivePanel {
    NONE,
    BRUSH_PICKER,
    BRUSH_EDITOR,
    COLOR_PICKER,
    LAYER_PANEL,
    FILTER_PANEL,
    CURVE_PANEL,
    MATERIAL_PANEL,
    SYMMETRY_PANEL,
    RULER_PANEL,
    SHAPE_PANEL,
    TEXT_PANEL,
    ANIMATION_TIMELINE
}

@Composable
fun EditorScreen(
    projectId: String,
    repository: ProjectRepository,
    onBackToGallery: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val projectDir = remember(projectId) { repository.getProjectDir(projectId) }

    // Load or create project metadata & layers
    val initialMeta = remember(projectId) {
        repository.getProjectMetadata(projectId) ?: ProjectMetadata(
            id = projectId,
            width = AppConfig.DEFAULT_CANVAS_WIDTH,
            height = AppConfig.DEFAULT_CANVAS_HEIGHT
        )
    }
    var projectMeta by remember { mutableStateOf(initialMeta) }

    val canvasController = remember(projectId) {
        val sessionDir = File(projectDir, "timelapse_session")
        CanvasController(
            canvasWidth = initialMeta.width,
            canvasHeight = initialMeta.height,
            sessionDir = sessionDir
        )
    }

    val referenceManager = remember { ReferenceManager() }
    val filterProcessor = remember { FilterProcessor() }
    val textToolManager = remember { TextToolManager() }
    val exporter = remember { ArtworkExporter() }

    var activeBrush by remember { mutableStateOf(BrushRegistry.defaultPresets[3]) } // G-Pen
    var activePanel by remember { mutableStateOf(ActivePanel.NONE) }
    var isAnimationEnabled by remember { mutableStateOf(initialMeta.isAnimation) }

    // Initialize animation timeline if needed
    LaunchedEffect(isAnimationEnabled) {
        if (isAnimationEnabled && canvasController.animationTimeline == null) {
            canvasController.animationTimeline = AnimationTimeline(
                canvasWidth = projectMeta.width,
                canvasHeight = projectMeta.height,
                scope = coroutineScope
            )
        }
    }

    // Load existing project data from disk if exists
    LaunchedEffect(projectId) {
        try {
            val loaded = ProjectSerializer.loadProject(projectDir)
            if (loaded != null) {
                projectMeta = loaded.first
                if (loaded.second.isNotEmpty()) {
                    canvasController.layerManager.replaceLayers(loaded.second)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    // Auto-save periodic worker
    LaunchedEffect(projectId) {
        while (isActive) {
            delay(AppConfig.AUTOSAVE_INTERVAL_MS)
            ProjectSerializer.saveProject(projectDir, projectMeta, canvasController.layerManager)
        }
    }

    fun saveArtwork() {
        coroutineScope.launch {
            val success = ProjectSerializer.saveProject(projectDir, projectMeta, canvasController.layerManager)
            if (success) {
                Toast.makeText(context, "Project Saved Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun exportArtwork() {
        coroutineScope.launch {
            val file = exporter.exportArtwork(canvasController.layerManager, projectMeta.name, ExportFormat.PNG)
            Toast.makeText(context, "Exported to: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // High-performance Native Canvas View
        AndroidView(
            factory = { ctx ->
                CanvasView(ctx, canvasController)
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top Floating Editor Bar
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            TopEditorBar(
                controller = canvasController,
                onBack = {
                    saveArtwork()
                    onBackToGallery()
                },
                onToggleReference = {
                    referenceManager.toggleVisibility()
                },
                onToggleSymmetry = {
                    activePanel = if (activePanel == ActivePanel.SYMMETRY_PANEL) ActivePanel.NONE else ActivePanel.SYMMETRY_PANEL
                },
                onToggleRulers = {
                    activePanel = if (activePanel == ActivePanel.RULER_PANEL) ActivePanel.NONE else ActivePanel.RULER_PANEL
                },
                onToggleMaterials = {
                    activePanel = if (activePanel == ActivePanel.MATERIAL_PANEL) ActivePanel.NONE else ActivePanel.MATERIAL_PANEL
                },
                onToggleFilters = {
                    activePanel = if (activePanel == ActivePanel.FILTER_PANEL) ActivePanel.NONE else ActivePanel.FILTER_PANEL
                },
                onToggleCurves = {
                    activePanel = if (activePanel == ActivePanel.CURVE_PANEL) ActivePanel.NONE else ActivePanel.CURVE_PANEL
                },
                onExport = { exportArtwork() }
            )
        }

        // Floating Reference PIP Window
        ReferenceWindowView(
            referenceManager = referenceManager,
            onClose = { referenceManager.close() }
        )

        // Quick Radial Shortcut Menu
        QuickRadialMenu(
            controller = canvasController,
            onOpenColor = { activePanel = ActivePanel.COLOR_PICKER },
            onOpenLayers = { activePanel = ActivePanel.LAYER_PANEL }
        )

        // Bottom Floating Dock Toolbar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomEditorBar(
                controller = canvasController,
                activeBrush = activeBrush,
                onOpenBrushPicker = { activePanel = ActivePanel.BRUSH_PICKER },
                onOpenColorPicker = { activePanel = ActivePanel.COLOR_PICKER },
                onOpenLayerPanel = { activePanel = ActivePanel.LAYER_PANEL },
                onOpenShapePanel = { activePanel = ActivePanel.SHAPE_PANEL },
                onOpenTextPanel = { activePanel = ActivePanel.TEXT_PANEL },
                onToggleAnimationTimeline = {
                    isAnimationEnabled = true
                    activePanel = if (activePanel == ActivePanel.ANIMATION_TIMELINE) ActivePanel.NONE else ActivePanel.ANIMATION_TIMELINE
                }
            )
        }

        // Animated Slide-up Panels
        AnimatedVisibility(
            visible = activePanel != ActivePanel.NONE,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            when (activePanel) {
                ActivePanel.BRUSH_PICKER -> {
                    BrushPickerPanel(
                        selectedBrush = activeBrush,
                        onBrushSelected = {
                            activeBrush = it
                            canvasController.brushProperties = it.properties
                            activePanel = ActivePanel.NONE
                        },
                        onOpenBrushEditor = { activePanel = ActivePanel.BRUSH_EDITOR },
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.BRUSH_EDITOR -> {
                    BrushEditorPanel(
                        properties = canvasController.brushProperties,
                        onPropertiesChanged = { canvasController.brushProperties = it },
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.COLOR_PICKER -> {
                    ColorPickerPanel(
                        colorManager = canvasController.colorManager,
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.LAYER_PANEL -> {
                    LayerPanel(
                        layerManager = canvasController.layerManager,
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.FILTER_PANEL -> {
                    FilterPanel(
                        activeLayer = canvasController.layerManager.getActiveRasterLayer(),
                        filterProcessor = filterProcessor,
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.CURVE_PANEL -> {
                    CurveEditorPanel(
                        activeLayer = canvasController.layerManager.getActiveRasterLayer(),
                        filterProcessor = filterProcessor,
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.MATERIAL_PANEL -> {
                    MaterialPickerPanel(
                        activeLayer = canvasController.layerManager.getActiveRasterLayer(),
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.SYMMETRY_PANEL -> {
                    SymmetryPanel(
                        symmetryManager = canvasController.symmetryManager,
                        canvasWidth = canvasController.canvasWidth.toFloat(),
                        canvasHeight = canvasController.canvasHeight.toFloat(),
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.RULER_PANEL -> {
                    RulerPanel(
                        rulerManager = canvasController.rulerManager,
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.SHAPE_PANEL -> {
                    ShapePanel(
                        shapeTool = canvasController.shapeTool,
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.TEXT_PANEL -> {
                    TextPanel(
                        activeLayer = canvasController.layerManager.getActiveRasterLayer(),
                        currentColor = canvasController.colorManager.currentColor.value,
                        textManager = textToolManager,
                        onClose = { activePanel = ActivePanel.NONE }
                    )
                }
                ActivePanel.ANIMATION_TIMELINE -> {
                    canvasController.animationTimeline?.let { timeline ->
                        AnimationTimelinePanel(
                            timeline = timeline,
                            onClose = { activePanel = ActivePanel.NONE }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
