package com.aurapaint.studio.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.R
import com.aurapaint.studio.canvas.CanvasController
import com.aurapaint.studio.core.theme.*

@Composable
fun TopEditorBar(
    controller: CanvasController,
    onBack: () -> Unit,
    onToggleReference: () -> Unit,
    onToggleSymmetry: () -> Unit,
    onToggleRulers: () -> Unit,
    onToggleMaterials: () -> Unit,
    onToggleFilters: () -> Unit,
    onToggleCurves: () -> Unit,
    onExport: () -> Unit
) {
    val canUndo by controller.undoManager.canUndo.collectAsState()
    val canRedo by controller.undoManager.canRedo.collectAsState()

    var showViewMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PanelBorder, RoundedCornerShape(16.dp)),
        color = PanelSurface.copy(alpha = 0.94f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Group: Back, Undo, Redo
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Gallery", tint = TextPrimary)
                }

                IconButton(
                    onClick = {
                        controller.undoManager.undo()
                        controller.onInvalidateView?.invoke()
                    },
                    enabled = canUndo
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) TextPrimary else TextMuted
                    )
                }

                IconButton(
                    onClick = {
                        controller.undoManager.redo()
                        controller.onInvalidateView?.invoke()
                    },
                    enabled = canRedo
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) TextPrimary else TextMuted
                    )
                }
            }

            // Right Group: Navigation presets, Reference, Symmetry, Rulers, More, Export
            Row(verticalAlignment = Alignment.CenterVertically) {
                // View Transform Menu (Fit Screen, Reset Rotation)
                Box {
                    IconButton(onClick = { showViewMenu = true }) {
                        Icon(painterResource(R.drawable.ic_fit_screen), contentDescription = "View Controls", tint = TextSecondary)
                    }

                    DropdownMenu(
                        expanded = showViewMenu,
                        onDismissRequest = { showViewMenu = false },
                        modifier = Modifier.background(PanelSurfaceElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Fit Canvas to Screen", color = TextPrimary) },
                            onClick = {
                                showViewMenu = false
                                controller.viewTransform.fitToScreen()
                                controller.onInvalidateView?.invoke()
                            },
                            leadingIcon = { Icon(painterResource(R.drawable.ic_fit_screen), contentDescription = null, tint = ElectricBlue) }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset Rotation", color = TextPrimary) },
                            onClick = {
                                showViewMenu = false
                                controller.viewTransform.resetRotation()
                                controller.onInvalidateView?.invoke()
                            },
                            leadingIcon = { Icon(painterResource(R.drawable.ic_rotate_reset), contentDescription = null, tint = ElectricBlue) }
                        )
                    }
                }

                // Reference window trigger
                IconButton(onClick = onToggleReference) {
                    Icon(painterResource(R.drawable.ic_reference), contentDescription = "Reference Window", tint = TextSecondary)
                }

                // Symmetry trigger
                IconButton(onClick = onToggleSymmetry) {
                    Icon(painterResource(R.drawable.ic_symmetry), contentDescription = "Symmetry", tint = TextSecondary)
                }

                // Ruler trigger
                IconButton(onClick = onToggleRulers) {
                    Icon(painterResource(R.drawable.ic_ruler), contentDescription = "Rulers", tint = TextSecondary)
                }

                // More Menu (Materials, Filters, Curves)
                Box {
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Tools", tint = TextSecondary)
                    }

                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false },
                        modifier = Modifier.background(PanelSurfaceElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Materials & Textures", color = TextPrimary) },
                            onClick = {
                                showMoreMenu = false
                                onToggleMaterials()
                            },
                            leadingIcon = { Icon(painterResource(R.drawable.ic_materials), contentDescription = null, tint = ElectricBlue) }
                        )
                        DropdownMenuItem(
                            text = { Text("Filters & Adjustments", color = TextPrimary) },
                            onClick = {
                                showMoreMenu = false
                                onToggleFilters()
                            },
                            leadingIcon = { Icon(painterResource(R.drawable.ic_filters), contentDescription = null, tint = ElectricBlue) }
                        )
                        DropdownMenuItem(
                            text = { Text("Tone Curves", color = TextPrimary) },
                            onClick = {
                                showMoreMenu = false
                                onToggleCurves()
                            },
                            leadingIcon = { Icon(painterResource(R.drawable.ic_curves), contentDescription = null, tint = ElectricBlue) }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Export Button
                Button(
                    onClick = onExport,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export", color = Color.White)
                }
            }
        }
    }
}
