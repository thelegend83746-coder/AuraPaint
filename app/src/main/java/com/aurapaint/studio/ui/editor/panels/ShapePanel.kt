package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.aurapaint.studio.shapes.ShapeTool
import com.aurapaint.studio.shapes.ShapeType

@Composable
fun ShapePanel(
    shapeTool: ShapeTool,
    onClose: () -> Unit
) {
    var currentShape by remember { mutableStateOf(shapeTool.currentShape) }
    var strokeWidth by remember { mutableStateOf(shapeTool.strokeWidth) }
    var isFilled by remember { mutableStateOf(shapeTool.isFilled) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
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
                    text = "Geometry & Shape Tools",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shape Selector Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ShapeType.values()) { shape ->
                    FilterChip(
                        selected = currentShape == shape,
                        onClick = {
                            currentShape = shape
                            shapeTool.currentShape = shape
                        },
                        label = { Text(shape.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stroke Width Slider
            ParameterSlider(
                label = "Stroke Width",
                value = strokeWidth,
                valueRange = 1f..60f,
                displayValue = "${strokeWidth.toInt()} px",
                onValueChange = {
                    strokeWidth = it
                    shapeTool.strokeWidth = it
                }
            )

            // Fill Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fill Shape with Secondary Color", style = Typography.bodyMedium, color = TextPrimary)
                Switch(
                    checked = isFilled,
                    onCheckedChange = {
                        isFilled = it
                        shapeTool.isFilled = it
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue)
                )
            }
        }
    }
}
