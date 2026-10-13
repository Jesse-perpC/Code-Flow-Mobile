package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CloudSyncSnapshotEntity
import com.example.model.GitBranchEntity
import com.example.model.GitCommitEntity
import com.example.model.GitRemoteEntity
import com.example.model.ServerConfigEntity
import com.example.model.TerminalHistoryEntity
import com.example.model.WorkspaceEntity
import com.example.model.WorkspaceFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    // Workspaces
    @Query("SELECT * FROM workspaces LIMIT 1")
    fun getWorkspace(): Flow<WorkspaceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: WorkspaceEntity)

    // Files
    @Query("SELECT * FROM workspace_files ORDER BY isFolder DESC, name ASC")
    fun getAllFiles(): Flow<List<WorkspaceFileEntity>>

    @Query("SELECT * FROM workspace_files WHERE id = :id LIMIT 1")
    suspend fun getFileById(id: String): WorkspaceFileEntity?

    @Query("SELECT * FROM workspace_files WHERE path = :path LIMIT 1")
    suspend fun getFileByPath(path: String): WorkspaceFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: WorkspaceFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<WorkspaceFileEntity>)

    @Update
    suspend fun updateFile(file: WorkspaceFileEntity)

    @Query("DELETE FROM workspace_files WHERE id = :id")
    suspend fun deleteFileById(id: String)

    @Query("SELECT COUNT(*) FROM workspace_files")
    suspend fun getFileCount(): Int

    // Server Config
    @Query("SELECT * FROM server_config WHERE id = 'config_singleton' LIMIT 1")
    fun getServerConfig(): Flow<ServerConfigEntity?>

    @Query("SELECT * FROM server_config WHERE id = 'config_singleton' LIMIT 1")
    suspend fun getServerConfigSync(): ServerConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerConfig(config: ServerConfigEntity)

    // Git
    @Query("SELECT * FROM git_commits ORDER BY timestamp DESC")
    fun getCommits(): Flow<List<GitCommitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommit(commit: GitCommitEntity)

    @Query("SELECT * FROM git_branches")
    fun getBranches(): Flow<List<GitBranchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: GitBranchEntity)

    @Query("UPDATE git_branches SET isCurrent = (name = :branchName)")
    suspend fun switchBranch(branchName: String)

    @Query("SELECT * FROM git_remotes LIMIT 1")
    fun getRemote(): Flow<GitRemoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemote(remote: GitRemoteEntity)

    // Terminal History
    @Query("SELECT * FROM terminal_history ORDER BY id DESC LIMIT 50")
    fun getTerminalHistory(): Flow<List<TerminalHistoryEntity>>

    @Insert
    suspend fun insertTerminalHistory(entry: TerminalHistoryEntity)

    @Query("DELETE FROM terminal_history")
    suspend fun clearTerminalHistory()

    // Cloud Sync
    @Query("SELECT * FROM cloud_sync_snapshots ORDER BY timestamp DESC")
    fun getSyncSnapshots(): Flow<List<CloudSyncSnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncSnapshot(snapshot: CloudSyncSnapshotEntity)
}
