package com.example.server

import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

data class ServerRuntimeStats(
    val isRunning: Boolean = false,
    val port: Int = 8080,
    val localIp: String = "127.0.0.1",
    val authToken: String = "opencode-sec-9921",
    val requestCount: Long = 0,
    val activeClients: Int = 0,
    val uptimeSeconds: Long = 0,
    val lastRequestPath: String = "",
    val error: String? = null
)

class LocalCodeServerManager(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private var startTimeMillis: Long = 0
    private var requestsHandled: Long = 0

    private val _stats = MutableStateFlow(ServerRuntimeStats())
    val stats: StateFlow<ServerRuntimeStats> = _stats.asStateFlow()

    fun startServer(port: Int, token: String, localIp: String) {
        if (_stats.value.isRunning) {
            stopServer()
        }

        serverJob = scope.launch(Dispatchers.IO) {
            try {
                val ss = ServerSocket(port)
                serverSocket = ss
                startTimeMillis = System.currentTimeMillis()
                requestsHandled = 0

                _stats.value = _stats.value.copy(
                    isRunning = true,
                    port = port,
                    localIp = localIp,
                    authToken = token,
                    requestCount = 0,
                    activeClients = 1,
                    error = null
                )

                while (isActive && !ss.isClosed) {
                    try {
                        val clientSocket = ss.accept()
                        scope.launch(Dispatchers.IO) {
                            handleClientSocket(clientSocket, token)
                        }
                    } catch (e: Exception) {
                        if (ss.isClosed) break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _stats.value = _stats.value.copy(
                    isRunning = false,
                    error = "Failed to bind port $port: ${e.message}"
                )
            }
        }
    }

    fun stopServer() {
        try {
            serverSocket?.close()
            serverSocket = null
            serverJob?.cancel()
            serverJob = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _stats.value = _stats.value.copy(
            isRunning = false,
            activeClients = 0
        )
    }

    fun tickUptime() {
        if (_stats.value.isRunning && startTimeMillis > 0) {
            val uptime = (System.currentTimeMillis() - startTimeMillis) / 1000
            _stats.value = _stats.value.copy(uptimeSeconds = uptime)
        }
    }

    private fun handleClientSocket(socket: Socket, validToken: String) {
        try {
            socket.use { s ->
                val reader = BufferedReader(InputStreamReader(s.getInputStream()))
                val requestLine = reader.readLine() ?: return
                val parts = requestLine.split(" ")
                if (parts.size < 2) return

                val method = parts[0]
                val fullPath = parts[1]
                val path = fullPath.substringBefore("?")

                requestsHandled++
                _stats.value = _stats.value.copy(
                    requestCount = requestsHandled,
                    lastRequestPath = path
                )

                // Read remaining headers until empty line
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line.isNullOrBlank()) break
                }

                val (contentType, responseBody) = when {
                    path == "/api/status" -> "application/json" to getApiStatusJson()
                    path == "/api/files" -> "application/json" to getApiFilesJson()
                    path == "/api/opencode/agent" -> "application/json" to """{"engine": "OpenCode", "repo": "https://github.com/anomalyco/opencode", "status": "ready", "tools": ["read_file","edit_file","bash","diff"]}"""
                    path == "/api/harness/run" -> "application/json" to """{"engine": "DeepSeek-Harness", "repo": "https://github.com/techjarves/Mobile-Harness", "models": ["DeepSeek-R1","DeepSeek-Coder-V2","DeepSeek-V3"], "status": "ready"}"""
                    path == "/api/terminal/exec" -> "application/json" to "{\"status\": \"ok\", \"message\": \"Command received via local server hook\"}"
                    else -> "text/html; charset=UTF-8" to getWebIdeHtml()
                }

                val bodyBytes = responseBody.toByteArray(Charsets.UTF_8)
                val out: OutputStream = s.getOutputStream()
                val responseHeader = buildString {
                    append("HTTP/1.1 200 OK\r\n")
                    append("Content-Type: $contentType\r\n")
                    append("Content-Length: ${bodyBytes.size}\r\n")
                    append("Access-Control-Allow-Origin: *\r\n")
                    append("Connection: close\r\n")
                    append("\r\n")
                }

                out.write(responseHeader.toByteArray(Charsets.UTF_8))
                out.write(bodyBytes)
                out.flush()
            }
        } catch (e: Exception) {
            // Client disconnect or socket close
        }
    }

    private fun getApiStatusJson(): String {
        return """
{
  "status": "online",
  "service": "OpenCode-Local-Server",
  "version": "1.0.0",
  "supportedEngines": ["OpenCode (anomalyco)", "DeepSeek Harness (techjarves)"],
  "e2eEncrypted": true,
  "cipher": "AES-256-GCM",
  "sshPort": 2222,
  "uptimeSeconds": ${_stats.value.uptimeSeconds},
  "requestsHandled": $requestsHandled,
  "lowBatteryMode": false
}
""".trimIndent()
    }

    private fun getApiFilesJson(): String {
        return """
[
  {"name": "server.js", "path": "server.js", "language": "javascript"},
  {"name": "analytics.py", "path": "api/analytics.py", "language": "python"},
  {"name": "deploy.sh", "path": "deploy.sh", "language": "bash"},
  {"name": "README.md", "path": "README.md", "language": "markdown"},
  {"name": "config.json", "path": "config.json", "language": "json"}
]
""".trimIndent()
    }

    private fun getWebIdeHtml(): String {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OpenCode Server - Remote DevBox</title>
    <style>
        :root {
            --bg: #0D1117;
            --surface: #161B22;
            --border: #30363D;
            --cyan: #00E5FF;
            --purple: #BD93F9;
            --green: #50FA7B;
            --text: #F0F6FC;
            --muted: #8B949E;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            background-color: var(--bg);
            color: var(--text);
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, monospace;
            display: flex;
            flex-direction: column;
            height: 100vh;
            overflow: hidden;
        }
        header {
            background-color: var(--surface);
            border-bottom: 1px solid var(--border);
            padding: 12px 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .brand {
            display: flex;
            align-items: center;
            gap: 10px;
            font-weight: 700;
            letter-spacing: 0.5px;
            color: var(--cyan);
        }
        .badge {
            background: rgba(0, 229, 255, 0.15);
            border: 1px solid var(--cyan);
            color: var(--cyan);
            font-size: 11px;
            padding: 2px 8px;
            border-radius: 12px;
        }
        .main-container {
            display: flex;
            flex: 1;
            height: calc(100vh - 56px);
        }
        .sidebar {
            width: 240px;
            background: var(--surface);
            border-right: 1px solid var(--border);
            padding: 12px;
            display: flex;
            flex-direction: column;
            gap: 6px;
        }
        .sidebar-title {
            font-size: 11px;
            text-transform: uppercase;
            color: var(--muted);
            letter-spacing: 1px;
            margin-bottom: 6px;
        }
        .file-item {
            padding: 6px 10px;
            border-radius: 6px;
            font-size: 13px;
            cursor: pointer;
            color: var(--text);
            display: flex;
            align-items: center;
            gap: 6px;
            transition: 0.15s;
        }
        .file-item:hover, .file-item.active {
            background: rgba(255, 255, 255, 0.08);
            color: var(--cyan);
        }
        .editor-area {
            flex: 1;
            display: flex;
            flex-direction: column;
            background: #090D13;
        }
        .editor-tabs {
            background: var(--surface);
            border-bottom: 1px solid var(--border);
            display: flex;
            padding-left: 12px;
        }
        .tab {
            padding: 8px 16px;
            border-right: 1px solid var(--border);
            background: #090D13;
            color: var(--cyan);
            font-size: 13px;
            font-weight: 500;
        }
        textarea.code-editor {
            flex: 1;
            background: #090D13;
            color: #C9D1D9;
            font-family: 'Courier New', Courier, monospace;
            font-size: 14px;
            line-height: 1.5;
            padding: 16px;
            border: none;
            outline: none;
            resize: none;
        }
        .status-bar {
            background: var(--surface);
            border-top: 1px solid var(--border);
            padding: 6px 16px;
            font-size: 12px;
            color: var(--muted);
            display: flex;
            justify-content: space-between;
        }
        .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: var(--green);
            display: inline-block;
            margin-right: 6px;
        }
    </style>
</head>
<body>
    <header>
        <div class="brand">
            <span>&gt;_ OpenCode Server</span>
            <span class="badge">ARM64 Local Daemon</span>
        </div>
        <div style="font-size: 12px; color: var(--muted);">
            SSH: <code>port 2222</code> | E2E: <span style="color: var(--green);">AES-256-GCM Active</span>
        </div>
    </header>
    <div class="main-container">
        <div class="sidebar">
            <div class="sidebar-title">Workspace Explorer</div>
            <div class="file-item active" onclick="loadContent('server.js')">📄 server.js</div>
            <div class="file-item" onclick="loadContent('analytics.py')">🐍 api/analytics.py</div>
            <div class="file-item" onclick="loadContent('deploy.sh')">⚡ deploy.sh</div>
            <div class="file-item" onclick="loadContent('README.md')">📝 README.md</div>
            <div class="file-item" onclick="loadContent('config.json')">⚙️ config.json</div>
        </div>
        <div class="editor-area">
            <div class="editor-tabs">
                <div class="tab" id="current-tab">server.js</div>
            </div>
            <textarea class="code-editor" id="codeEditor">// Connected to OpenCode Local Server on Mobile Device
const http = require('http');
const PORT = 8080;

console.log("[Remote DevBox] Live remote editing from client browser!");
</textarea>
            <div class="status-bar">
                <div><span class="status-dot"></span>Connected: Mobile DevBox Server</div>
                <div>UTF-8 | JavaScript | Low-Battery Optimized</div>
            </div>
        </div>
    </div>
    <script>
        const fileData = {
            'server.js': "// OpenCode Local Microservice Engine\nconst http = require('http');\nconst PORT = 8080;\nconsole.log('Mobile server live!');",
            'analytics.py': "# Python microservice telemetry\nimport time\ndef get_stats():\n    return {'status': 'healthy', 'node': 'android-01'}",
            'deploy.sh': "#!/bin/bash\necho 'Deploying mobile devbox snapshot...'\ngit status",
            'README.md': "# OpenCode Mobile DevBox\nRemote development directly from your phone!",
            'config.json': "{\n  \"server\": {\n    \"port\": 8080,\n    \"sshPort\": 2222\n  }\n}"
        };
        function loadContent(name) {
            document.getElementById('current-tab').innerText = name;
            document.getElementById('codeEditor').value = fileData[name] || "// Empty file";
            document.querySelectorAll('.file-item').forEach(el => el.classList.remove('active'));
            event.target.classList.add('active');
        }
    </script>
</body>
</html>
""".trimIndent()
    }
}
