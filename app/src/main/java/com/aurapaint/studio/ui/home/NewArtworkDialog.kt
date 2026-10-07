package com.aurapaint.studio.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aurapaint.studio.core.theme.*
import com.aurapaint.studio.project.PresetCanvas

@Composable
fun NewArtworkDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, width: Int, height: Int, dpi: Int, isAnimation: Boolean) -> Unit
) {
    var artworkName by remember { mutableStateOf("New Artwork") }
    var selectedPreset by remember { mutableStateOf(PresetCanvas.defaultPresets[0]) }
    var customWidth by remember { mutableStateOf("2048") }
    var customHeight by remember { mutableStateOf("2048") }
    var isAnimationMode by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, PanelBorder, RoundedCornerShape(20.dp)),
            color = PanelSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Artwork Canvas",
                        style = Typography.titleLarge,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Artwork Name Field
                OutlinedTextField(
                    value = artworkName,
                    onValueChange = { artworkName = it },
                    label = { Text("Artwork Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = PanelBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Canvas Presets",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Preset List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    items(PresetCanvas.defaultPresets) { preset ->
                        val isSelected = preset.name == selectedPreset.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ElectricBlue.copy(alpha = 0.2f) else PanelSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricBlue else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedPreset = preset
                                    customWidth = preset.width.toString()
                                    customHeight = preset.height.toString()
                                    if (preset.name.contains("Animation", ignoreCase = true)) {
                                        isAnimationMode = true
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = preset.name,
                                    style = Typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ElectricBlue else TextPrimary
                                )
                                Text(
                                    text = "${preset.width} × ${preset.height} px (${preset.dpi} DPI)",
                                    style = Typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Width / Height fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = customWidth,
                        onValueChange = { customWidth = it.filter { char -> char.isDigit() } },
                        label = { Text("Width", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = PanelBorder
                        )
                    )
                    OutlinedTextField(
                        value = customHeight,
                        onValueChange = { customHeight = it.filter { char -> char.isDigit() } },
                        label = { Text("Height", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = PanelBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Animation mode switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Frame-by-Frame Animation",
                        style = Typography.bodyLarge,
                        color = TextPrimary
                    )
                    Switch(
                        checked = isAnimationMode,
                        onCheckedChange = { isAnimationMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextPrimary,
                            checkedTrackColor = ElectricBlue
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Create Button
                Button(
                    onClick = {
                        val w = (customWidth.toIntOrNull() ?: 2048).coerceIn(256, 4096)
                        val h = (customHeight.toIntOrNull() ?: 2048).coerceIn(256, 4096)
                        onCreate(artworkName, w, h, selectedPreset.dpi, isAnimationMode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Canvas",
                        style = Typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
