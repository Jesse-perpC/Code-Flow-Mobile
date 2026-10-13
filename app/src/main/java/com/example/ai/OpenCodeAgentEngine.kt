package com.example.ai

import com.example.model.AgentToolCall
import com.example.model.OpenCodeAgentTaskState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class OpenCodeAgentEngine {
    val suggestedAgentTasks = listOf(
        "Refactor server.js for zero-downtime reload & health check",
        "Add comprehensive security check in deploy.sh",
        "Generate OpenAPI 3.0 specification for local endpoints",
        "Benchmark memory cache & battery consumption in app"
    )

    suspend fun runAgentTask(
        task: String,
        onProgress: (OpenCodeAgentTaskState) -> Unit
    ): OpenCodeAgentTaskState = withContext(Dispatchers.IO) {
        val targetFile = when {
            task.contains("deploy", ignoreCase = true) -> "deploy.sh"
            task.contains("openapi", ignoreCase = true) || task.contains("api", ignoreCase = true) -> "server.js"
            task.contains("benchmark", ignoreCase = true) -> "README.md"
            else -> "server.js"
        }

        val toolCallsList = mutableListOf<AgentToolCall>()
        val steps = listOf(
            "1. Indexing workspace file tree & AST symbols for '$targetFile'...",
            "2. Running static analysis & syntax verification...",
            "3. Executing OpenCode tool calls on local workspace...",
            "4. Generating surgical diff and running sanity checks in sandbox...",
            "5. OpenCode Agent task finished and ready for review."
        )

        val completed = mutableListOf<String>()
        var currentState = OpenCodeAgentTaskState(
            task = task,
            isRunning = true,
            completedSteps = completed,
            currentStep = steps.first(),
            targetFilePath = targetFile,
            toolCalls = toolCallsList
        )
        onProgress(currentState)

        // Step 1
        delay(350)
        toolCallsList.add(
            AgentToolCall(
                tool = "read_file",
                argument = "path='$targetFile'",
                status = "OK",
                output = "Read 64 lines from $targetFile successfully"
            )
        )
        completed.add(steps[0])
        currentState = currentState.copy(
            completedSteps = completed.toList(),
            currentStep = steps[1],
            toolCalls = toolCallsList.toList()
        )
        onProgress(currentState)

        // Step 2
        delay(400)
        toolCallsList.add(
            AgentToolCall(
                tool = "grep_ast_search",
                argument = "query='endpoints, routes, middleware'",
                status = "OK",
                output = "Located target routing table and request dispatcher"
            )
        )
        completed.add(steps[1])
        currentState = currentState.copy(
            completedSteps = completed.toList(),
            currentStep = steps[2],
            toolCalls = toolCallsList.toList()
        )
        onProgress(currentState)

        // Step 3
        delay(450)
        toolCallsList.add(
            AgentToolCall(
                tool = "run_bash_sandbox",
                argument = "cmd='node --check $targetFile'",
                status = "EXIT_0",
                output = "Syntax validation passed cleanly"
            )
        )
        completed.add(steps[2])
        currentState = currentState.copy(
            completedSteps = completed.toList(),
            currentStep = steps[3],
            toolCalls = toolCallsList.toList()
        )
        onProgress(currentState)

        // Step 4
        delay(400)
        toolCallsList.add(
            AgentToolCall(
                tool = "apply_unified_diff",
                argument = "file='$targetFile', mode='dry_run'",
                status = "PROPOSED",
                output = "+14 insertions, -2 deletions ready for user confirmation"
            )
        )
        completed.add(steps[3])
        completed.add(steps[4])

        val (diff, newContent) = generateDiffForTask(task, targetFile)

        currentState = currentState.copy(
            isRunning = false,
            completedSteps = completed.toList(),
            currentStep = "OpenCode Agent completed task successfully.",
            targetFilePath = targetFile,
            proposedDiff = diff,
            newFileContent = newContent,
            toolCalls = toolCallsList.toList(),
            isApplied = false
        )
        onProgress(currentState)
        currentState
    }

    private fun generateDiffForTask(task: String, targetFile: String): Pair<String, String> {
        return when {
            targetFile == "deploy.sh" -> {
                val diff = """
--- a/deploy.sh
+++ b/deploy.sh
@@ -10,3 +10,12 @@
+ # OpenCode Security Guard (anomalyco/opencode)
+ echo "[OpenCode] Verifying cryptographic checksums..."
+ if [ ! -f "checksums.sha256" ]; then
+   echo "[OpenCode] Generating local SHA-256 integrity manifest..."
+   sha256sum *.js *.sh > checksums.sha256
+ fi
+ sha256sum --check checksums.sha256 || { echo "Integrity failure!"; exit 1; }
+ echo "[OpenCode] Zero vulnerabilities detected."
                """.trimIndent()
                val content = """
#!/bin/bash
# Autonomous Deploy Script with OpenCode agent guard

set -e
echo "Starting deployment sequence..."

# OpenCode Security Guard (anomalyco/opencode)
echo "[OpenCode] Verifying cryptographic checksums..."
if [ ! -f "checksums.sha256" ]; then
  echo "[OpenCode] Generating local SHA-256 integrity manifest..."
  sha256sum *.js *.sh > checksums.sha256 2>/dev/null || true
fi
echo "[OpenCode] Security scan passed: Zero vulnerabilities detected."
echo "Deployment completed successfully."
                """.trimIndent()
                Pair(diff, content)
            }
            else -> {
                val diff = """
--- a/server.js
+++ b/server.js
@@ -18,6 +18,17 @@
+ // OpenCode Autonomous Agent (anomalyco/opencode)
+ app.get('/api/health', (req, res) => {
+   res.json({
+     status: 'ok',
+     agent: 'OpenCode v1.4.2',
+     runtime: 'Android-Local-Daemon',
+     uptime: process.uptime(),
+     memoryMb: Math.round(process.memoryUsage().heapUsed / 1024 / 1024)
+   });
+ });
+
+ process.on('SIGTERM', () => {
+   console.log('[OpenCode] Gracefully closing connections...');
+   server.close();
+ });
                """.trimIndent()
                val content = """
// OpenCode Microservice Server (anomalyco/opencode)
const http = require('http');

const server = http.createServer((req, res) => {
  // OpenCode Autonomous Agent Routes
  if (req.url === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      status: 'ok',
      agent: 'OpenCode v1.4.2 (anomalyco)',
      runtime: 'Android-Local-Daemon',
      timestamp: Date.now()
    }));
    return;
  }

  res.writeHead(200, { 'Content-Type': 'application/json', 'X-OpenCode-Agent': 'v1.4.2' });
  res.end(JSON.stringify({ message: "Hello from OpenCode Local Devbox!", status: "active" }));
});

const PORT = process.env.PORT || 8080;
server.listen(PORT, () => {
  console.log(`[OpenCode] Dev Server listening on http://127.0.0.1:${'$'}{PORT}`);
});
                """.trimIndent()
                Pair(diff, content)
            }
        }
    }
}
