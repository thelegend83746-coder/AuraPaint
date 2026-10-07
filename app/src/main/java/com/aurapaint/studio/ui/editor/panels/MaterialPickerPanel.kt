package com.aurapaint.studio.ui.editor.panels

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.layers.RasterLayer
import com.aurapaint.studio.materials.MaterialAsset
import com.aurapaint.studio.materials.MaterialManager

@Composable
fun MaterialPickerPanel(
    activeLayer: RasterLayer?,
    onClose: () -> Unit
) {
    if (activeLayer == null) return

    val materials = remember { MaterialManager.presets }
    var scale by remember { mutableStateOf(1.0f) }
    var opacity by remember { mutableStateOf(0.8f) }

    fun applyMaterial(material: MaterialAsset) {
        val patternBmp = material.renderPattern(activeLayer.width, activeLayer.height, scale)
        val canvas = activeLayer.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            alpha = (opacity * 255).toInt()
        }
        canvas.drawBitmap(patternBmp, 0f, 0f, paint)
        activeLayer.markDirty()
        patternBmp.recycle()
        onClose()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(440.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(1.dp, PanelBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = PanelSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Materials & Screentones",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scale & Opacity controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pattern Scale: ${(scale * 100).toInt()}%", style = Typography.bodyMedium, color = TextSecondary)
                    Slider(
                        value = scale,
                        onValueChange = { scale = it },
                        valueRange = 0.5f..3.0f,
                        colors = SliderDefaults.colors(thumbColor = ElectricBlue, activeTrackColor = ElectricBlue)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Opacity: ${(opacity * 100).toInt()}%", style = Typography.bodyMedium, color = TextSecondary)
                    Slider(
                        value = opacity,
                        onValueChange = { opacity = it },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = ElectricBlue, activeTrackColor = ElectricBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Material Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(materials) { mat ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
                            .clickable { applyMaterial(mat) },
                        color = PanelSurfaceElevated
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = mat.name,
                                style = Typography.bodyLarge,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mat.category.title,
                                style = Typography.labelSmall,
                                color = ElectricBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
