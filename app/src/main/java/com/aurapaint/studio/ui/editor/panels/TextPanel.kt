package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.layers.RasterLayer
import com.aurapaint.studio.text.TextElement
import com.aurapaint.studio.text.TextToolManager

@Composable
fun TextPanel(
    activeLayer: RasterLayer?,
    currentColor: Int,
    textManager: TextToolManager,
    onClose: () -> Unit
) {
    if (activeLayer == null) return

    var textContent by remember { mutableStateOf("Artwork Title") }
    var fontSize by remember { mutableStateOf(54f) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }

    fun commitText() {
        val element = TextElement(
            text = textContent,
            fontSize = fontSize,
            color = currentColor,
            isBold = isBold,
            isItalic = isItalic,
            posX = activeLayer.width * 0.15f,
            posY = activeLayer.height * 0.45f
        )
        textManager.render(activeLayer.canvas, element)
        activeLayer.markDirty()
        onClose()
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
                    text = "Typography & Text Layer",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                Row {
                    IconButton(onClick = { commitText() }) {
                        Icon(Icons.Default.Check, contentDescription = "Commit", tint = StatusSuccess)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = textContent,
                onValueChange = { textContent = it },
                label = { Text("Enter Text", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = PanelBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ParameterSlider(
                label = "Font Size",
                value = fontSize,
                valueRange = 16f..160f,
                displayValue = "${fontSize.toInt()} pt",
                onValueChange = { fontSize = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = isBold,
                    onClick = { isBold = !isBold },
                    label = { Text("Bold") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue)
                )
                FilterChip(
                    selected = isItalic,
                    onClick = { isItalic = !isItalic },
                    label = { Text("Italic") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue)
                )
            }
        }
    }
}
