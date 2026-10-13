package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import com.example.model.ExecutionEngine
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.GreenNeon
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkVariant
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ServerDashboardScreen(
    viewModel: MainViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val serverStats by viewModel.serverStats.collectAsState()
    val sshStats by viewModel.sshStats.collectAsState()
    val localIp by viewModel.localIp.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val config by viewModel.serverConfig.collectAsState()
    val currentEngine by viewModel.currentEngine.collectAsState()
    val clipboard = LocalClipboardManager.current

    val isLowBattery = config?.lowBatteryMode ?: false

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("server_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Engine Choice Banner Card (OpenCode vs DeepSeek Harness)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon.copy(alpha = 0.5f) else PurpleNeon.copy(alpha = 0.5f),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("engine_mode_dashboard_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentEngine == ExecutionEngine.OPENCODE) Icons.Default.Terminal else Icons.Default.Psychology,
                                contentDescription = null,
                                tint = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Active Engine & AI Harness",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon.copy(alpha = 0.15f) else PurpleNeon.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentEngine.shortName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Choose between OpenCode autonomous agent or DeepSeek mobile harness runtime:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: OpenCode (anomalyco)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon.copy(alpha = 0.12f) else SurfaceDarkVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else SurfaceHighlight,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setExecutionEngine(ExecutionEngine.OPENCODE) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "OpenCode",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else TextPrimary
                                    )
                                    if (currentEngine == ExecutionEngine.OPENCODE) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text("anomalyco/opencode", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = CyanNeon)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("Autonomous agent & tools", fontSize = 10.sp, color = TextMuted)
                            }
                        }

                        // Option 2: DeepSeek Harness (techjarves)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentEngine == ExecutionEngine.DEEPSEEK_HARNESS) PurpleNeon.copy(alpha = 0.12f) else SurfaceDarkVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (currentEngine == ExecutionEngine.DEEPSEEK_HARNESS) PurpleNeon else SurfaceHighlight,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setExecutionEngine(ExecutionEngine.DEEPSEEK_HARNESS) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "DeepSeek Harness",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (currentEngine == ExecutionEngine.DEEPSEEK_HARNESS) PurpleNeon else TextPrimary
                                    )
                                    if (currentEngine == ExecutionEngine.DEEPSEEK_HARNESS) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PurpleNeon, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text("techjarves/Mobile-Harness", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = PurpleNeon)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("R1 reasoning & assertions", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onNavigate(AppScreen.AI_HARNESS) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon,
                            contentColor = DeepBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentEngine == ExecutionEngine.OPENCODE) "Open OpenCode Agent Studio" else "Open DeepSeek Mobile Harness",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Main Hero Server Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (serverStats.isRunning) CyanNeon.copy(alpha = 0.4f) else SurfaceHighlight, RoundedCornerShape(14.dp))
                    .testTag("code_server_hero_card")
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
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (serverStats.isRunning) GreenNeon else Color(0xFFFF5555))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Local Code Server",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleServer(!serverStats.isRunning) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (serverStats.isRunning) Color(0xFF381E28) else CyanNeon,
                                contentColor = if (serverStats.isRunning) Color(0xFFFF7B72) else DeepBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_server_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = "Toggle Server",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (serverStats.isRunning) "Stop" else "Start",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Hosts a local ARM64 code server environment directly on this device, exposing a full web IDE and REST APIs for remote client browsers.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Connection URL bar
                    val webUrl = "http://$localIp:${serverStats.port}"
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepBackground),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Remote Access URL (LAN)", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = webUrl,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = CyanNeon,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(webUrl))
                                        viewModel.showMessage("Copied server URL to clipboard")
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = CyanNeon, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(label = "Uptime", value = "${serverStats.uptimeSeconds}s", modifier = Modifier.weight(1f))
                        MetricPill(label = "Requests", value = "${serverStats.requestCount}", modifier = Modifier.weight(1f))
                        MetricPill(label = "Active Clients", value = "${serverStats.activeClients}", modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // SSH Access Daemon Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (sshStats.isRunning) PurpleNeon.copy(alpha = 0.4f) else SurfaceHighlight, RoundedCornerShape(14.dp))
                    .testTag("ssh_daemon_card")
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
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (sshStats.isRunning) GreenNeon else Color(0xFFFF5555))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SSH Daemon & Tunnel",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleSsh(!sshStats.isRunning) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sshStats.isRunning) Color(0xFF2E223D) else PurpleNeon,
                                contentColor = if (sshStats.isRunning) PurpleNeon else DeepBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_ssh_button")
                        ) {
                            Text(
                                text = if (sshStats.isRunning) "Active (:2222)" else "Disabled",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val sshCmd = "ssh developer@$localIp -p ${sshStats.port}"
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepBackground),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sshCmd,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(sshCmd))
                                    viewModel.showMessage("Copied SSH command to clipboard")
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy SSH Command", tint = PurpleNeon, modifier = Modifier.size(15.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fingerprint: ${sshStats.hostFingerprintEd25519.take(24)}...",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        StatusBadge(text = "AES-256-GCM", isActive = true, activeColor = GreenNeon)
                    }
                }
            }
        }

        // Low Battery Consumption & Power Governor Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("low_battery_governor_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Battery Governor",
                                tint = if (isLowBattery) GreenNeon else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Low Battery Governor",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Ensures extended coding sessions without draining device",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Switch(
                            checked = isLowBattery,
                            onCheckedChange = { viewModel.toggleLowBatteryMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DeepBackground,
                                checkedTrackColor = GreenNeon
                            ),
                            modifier = Modifier.testTag("low_battery_switch")
                        )
                    }

                    if (isLowBattery) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(GreenNeon.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Profile: 75% CPU cap • 30fps Terminal throttle • Wakelock throttled (~310mW draw)",
                                color = GreenNeon,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Fast Action Navigation Grid
        item {
            Text(
                text = "DEVELOPMENT WORKSPACE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WorkspaceActionCard(
                    title = "Code Editor",
                    subtitle = "5 files • Syntax themes",
                    icon = Icons.Default.Code,
                    accentColor = CyanNeon,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.EDITOR) }
                )
                WorkspaceActionCard(
                    title = "Terminal",
                    subtitle = "UNIX shell & git",
                    icon = Icons.Default.Terminal,
                    accentColor = PurpleNeon,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.TERMINAL) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WorkspaceActionCard(
                    title = "Git Control",
                    subtitle = "GitHub • Diffs • Push",
                    icon = Icons.Default.Share,
                    accentColor = PinkNeon,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.GIT) }
                )
                WorkspaceActionCard(
                    title = "Cloud Sync & Peers",
                    subtitle = "3 live collaborators",
                    icon = Icons.Default.Security,
                    accentColor = GreenNeon,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.SYNC_SETTINGS) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            WorkspaceActionCard(
                title = if (currentEngine == ExecutionEngine.OPENCODE) "OpenCode Agent Studio" else "DeepSeek Mobile Harness",
                subtitle = if (currentEngine == ExecutionEngine.OPENCODE) "Autonomous file edits, diff review & tool calling" else "DeepSeek-R1 reasoning testbed & SWE-bench assertions",
                icon = if (currentEngine == ExecutionEngine.OPENCODE) Icons.Default.AutoAwesome else Icons.Default.Psychology,
                accentColor = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNavigate(AppScreen.AI_HARNESS) }
            )
        }
    }
}

@Composable
fun MetricPill(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkVariant),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Text(text = label, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
fun WorkspaceActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, SurfaceHighlight, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}
