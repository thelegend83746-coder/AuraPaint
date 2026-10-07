package com.aurapaint.studio.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurapaint.studio.R
import com.aurapaint.studio.core.AppConfig
import com.aurapaint.studio.core.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    var fingerDrawing by remember { mutableStateOf(true) }
    var stylusPressure by remember { mutableStateOf(true) }
    var hardwareGpu by remember { mutableStateOf(true) }
    var leftHandedMode by remember { mutableStateOf(false) }
    var autoSaveInterval by remember { mutableStateOf("60 seconds") }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Studio Settings",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Branding Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, PanelBorder, RoundedCornerShape(16.dp)),
                color = PanelSurface
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_aurapaint_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = AppConfig.APP_NAME,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Version ${AppConfig.APP_VERSION} • Production Native Studio",
                            style = Typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = AppConfig.APP_TAGLINE,
                            style = Typography.labelSmall,
                            color = ElectricBlue
                        )
                    }
                }
            }

            // Section: Input & Stylus
            SettingsSection(title = "Stylus & Touch Input") {
                SettingsSwitchRow(
                    title = "Enable Finger Drawing",
                    subtitle = "Allows drawing with touch in addition to stylus",
                    checked = fingerDrawing,
                    onCheckedChange = { fingerDrawing = it }
                )
                SettingsSwitchRow(
                    title = "Stylus Pressure Dynamics",
                    subtitle = "Use S-Pen / Active stylus pressure for brush stroke weight",
                    checked = stylusPressure,
                    onCheckedChange = { stylusPressure = it }
                )
            }

            // Section: Interface & Layout
            SettingsSection(title = "User Interface") {
                SettingsSwitchRow(
                    title = "Left-Handed Mode",
                    subtitle = "Mirrors floating docks for comfortable left-hand access",
                    checked = leftHandedMode,
                    onCheckedChange = { leftHandedMode = it }
                )
            }

            // Section: Performance & Engine
            SettingsSection(title = "Engine & Performance") {
                SettingsSwitchRow(
                    title = "Hardware Acceleration (GPU)",
                    subtitle = "60 FPS hardware double-buffering for interactive strokes",
                    checked = hardwareGpu,
                    onCheckedChange = { hardwareGpu = it }
                )
            }

            // Section: Storage & Autosave
            SettingsSection(title = "Storage & Project Recovery") {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("Project Directory", style = Typography.bodyLarge, color = TextPrimary)
                    Text(
                        text = AppConfig.BASE_PROJECTS_DIR,
                        style = Typography.bodyMedium,
                        color = ElectricBlue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("Auto-Save Interval", style = Typography.bodyLarge, color = TextPrimary)
                    Text("Every 60 seconds (active background timer)", style = Typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = ElectricBlue,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, PanelBorder, RoundedCornerShape(14.dp)),
            color = PanelSurface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = Typography.bodyLarge, color = TextPrimary)
            Text(subtitle, style = Typography.bodyMedium, fontSize = 11.sp, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPrimary,
                checkedTrackColor = ElectricBlue
            )
        )
    }
}
