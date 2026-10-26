package com.example.ui

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.ExecutionEngine
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.GreenNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkVariant
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ConsoleLogItem(
    val level: String, // "INFO", "WARN", "ERR", "NET"
    val timestamp: String,
    val message: String
)

@Composable
fun WebPreviewScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentEngine by viewModel.currentEngine.collectAsState()
    val serverStats by viewModel.serverStats.collectAsState()

    var currentUrl by remember { mutableStateOf("http://127.0.0.1:8080") }
    var inputUrl by remember { mutableStateOf("http://127.0.0.1:8080") }
    var showConsole by remember { mutableStateOf(false) }
    var activeConsoleFilter by remember { mutableStateOf("ALL") }

    val consoleLogs = remember {
        mutableStateListOf(
            ConsoleLogItem("INFO", "10:14:01", "Subsystem loopback bridge bound to 127.0.0.1:8080"),
            ConsoleLogItem("NET", "10:14:02", "GET / 200 OK (text/html; charset=utf-8) 1.2ms"),
            ConsoleLogItem("INFO", "10:14:03", "WebSocket connected: /ws/ide (session_active: true)"),
            ConsoleLogItem("NET", "10:14:05", "GET /api/workspace/files 200 OK (application/json) 0.8ms"),
            ConsoleLogItem("WARN", "10:14:06", "CORS policy active: restricted to localhost and LAN"),
            ConsoleLogItem("INFO", "10:14:10", "OpenCode Monaco editor initialized with TypeScript language server")
        )
    }

    val accentColor = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
            .testTag("web_preview_screen")
    ) {
        // Browser Controls Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.showMessage("Browser back navigation") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.showMessage("Browser forward navigation") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            currentUrl = inputUrl
                            consoleLogs.add(ConsoleLogItem("NET", "Now", "Reloading $currentUrl"))
                            viewModel.showMessage("Reloading $currentUrl")
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Address Bar
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("browser_address_bar"),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = SurfaceHighlight,
                            focusedContainerColor = SurfaceDarkVariant,
                            unfocusedContainerColor = SurfaceDarkVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showMessage("Unable to open browser: ${e.message}")
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open in External Browser",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Sub-status indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (serverStats.isRunning) GreenNeon else Color(0xFFFF5555))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (serverStats.isRunning) "Local Daemon Active (127.0.0.1:8080)" else "Local Daemon Offline (Port 8080)",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceDarkVariant)
                            .clickable { showConsole = !showConsole }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dev Console (${consoleLogs.size})",
                            fontSize = 10.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = if (showConsole) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Main Web View Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        loadUrl(currentUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Fallback overlay banner if daemon is stopped
            if (!serverStats.isRunning) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Local Web Server Paused",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Start the local daemon from the Server tab to preview your live code server at 127.0.0.1:8080.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.toggleServer(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text(
                                text = "Start HTTP Daemon (:8080)",
                                color = DeepBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Expandable DevTools Console Drawer
        AnimatedVisibility(visible = showConsole) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, SurfaceHighlight, RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DevTools Console",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            listOf("ALL", "INFO", "NET", "WARN").forEach { filter ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (activeConsoleFilter == filter) accentColor.copy(alpha = 0.2f) else SurfaceDarkVariant)
                                        .clickable { activeConsoleFilter = filter }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = filter,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeConsoleFilter == filter) accentColor else TextMuted
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Clear",
                            fontSize = 11.sp,
                            color = CyanNeon,
                            modifier = Modifier
                                .clickable { consoleLogs.clear() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0D1117))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val filtered = if (activeConsoleFilter == "ALL") {
                            consoleLogs
                        } else {
                            consoleLogs.filter { it.level == activeConsoleFilter }
                        }

                        items(filtered) { log ->
                            val levelColor = when (log.level) {
                                "ERR" -> Color(0xFFFF5555)
                                "WARN" -> Color(0xFFFFB86C)
                                "NET" -> GreenNeon
                                else -> CyanNeon
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = log.timestamp,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(levelColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = log.level,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = levelColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = log.message,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
