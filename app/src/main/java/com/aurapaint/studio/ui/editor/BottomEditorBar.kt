package com.aurapaint.studio.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.R
import com.aurapaint.studio.brush.BrushPreset
import com.aurapaint.studio.canvas.CanvasController
import com.aurapaint.studio.canvas.DrawingTool
import com.aurapaint.studio.core.theme.*

@Composable
fun BottomEditorBar(
    controller: CanvasController,
    activeBrush: BrushPreset,
    onOpenBrushPicker: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onOpenLayerPanel: () -> Unit,
    onOpenShapePanel: () -> Unit,
    onOpenTextPanel: () -> Unit,
    onToggleAnimationTimeline: () -> Unit
) {
    val activeTool by controller.activeTool.collectAsState()
    val currentColor by controller.colorManager.currentColor.collectAsState()
    val layers by controller.layerManager.layersState.collectAsState()

    var showSizePopup by remember { mutableStateOf(false) }
    var currentSize by remember { mutableStateOf(controller.brushProperties.size) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Size slider popup if expanded
        if (showSizePopup) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
                color = PanelSurfaceElevated
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Size: ${currentSize.toInt()}px", style = Typography.labelSmall, color = TextPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Slider(
                        value = currentSize,
                        onValueChange = {
                            currentSize = it
                            controller.brushProperties = controller.brushProperties.copy(size = it)
                        },
                        valueRange = 1f..300f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(thumbColor = ElectricBlue, activeTrackColor = ElectricBlue)
                    )
                }
            }
        }

        // Primary Dock Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, PanelBorder, RoundedCornerShape(18.dp)),
            color = PanelSurface.copy(alpha = 0.95f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brush Tool
                IconButton(
                    onClick = {
                        controller.setTool(DrawingTool.BRUSH)
                        onOpenBrushPicker()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTool == DrawingTool.BRUSH) ElectricBlue else Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_brush),
                        contentDescription = "Brush",
                        tint = if (activeTool == DrawingTool.BRUSH) Color.White else TextPrimary
                    )
                }

                // Eraser Tool
                IconButton(
                    onClick = { controller.setTool(DrawingTool.ERASER) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTool == DrawingTool.ERASER) ElectricBlue else Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_eraser),
                        contentDescription = "Eraser",
                        tint = if (activeTool == DrawingTool.ERASER) Color.White else TextPrimary
                    )
                }

                // Size Quick Toggle Pill
                Surface(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PanelSurfaceElevated)
                        .border(1.dp, PanelBorder, RoundedCornerShape(8.dp))
                        .clickable { showSizePopup = !showSizePopup },
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentSize.toInt()}px",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                // Color Swatch Button (Opens Color Picker)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(currentColor))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable(onClick = onOpenColorPicker)
                )

                // Fill Tool
                IconButton(
                    onClick = { controller.setTool(DrawingTool.FILL) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTool == DrawingTool.FILL) ElectricBlue else Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_fill),
                        contentDescription = "Fill",
                        tint = if (activeTool == DrawingTool.FILL) Color.White else TextPrimary
                    )
                }

                // Shapes Tool
                IconButton(
                    onClick = {
                        controller.setTool(DrawingTool.SHAPE)
                        onOpenShapePanel()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTool == DrawingTool.SHAPE) ElectricBlue else Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shapes),
                        contentDescription = "Shapes",
                        tint = if (activeTool == DrawingTool.SHAPE) Color.White else TextPrimary
                    )
                }

                // Transform Tool
                IconButton(
                    onClick = { controller.setTool(DrawingTool.TRANSFORM) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTool == DrawingTool.TRANSFORM) ElectricBlue else Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_transform),
                        contentDescription = "Transform",
                        tint = if (activeTool == DrawingTool.TRANSFORM) Color.White else TextPrimary
                    )
                }

                // Selection Tool
                IconButton(
                    onClick = { controller.setTool(DrawingTool.SELECTION) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTool == DrawingTool.SELECTION) ElectricBlue else Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_selection),
                        contentDescription = "Selection",
                        tint = if (activeTool == DrawingTool.SELECTION) Color.White else TextPrimary
                    )
                }

                // Animation Timeline Toggle
                IconButton(
                    onClick = onToggleAnimationTimeline,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_animation),
                        contentDescription = "Animation",
                        tint = VioletAccent
                    )
                }

                // Layer Panel Button with badge
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PanelSurfaceElevated)
                        .border(1.dp, PanelBorder, RoundedCornerShape(10.dp))
                        .clickable(onClick = onOpenLayerPanel),
                    color = Color.Transparent
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_layers),
                            contentDescription = "Layers",
                            tint = TextPrimary
                        )
                        Surface(
                            color = ElectricBlue,
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                        ) {
                            Text(
                                text = "${layers.size}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
