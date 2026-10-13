package com.example.terminal

import com.example.data.AppDatabase
import com.example.model.GitCommitEntity
import com.example.model.TerminalHistoryEntity
import com.example.model.WorkspaceFileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class TerminalLineType {
    COMMAND, OUTPUT_NORMAL, OUTPUT_SUCCESS, OUTPUT_ERROR, OUTPUT_INFO, OUTPUT_ACCENT
}

data class TerminalLine(
    val text: String,
    val type: TerminalLineType = TerminalLineType.OUTPUT_NORMAL
)

class TerminalEngine(
    private val database: AppDatabase
) {
    private val _lines = MutableStateFlow<List<TerminalLine>>(
        listOf(
            TerminalLine("OpenCode Mobile DevBox UNIX Shell (v2.4.0-arm64)", TerminalLineType.OUTPUT_INFO),
            TerminalLine("Local HTTP Code Server: port 8080 | SSH: port 2222", TerminalLineType.OUTPUT_ACCENT),
            TerminalLine("Type 'help' for available commands or use the accessory bar below.", TerminalLineType.OUTPUT_NORMAL),
            TerminalLine("")
        )
    )
    val lines: StateFlow<List<TerminalLine>> = _lines.asStateFlow()

    private var currentDir = "/workspace"
    private val commandHistoryList = mutableListOf<String>()

    fun clear() {
        _lines.value = listOf(
            TerminalLine("Terminal cleared.", TerminalLineType.OUTPUT_INFO)
        )
    }

    suspend fun execute(commandStr: String): Int {
        val trimmed = commandStr.trim()
        if (trimmed.isEmpty()) return 0

        commandHistoryList.add(trimmed)

        val newLines = _lines.value.toMutableList()
        newLines.add(TerminalLine("developer@devbox:$currentDir$ $trimmed", TerminalLineType.COMMAND))

        val parts = trimmed.split("\\s+".toRegex())
        val cmd = parts[0].lowercase()
        val args = parts.drop(1)

        var exitCode = 0
        val outputLines = mutableListOf<TerminalLine>()

        when (cmd) {
            "help" -> {
                outputLines.add(TerminalLine("=== OpenCode Built-in Shell Utilities ===", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine("  ls [-l]          List directory contents"))
                outputLines.add(TerminalLine("  cat <file>       Read file contents"))
                outputLines.add(TerminalLine("  pwd              Print working directory"))
                outputLines.add(TerminalLine("  cd [dir]         Change directory"))
                outputLines.add(TerminalLine("  touch <file>     Create empty file"))
                outputLines.add(TerminalLine("  rm <file>        Remove file"))
                outputLines.add(TerminalLine("  node <file>      Run Node.js script in sandbox"))
                outputLines.add(TerminalLine("  python <file>    Run Python script in sandbox"))
                outputLines.add(TerminalLine("  bash <file>      Execute shell script"))
                outputLines.add(TerminalLine("  git <command>    Git operations (status, log, diff, commit, push, pull)"))
                outputLines.add(TerminalLine("  code-server      Inspect or restart code-server"))
                outputLines.add(TerminalLine("  ssh              SSH daemon status & active sessions"))
                outputLines.add(TerminalLine("  top              Live process list & low battery status"))
                outputLines.add(TerminalLine("  opencode [run|status|doctor]  Anomaly Co OpenCode autonomous coding agent"))
                outputLines.add(TerminalLine("  deepseek [eval|models|think]  TechJarves DeepSeek Mobile Harness runner"))
                outputLines.add(TerminalLine("  harness  [run|bench|test]     Run mobile SWE-bench style assertions"))
                outputLines.add(TerminalLine("  whoami, uptime   System telemetry"))
                outputLines.add(TerminalLine("  clear            Clear terminal screen"))
            }

            "clear" -> {
                _lines.value = emptyList()
                return 0
            }

            "pwd" -> {
                outputLines.add(TerminalLine(currentDir, TerminalLineType.OUTPUT_NORMAL))
            }

            "whoami" -> {
                outputLines.add(TerminalLine("developer (uid=1000 gid=1000 groups=wheel,docker,ssh)", TerminalLineType.OUTPUT_NORMAL))
            }

            "date" -> {
                val fmt = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US)
                outputLines.add(TerminalLine(fmt.format(Date()), TerminalLineType.OUTPUT_NORMAL))
            }

            "uptime" -> {
                outputLines.add(TerminalLine("up 4 hours 12 mins, 2 users, load average: 0.18, 0.22, 0.15", TerminalLineType.OUTPUT_NORMAL))
            }

            "uname" -> {
                outputLines.add(TerminalLine("Linux opencode-android 6.1.75-android14-arm64 #1 SMP PREEMPT aarch64 GNU/Linux", TerminalLineType.OUTPUT_NORMAL))
            }

            "ls" -> {
                val files = database.workspaceDao().getAllFiles()
                // Synchronous fetch from db
                val fileList = database.workspaceDao().getFileCount()
                outputLines.add(TerminalLine("total $fileList items in $currentDir", TerminalLineType.OUTPUT_INFO))
                outputLines.add(TerminalLine("drwxr-xr-x  4 developer developer  4096 Oct 11 00:20 .", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("drwxr-xr-x 12 developer developer  4096 Oct 11 00:15 ..", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("drwxr-xr-x  3 developer developer  4096 Oct 11 00:20 api/", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine("-rw-r--r--  1 developer developer  1284 Oct 11 00:22 server.js", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("-rwxr-xr-x  1 developer developer   642 Oct 11 00:18 deploy.sh", TerminalLineType.OUTPUT_SUCCESS))
                outputLines.add(TerminalLine("-rw-r--r--  1 developer developer   890 Oct 11 00:21 README.md", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("-rw-r--r--  1 developer developer   356 Oct 11 00:19 config.json", TerminalLineType.OUTPUT_NORMAL))
            }

            "cd" -> {
                val target = args.firstOrNull() ?: "/workspace"
                if (target == ".." || target == "../") {
                    currentDir = "/workspace"
                } else if (target.startsWith("/")) {
                    currentDir = target
                } else {
                    currentDir = "/workspace/$target"
                }
                outputLines.add(TerminalLine("Changed directory to $currentDir", TerminalLineType.OUTPUT_INFO))
            }

            "cat" -> {
                if (args.isEmpty()) {
                    outputLines.add(TerminalLine("cat: missing operand", TerminalLineType.OUTPUT_ERROR))
                    exitCode = 1
                } else {
                    val filename = args[0]
                    val file = database.workspaceDao().getFileByPath(filename)
                        ?: database.workspaceDao().getFileByPath("api/$filename")
                    if (file != null) {
                        file.content.lines().forEach { line ->
                            outputLines.add(TerminalLine(line, TerminalLineType.OUTPUT_NORMAL))
                        }
                    } else {
                        outputLines.add(TerminalLine("cat: $filename: No such file or directory", TerminalLineType.OUTPUT_ERROR))
                        exitCode = 1
                    }
                }
            }

            "touch" -> {
                if (args.isEmpty()) {
                    outputLines.add(TerminalLine("touch: missing file operand", TerminalLineType.OUTPUT_ERROR))
                    exitCode = 1
                } else {
                    val name = args[0]
                    val newFile = WorkspaceFileEntity(
                        id = UUID.randomUUID().toString(),
                        path = name,
                        name = name,
                        content = "// Newly created file\n",
                        language = if (name.endsWith(".py")) "python" else if (name.endsWith(".js")) "javascript" else "text",
                        gitStatus = "UNTRACKED"
                    )
                    database.workspaceDao().insertFile(newFile)
                    outputLines.add(TerminalLine("Created file $name", TerminalLineType.OUTPUT_SUCCESS))
                }
            }

            "rm" -> {
                if (args.isEmpty()) {
                    outputLines.add(TerminalLine("rm: missing operand", TerminalLineType.OUTPUT_ERROR))
                    exitCode = 1
                } else {
                    val name = args[0]
                    val target = database.workspaceDao().getFileByPath(name)
                    if (target != null) {
                        database.workspaceDao().deleteFileById(target.id)
                        outputLines.add(TerminalLine("Removed '$name'", TerminalLineType.OUTPUT_INFO))
                    } else {
                        outputLines.add(TerminalLine("rm: cannot remove '$name': No such file", TerminalLineType.OUTPUT_ERROR))
                        exitCode = 1
                    }
                }
            }

            "node" -> {
                val script = args.firstOrNull() ?: "server.js"
                outputLines.add(TerminalLine("[Node.js v20.12.0 ARM64] Executing $script...", TerminalLineType.OUTPUT_INFO))
                outputLines.add(TerminalLine("[OpenCode] Daemon running at http://0.0.0.0:8080", TerminalLineType.OUTPUT_SUCCESS))
                outputLines.add(TerminalLine("[OpenCode] E2E encryption initialized with cipher AES-256-GCM", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine("[OpenCode] Server listening for local & LAN clients.", TerminalLineType.OUTPUT_NORMAL))
            }

            "python", "python3" -> {
                val script = args.firstOrNull() ?: "api/analytics.py"
                outputLines.add(TerminalLine("[Python 3.12.2 ARM64] Executing $script...", TerminalLineType.OUTPUT_INFO))
                outputLines.add(TerminalLine("DevBox Status: {'node_id': 'android-node-01', 'uptime_seconds': 142.3, 'ssh_port': 2222, 'cipher': 'AES-256-GCM', 'cpu_throttled': True, 'power_draw_mw': 320.5}", TerminalLineType.OUTPUT_SUCCESS))
                outputLines.add(TerminalLine("Tests passed: 4/4 (0 failures)", TerminalLineType.OUTPUT_NORMAL))
            }

            "bash", "sh" -> {
                val script = args.firstOrNull() ?: "deploy.sh"
                outputLines.add(TerminalLine("Executing $script...", TerminalLineType.OUTPUT_INFO))
                outputLines.add(TerminalLine("==========================================", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine(" [OpenCode] Remote Mobile Deploy Pipeline", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine("==========================================", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine(">> Checking SSH Daemon status on port 2222... OK", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine(">> Verifying local Git repository working tree... clean", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine(">> Running automated test suites in sandbox... PASS", TerminalLineType.OUTPUT_SUCCESS))
                outputLines.add(TerminalLine(">> Synchronizing encrypted snapshot with cloud mesh... SYNCED", TerminalLineType.OUTPUT_SUCCESS))
                outputLines.add(TerminalLine(">> [SUCCESS] Service deployed and live on http://localhost:8080", TerminalLineType.OUTPUT_SUCCESS))
            }

            "git" -> {
                val subCmd = args.firstOrNull() ?: "status"
                when (subCmd) {
                    "status" -> {
                        outputLines.add(TerminalLine("On branch main", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("Your branch is up to date with 'origin/main'.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("Changes not staged for commit:", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("  modified:   server.js", TerminalLineType.OUTPUT_ERROR))
                        outputLines.add(TerminalLine("Untracked files:", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("  api/telemetry_cache.json", TerminalLineType.OUTPUT_ERROR))
                        outputLines.add(TerminalLine("no changes added to commit (use \"git add\" to stage)", TerminalLineType.OUTPUT_NORMAL))
                    }
                    "log" -> {
                        outputLines.add(TerminalLine("commit a1b2c3d (HEAD -> main, origin/main)", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("Author: DevBox Architect <lead@opencode.dev>", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("Date:   Sun Oct 11 00:15:32 2026 -0700", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("    feat: add low battery governor and e2e encrypted sync", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine(""))
                        outputLines.add(TerminalLine("commit 8e4a91b", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("Author: DevBox Architect <lead@opencode.dev>", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("Date:   Sat Oct 10 23:40:11 2026 -0700", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("    feat: initialize code-server local HTTP daemon", TerminalLineType.OUTPUT_INFO))
                    }
                    "diff" -> {
                        outputLines.add(TerminalLine("diff --git a/server.js b/server.js", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("--- a/server.js", TerminalLineType.OUTPUT_ERROR))
                        outputLines.add(TerminalLine("+++ b/server.js", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("@@ -8,3 +8,5 @@ const server = http.createServer((req, res) => {", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("-    res.writeHead(200);", TerminalLineType.OUTPUT_ERROR))
                        outputLines.add(TerminalLine("+    res.setHeader('X-Powered-By', 'OpenCode-Android-Server');", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("+    res.writeHead(200);", TerminalLineType.OUTPUT_SUCCESS))
                    }
                    "add" -> {
                        outputLines.add(TerminalLine("Staged changes for commit.", TerminalLineType.OUTPUT_SUCCESS))
                    }
                    "commit" -> {
                        val commitMsg = if (args.size > 2 && args[1] == "-m") args.drop(2).joinToString(" ").replace("\"", "") else "Mobile commit"
                        val newSha = UUID.randomUUID().toString().take(7)
                        database.workspaceDao().insertCommit(
                            GitCommitEntity(
                                id = newSha,
                                message = commitMsg,
                                author = "Mobile Dev <developer@opencode.local>",
                                timestamp = System.currentTimeMillis(),
                                branch = "main",
                                diffSummary = "+15 -4 lines"
                            )
                        )
                        outputLines.add(TerminalLine("[main $newSha] $commitMsg", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine(" 2 files changed, 15 insertions(+), 4 deletions(-)", TerminalLineType.OUTPUT_NORMAL))
                    }
                    "push" -> {
                        outputLines.add(TerminalLine("Enumerating objects: 5, done.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("Counting objects: 100% (5/5), done.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("Compressing objects: 100% (3/3), done.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("Writing objects: 100% (3/3), 420 bytes | 420.00 KiB/s, done.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("To https://github.com/developer/mobile-devbox-microservice.git", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("   a1b2c3d..${UUID.randomUUID().toString().take(7)}  main -> main", TerminalLineType.OUTPUT_SUCCESS))
                    }
                    "pull" -> {
                        outputLines.add(TerminalLine("remote: Enumerating objects: 3, done.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("From https://github.com/developer/mobile-devbox-microservice.git", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("Already up to date.", TerminalLineType.OUTPUT_SUCCESS))
                    }
                    "branch" -> {
                        outputLines.add(TerminalLine("* main", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("  feat/ssh-tunnel", TerminalLineType.OUTPUT_NORMAL))
                    }
                    else -> {
                        outputLines.add(TerminalLine("git: '$subCmd' is not a supported git command.", TerminalLineType.OUTPUT_ERROR))
                        exitCode = 1
                    }
                }
            }

            "code-server" -> {
                val sub = args.firstOrNull() ?: "status"
                when (sub) {
                    "status" -> {
                        outputLines.add(TerminalLine("Code Server Status: RUNNING", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("  Port: 8080", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("  Web IDE: http://localhost:8080", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("  Encryption: AES-256-GCM E2E Active", TerminalLineType.OUTPUT_NORMAL))
                    }
                    "restart" -> {
                        outputLines.add(TerminalLine("Restarting OpenCode local daemon...", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("Bound to 0.0.0.0:8080. Server restarted successfully.", TerminalLineType.OUTPUT_SUCCESS))
                    }
                    else -> outputLines.add(TerminalLine("Usage: code-server [status|restart]", TerminalLineType.OUTPUT_NORMAL))
                }
            }

            "ssh" -> {
                outputLines.add(TerminalLine("OpenSSH Daemon 9.6p1 (ARM64)", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine("  Listening on: 0.0.0.0:2222", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("  Active Sessions: 1 connected from 192.168.1.145", TerminalLineType.OUTPUT_SUCCESS))
                outputLines.add(TerminalLine("  Host Key Fingerprint: SHA256:7mG89q10PksWvN42LkM09XhT/Jv1wE8bQ2y", TerminalLineType.OUTPUT_INFO))
            }

            "top" -> {
                outputLines.add(TerminalLine("top - 00:28:40 up 4:12,  2 users,  load average: 0.18, 0.22, 0.15", TerminalLineType.OUTPUT_INFO))
                outputLines.add(TerminalLine("Tasks: 14 total,   1 running,  13 sleeping,   0 stopped,   0 zombie", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("%Cpu(s):  2.4 us,  1.1 sy,  0.0 ni, 96.2 id (LOW BATTERY GOVERNOR: 75% CAP)", TerminalLineType.OUTPUT_SUCCESS))
                outputLines.add(TerminalLine("MiB Mem :   7842.0 total,   4210.5 free,   1420.2 used,   2211.3 buff/cache", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND", TerminalLineType.OUTPUT_ACCENT))
                outputLines.add(TerminalLine("  401 devbox    20   0  145.2m  48.1m  12.4m S   1.8   0.6   0:14.22 opencode-server", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("  402 devbox    20   0   42.1m  16.8m   8.2m S   0.4   0.2   0:04.10 sshd: port-2222", TerminalLineType.OUTPUT_NORMAL))
                outputLines.add(TerminalLine("  410 devbox    20   0   88.5m  22.4m  10.1m S   0.2   0.3   0:01.05 node server.js", TerminalLineType.OUTPUT_NORMAL))
            }

            "opencode" -> {
                val sub = args.firstOrNull() ?: "status"
                when (sub) {
                    "doctor", "status" -> {
                        outputLines.add(TerminalLine("=== OpenCode Agent System Diagnostics ===", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("  Repo: https://github.com/anomalyco/opencode", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("  Agent Runtime: v1.4.2 Autonomous Local Daemon", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("  Tool Handlers: read_file, edit_file, bash, grep, git_patch", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("  HTTP Server Daemon: Active on http://127.0.0.1:8080", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("  Tip: Switch to the 'AI Agent' screen to run interactive multi-step tasks", TerminalLineType.OUTPUT_NORMAL))
                    }
                    "run" -> {
                        val taskDesc = args.drop(1).joinToString(" ").ifBlank { "Refactor workspace code" }
                        outputLines.add(TerminalLine("[OpenCode] Launching agent task: \"$taskDesc\"", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("[OpenCode] 1/5 Indexing AST symbol tree for workspace...", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("[OpenCode] 2/5 Tool call: read_file(\"server.js\") -> 64 lines", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("[OpenCode] 3/5 Tool call: node --check server.js [PASS]", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("[OpenCode] 4/5 Generated unified patch (+14 lines)", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("[OpenCode] 5/5 Agent task completed. Review diff in AI Agent screen.", TerminalLineType.OUTPUT_SUCCESS))
                    }
                    else -> {
                        outputLines.add(TerminalLine("OpenCode CLI: anomalyco/opencode", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("Usage: opencode [status|doctor|run <task>]", TerminalLineType.OUTPUT_NORMAL))
                    }
                }
            }

            "deepseek", "harness" -> {
                val sub = args.firstOrNull() ?: "status"
                when (sub) {
                    "status" -> {
                        outputLines.add(TerminalLine("=== DeepSeek Mobile Harness Environment ===", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("  Harness Repo: https://github.com/techjarves/Mobile-Harness", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("  Active Model: DeepSeek-R1 (Reasoning Chain with <think>)", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("  Available Models: DeepSeek-R1, DeepSeek-Coder-V2, DeepSeek-V3", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("  Sandbox Runtime: Isolated Linux Namespace (ARM64)", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("  Assertions Engine: SWE-bench test vectors enabled", TerminalLineType.OUTPUT_NORMAL))
                    }
                    "bench", "test", "run" -> {
                        outputLines.add(TerminalLine("[Mobile-Harness] Executing DeepSeek benchmark testbed...", TerminalLineType.OUTPUT_ACCENT))
                        outputLines.add(TerminalLine("<think>", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("1. Goal: Verify algorithmic correctness & memory footprint.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("2. Model: DeepSeek-R1 reasoning tokens generated.", TerminalLineType.OUTPUT_NORMAL))
                        outputLines.add(TerminalLine("</think>", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("[Harness] Test 1: assert bfs_traversal == passed (8ms)", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("[Harness] Test 2: assert boundary_null_root == passed (3ms)", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("[Harness] Test 3: assert memory_alloc <= 35MB == passed (4ms)", TerminalLineType.OUTPUT_SUCCESS))
                        outputLines.add(TerminalLine("[Harness] Benchmark complete: 3/3 passed (100% success rate, 32.4MB heap)", TerminalLineType.OUTPUT_SUCCESS))
                    }
                    else -> {
                        outputLines.add(TerminalLine("DeepSeek Mobile Harness: techjarves/Mobile-Harness", TerminalLineType.OUTPUT_INFO))
                        outputLines.add(TerminalLine("Usage: harness [status|run|bench|test]", TerminalLineType.OUTPUT_NORMAL))
                    }
                }
            }

            else -> {
                outputLines.add(TerminalLine("bash: $cmd: command not found. Type 'help' for command list.", TerminalLineType.OUTPUT_ERROR))
                exitCode = 127
            }
        }

        newLines.addAll(outputLines)
        newLines.add(TerminalLine(""))
        _lines.value = newLines

        database.workspaceDao().insertTerminalHistory(
            TerminalHistoryEntity(
                command = trimmed,
                output = outputLines.joinToString("\n") { it.text },
                directory = currentDir,
                exitCode = exitCode
            )
        )

        return exitCode
    }
}
