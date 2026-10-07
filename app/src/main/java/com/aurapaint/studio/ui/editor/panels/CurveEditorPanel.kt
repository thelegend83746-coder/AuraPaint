package com.aurapaint.studio.ui.editor.panels

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.filters.CurveControlPoint
import com.aurapaint.studio.filters.FilterProcessor
import com.aurapaint.studio.filters.FilterType
import com.aurapaint.studio.filters.ToneCurve
import com.aurapaint.studio.layers.RasterLayer
import kotlinx.coroutines.launch

enum class CurveChannel(val displayName: String, val color: Color) {
    RGB("RGB (Master)", Color.White),
    RED("Red Channel", Color(0xFFFF4565)),
    GREEN("Green Channel", Color(0xFF00D284)),
    BLUE("Blue Channel", Color(0xFF3D7BFF))
}

@Composable
fun CurveEditorPanel(
    activeLayer: RasterLayer?,
    filterProcessor: FilterProcessor,
    onClose: () -> Unit
) {
    if (activeLayer == null) return

    val coroutineScope = rememberCoroutineScope()
    val toneCurve = remember { ToneCurve() }
    var activeChannel by remember { mutableStateOf(CurveChannel.RGB) }

    val originalSnapshot = remember(activeLayer) {
        activeLayer.bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }

    fun applyCurve() {
        toneCurve.buildAllLuts()
        coroutineScope.launch {
            val result = filterProcessor.applyFilter(
                source = originalSnapshot,
                type = FilterType.TONE_CURVES,
                curve = toneCurve
            )
            activeLayer.replaceBitmap(result)
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
                    text = "Tone Curves",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                Row {
                    IconButton(onClick = {
                        toneCurve.reset()
                        applyCurve()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextSecondary)
                    }
                    IconButton(onClick = {
                        originalSnapshot.recycle()
                        onClose()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Apply", tint = StatusSuccess)
                    }
                    IconButton(onClick = {
                        activeLayer.replaceBitmap(originalSnapshot)
                        onClose()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = StatusError)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Channel Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CurveChannel.values().forEach { ch ->
                    FilterChip(
                        selected = activeChannel == ch,
                        onClick = { activeChannel = ch },
                        label = { Text(ch.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ch.color.copy(alpha = 0.3f),
                            selectedLabelColor = ch.color
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Tone Curve Graph
            val currentPoints = when (activeChannel) {
                CurveChannel.RGB -> toneCurve.masterPoints
                CurveChannel.RED -> toneCurve.redPoints
                CurveChannel.GREEN -> toneCurve.greenPoints
                CurveChannel.BLUE -> toneCurve.bluePoints
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CanvasWorkspace)
                    .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
                    .pointerInput(activeChannel) {
                        detectDragGestures { change, _ ->
                            val normX = (change.position.x / size.width).coerceIn(0f, 1f) * 255f
                            val normY = (1f - change.position.y / size.height).coerceIn(0f, 1f) * 255f

                            // Adjust midpoint or add point
                            if (currentPoints.size == 2) {
                                currentPoints.add(1, CurveControlPoint(normX, normY))
                            } else if (currentPoints.size > 2) {
                                currentPoints[1].x = normX
                                currentPoints[1].y = normY
                            }
                            applyCurve()
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw grid lines
                    val stepW = size.width / 4f
                    val stepH = size.height / 4f
                    for (i in 1..3) {
                        drawLine(Color(0xFF2A2D36), Offset(i * stepW, 0f), Offset(i * stepW, size.height))
                        drawLine(Color(0xFF2A2D36), Offset(0f, i * stepH), Offset(size.width, i * stepH))
                    }

                    // Diagonal base reference
                    drawLine(Color(0xFF4A4E5C), Offset(0f, size.height), Offset(size.width, 0f), strokeWidth = 1.5f)

                    // Draw curve line
                    val path = Path()
                    val p0 = currentPoints.first()
                    path.moveTo((p0.x / 255f) * size.width, (1f - p0.y / 255f) * size.height)

                    for (i in 1 until currentPoints.size) {
                        val pt = currentPoints[i]
                        path.lineTo((pt.x / 255f) * size.width, (1f - pt.y / 255f) * size.height)
                    }

                    drawPath(
                        path = path,
                        color = activeChannel.color,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f, cap = StrokeCap.Round)
                    )

                    // Draw control points
                    for (pt in currentPoints) {
                        val cx = (pt.x / 255f) * size.width
                        val cy = (1f - pt.y / 255f) * size.height
                        drawCircle(Color.White, radius = 9f, center = Offset(cx, cy))
                        drawCircle(activeChannel.color, radius = 6f, center = Offset(cx, cy))
                    }
                }
            }
        }
    }
}
