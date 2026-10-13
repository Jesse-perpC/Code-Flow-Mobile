package com.example.data

import com.example.model.CloudSyncSnapshotEntity
import com.example.model.GitBranchEntity
import com.example.model.GitCommitEntity
import com.example.model.GitRemoteEntity
import com.example.model.ServerConfigEntity
import com.example.model.WorkspaceEntity
import com.example.model.WorkspaceFileEntity

object SampleData {
    val initialWorkspace = WorkspaceEntity(
        id = "default-project",
        name = "mobile-devbox-microservice",
        rootPath = "/workspace"
    )

    val initialConfig = ServerConfigEntity(
        id = "config_singleton",
        port = 8080,
        sshPort = 2222,
        isServerRunning = true,
        isSshRunning = true,
        authToken = "opencode-sec-9921",
        tlsEnabled = true,
        lowBatteryMode = false,
        cpuThrottleLimit = 80,
        keepScreenAwake = true,
        selectedEditorTheme = "GITHUB_DARK",
        fontSizeSp = 13.5f,
        e2eEncryptionEnabled = true,
        realTimeCollabEnabled = true,
        customDomainTunnel = "devbox-mobile.mesh.local"
    )

    val initialBranches = listOf(
        GitBranchEntity(id = "main", name = "main", isCurrent = true, headCommit = "a1b2c3d"),
        GitBranchEntity(id = "feat/ssh-tunnel", name = "feat/ssh-tunnel", isCurrent = false, headCommit = "9f8e7d6")
    )

    val initialCommits = listOf(
        GitCommitEntity(
            id = "a1b2c3d",
            message = "feat: add low battery governor and e2e encrypted sync",
            author = "DevBox Architect <lead@opencode.dev>",
            timestamp = System.currentTimeMillis() - 3600000 * 2,
            branch = "main",
            diffSummary = "+48 -12 lines"
        ),
        GitCommitEntity(
            id = "8e4a91b",
            message = "feat: initialize code-server local HTTP daemon and terminal shell",
            author = "DevBox Architect <lead@opencode.dev>",
            timestamp = System.currentTimeMillis() - 3600000 * 24,
            branch = "main",
            diffSummary = "+140 -0 lines"
        )
    )

    val initialRemote = GitRemoteEntity(
        id = "origin",
        name = "origin",
        url = "https://github.com/developer/mobile-devbox-microservice.git",
        provider = "GitHub",
        branch = "main"
    )

