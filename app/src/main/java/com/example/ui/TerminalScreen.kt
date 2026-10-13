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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminal.TerminalLine
import com.example.terminal.TerminalLineType
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.GreenNeon
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkVariant
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TermBackground
import com.example.ui.theme.TermBlue
import com.example.ui.theme.TermCyan
import com.example.ui.theme.TermGreen
import com.example.ui.theme.TermMagenta
import com.example.ui.theme.TermRed
import com.example.ui.theme.TermYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun TerminalScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val terminalLines by viewModel.terminalLines.collectAsState()
    var inputCommand by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }
    var historyIndex by remember { mutableStateOf(-1) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto scroll to bottom when lines change
    LaunchedEffect(terminalLines.size) {
        if (terminalLines.isNotEmpty()) {
            listState.animateScrollToItem(terminalLines.size - 1)
        }
    }

    val quickCommands = listOf(
        "ls -la", "cat server.js", "git status", "git log", "top", "node server.js", "python api/analytics.py", "bash deploy.sh", "code-server status", "ssh", "clear"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TermBackground)
            .testTag("terminal_screen")
    ) {
        // Quick Shell Presets
        val presetsScroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .horizontalScroll(presetsScroll)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            quickCommands.forEach { cmd ->
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceDarkVariant)
                        .border(1.dp, SurfaceHighlight, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("quick_cmd_$cmd")
                ) {
                    Text(
                        text = cmd,
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)).clickable {
                            history.add(cmd)
                            viewModel.executeTerminalCommand(cmd)
                        }
                    )
                }
            }

            IconButton(
                onClick = { viewModel.clearTerminal() },
                modifier = Modifier.size(26.dp).testTag("clear_terminal_button")
            ) {
                Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear Terminal", tint = TextMuted)
            }
        }

        // Terminal Output Screen
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("terminal_output_list")
        ) {
            items(terminalLines) { line ->
                val color = when (line.type) {
                    TerminalLineType.COMMAND -> CyanNeon
                    TerminalLineType.OUTPUT_SUCCESS -> TermGreen
                    TerminalLineType.OUTPUT_ERROR -> TermRed
                    TerminalLineType.OUTPUT_INFO -> TermBlue
                    TerminalLineType.OUTPUT_ACCENT -> PurpleNeon
                    TerminalLineType.OUTPUT_NORMAL -> TextPrimary
                }

                Text(
                    text = line.text,
                    color = color,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = if (line.type == TerminalLineType.COMMAND) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        // Command Input Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ ",
                    color = GreenNeon,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                BasicTextField(
                    value = inputCommand,
                    onValueChange = { inputCommand = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = TextPrimary
                    ),
                    cursorBrush = SolidColor(CyanNeon),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputCommand.isNotBlank()) {
                                val cmd = inputCommand.trim()
                                history.add(cmd)
                                historyIndex = history.size
                                viewModel.executeTerminalCommand(cmd)
                                inputCommand = ""
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .testTag("terminal_input_field")
                )

                // History Up
                IconButton(
                    onClick = {
                        if (history.isNotEmpty() && historyIndex > 0) {
                            historyIndex--
                            inputCommand = history[historyIndex]
                        }
                    },
                    modifier = Modifier.size(28.dp).testTag("terminal_history_up")
                ) {
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "History Up", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }

                // History Down
                IconButton(
                    onClick = {
                        if (history.isNotEmpty() && historyIndex < history.size - 1) {
                            historyIndex++
                            inputCommand = history[historyIndex]
                        } else {
                            historyIndex = history.size
                            inputCommand = ""
                        }
                    },
                    modifier = Modifier.size(28.dp).testTag("terminal_history_down")
                ) {
                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "History Down", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }

                // Execute Send
                IconButton(
                    onClick = {
                        if (inputCommand.isNotBlank()) {
                            val cmd = inputCommand.trim()
                            history.add(cmd)
                            historyIndex = history.size
                            viewModel.executeTerminalCommand(cmd)
                            inputCommand = ""
                        }
                    },
                    modifier = Modifier.size(32.dp).testTag("terminal_send_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Execute Command", tint = CyanNeon, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Seamless Keyboard Accessory Bar
        AccessoryKeyboardBar(
            onSymbolClick = { symbol ->
                when (symbol) {
                    "Tab" -> {
                        // Quick autocomplete simulation
                        if (inputCommand.startsWith("cat s")) inputCommand = "cat server.js"
                        else if (inputCommand.startsWith("git s")) inputCommand = "git status"
                        else if (inputCommand.startsWith("p")) inputCommand = "python api/analytics.py"
                        else if (inputCommand.startsWith("n")) inputCommand = "node server.js"
                        else inputCommand += "    "
                    }
                    "Esc" -> inputCommand = ""
                    "Ctrl" -> {
                        // Simulate Ctrl+C interrupt
                        viewModel.executeTerminalCommand("^C")
                        inputCommand = ""
                    }
                    "Alt" -> {}
                    else -> inputCommand += symbol
                }
            }
        )
    }
}
