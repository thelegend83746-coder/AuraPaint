package com.aurapaint.studio.ui.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.reference.ReferenceManager
import kotlin.math.roundToInt

@Composable
fun ReferenceWindowView(
    referenceManager: ReferenceManager,
    onClose: () -> Unit
) {
    val isVisible by referenceManager.isVisible.collectAsState()
    if (!isVisible) return

    var offsetX by remember { mutableStateOf(referenceManager.posX) }
    var offsetY by remember { mutableStateOf(referenceManager.posY) }

    Surface(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(width = 240.dp, height = 180.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, ElectricBlue, RoundedCornerShape(14.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    referenceManager.posX = offsetX
                    referenceManager.posY = offsetY
                }
            },
        color = PanelSurface.copy(alpha = referenceManager.opacity)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Title Header / Handle Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PanelSurfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reference", style = Typography.labelSmall, color = TextPrimary)
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }

            // Image Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CanvasWorkspace),
                contentAlignment = Alignment.Center
            ) {
                val bmp = referenceManager.referenceBitmap
                if (bmp != null && !bmp.isRecycled) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Reference Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("No reference loaded", style = Typography.labelSmall, color = TextMuted)
                }
            }
        }
    }
}
