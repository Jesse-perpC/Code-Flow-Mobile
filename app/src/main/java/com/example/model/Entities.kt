package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String = "default-project",
    val name: String = "remote-devbox",
    val rootPath: String = "/workspace",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workspace_files")
data class WorkspaceFileEntity(
    @PrimaryKey val id: String,
    val workspaceId: String = "default-project",
    val path: String,
    val name: String,
    val content: String = "",
    val language: String = "text",
    val isFolder: Boolean = false,
    val parentPath: String = "",
    val lastModified: Long = System.currentTimeMillis(),
    val gitStatus: String = "UNMODIFIED" // UNMODIFIED, MODIFIED, STAGED, UNTRACKED
)

@Entity(tableName = "git_commits")
data class GitCommitEntity(
    @PrimaryKey val id: String, // 7-8 char SHA
    val workspaceId: String = "default-project",
    val message: String,
    val author: String = "Mobile Dev <developer@opencode.local>",
    val timestamp: Long = System.currentTimeMillis(),
    val branch: String = "main",
    val diffSummary: String = "+12 -3 lines"
)

@Entity(tableName = "git_branches")
data class GitBranchEntity(
    @PrimaryKey val id: String, // branch name e.g. "main"
    val workspaceId: String = "default-project",
    val name: String,
    val isCurrent: Boolean = false,
    val headCommit: String = ""
)

@Entity(tableName = "git_remotes")
data class GitRemoteEntity(
    @PrimaryKey val id: String = "origin",
    val workspaceId: String = "default-project",
    val name: String = "origin",
    val url: String = "https://github.com/developer/remote-devbox.git",
    val provider: String = "GitHub", // GitHub, GitLab, Bitbucket
    val branch: String = "main",
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "server_config")
data class ServerConfigEntity(
    @PrimaryKey val id: String = "config_singleton",
    val port: Int = 8080,
    val sshPort: Int = 2222,
    val isServerRunning: Boolean = false,
    val isSshRunning: Boolean = false,
    val authToken: String = "dev-secret-token",
    val tlsEnabled: Boolean = true,
    val lowBatteryMode: Boolean = false,
    val cpuThrottleLimit: Int = 75,
    val keepScreenAwake: Boolean = true,
    val selectedEditorTheme: String = "GITHUB_DARK",
    val fontSizeSp: Float = 13.0f,
    val e2eEncryptionEnabled: Boolean = true,
    val realTimeCollabEnabled: Boolean = true,
    val customDomainTunnel: String = "devbox-local.tunnel.mesh"
)

@Entity(tableName = "terminal_history")
data class TerminalHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val command: String,
    val output: String,
    val directory: String = "/workspace",
    val exitCode: Int = 0
)

@Entity(tableName = "cloud_sync_snapshots")
data class CloudSyncSnapshotEntity(
    @PrimaryKey val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val encryptedHash: String,
    val fileCount: Int,
    val syncStatus: String = "SYNCED", // SYNCED, PENDING, OFFLINE
    val activePeersCount: Int = 2
)
