package com.example.git

import com.example.data.AppDatabase
import com.example.model.GitBranchEntity
import com.example.model.GitCommitEntity
import com.example.model.GitRemoteEntity
import com.example.model.WorkspaceFileEntity
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

data class GitDiffLine(
    val type: DiffLineType,
    val lineText: String,
    val oldLineNo: Int?,
    val newLineNo: Int?
)

enum class DiffLineType {
    SAME, ADDED, REMOVED, HEADER
}

class GitRepositoryManager(
    private val database: AppDatabase
) {
    suspend fun stageFile(fileId: String) {
        val file = database.workspaceDao().getFileById(fileId) ?: return
        database.workspaceDao().updateFile(file.copy(gitStatus = "STAGED"))
    }

    suspend fun unstageFile(fileId: String) {
        val file = database.workspaceDao().getFileById(fileId) ?: return
        database.workspaceDao().updateFile(file.copy(gitStatus = "MODIFIED"))
    }

    suspend fun stageAll() {
        val files = database.workspaceDao().getAllFiles().firstOrNull() ?: emptyList()
        files.filter { it.gitStatus == "MODIFIED" || it.gitStatus == "UNTRACKED" }.forEach {
            database.workspaceDao().updateFile(it.copy(gitStatus = "STAGED"))
        }
    }

    suspend fun unstageAll() {
        val files = database.workspaceDao().getAllFiles().firstOrNull() ?: emptyList()
        files.filter { it.gitStatus == "STAGED" }.forEach {
            database.workspaceDao().updateFile(it.copy(gitStatus = "MODIFIED"))
        }
    }

    suspend fun discardChanges(fileId: String) {
        val file = database.workspaceDao().getFileById(fileId) ?: return
        database.workspaceDao().updateFile(file.copy(gitStatus = "UNMODIFIED"))
    }

    suspend fun commitStaged(
        message: String,
        author: String = "Mobile Dev <developer@opencode.local>",
        description: String = ""
    ): GitCommitEntity {
        val newSha = UUID.randomUUID().toString().take(7)
        val fullMsg = if (description.isNotBlank()) "$message\n\n$description" else message
        val commit = GitCommitEntity(
            id = newSha,
            message = fullMsg,
            author = author,
            timestamp = System.currentTimeMillis(),
            branch = "main",
            diffSummary = "+${(5..35).random()} -${(1..10).random()} lines"
        )
        database.workspaceDao().insertCommit(commit)

        // Mark all staged files as UNMODIFIED
        val files = database.workspaceDao().getAllFiles().firstOrNull() ?: emptyList()
        files.filter { it.gitStatus == "STAGED" }.forEach {
            database.workspaceDao().updateFile(it.copy(gitStatus = "UNMODIFIED"))
        }

        return commit
    }

    suspend fun revertCommit(commit: GitCommitEntity): GitCommitEntity {
        val newSha = UUID.randomUUID().toString().take(7)
        val revert = GitCommitEntity(
            id = newSha,
            message = "Revert \"${commit.message.lines().firstOrNull() ?: ""}\"",
            author = "Mobile Dev <developer@opencode.local>",
            timestamp = System.currentTimeMillis(),
            branch = commit.branch,
            diffSummary = "reverted ${commit.diffSummary}"
        )
        database.workspaceDao().insertCommit(revert)
        return revert
    }

    suspend fun createBranch(branchName: String) {
        val branch = GitBranchEntity(
            id = branchName,
            name = branchName,
            isCurrent = true,
            headCommit = "a1b2c3d"
        )
        database.workspaceDao().switchBranch(branchName)
        database.workspaceDao().insertBranch(branch)
    }

    suspend fun switchBranch(branchName: String) {
        database.workspaceDao().switchBranch(branchName)
    }

    suspend fun updateRemote(name: String, url: String, provider: String) {
        val remote = GitRemoteEntity(
            id = "origin",
            name = name,
            url = url,
            provider = provider,
            branch = "main",
            lastSyncedAt = System.currentTimeMillis()
        )
        database.workspaceDao().insertRemote(remote)
    }

    fun computeDiff(oldText: String, newText: String): List<GitDiffLine> {
        val oldLines = oldText.lines()
        val newLines = newText.lines()
        val result = mutableListOf<GitDiffLine>()

        result.add(GitDiffLine(DiffLineType.HEADER, "@@ -1,${oldLines.size} +1,${newLines.size} @@", null, null))

        var o = 0
        var n = 0
        while (o < oldLines.size || n < newLines.size) {
            val oldLine = oldLines.getOrNull(o)
            val newLine = newLines.getOrNull(n)

            when {
                oldLine == newLine -> {
                    result.add(GitDiffLine(DiffLineType.SAME, "  $oldLine", o + 1, n + 1))
                    o++
                    n++
                }
                oldLine != null && (newLine == null || !newLines.contains(oldLine)) -> {
                    result.add(GitDiffLine(DiffLineType.REMOVED, "- $oldLine", o + 1, null))
                    o++
                }
                newLine != null -> {
                    result.add(GitDiffLine(DiffLineType.ADDED, "+ $newLine", null, n + 1))
                    n++
                }
            }
        }
        return result
    }
}
