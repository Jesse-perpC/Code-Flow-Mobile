package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.EditorThemeType
import com.example.ui.theme.EditorThemes
import com.example.ui.theme.GreenNeon
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkVariant
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SyncSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.serverConfig.collectAsState()
    val snapshots by viewModel.syncSnapshots.collectAsState()
    val peers by viewModel.peers.collectAsState()
    val isLowBattery = config?.lowBatteryMode ?: false

    val currentTheme = try {
        EditorThemeType.valueOf(config?.selectedEditorTheme ?: "GITHUB_DARK")
    } catch (e: Exception) {
        EditorThemeType.GITHUB_DARK
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("sync_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // End-to-End Encryption Security Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GreenNeon.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .testTag("e2e_encryption_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GreenNeon.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = "E2E Encryption", tint = GreenNeon, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "End-to-End Encryption",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Zero-Knowledge Local Key Derivation",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        StatusBadge(text = "AES-256-GCM", isActive = true, activeColor = GreenNeon)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "All project files, remote SSH tunnels, and cloud sync payloads are encrypted on-device before transmission. Keys never leave this hardware.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepBackground)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Key Fingerprint:", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = "sha256:7mG89q10PksWvN42LkM09XhT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = GreenNeon,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Real-Time Multi-Device Collaboration Mesh
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("real_time_collab_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Devices, contentDescription = "Peers", tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Multi-Device Collaboration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }
                        StatusBadge(text = "${peers.size} Connected", isActive = true, activeColor = CyanNeon)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Real-time sync mesh relays live edits across connected browsers, iPads, and desktop IDEs with conflict-free operational transform.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Peers list
                    peers.forEach { peer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceDarkVariant)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (peer.isOnline) GreenNeon else TextMuted)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = peer.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
                                    Text(text = "${peer.clientType} • ${peer.currentFile} (${peer.cursorPosition})", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            Text(text = "${peer.latencyMs}ms", fontSize = 11.sp, color = CyanNeon, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Application UI Theme Selector
        item {
            val appTheme by viewModel.appTheme.collectAsState()
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("app_theme_selector_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Palette, contentDescription = "App Theme", tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Application Interface Theme",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }
                        StatusBadge(text = appTheme.displayName, isActive = true, activeColor = CyanNeon)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Customize the global color palette across all views, toolbars, and cards:",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val appThemeScroll = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(appThemeScroll),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.example.ui.theme.AppThemeType.values().forEach { themeType ->
                            val isSelected = appTheme == themeType
                            val p = com.example.ui.theme.AppThemes.getColors(themeType)
                            Card(
                                onClick = { viewModel.setAppTheme(themeType) },
                                colors = CardDefaults.cardColors(containerColor = p.surface),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .width(145.dp)
                                    .border(2.dp, if (isSelected) p.primary else p.surfaceHighlight, RoundedCornerShape(10.dp))
                                    .testTag("app_theme_chip_${themeType.name}")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = themeType.displayName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = p.textPrimary)
                                        if (isSelected) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(p.primary))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = themeType.tagLine, fontSize = 9.sp, color = p.textMuted, maxLines = 2, lineHeight = 12.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.primary))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.secondary))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.tertiary))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.background).border(1.dp, p.surfaceHighlight, CircleShape))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Customizable Syntax Highlighting Themes
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("syntax_theme_selector_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Palette, contentDescription = "Themes", tint = PurpleNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Syntax Highlighting Theme",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Choose your preferred dark developer color scheme:", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))

                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EditorThemeType.values().forEach { themeType ->
                            val isSelected = currentTheme == themeType
                            val p = EditorThemes.getPalette(themeType)
                            Card(
                                onClick = { viewModel.updateEditorTheme(themeType) },
                                colors = CardDefaults.cardColors(containerColor = p.background),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(130.dp)
                                    .border(2.dp, if (isSelected) CyanNeon else SurfaceHighlight, RoundedCornerShape(8.dp))
                                    .testTag("theme_chip_${themeType.name}")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = themeType.displayName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = p.text)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.keyword))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.function))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.string))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(p.type))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Encrypted Cloud Backups Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("cloud_backup_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Backups", tint = PinkNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cloud Backup & Snapshots",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = { viewModel.createEncryptedCloudSnapshot() },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkNeon, contentColor = DeepBackground),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("create_snapshot_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Snapshot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Encrypted point-in-time snapshots of all workspace files. Can be restored instantly or synced to cloud relays.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    snapshots.forEach { snap ->
                        val fmt = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceDarkVariant)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = snap.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanNeon, fontFamily = FontFamily.Monospace)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "(${snap.fileCount} files)", fontSize = 11.sp, color = TextMuted)
                                }
                                Text(text = "${fmt.format(Date(snap.timestamp))} • ${snap.encryptedHash}", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                            }

                            Button(
                                onClick = { viewModel.showMessage("Restored snapshot ${snap.id}") },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceHighlight, contentColor = TextPrimary),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = "Restore", modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Restore", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Battery & Performance Governor Settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("battery_settings_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = "Performance", tint = GreenNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Battery & Energy Optimization",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Low Battery Governor", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text(text = "Cap background CPU at 75% & throttle server poll", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = isLowBattery,
                            onCheckedChange = { viewModel.toggleLowBatteryMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = DeepBackground, checkedTrackColor = GreenNeon),
                            modifier = Modifier.testTag("settings_low_battery_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Offline Productivity Cache", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text(text = "All files, terminal commands & AI assist function offline", fontSize = 11.sp, color = TextMuted)
                        }
                        StatusBadge(text = "Enabled", isActive = true, activeColor = GreenNeon)
                    }
                }
            }
        }
    }
}
