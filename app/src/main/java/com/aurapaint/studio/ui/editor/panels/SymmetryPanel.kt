package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.symmetry.SymmetryManager
import com.aurapaint.studio.symmetry.SymmetryMode

@Composable
fun SymmetryPanel(
    symmetryManager: SymmetryManager,
    canvasWidth: Float,
    canvasHeight: Float,
    onClose: () -> Unit
) {
    val currentMode by symmetryManager.mode.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
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
                    text = "Symmetry & Kaleidoscope",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Center Reset Button
            OutlinedButton(
                onClick = {
                    symmetryManager.centerX = canvasWidth / 2f
                    symmetryManager.centerY = canvasHeight / 2f
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ElectricBlue))
            ) {
                Icon(Icons.Default.FilterCenterFocus, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Center Axis on Canvas")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Symmetry Modes List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SymmetryMode.values()) { mode ->
                    val isSelected = mode == currentMode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ElectricBlue.copy(alpha = 0.2f) else PanelSurfaceElevated)
                            .border(1.dp, if (isSelected) ElectricBlue else PanelBorder, RoundedCornerShape(10.dp))
                            .clickable { symmetryManager.setMode(mode) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mode.title,
                            style = Typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ElectricBlue else TextPrimary
                        )
                        if (isSelected) {
                            Text("ACTIVE", style = Typography.labelSmall, color = ElectricBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
