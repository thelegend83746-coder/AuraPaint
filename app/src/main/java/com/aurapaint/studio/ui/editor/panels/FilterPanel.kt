package com.aurapaint.studio.ui.editor.panels

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.filters.FilterProcessor
import com.aurapaint.studio.filters.FilterType
import com.aurapaint.studio.layers.RasterLayer
import kotlinx.coroutines.launch

@Composable
fun FilterPanel(
    activeLayer: RasterLayer?,
    filterProcessor: FilterProcessor,
    onClose: () -> Unit
) {
    if (activeLayer == null) return

    val coroutineScope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf(FilterType.BRIGHTNESS_CONTRAST) }
    var param1 by remember { mutableStateOf(0f) }
    var param2 by remember { mutableStateOf(0f) }

    // Original bitmap snapshot for live comparison and cancel
    val originalSnapshot = remember(activeLayer) {
        activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }

    var isProcessing by remember { mutableStateOf(false) }

    fun processFilter() {
        if (isProcessing) return
        isProcessing = true
        coroutineScope.launch {
            val result = filterProcessor.applyFilter(
                source = originalSnapshot,
                type = selectedFilter,
                param1 = param1,
                param2 = param2
            )
            activeLayer.replaceBitmap(result)
            isProcessing = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
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
                    text = "Filters & Adjustments",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                Row {
                    IconButton(onClick = {
                        // Apply: keep current filtered result and clean up
                        originalSnapshot.recycle()
                        onClose()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Apply", tint = StatusSuccess)
                    }
                    IconButton(onClick = {
                        // Cancel: revert to original
                        activeLayer.replaceBitmap(originalSnapshot)
                        onClose()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = StatusError)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Selector Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FilterType.values()) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = {
                            selectedFilter = filter
                            param1 = 0f
                            param2 = 0f
                            processFilter()
                        },
                        label = { Text(filter.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Parameter Sliders based on selected filter
            when (selectedFilter) {
                FilterType.BRIGHTNESS_CONTRAST -> {
                    ParameterSlider(
                        label = "Brightness",
                        value = param1,
                        valueRange = -100f..100f,
                        displayValue = "${param1.toInt()}",
                        onValueChange = { param1 = it; processFilter() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ParameterSlider(
                        label = "Contrast",
                        value = param2,
                        valueRange = -100f..100f,
                        displayValue = "${param2.toInt()}",
                        onValueChange = { param2 = it; processFilter() }
                    )
                }
                FilterType.GAUSSIAN_BLUR -> {
                    ParameterSlider(
                        label = "Blur Radius",
                        value = param1,
                        valueRange = 1f..25f,
                        displayValue = "${param1.toInt()} px",
                        onValueChange = { param1 = it; processFilter() }
                    )
                }
                FilterType.PIXELATE -> {
                    ParameterSlider(
                        label = "Block Size",
                        value = param1,
                        valueRange = 2f..64f,
                        displayValue = "${param1.toInt()} px",
                        onValueChange = { param1 = it; processFilter() }
                    )
                }
                FilterType.THRESHOLD -> {
                    ParameterSlider(
                        label = "Threshold Level",
                        value = param1,
                        valueRange = 1f..254f,
                        displayValue = "${param1.toInt()}",
                        onValueChange = { param1 = it; processFilter() }
                    )
                }
                else -> {
                    ParameterSlider(
                        label = "Intensity",
                        value = param1,
                        valueRange = -100f..100f,
                        displayValue = "${param1.toInt()}",
                        onValueChange = { param1 = it; processFilter() }
                    )
                }
            }
        }
    }
}