    val initialFiles = listOf(
        WorkspaceFileEntity(
            id = "file-server-js",
            path = "server.js",
            name = "server.js",
            language = "javascript",
            isFolder = false,
            content = """// OpenCode Local Microservice Engine
const http = require('http');

const PORT = process.env.PORT || 8080;
const AUTH_KEY = process.env.DEVBOX_KEY || 'e2e-auth-token';

const server = http.createServer((req, res) => {
    // E2E Secured local endpoint
    res.setHeader('Content-Type', 'application/json');
    res.setHeader('X-Powered-By', 'OpenCode-Android-Server');

    if (req.url === '/health') {
        res.writeHead(200);
        return res.end(JSON.stringify({
            status: 'online',
            runtime: 'Node.js v20.12 (ARM64)',
            device: 'Mobile DevBox Server',
            lowBatteryGovernor: 'active',
            e2eEncrypted: true,
            timestamp: Date.now()
        }));
    }

    res.writeHead(200);
    res.end(JSON.stringify({
        message: 'OpenCode Local Server responding successfully!',
        endpoints: ['/health', '/metrics', '/ws-terminal']
    }));
});

server.listen(PORT, () => {
    console.log(`[OpenCode] Daemon running at http://0.0.0.0:${'$'}{PORT}`);
});
""".trimIndent()
        ),
        WorkspaceFileEntity(
            id = "file-app-py",
            path = "api/analytics.py",
            name = "analytics.py",
            language = "python",
            isFolder = false,
            parentPath = "api",
            content = """# Mobile DevBox Analytics & Telemetry Engine
import time
import hashlib
from typing import Dict, Any

class DevBoxTelemetry:
    def __init__(self, node_id: str = "android-node-01"):
        self.node_id = node_id
        self.start_time = time.time()
        self.active_sessions = 3

    def get_system_health(self) -> Dict[str, Any]:
        uptime_sec = time.time() - self.start_time
        return {
            "node_id": self.node_id,
            "uptime_seconds": round(uptime_sec, 2),
            "ssh_port": 2222,
            "cipher": "AES-256-GCM",
            "cpu_throttled": True,
            "power_draw_mw": 320.5
        }

    def compute_e2e_signature(self, payload: str, secret_key: str) -> str:
        combined = f"{payload}:{secret_key}:{self.node_id}"
        return hashlib.sha256(combined.encode()).hexdigest()

if __name__ == "__main__":
    telemetry = DevBoxTelemetry()
    print("DevBox Status:", telemetry.get_system_health())
""".trimIndent()
        ),
        WorkspaceFileEntity(
            id = "file-deploy-sh",
            path = "deploy.sh",
            name = "deploy.sh",
            language = "bash",
            isFolder = false,
            content = """#!/usr/bin/env bash
# OpenCode Zero-Downtime Deployment & SSH Hook
set -euo pipefail

echo "=========================================="
echo " [OpenCode] Remote Mobile Deploy Pipeline"
echo "=========================================="

echo ">> Checking SSH Daemon status on port 2222..."
echo ">> Verifying local Git repository working tree..."
git status --short

echo ">> Running automated test suites in sandbox..."
python3 -m unittest discover -s ./tests || true

echo ">> Synchronizing encrypted snapshot with cloud mesh..."
echo ">> [SUCCESS] Service deployed and live on http://localhost:8080"
""".trimIndent()
        ),
        WorkspaceFileEntity(
            id = "file-readme",
            path = "README.md",
            name = "README.md",
            language = "markdown",
            isFolder = false,
            content = """# OpenCode Mobile DevBox

Welcome to your locally hosted **OpenCode Server** running directly on your Android device!

### Key Features
- **Local Code Server**: Full web IDE hosted on `http://<your-device-ip>:8080`
- **SSH Daemon**: Connect from any laptop or desktop using:
  `ssh opencode@<ip> -p 2222`
- **Interactive Shell**: Embedded UNIX-style terminal with quick accessory keyboard
- **AI Completion Engine**: Powered by Gemini API with offline heuristic completion fallback
- **Git Version Control**: Full branch management, visual diffs, and remote sync
- **End-to-End Encryption**: AES-256-GCM encrypted cloud backups & real-time collaboration
- **Low Battery Governor**: Adaptive CPU throttling and true OLED black mode
""".trimIndent()
        ),
        WorkspaceFileEntity(
            id = "file-config-json",
            path = "config.json",
            name = "config.json",
            language = "json",
            isFolder = false,
            content = """{
  "server": {
    "name": "Mobile DevBox OpenCode Server",
    "httpPort": 8080,
    "sshPort": 2222,
    "host": "0.0.0.0",
    "tunnelEnabled": true
  },
  "security": {
    "encryption": "AES-256-GCM",
    "keyDerivation": "PBKDF2-SHA256",
    "requireAuth": true
  },
  "git": {
    "defaultBranch": "main",
    "remote": "origin"
  },
  "batteryOptimization": {
    "throttleBackgroundCpu": true,
    "terminalFps": 30
  }
}
""".trimIndent()
        )
    )

    val initialSnapshots = listOf(
        CloudSyncSnapshotEntity(
            id = "snap-9801",
            timestamp = System.currentTimeMillis() - 1800000,
            encryptedHash = "sha256:d8a2...3f1c (AES-256-GCM)",
            fileCount = 5,
            syncStatus = "SYNCED",
            activePeersCount = 3
        ),
        CloudSyncSnapshotEntity(
            id = "snap-9755",
            timestamp = System.currentTimeMillis() - 7200000,
            encryptedHash = "sha256:7c11...889a (AES-256-GCM)",
            fileCount = 4,
            syncStatus = "SYNCED",
            activePeersCount = 2
        )
    )
}
