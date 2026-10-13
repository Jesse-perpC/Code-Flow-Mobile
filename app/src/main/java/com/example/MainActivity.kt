package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.AppBottomNav
import com.example.ui.AppHeader
import com.example.ui.AppScreen
import com.example.ui.CodeEditorScreen
import com.example.ui.GitScreen
import com.example.ui.MainViewModel
import com.example.ui.RemoteConnectDialog
import com.example.ui.ServerDashboardScreen
import com.example.ui.SyncSettingsScreen
import com.example.ui.TerminalScreen
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.OpenCodeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val serverConfig by viewModel.serverConfig.collectAsState()
            val appTheme by viewModel.appTheme.collectAsState()
            val isOledMode = serverConfig?.lowBatteryMode ?: false

            OpenCodeTheme(appThemeType = appTheme, isOledMode = isOledMode) {
                OpenCodeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OpenCodeApp(viewModel: MainViewModel) {
    val currentEngine by viewModel.currentEngine.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val serverStats by viewModel.serverStats.collectAsState()
    val sshStats by viewModel.sshStats.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val serverConfig by viewModel.serverConfig.collectAsState()
    val localIp by viewModel.localIp.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showRemoteConnectDialog by remember { mutableStateOf(false) }
    var showThemePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Handle system back navigation if not on dashboard
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBackground),
        topBar = {
            AppHeader(
                serverRunning = serverStats.isRunning,
                sshRunning = sshStats.isRunning,
                batteryLevel = batteryLevel,
                isCharging = isCharging,
                lowBatteryMode = serverConfig?.lowBatteryMode ?: false,
                currentEngine = currentEngine,
                onToggleEngine = {
                    viewModel.setExecutionEngine(
                        if (currentEngine == com.example.model.ExecutionEngine.OPENCODE)
                            com.example.model.ExecutionEngine.DEEPSEEK_HARNESS
                        else
                            com.example.model.ExecutionEngine.OPENCODE
                    )
                },
                onRemoteConnectClick = { showRemoteConnectDialog = true },
                onThemePickerClick = { showThemePickerDialog = true }
            )
        },
        bottomBar = {
            AppBottomNav(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DeepBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.DASHBOARD -> ServerDashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.AI_HARNESS -> com.example.ui.AiHarnessScreen(
                        viewModel = viewModel
                    )
                    AppScreen.EDITOR -> CodeEditorScreen(
                        viewModel = viewModel
                    )
                    AppScreen.TERMINAL -> TerminalScreen(
                        viewModel = viewModel
                    )
                    AppScreen.GIT -> GitScreen(
                        viewModel = viewModel
                    )
                    AppScreen.SYNC_SETTINGS -> SyncSettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (showRemoteConnectDialog) {
        RemoteConnectDialog(
            localIp = localIp,
            port = serverStats.port,
            sshPort = sshStats.port,
            authToken = serverStats.authToken,
            onDismiss = { showRemoteConnectDialog = false }
        )
    }

    if (showThemePickerDialog) {
        com.example.ui.ThemeSelectionDialog(
            currentTheme = viewModel.appTheme.collectAsState().value,
            onSelectTheme = { viewModel.setAppTheme(it) },
            onDismiss = { showThemePickerDialog = false }
        )
    }
}
