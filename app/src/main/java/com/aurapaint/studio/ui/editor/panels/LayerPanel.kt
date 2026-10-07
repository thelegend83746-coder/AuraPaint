package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.layers.BlendMode
import com.aurapaint.studio.layers.LayerManager
import com.aurapaint.studio.layers.RasterLayer

@Composable
fun LayerPanel(
    layerManager: LayerManager,
    onClose: () -> Unit
) {
    val layers by layerManager.layersState.collectAsState()
    val activeIdx by layerManager.activeLayerIndex.collectAsState()
    val activeLayer = layerManager.getActiveLayer()

    var showBlendDropdown by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(1.dp, PanelBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = PanelSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Layers (${layers.size})",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                Row {
                    // Add Raster
                    IconButton(onClick = { layerManager.addRasterLayer() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Layer", tint = ElectricBlue)
                    }
                    // Duplicate
                    IconButton(onClick = { layerManager.duplicateActiveLayer() }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextSecondary)
                    }
                    // Merge Down
                    IconButton(onClick = { layerManager.mergeDown(activeIdx) }) {
                        Icon(Icons.Default.CallMerge, contentDescription = "Merge Down", tint = TextSecondary)
                    }
                    // Delete
                    IconButton(onClick = { layerManager.deleteActiveLayer() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError)
                    }
                    // Close
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Layer Controls (Opacity & Blend Mode)
            if (activeLayer != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Opacity Slider
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Opacity", style = Typography.bodyMedium, color = TextSecondary)
                            Text("${(activeLayer.opacity * 100).toInt()}%", style = Typography.bodyMedium, fontWeight = FontWeight.Bold, color = ElectricBlue)
                        }
                        Slider(
                            value = activeLayer.opacity,
                            onValueChange = {
                                activeLayer.opacity = it
                                (activeLayer as? RasterLayer)?.markDirty()
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue,
                                inactiveTrackColor = PanelBorder
                            )
                        )
                    }

                    // Blend Mode Dropdown Trigger
                    Box {
                        OutlinedButton(
                            onClick = { showBlendDropdown = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(PanelBorder))
                        ) {
                            Text(activeLayer.blendMode.displayName, fontSize = 12.sp)
                        }

                        DropdownMenu(
                            expanded = showBlendDropdown,
                            onDismissRequest = { showBlendDropdown = false },
                            modifier = Modifier.background(PanelSurfaceElevated)
                        ) {
                            BlendMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode.displayName, color = TextPrimary) },
                                    onClick = {
                                        activeLayer.blendMode = mode
                                        showBlendDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Alpha Lock & Clipping Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = activeLayer.isAlphaLocked,
                        onClick = { activeLayer.isAlphaLocked = !activeLayer.isAlphaLocked },
                        label = { Text("Alpha Lock") },
                        leadingIcon = {
                            Icon(if (activeLayer.isAlphaLocked) Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue)
                    )

                    FilterChip(
                        selected = activeLayer.isClippingMask,
                        onClick = { activeLayer.isClippingMask = !activeLayer.isClippingMask },
                        label = { Text("Clipping") },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = VioletAccent)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Layer Stack List (Top layer displayed first)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reverse iterate so highest index is on top
                val reversedIndices = layers.indices.reversed().toList()

                itemsIndexed(reversedIndices) { _, layerIndex ->
                    val layer = layers[layerIndex]
                    val isSelected = layerIndex == activeIdx

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricBlue.copy(alpha = 0.2f) else PanelSurfaceElevated)
                            .border(1.dp, if (isSelected) ElectricBlue else PanelBorder, RoundedCornerShape(12.dp))
                            .clickable { layerManager.setActiveLayerIndex(layerIndex) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Visibility Eye Toggle
                            IconButton(
                                onClick = { layer.isVisible = !layer.isVisible },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Visibility",
                                    tint = if (layer.isVisible) ElectricBlue else TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Thumbnail
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CanvasWorkspace)
                                    .border(1.dp, PanelBorder, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                val thumb = layer.getThumbnail()
                                if (thumb != null && !thumb.isRecycled) {
                                    Image(
                                        bitmap = thumb.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = layer.name,
                                    style = Typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ElectricBlue else TextPrimary
                                )
                                Text(
                                    text = "${layer.blendMode.displayName} • ${(layer.opacity * 100).toInt()}%",
                                    style = Typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Badges for Alpha lock / Clipping
                        Row {
                            if (layer.isClippingMask) {
                                Icon(Icons.Default.ContentCut, contentDescription = null, tint = VioletAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            if (layer.isAlphaLocked) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
