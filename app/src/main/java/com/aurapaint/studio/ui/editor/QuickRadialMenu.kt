package com.aurapaint.studio.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.R
import com.aurapaint.studio.canvas.CanvasController
import com.aurapaint.studio.canvas.DrawingTool
import com.aurapaint.studio.core.theme.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QuickRadialMenu(
    controller: CanvasController,
    onOpenColor: () -> Unit,
    onOpenLayers: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(40f) }
    var offsetY by remember { mutableStateOf(300f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        // Expanded Radial Buttons
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                val radius = 64.0 // Distance from center

                // 6 Radial action buttons around the center
                // 1. Brush (top)
                RadialButton(
                    angleDegrees = 270.0,
                    radius = radius,
                    icon = { Icon(painterResource(R.drawable.ic_brush), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                    onClick = {
                        controller.setTool(DrawingTool.BRUSH)
                        isExpanded = false
                    }
                )

                // 2. Eraser (top-right)
                RadialButton(
                    angleDegrees = 330.0,
                    radius = radius,
                    icon = { Icon(painterResource(R.drawable.ic_eraser), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                    onClick = {
                        controller.setTool(DrawingTool.ERASER)
                        isExpanded = false
                    }
                )

                // 3. Eyedropper (right)
                RadialButton(
                    angleDegrees = 30.0,
                    radius = radius,
                    icon = { Icon(Icons.Default.Colorize, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                    onClick = {
                        controller.setTool(DrawingTool.EYEDROPPER)
                        isExpanded = false
                    }
                )

                // 4. Undo (bottom)
                RadialButton(
                    angleDegrees = 90.0,
                    radius = radius,
                    icon = { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                    onClick = {
                        controller.undoManager.undo()
                        controller.onInvalidateView?.invoke()
                        isExpanded = false
                    }
                )

                // 5. Redo (bottom-left)
                RadialButton(
                    angleDegrees = 150.0,
                    radius = radius,
                    icon = { Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                    onClick = {
                        controller.undoManager.redo()
                        controller.onInvalidateView?.invoke()
                        isExpanded = false
                    }
                )

                // 6. Fit View (top-left)
                RadialButton(
                    angleDegrees = 210.0,
                    radius = radius,
                    icon = { Icon(painterResource(R.drawable.ic_fit_screen), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                    onClick = {
                        controller.viewTransform.fitToScreen()
                        controller.onInvalidateView?.invoke()
                        isExpanded = false
                    }
                )
            }
        }

        // Center Floating Knob Trigger
        Surface(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isExpanded) VioletAccent else PanelSurfaceElevated)
                .border(2.dp, ElectricBlue, CircleShape)
                .clickable { isExpanded = !isExpanded },
            color = Color.Transparent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.FlashOn,
                    contentDescription = "Quick Menu",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun RadialButton(
    angleDegrees: Double,
    radius: Double,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val angleRad = Math.toRadians(angleDegrees)
    val bx = (radius * cos(angleRad)).toFloat()
    val by = (radius * sin(angleRad)).toFloat()

    Box(
        modifier = Modifier
            .offset { IntOffset(bx.roundToInt(), by.roundToInt()) }
            .size(36.dp)
            .clip(CircleShape)
            .background(PanelSurfaceElevated)
            .border(1.dp, ElectricBlue, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}
