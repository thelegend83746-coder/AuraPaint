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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.rulers.RulerManager
import com.aurapaint.studio.rulers.RulerType

@Composable
fun RulerPanel(
    rulerManager: RulerManager,
    onClose: () -> Unit
) {
    val currentType by rulerManager.type.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
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
                    text = "Drawing Rulers & Guides",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ruler Types List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RulerType.values()) { type ->
                    val isSelected = type == currentType
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) VioletAccent.copy(alpha = 0.2f) else PanelSurfaceElevated)
                            .border(1.dp, if (isSelected) VioletAccent else PanelBorder, RoundedCornerShape(10.dp))
                            .clickable { rulerManager.setType(type) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = type.title,
                            style = Typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) VioletAccent else TextPrimary
                        )
                        if (isSelected) {
                            Text("ACTIVE", style = Typography.labelSmall, color = VioletAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
