package com.aurapaint.studio.ui.editor.panels

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.animation.AnimationTimeline
import com.aurapaint.studio.core.theme.*

@Composable
fun AnimationTimelinePanel(
    timeline: AnimationTimeline,
    onClose: () -> Unit
) {
    val frames by timeline.framesState.collectAsState()
    val currentFrameIdx by timeline.currentFrameIndex.collectAsState()
    val isPlaying by timeline.isPlaying.collectAsState()
    val fps by timeline.fps.collectAsState()

    var showOnionSkinSettings by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(1.dp, PanelBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = PanelSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header / Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause & Navigation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { timeline.togglePlayPause() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricBlue)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { timeline.prevFrame() }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Frame", tint = TextPrimary)
                    }

                    Text(
                        text = "${currentFrameIdx + 1} / ${frames.size}",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(onClick = { timeline.nextFrame() }) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Frame", tint = TextPrimary)
                    }
                }

                // Middle actions: Add, Duplicate, Delete, Onion skin
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = timeline.onionSkin.isEnabled,
                        onClick = { timeline.onionSkin.isEnabled = !timeline.onionSkin.isEnabled },
                        label = { Text("Onion Skin", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = VioletAccent)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = { timeline.addFrame() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Frame", tint = ElectricBlue)
                    }
                    IconButton(onClick = { timeline.duplicateCurrentFrame() }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate Frame", tint = TextSecondary)
                    }
                    IconButton(onClick = { timeline.deleteCurrentFrame() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Frame", tint = StatusError)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Frames Filmstrip Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(frames) { index, frame ->
                    val isSelected = index == currentFrameIdx

                    Surface(
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ElectricBlue else PanelBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { timeline.selectFrame(index) },
                        color = if (isSelected) ElectricBlue.copy(alpha = 0.2f) else PanelSurfaceElevated
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "#${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ElectricBlue else TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            // Frame Thumbnail
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CanvasWorkspace),
                                contentAlignment = Alignment.Center
                            ) {
                                val thumb = frame.getThumbnail()
                                if (!thumb.isRecycled) {
                                    Image(
                                        bitmap = thumb.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            // FPS Rate Slider Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Playback Speed: $fps FPS", style = Typography.labelSmall, color = TextSecondary)
                Slider(
                    value = fps.toFloat(),
                    onValueChange = { timeline.setFps(it.toInt()) },
                    valueRange = 1f..30f,
                    modifier = Modifier.width(160.dp),
                    colors = SliderDefaults.colors(thumbColor = ElectricBlue, activeTrackColor = ElectricBlue)
                )
            }
        }
    }
}
