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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import com.example.model.ExecutionEngine
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
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
fun AppHeader(
    serverRunning: Boolean,
    sshRunning: Boolean,
    batteryLevel: Int,
    isCharging: Boolean,
    lowBatteryMode: Boolean,
    currentEngine: ExecutionEngine = ExecutionEngine.OPENCODE,
    onToggleEngine: () -> Unit = {},
    onRemoteConnectClick: () -> Unit,
    onThemePickerClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isOpenCode = currentEngine == ExecutionEngine.OPENCODE
    val engineColor = if (isOpenCode) CyanNeon else PurpleNeon
    val oppositeEngineColor = if (isOpenCode) PurpleNeon else CyanNeon

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_header"),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Main Branding & Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Authentic Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(engineColor.copy(alpha = 0.15f))
                            .border(1.dp, engineColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isOpenCode) ">_" else "D",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = engineColor,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isOpenCode) "opencode" else "Mobile-Harness",
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontFamily = if (isOpenCode) FontFamily.Monospace else FontFamily.Default
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(engineColor.copy(alpha = 0.18f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (isOpenCode) "v1.4.2" else "TechJarves",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = engineColor
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            StatusBadge(
                                text = if (serverRunning) ":8080" else "OFF",
                                isActive = serverRunning,
                                activeColor = CyanNeon
                            )
                        }
                        Text(
                            text = if (isOpenCode) "~/workspace/opencode-project • git:(main*)" else "Ubuntu 20.04 LTS (PRoot ARM64) • 57 Tools",
                            color = if (lowBatteryMode) GreenNeon else TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Header Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // One-Tap Switcher to the other engine
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(oppositeEngineColor.copy(alpha = 0.15f))
                            .border(1.dp, oppositeEngineColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .clickable(onClick = onToggleEngine)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("header_engine_switch_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Engine",
                                tint = oppositeEngineColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOpenCode) "Switch to Harness" else "Switch to OpenCode",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = oppositeEngineColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Battery indicator
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceDarkVariant)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                            contentDescription = "Battery Status",
                            tint = if (batteryLevel < 20) Color(0xFFFF5555) else GreenNeon,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$batteryLevel%",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onRemoteConnectClick,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("remote_connect_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Remote Connect",
                            tint = CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    val bg = if (isActive) activeColor.copy(alpha = 0.15f) else Color(0xFF2D333B)
    val fg = if (isActive) activeColor else TextMuted
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun AppBottomNav(
    currentScreen: AppScreen,
    currentEngine: ExecutionEngine = ExecutionEngine.OPENCODE,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOpenCode = currentEngine == ExecutionEngine.OPENCODE
    val accentColor = if (isOpenCode) CyanNeon else PurpleNeon

    val items = if (isOpenCode) {
        listOf(
            Triple(AppScreen.AI_HARNESS, "Agent (TUI)", Icons.Default.Terminal),
            Triple(AppScreen.WEB_PREVIEW, "Web GUI", Icons.Default.Language),
            Triple(AppScreen.EDITOR, "Editor", Icons.Default.Code),
            Triple(AppScreen.TERMINAL, "Terminal", Icons.Default.Terminal),
            Triple(AppScreen.GIT, "Diffs", Icons.Default.Share),
            Triple(AppScreen.DASHBOARD, "Daemon", Icons.Default.Dns)
        )
    } else {
        listOf(
            Triple(AppScreen.AI_HARNESS, "Agent", Icons.Default.Psychology),
            Triple(AppScreen.TERMINAL, "Terminal", Icons.Default.Terminal),
            Triple(AppScreen.EDITOR, "Files", Icons.Default.Folder),
            Triple(AppScreen.WEB_PREVIEW, "Preview", Icons.Default.Language),
            Triple(AppScreen.TOOLS_EXPLORER, "57 Tools", Icons.Default.DeveloperMode),
            Triple(AppScreen.DASHBOARD, "System", Icons.Default.Dns)
        )
    }

    NavigationBar(
        modifier = modifier.testTag("main_bottom_nav"),
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        items.forEach { (screen, label, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) accentColor else TextMuted
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) accentColor else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SurfaceHighlight
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}

@Composable
fun AccessoryKeyboardBar(
    onSymbolClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val symbols = listOf("Tab", "Esc", "Ctrl", "Alt", "{", "}", "[", "]", "(", ")", ";", ":", "\"", "'", "`", "$", "|", "/", "\\", "~", "!", "=", ">", "<", "+", "-", "_", "#")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDarkVariant)
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        symbols.forEach { sym ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceHighlight)
                    .clickable { onSymbolClick(sym) }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("acc_key_$sym"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sym,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun RemoteConnectDialog(
    localIp: String,
    port: Int,
    sshPort: Int,
    authToken: String,
    onDismiss: () -> Unit
) {
    val clipboard: ClipboardManager = LocalClipboardManager.current
    val webUrl = "http://$localIp:$port"
    val sshCmd = "ssh developer@$localIp -p $sshPort"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Remote Connection Guide",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 17.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Connect from your laptop, iPad, or remote workstation over local Wi-Fi or hotspot:",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Web IDE URL card
                Text(text = "1. Web IDE / Code-Server Browser URL", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = webUrl,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        IconButton(
                            onClick = { clipboard.setText(AnnotatedString(webUrl)) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = CyanNeon)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SSH Access Command Card
                Text(text = "2. SSH Terminal Access (Any SSH Client)", color = PurpleNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
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
                            onClick = { clipboard.setText(AnnotatedString(sshCmd)) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy SSH Command", tint = PurpleNeon)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auth Token Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDarkVariant)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Auth Token:", color = TextMuted, fontSize = 11.sp)
                        Text(text = authToken, color = GreenNeon, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    StatusBadge(text = "AES-256 E2E", isActive = true, activeColor = GreenNeon)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DeepBackground)
            ) {
                Text("Got It", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = SurfaceDark
    )
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: com.example.ui.theme.AppThemeType,
    onSelectTheme: (com.example.ui.theme.AppThemeType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Interface Theme",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 17.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Pick an aesthetic designed for prolonged coding sessions:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                com.example.ui.theme.AppThemeType.values().forEach { themeType ->
                    val isSelected = currentTheme == themeType
                    val p = com.example.ui.theme.AppThemes.getColors(themeType)
                    Card(
                        onClick = {
                            onSelectTheme(themeType)
                            onDismiss()
                        },
                        colors = CardDefaults.cardColors(containerColor = p.surface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                2.dp,
                                if (isSelected) p.primary else p.surfaceHighlight,
                                RoundedCornerShape(10.dp)
                            )
                            .testTag("theme_card_${themeType.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = themeType.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = p.textPrimary
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        StatusBadge(text = "ACTIVE", isActive = true, activeColor = p.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = themeType.tagLine,
                                    fontSize = 11.sp,
                                    color = p.textMuted
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(p.primary))
                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(p.secondary))
                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(p.tertiary))
                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(p.background).border(1.dp, p.surfaceHighlight, CircleShape))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DeepBackground)
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = SurfaceDark
    )
}
