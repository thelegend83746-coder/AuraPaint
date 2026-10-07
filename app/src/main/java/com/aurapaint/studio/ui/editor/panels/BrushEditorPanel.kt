package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.brush.BrushProperties
import com.aurapaint.studio.core.theme.*

@Composable
fun BrushEditorPanel(
    properties: BrushProperties,
    onPropertiesChanged: (BrushProperties) -> Unit,
    onClose: () -> Unit
) {
    var brushSize by remember { mutableStateOf(properties.size) }
    var opacity by remember { mutableStateOf(properties.opacity) }
    var flow by remember { mutableStateOf(properties.flow) }
    var spacing by remember { mutableStateOf(properties.spacing) }
    var hardness by remember { mutableStateOf(properties.hardness) }
    var stabilization by remember { mutableStateOf(properties.stabilization) }
    var scatter by remember { mutableStateOf(properties.scatter) }
    var pressureSize by remember { mutableStateOf(properties.pressureSize) }
    var pressureOpacity by remember { mutableStateOf(properties.pressureOpacity) }

    // Interactive Scratchpad Test Points
    val testPoints = remember { mutableStateListOf<Offset>() }

    fun update() {
        val updated = properties.copy(
            size = brushSize,
            opacity = opacity,
            flow = flow,
            spacing = spacing,
            hardness = hardness,
            stabilization = stabilization,
            scatter = scatter,
            pressureSize = pressureSize,
            pressureOpacity = pressureOpacity
        )
        onPropertiesChanged(updated)
    }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Brush Studio / Customizer",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            // Scratchpad Stroke Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CanvasWorkspace)
                    .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                testPoints.clear()
                                testPoints.add(offset)
                            },
                            onDrag = { change, _ ->
                                testPoints.add(change.position)
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (testPoints.isEmpty()) {
                        // Default sample curve
                        drawLine(
                            color = ElectricBlue.copy(alpha = opacity),
                            start = Offset(20f, size.height / 2f),
                            end = Offset(size.width - 20f, size.height / 2f),
                            strokeWidth = brushSize.coerceIn(2f, 32f),
                            cap = StrokeCap.Round
                        )
                    } else {
                        for (i in 1 until testPoints.size) {
                            drawLine(
                                color = ElectricBlue.copy(alpha = opacity),
                                start = testPoints[i - 1],
                                end = testPoints[i],
                                strokeWidth = brushSize.coerceIn(2f, 32f),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
                Text(
                    text = if (testPoints.isEmpty()) "Test brush stroke here" else "Clear scratchpad",
                    style = Typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable parameters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Size Slider
                ParameterSlider(
                    label = "Size",
                    value = brushSize,
                    valueRange = 1f..300f,
                    displayValue = "${brushSize.toInt()} px",
                    onValueChange = { brushSize = it; update() }
                )

                // Opacity Slider
                ParameterSlider(
                    label = "Opacity",
                    value = opacity,
                    valueRange = 0.05f..1f,
                    displayValue = "${(opacity * 100).toInt()}%",
                    onValueChange = { opacity = it; update() }
                )

                // Flow Slider
                ParameterSlider(
                    label = "Flow",
                    value = flow,
                    valueRange = 0.05f..1f,
                    displayValue = "${(flow * 100).toInt()}%",
                    onValueChange = { flow = it; update() }
                )

                // Hardness Slider
                ParameterSlider(
                    label = "Hardness",
                    value = hardness,
                    valueRange = 0.01f..1f,
                    displayValue = "${(hardness * 100).toInt()}%",
                    onValueChange = { hardness = it; update() }
                )

                // Spacing Slider
                ParameterSlider(
                    label = "Spacing",
                    value = spacing,
                    valueRange = 0.02f..1f,
                    displayValue = "${(spacing * 100).toInt()}%",
                    onValueChange = { spacing = it; update() }
                )

                // Stabilizer Slider
                ParameterSlider(
                    label = "Stabilizer (Stroke Smoothing)",
                    value = stabilization,
                    valueRange = 0f..10f,
                    displayValue = "${stabilization.toInt()}",
                    onValueChange = { stabilization = it; update() }
                )

                // Scatter Slider
                ParameterSlider(
                    label = "Scatter Jitter",
                    value = scatter,
                    valueRange = 0f..40f,
                    displayValue = "${scatter.toInt()} px",
                    onValueChange = { scatter = it; update() }
                )

                // Dynamics Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stylus Pressure Controls Size", style = Typography.bodyMedium, color = TextPrimary)
                    Switch(
                        checked = pressureSize,
                        onCheckedChange = { pressureSize = it; update() },
                        colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stylus Pressure Controls Opacity", style = Typography.bodyMedium, color = TextPrimary)
                    Switch(
                        checked = pressureOpacity,
                        onCheckedChange = { pressureOpacity = it; update() },
                        colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue)
                    )
                }
            }
        }
    }
}

@Composable
fun ParameterSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = Typography.bodyMedium, color = TextSecondary)
            Text(text = displayValue, style = Typography.bodyMedium, fontWeight = FontWeight.Bold, color = ElectricBlue)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = ElectricBlue,
                activeTrackColor = ElectricBlue,
                inactiveTrackColor = PanelBorder
            )
        )
    }
}
