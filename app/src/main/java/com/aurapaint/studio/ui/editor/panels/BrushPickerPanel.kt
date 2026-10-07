package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.brush.BrushCategory
import com.aurapaint.studio.brush.BrushPreset
import com.aurapaint.studio.brush.BrushRegistry
import com.aurapaint.studio.core.theme.*

@Composable
fun BrushPickerPanel(
    selectedBrush: BrushPreset,
    onBrushSelected: (BrushPreset) -> Unit,
    onOpenBrushEditor: () -> Unit,
    onClose: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<BrushCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val allBrushes = remember { BrushRegistry.defaultPresets }

    val filteredBrushes = remember(selectedCategory, searchQuery, showFavoritesOnly) {
        allBrushes.filter { brush ->
            (selectedCategory == null || brush.category == selectedCategory) &&
                    (searchQuery.isBlank() || brush.name.contains(searchQuery, ignoreCase = true)) &&
                    (!showFavoritesOnly || brush.isFavorite)
        }
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
                    text = "Brush Library",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                Row {
                    IconButton(onClick = onOpenBrushEditor) {
                        Icon(Icons.Default.Settings, contentDescription = "Edit Brush", tint = ElectricBlue)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Brushes") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(BrushCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.title.replace("&amp;", "&")) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Brush Presets List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredBrushes, key = { it.id }) { brush ->
                    val isSelected = brush.id == selectedBrush.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricBlue.copy(alpha = 0.2f) else PanelSurfaceElevated)
                            .border(1.dp, if (isSelected) ElectricBlue else PanelBorder, RoundedCornerShape(12.dp))
                            .clickable { onBrushSelected(brush) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = brush.name,
                                    style = Typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ElectricBlue else TextPrimary
                                )
                                if (brush.isFavorite) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = StatusWarning,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = brush.description,
                                style = Typography.bodyMedium,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        // Real Dynamic Brush Stroke Preview Canvas
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CanvasWorkspace),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = brush.properties.size.coerceIn(2f, 16f)
                                val strokeAlpha = brush.properties.opacity.coerceIn(0.2f, 1f)
                                drawLine(
                                    color = if (isSelected) ElectricBlue else Color.White.copy(alpha = strokeAlpha),
                                    start = Offset(10f, size.height / 2f),
                                    end = Offset(size.width - 10f, size.height / 2f),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
