package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.color.ColorManager
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.core.util.ColorUtils

@Composable
fun ColorPickerPanel(
    colorManager: ColorManager,
    onClose: () -> Unit
) {
    val currentColor by colorManager.currentColor.collectAsState()
    val previousColor by colorManager.previousColor.collectAsState()
    val recentColors by colorManager.recentColors.collectAsState()
    val palettes by colorManager.palettes.collectAsState()
    val activePaletteIdx by colorManager.activePaletteIndex.collectAsState()

    // HSV State
    val initialHsv = remember(currentColor) { ColorUtils.colorToHsv(currentColor) }
    var hue by remember { mutableStateOf(initialHsv[0]) }
    var sat by remember { mutableStateOf(initialHsv[1]) }
    var value by remember { mutableStateOf(initialHsv[2]) }

    fun updateColor(h: Float, s: Float, v: Float) {
        hue = h
        sat = s
        value = v
        val newCol = ColorUtils.hsvToColor(h, s, v)
        colorManager.setColor(newCol)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(510.dp)
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
                    text = "Color Studio",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current vs Previous Swatches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Previous color circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(previousColor))
                            .border(1.5.dp, PanelBorder, CircleShape)
                            .clickable { colorManager.swapCurrentAndPrevious() }
                    )
                    IconButton(onClick = { colorManager.swapCurrentAndPrevious() }) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = TextSecondary)
                    }
                    // Current active color circle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(currentColor))
                            .border(2.dp, Color.White, CircleShape)
                    )
                }

                Text(
                    text = ColorUtils.toHexString(currentColor),
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Saturation & Value 2D Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val s = (change.position.x / size.width).coerceIn(0f, 1f)
                            val v = (1f - change.position.y / size.height).coerceIn(0f, 1f)
                            updateColor(hue, s, v)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pureHueColor = Color(ColorUtils.hsvToColor(hue, 1f, 1f))
                    // Draw horizontal saturation gradient then vertical value gradient
                    drawRect(pureHueColor)
                    // Draw white to transparent (horizontal sat)
                    drawRect(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color.White, Color.Transparent)
                        )
                    )
                    // Draw transparent to black (vertical val)
                    drawRect(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black)
                        )
                    )

                    // Draw selection indicator circle
                    val indicatorX = sat * size.width
                    val indicatorY = (1f - value) * size.height
                    drawCircle(Color.White, radius = 10f, center = Offset(indicatorX, indicatorY))
                    drawCircle(Color.Black, radius = 8f, center = Offset(indicatorX, indicatorY))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hue Slider
            Column {
                Text("Hue", style = Typography.bodyMedium, color = TextSecondary)
                Slider(
                    value = hue,
                    onValueChange = { updateColor(it, sat, value) },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricBlue,
                        activeTrackColor = ElectricBlue,
                        inactiveTrackColor = PanelBorder
                    )
                )
            }

            // Recent Colors Swatches
            Text("Recent Swatches", style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentColors) { col ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(col))
                            .border(1.dp, if (col == currentColor) Color.White else PanelBorder, CircleShape)
                            .clickable {
                                val hsv = ColorUtils.colorToHsv(col)
                                updateColor(hsv[0], hsv[1], hsv[2])
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preset Palette Swatches
            val activePalette = palettes.getOrNull(activePaletteIdx)
            if (activePalette != null) {
                Text(activePalette.name, style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activePalette.colors) { col ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(col))
                                .border(1.dp, if (col == currentColor) Color.White else PanelBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                val hsv = ColorUtils.colorToHsv(col)
                                updateColor(hsv[0], hsv[1], hsv[2])
                            }
                        )
                    }
                }
            }
        }
    }
}
