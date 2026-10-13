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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import com.example.model.ExecutionEngine
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WorkspaceFileEntity
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

@Composable
fun CodeEditorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val files by viewModel.files.collectAsState()
    val currentFile by viewModel.currentSelectedFile.collectAsState()
    val editorContent by viewModel.editorContent.collectAsState()
    val isDirty by viewModel.isEditorDirty.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()
    val aiSuggestion by viewModel.aiSuggestion.collectAsState()
    val aiExplanation by viewModel.aiExplanation.collectAsState()
    val config by viewModel.serverConfig.collectAsState()
    val currentEngine by viewModel.currentEngine.collectAsState()

    var showNewFileDialog by remember { mutableStateOf(false) }
    var showAiPromptDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val activeThemeType = try {
        EditorThemeType.valueOf(config?.selectedEditorTheme ?: "GITHUB_DARK")
    } catch (e: Exception) {
        EditorThemeType.GITHUB_DARK
    }
    val palette = EditorThemes.getPalette(activeThemeType)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .testTag("code_editor_screen")
    ) {
        // Files Tab Bar
        val tabsScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .horizontalScroll(tabsScrollState)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            files.forEach { file ->
                val isSelected = currentFile?.id == file.id
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) palette.background else SurfaceDarkVariant)
                        .border(1.dp, if (isSelected) CyanNeon.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(6.dp))
                        .clickable { viewModel.selectFile(file) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("file_tab_${file.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = file.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyanNeon else TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                        if (isSelected && isDirty) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(PinkNeon)
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = { showNewFileDialog = true },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("new_file_tab_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New File", tint = CyanNeon)
            }
        }

        // Action Toolbar (Save, AI Complete, Explain, Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDarkVariant)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Save Button
                Button(
                    onClick = { viewModel.saveCurrentFile() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDirty) CyanNeon else SurfaceHighlight,
                        contentColor = if (isDirty) DeepBackground else TextMuted
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("save_file_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isDirty) "Save *" else "Saved", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Engine Mode Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon.copy(alpha = 0.18f) else PurpleNeon.copy(alpha = 0.18f))
                        .clickable {
                            viewModel.setExecutionEngine(
                                if (currentEngine == ExecutionEngine.OPENCODE) ExecutionEngine.DEEPSEEK_HARNESS else ExecutionEngine.OPENCODE
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("editor_engine_chip")
                ) {
                    Text(
                        text = if (currentEngine == ExecutionEngine.OPENCODE) "⚡ OpenCode" else "🧠 DeepSeek",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // AI Complete Button
                Button(
                    onClick = { viewModel.requestAiCodeCompletion("Complete current function or add robust unit tests") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = (if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon).copy(alpha = 0.2f),
                        contentColor = if (currentEngine == ExecutionEngine.OPENCODE) CyanNeon else PurpleNeon
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("ai_complete_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Complete", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "AI Copilot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Prompt AI
                IconButton(
                    onClick = { showAiPromptDialog = true },
                    modifier = Modifier.size(30.dp).testTag("ai_prompt_button")
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = "AI Custom Prompt", tint = PinkNeon, modifier = Modifier.size(16.dp))
                }

                // AI Explain
                IconButton(
                    onClick = { viewModel.requestAiCodeExplanation() },
                    modifier = Modifier.size(30.dp).testTag("ai_explain_button")
                ) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "AI Explain", tint = GreenNeon, modifier = Modifier.size(16.dp))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${currentFile?.language?.uppercase() ?: "TEXT"} • ${activeThemeType.displayName}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.size(28.dp).testTag("delete_file_button")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete File", tint = Color(0xFFFF5555), modifier = Modifier.size(16.dp))
                }
            }
        }

        // AI Loading Indicator
        if (aiLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleNeon.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = PurpleNeon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Code Engine generating intelligent completion (Gemini / Offline Assist)...",
                    color = PurpleNeon,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // AI Suggestion Banner
        if (aiSuggestion != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PurpleNeon, RoundedCornerShape(0.dp))
                    .testTag("ai_suggestion_banner")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Suggestion (Review & Insert):",
                            color = PurpleNeon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row {
                            Button(
                                onClick = { viewModel.acceptAiCompletion() },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenNeon, contentColor = DeepBackground),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("accept_ai_completion_button")
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Accept", modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { viewModel.dismissAiCompletion() },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = aiSuggestion ?: "",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFA5D6FF),
                        lineHeight = 15.sp,
                        maxLines = 6
                    )
                }
            }
        }

        // AI Explanation Sheet
        if (aiExplanation != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GreenNeon.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Code Analysis & Insights", color = GreenNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        IconButton(onClick = { viewModel.dismissAiExplanation() }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        text = aiExplanation ?: "",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp,
                        maxLines = 8,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }

        // Main Editor Surface (Line Numbers + Editor Text)
        val editorScrollState = rememberScrollState()
        val linesCount = editorContent.lines().size.coerceAtLeast(1)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(palette.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(editorScrollState)
            ) {
                // Line Numbers Gutter
                Column(
                    modifier = Modifier
                        .width(42.dp)
                        .background(palette.background.copy(alpha = 0.85f))
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..linesCount) {
                        Text(
                            text = "$i",
                            color = palette.lineNumbers,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 19.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(SurfaceDarkVariant)
                )

                // Actual Code Input Field
                BasicTextField(
                    value = editorContent,
                    onValueChange = { viewModel.onEditorContentChange(it) },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = palette.text,
                        lineHeight = 19.sp
                    ),
                    cursorBrush = SolidColor(palette.accent),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, top = 12.dp, end = 12.dp, bottom = 24.dp)
                        .testTag("code_editor_text_field")
                )
            }
        }

        // Seamless Keyboard Accessory Bar
        AccessoryKeyboardBar(
            onSymbolClick = { symbol ->
                val addition = when (symbol) {
                    "Tab" -> "    "
                    "Esc" -> ""
                    "Ctrl" -> ""
                    "Alt" -> ""
                    else -> symbol
                }
                viewModel.onEditorContentChange(editorContent + addition)
            }
        )
    }

    // New File Dialog
    if (showNewFileDialog) {
        var newFileName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            title = { Text("Create Workspace File", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter filename with extension (e.g., service.py, index.js, test.sh):", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        placeholder = { Text("main.py", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = SurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_file_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            viewModel.createNewFile(newFileName.trim())
                            showNewFileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DeepBackground),
                    modifier = Modifier.testTag("confirm_create_file_button")
                ) {
                    Text("Create", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewFileDialog = false }) { Text("Cancel") }
            },
            containerColor = SurfaceDark
        )
    }

    // AI Custom Prompt Dialog
    if (showAiPromptDialog) {
        var promptText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAiPromptDialog = false },
            title = { Text("AI Code Generator", color = PurpleNeon, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Describe what code to generate for ${currentFile?.name}:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        placeholder = { Text("e.g. Add an SSH tunnel heartbeat monitor in Python", color = TextMuted) },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleNeon,
                            unfocusedBorderColor = SurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("ai_prompt_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (promptText.isNotBlank()) {
                            viewModel.requestAiCodeCompletion(promptText.trim())
                            showAiPromptDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon, contentColor = DeepBackground),
                    modifier = Modifier.testTag("submit_ai_prompt_button")
                ) {
                    Text("Generate", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAiPromptDialog = false }) { Text("Cancel") }
            },
            containerColor = SurfaceDark
        )
    }

    // Delete File Confirmation
    if (showDeleteConfirmDialog && currentFile != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete File", color = Color(0xFFFF5555), fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${currentFile?.name}'?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        currentFile?.let { viewModel.deleteFile(it) }
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5555), contentColor = DeepBackground),
                    modifier = Modifier.testTag("confirm_delete_file_button")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
            },
            containerColor = SurfaceDark
        )
    }
}
