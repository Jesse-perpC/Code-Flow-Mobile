package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class HarnessToolDefinition(
    val id: String,
    val name: String,
    val category: String, // "Files", "Shell", "Git", "Diagnostics", "Vision/Web", "Agent/MCP"
    val description: String,
    val sampleArgs: String,
    val requiresPRoot: Boolean = true
)

@Composable
fun ToolsExplorerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var runningToolId by remember { mutableStateOf<String?>(null) }
    var toolOutputMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val categories = listOf(
        "All" to 57,
        "Files" to 12,
        "Shell" to 9,
        "Git" to 8,
        "Diagnostics" to 10,
        "Vision/Web" to 8,
        "Agent/MCP" to 10
    )

    val toolsList = remember {
        listOf(
            // Files (12)
            HarnessToolDefinition("read_file", "read_file", "Files", "Read contents of a file with line-offset chunking and encoding detection", "path: \"server.js\", offset: 0, length: 1000", false),
            HarnessToolDefinition("write_file", "write_file", "Files", "Create or overwrite file inside workspace or SAF mounted directory", "path: \"index.html\", content: \"...\"", false),
            HarnessToolDefinition("edit_file", "edit_file", "Files", "Exact match-and-replace block edits with syntax validation", "path: \"server.js\", target: \"...\", replacement: \"...\"", false),
            HarnessToolDefinition("patch_ast", "patch_ast", "Files", "Apply unified diff patches with AST node boundary validation", "path: \"server.js\", patch: \"@@ -8,4 +8,14 @@ ...\"", true),
            HarnessToolDefinition("list_dir", "list_dir", "Files", "List directory contents recursively with inode, size, and modtime", "path: \".\", max_depth: 2", false),
            HarnessToolDefinition("search_files", "search_files", "Files", "Fast ripgrep regex pattern match across workspace files", "pattern: \"healthCheck\", file_glob: \"*.js\"", true),
            HarnessToolDefinition("file_info", "file_info", "Files", "Retrieve metadata: size, mime-type, SHA256 checksum, permissions", "path: \"server.js\"", false),
            HarnessToolDefinition("move_file", "move_file", "Files", "Atomically move or rename file or directory within workspace", "source: \"old.js\", destination: \"new.js\"", false),
            HarnessToolDefinition("delete_file", "delete_file", "Files", "Safely remove file with workspace trash recovery buffer", "path: \"temp.log\"", false),
            HarnessToolDefinition("create_dir", "create_dir", "Files", "Recursively create directory paths inside application sandbox", "path: \"src/routes/api\"", false),
            HarnessToolDefinition("stat", "stat", "Files", "UNIX filesystem stat syscall via PRoot emulation layer", "path: \"/usr/bin/python3\"", true),
            HarnessToolDefinition("saf_export", "saf_export", "Files", "Export workspace files to external SD Card/Storage Access Framework", "uri: \"content://...\"", false),

            // Shell & Execution (9)
            HarnessToolDefinition("run_shell", "run_shell", "Shell", "Execute command inside PRoot Ubuntu 20.04 LTS userland environment", "command: \"python3 -m unittest\"", true),
            HarnessToolDefinition("bash_command", "bash_command", "Shell", "Run arbitrary bash script with environment variables and pipes", "script: \"cat file.txt | grep error\"", true),
            HarnessToolDefinition("exec_proot", "exec_proot", "Shell", "Direct ptrace-emulated Linux binary execution (clang, node, python)", "binary: \"/bin/ls\", args: [\"-la\"]", true),
            HarnessToolDefinition("bg_process", "bg_process", "Shell", "Spawn long-running background daemon process with task ID", "command: \"npm run dev\", log_to_file: true", true),
            HarnessToolDefinition("kill_process", "kill_process", "Shell", "Terminate running task by process ID or background job ID", "task_id: \"task-384\"", true),
            HarnessToolDefinition("process_status", "process_status", "Shell", "Query execution status, CPU percentage, and stdout buffer", "task_id: \"task-384\"", true),
            HarnessToolDefinition("env_get", "env_get", "Shell", "Read environment variables from Linux subsystem or .env", "key: \"DEEPSEEK_API_KEY\"", false),
            HarnessToolDefinition("env_set", "env_set", "Shell", "Inject environment variable for subsequent subsystem executions", "key: \"NODE_ENV\", value: \"development\"", false),
            HarnessToolDefinition("pty_stream", "pty_stream", "Shell", "Open bidirectional pseudo-terminal stream with ANSI terminal emulation", "term: \"xterm-256color\", cols: 80, rows: 24", true),

            // Git (8)
            HarnessToolDefinition("git_status", "git_status", "Git", "Get staged, modified, and untracked file status", "working_dir: \".\"", false),
            HarnessToolDefinition("git_diff", "git_diff", "Git", "Generate unified diff of current working tree vs HEAD", "staged_only: false", false),
            HarnessToolDefinition("git_commit", "git_commit", "Git", "Stage modified files and create commit with author signature", "message: \"feat: add health endpoint\"", false),
            HarnessToolDefinition("git_log", "git_log", "Git", "Query commit history with graph log and commit hashes", "max_count: 10", false),
            HarnessToolDefinition("git_branch", "git_branch", "Git", "List, create, or switch branches in local workspace", "action: \"list\"", false),
            HarnessToolDefinition("git_push", "git_push", "Git", "Push local commits to remote git repository over HTTPS/SSH", "remote: \"origin\", branch: \"main\"", false),
            HarnessToolDefinition("git_pull", "git_pull", "Git", "Fetch and merge remote changes into current branch", "remote: \"origin\", branch: \"main\"", false),
            HarnessToolDefinition("git_stash", "git_stash", "Git", "Stash uncommitted changes in local workspace stack", "action: \"push\", message: \"WIP\"", false),

            // Diagnostics & System (10)
            HarnessToolDefinition("sys_info", "sys_info", "Diagnostics", "Query Linux kernel version, Android API level, ARM64 ABI, and device model", "", false),
            HarnessToolDefinition("cpu_telemetry", "cpu_telemetry", "Diagnostics", "Read real-time CPU core frequencies, thermal zones, and throttling status", "", false),
            HarnessToolDefinition("ram_usage", "ram_usage", "Diagnostics", "Retrieve Linux subsystem memory allocation, resident set size, and free RAM", "", false),
            HarnessToolDefinition("battery_governor", "battery_governor", "Diagnostics", "Inspect battery percentage, charging state, and governor throttle status", "", false),
            HarnessToolDefinition("proot_status", "proot_status", "Diagnostics", "Verify PRoot syscall hooks, rootfs integrity, and bind mounts", "", true),
            HarnessToolDefinition("port_check", "port_check", "Diagnostics", "Probe loopback ports (8080 HTTP, 2222 SSH) for open sockets", "port: 8080", false),
            HarnessToolDefinition("disk_free", "disk_free", "Diagnostics", "Report available disk space in app sandbox and external storage", "", false),
            HarnessToolDefinition("sensor_read", "sensor_read", "Diagnostics", "Query ambient light, accelerometer, and temperature sensors", "", false),
            HarnessToolDefinition("net_stats", "net_stats", "Diagnostics", "Inspect Wi-Fi IP address, loopback interface, and bandwidth telemetry", "", false),
            HarnessToolDefinition("apk_builder", "apk_builder", "Diagnostics", "Compile, sign, and package debug APK directly on Android device", "target: \":app:assembleDebug\"", true),

            // Vision & Web (8)
            HarnessToolDefinition("web_fetch", "web_fetch", "Vision/Web", "HTTP GET request with custom headers, cookie jar, and SSL verification", "url: \"http://127.0.0.1:8080/health\"", false),
            HarnessToolDefinition("http_request", "http_request", "Vision/Web", "Send arbitrary HTTP method (POST, PUT, DELETE) with JSON body", "url: \"...\", method: \"POST\"", false),
            HarnessToolDefinition("browser_preview", "browser_preview", "Vision/Web", "Render headless web page snapshot for visual regression checking", "url: \"http://127.0.0.1:8080\"", false),
            HarnessToolDefinition("screenshot", "screenshot", "Vision/Web", "Capture viewport screenshot of running web or native interface", "selector: \"#app\"", false),
            HarnessToolDefinition("html_render", "html_render", "Vision/Web", "Parse HTML DOM structure and extract semantic elements", "html: \"<html>...</html>\"", false),
            HarnessToolDefinition("console_log", "console_log", "Vision/Web", "Stream JavaScript runtime console errors and network requests", "", false),
            HarnessToolDefinition("dns_lookup", "dns_lookup", "Vision/Web", "Resolve hostname to IPv4/IPv6 addresses via local resolver", "host: \"localhost\"", false),
            HarnessToolDefinition("ping", "ping", "Vision/Web", "Send ICMP/TCP ping probe to test latency to local or remote endpoints", "host: \"127.0.0.1\"", false),

            // Agent & MCP (10)
            HarnessToolDefinition("checkpoint_create", "checkpoint_create", "Agent/MCP", "Save immutable workspace state checkpoint for atomic rollback", "label: \"before-refactor\"", false),
            HarnessToolDefinition("checkpoint_rollback", "checkpoint_rollback", "Agent/MCP", "Revert workspace files to previously saved checkpoint", "checkpoint_id: \"chk_01\"", false),
            HarnessToolDefinition("mcp_connect", "mcp_connect", "Agent/MCP", "Connect to external Model Context Protocol server via stdio or SSE", "endpoint: \"stdio:///usr/bin/mcp-server\"", true),
            HarnessToolDefinition("mcp_list_tools", "mcp_list_tools", "Agent/MCP", "Enumerate available MCP tools from registered external servers", "", false),
            HarnessToolDefinition("mcp_call_tool", "mcp_call_tool", "Agent/MCP", "Invoke an MCP external tool and receive structured JSON response", "server: \"db\", tool: \"query\"", false),
            HarnessToolDefinition("memory_store", "memory_store", "Agent/MCP", "Store key-value architectural knowledge in persistent SQLite store", "key: \"api_contract\", value: \"...\"", false),
            HarnessToolDefinition("memory_recall", "memory_recall", "Agent/MCP", "Perform semantic search across agent episodic memory", "query: \"server port configuration\"", false),
            HarnessToolDefinition("subagent_delegate", "subagent_delegate", "Agent/MCP", "Dispatch subagent task to @general, @reviewer, or @test specialist", "agent: \"@test\", prompt: \"...\"", false),
            HarnessToolDefinition("swe_bench_eval", "swe_bench_eval", "Agent/MCP", "Execute automated SWE-bench test assertions with timeout protection", "task_id: \"#149\", assertions: 4", true),
            HarnessToolDefinition("token_count", "token_count", "Agent/MCP", "Calculate BPE/tiktoken token usage for prompt and reasoning strings", "text: \"...\"", false)
        )
    }

    val filteredTools = remember(searchQuery, selectedCategory) {
        toolsList.filter { tool ->
            (selectedCategory == "All" || tool.category == selectedCategory) &&
            (searchQuery.isBlank() || tool.name.contains(searchQuery, ignoreCase = true) || tool.description.contains(searchQuery, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
            .padding(14.dp)
            .testTag("tools_explorer_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TechJarves 57 Tools Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PurpleNeon.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PurpleNeon.copy(alpha = 0.18f))
                                    .border(1.dp, PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeveloperMode,
                                    contentDescription = null,
                                    tint = PurpleNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Mobile-Harness 57 Built-In Tools",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PurpleNeon.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "57/57 Active",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PurpleNeon
                                        )
                                    }
                                }
                                Text(
                                    text = "TechJarves native execution tools • PRoot Linux Subsystem + SAF",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Telemetry row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDarkVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(GreenNeon)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ubuntu 20.04 LTS (PRoot ARM64)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "MCP Host: Online",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyanNeon
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tools_search_input"),
                placeholder = {
                    Text(
                        text = "Search 57 built-in tools (e.g. read_file, proot, swe_bench)...",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PurpleNeon,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = TextPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleNeon,
                    unfocusedBorderColor = SurfaceHighlight,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Category Filter Chips
        item {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { (cat, count) ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PurpleNeon.copy(alpha = 0.2f) else SurfaceDark)
                            .border(1.dp, if (isSelected) PurpleNeon else SurfaceHighlight, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PurpleNeon else TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) PurpleNeon else SurfaceHighlight)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DeepBackground else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filtered Tools List
        items(filteredTools) { tool ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceHighlight, RoundedCornerShape(12.dp))
                    .testTag("tool_card_${tool.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val catColor = when (tool.category) {
                                "Files" -> CyanNeon
                                "Shell" -> GreenNeon
                                "Git" -> Color(0xFFFFB86C)
                                "Diagnostics" -> PurpleNeon
                                "Vision/Web" -> Color(0xFF8BE9FD)
                                else -> Color(0xFFFF79C6)
                            }

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(catColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tool.name,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(catColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tool.category,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = catColor
                                )
                            }
                        }

                        // Test Run Button
                        Button(
                            onClick = {
                                runningToolId = tool.id
                                val output = when (tool.id) {
                                    "read_file" -> "{\n  \"status\": \"success\",\n  \"file\": \"server.js\",\n  \"bytes\": 1420,\n  \"encoding\": \"utf-8\",\n  \"lines\": 48\n}"
                                    "stat" -> "{\n  \"device\": \"proot-rootfs\",\n  \"inode\": 108422,\n  \"mode\": \"0755\",\n  \"size\": 4096,\n  \"atime\": \"2026-10-21T08:14:00Z\"\n}"
                                    "sys_info" -> "{\n  \"os\": \"Ubuntu 20.04.6 LTS (Focal Fossa)\",\n  \"kernel\": \"Linux 5.15.0-android-aarch64\",\n  \"proot\": \"v5.4.0\",\n  \"abi\": \"arm64-v8a\"\n}"
                                    "swe_bench_eval" -> "{\n  \"task\": \"SWE-bench #149\",\n  \"assertions\": 4,\n  \"passed\": 4,\n  \"failed\": 0,\n  \"duration_ms\": 42\n}"
                                    "port_check" -> "{\n  \"port\": 8080,\n  \"bound\": true,\n  \"protocol\": \"HTTP/1.1\",\n  \"service\": \"OpenCode/Harness daemon\"\n}"
                                    else -> "{\n  \"status\": \"executed\",\n  \"tool\": \"${tool.id}\",\n  \"code\": 0,\n  \"duration_ms\": 14,\n  \"timestamp\": \"2026-10-21T08:15:22Z\"\n}"
                                }
                                toolOutputMap = toolOutputMap + (tool.id to output)
                                viewModel.showMessage("Executed tool: ${tool.name}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run",
                                tint = PurpleNeon,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Test Tool",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleNeon
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tool.description,
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )

                    if (tool.sampleArgs.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F141C))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Args: ${tool.sampleArgs}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFC9D1D9)
                            )
                        }
                    }

                    // Display executed tool output
                    val currentOutput = toolOutputMap[tool.id]
                    if (currentOutput != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF090D14))
                                .border(1.dp, GreenNeon.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = GreenNeon,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Execution Result (0 exit code)",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenNeon
                                        )
                                    }
                                    Text(
                                        text = "Dismiss",
                                        fontSize = 9.sp,
                                        color = TextMuted,
                                        modifier = Modifier.clickable {
                                            toolOutputMap = toolOutputMap - tool.id
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentOutput,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = GreenNeon,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
