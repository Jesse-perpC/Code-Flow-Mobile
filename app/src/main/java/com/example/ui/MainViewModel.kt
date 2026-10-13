package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiCompletionResult
import com.example.ai.GeminiCodeEngine
import com.example.data.AppDatabase
import com.example.data.SampleData
import com.example.git.GitRepositoryManager
import com.example.model.CloudSyncSnapshotEntity
import com.example.model.GitBranchEntity
import com.example.model.GitCommitEntity
import com.example.model.GitRemoteEntity
import com.example.model.ServerConfigEntity
import com.example.model.WorkspaceFileEntity
import com.example.security.EncryptionManager
import com.example.security.PeerCollaborator
import com.example.server.LocalCodeServerManager
import com.example.server.LocalSshManager
import com.example.server.NetworkUtils
import com.example.server.ServerRuntimeStats
import com.example.server.SshDaemonStats
import com.example.terminal.TerminalEngine
import com.example.terminal.TerminalLine
import com.example.ai.DeepSeekHarnessEngine
import com.example.ai.OpenCodeAgentEngine
import com.example.model.DeepSeekHarnessResult
import com.example.model.ExecutionEngine
import com.example.model.OpenCodeAgentTaskState
import com.example.ui.theme.AppThemeType
import com.example.ui.theme.EditorThemeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    DASHBOARD,
    AI_HARNESS,
    EDITOR,
    TERMINAL,
    GIT,
    SYNC_SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.workspaceDao()

    val serverManager = LocalCodeServerManager(database, viewModelScope)
    val sshManager = LocalSshManager()
    val terminalEngine = TerminalEngine(database)
    val aiEngine = GeminiCodeEngine()
    val gitManager = GitRepositoryManager(database)
    val encryptionManager = EncryptionManager()
    val deepSeekHarnessEngine = DeepSeekHarnessEngine()
    val opencodeAgentEngine = OpenCodeAgentEngine()

    // Active Execution Engine (OpenCode vs DeepSeek Harness)
    private val _currentEngine = MutableStateFlow(ExecutionEngine.OPENCODE)
    val currentEngine: StateFlow<ExecutionEngine> = _currentEngine.asStateFlow()

    private val _harnessResult = MutableStateFlow<DeepSeekHarnessResult?>(null)
    val harnessResult: StateFlow<DeepSeekHarnessResult?> = _harnessResult.asStateFlow()

    private val _isHarnessRunning = MutableStateFlow(false)
    val isHarnessRunning: StateFlow<Boolean> = _isHarnessRunning.asStateFlow()

    private val _selectedDeepSeekModel = MutableStateFlow("DeepSeek-R1 (Reasoning Chain)")
    val selectedDeepSeekModel: StateFlow<String> = _selectedDeepSeekModel.asStateFlow()

    fun setSelectedDeepSeekModel(model: String) {
        _selectedDeepSeekModel.value = model
    }

    private val _opencodeAgentState = MutableStateFlow<OpenCodeAgentTaskState?>(null)
    val opencodeAgentState: StateFlow<OpenCodeAgentTaskState?> = _opencodeAgentState.asStateFlow()

    fun setExecutionEngine(engine: ExecutionEngine) {
        _currentEngine.value = engine
        showMessage("Switched runtime to ${engine.title}")
    }

    fun runDeepSeekHarness(task: String, model: String = _selectedDeepSeekModel.value) {
        viewModelScope.launch {
            _isHarnessRunning.value = true
            _harnessResult.value = null
            showMessage("Executing DeepSeek Mobile Harness on $model...")
            val result = deepSeekHarnessEngine.executeHarnessTest(task, model)
            _harnessResult.value = result
            _isHarnessRunning.value = false
            showMessage("DeepSeek evaluation completed: ${result.testsPassed}/${result.totalTests} tests passed (${result.executionTimeMs}ms)")
        }
    }

    fun runOpenCodeAgentTask(task: String) {
        viewModelScope.launch {
            showMessage("OpenCode Agent started: $task")
            opencodeAgentEngine.runAgentTask(task) { state ->
                _opencodeAgentState.value = state
            }
            showMessage("OpenCode Agent finished task!")
        }
    }

    fun applyAgentDiffToWorkspace(state: OpenCodeAgentTaskState) {
        viewModelScope.launch {
            val contentToApply = state.newFileContent ?: return@launch
            val existing = dao.getFileByPath(state.targetFilePath)
            if (existing != null) {
                val updated = existing.copy(
                    content = contentToApply,
                    lastModified = System.currentTimeMillis(),
                    gitStatus = "MODIFIED"
                )
                dao.updateFile(updated)
            } else {
                dao.insertFile(
                    WorkspaceFileEntity(
                        id = UUID.randomUUID().toString(),
                        path = state.targetFilePath,
                        name = state.targetFilePath.substringAfterLast('/'),
                        content = contentToApply,
                        language = if (state.targetFilePath.endsWith(".sh")) "bash" else "javascript",
                        gitStatus = "MODIFIED"
                    )
                )
            }
            _opencodeAgentState.value = state.copy(isApplied = true)
            if (_currentSelectedFile.value?.path == state.targetFilePath) {
                _editorContent.value = contentToApply
            }
            showMessage("Applied OpenCode patch to ${state.targetFilePath}")
        }
    }

    fun saveHarnessSolutionToFile(result: DeepSeekHarnessResult, customFileName: String? = null) {
        viewModelScope.launch {
            val fileName = customFileName ?: "deepseek_solution.py"
            val existing = dao.getFileByPath(fileName)
            if (existing != null) {
                val updated = existing.copy(
                    content = result.generatedCode,
                    lastModified = System.currentTimeMillis(),
                    gitStatus = "MODIFIED"
                )
                dao.updateFile(updated)
            } else {
                dao.insertFile(
                    WorkspaceFileEntity(
                        id = UUID.randomUUID().toString(),
                        path = fileName,
                        name = fileName,
                        content = result.generatedCode,
                        language = "python",
                        gitStatus = "UNTRACKED"
                    )
                )
            }
            _harnessResult.value = result.copy(isSavedToWorkspace = true, savedFileName = fileName)
            showMessage("Saved DeepSeek solution to $fileName in workspace")
        }
    }

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Database flows
    val files: StateFlow<List<WorkspaceFileEntity>> = dao.getAllFiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commits: StateFlow<List<GitCommitEntity>> = dao.getCommits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val branches: StateFlow<List<GitBranchEntity>> = dao.getBranches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val remote: StateFlow<GitRemoteEntity?> = dao.getRemote()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val serverConfig: StateFlow<ServerConfigEntity?> = dao.getServerConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val syncSnapshots: StateFlow<List<CloudSyncSnapshotEntity>> = dao.getSyncSnapshots()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Engine flows
    val serverStats: StateFlow<ServerRuntimeStats> = serverManager.stats
    val sshStats: StateFlow<SshDaemonStats> = sshManager.stats
    val terminalLines: StateFlow<List<TerminalLine>> = terminalEngine.lines

    // Active File & Editor State
    private val _currentSelectedFile = MutableStateFlow<WorkspaceFileEntity?>(null)
    val currentSelectedFile: StateFlow<WorkspaceFileEntity?> = _currentSelectedFile.asStateFlow()

    private val _editorContent = MutableStateFlow("")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    private val _isEditorDirty = MutableStateFlow(false)
    val isEditorDirty: StateFlow<Boolean> = _isEditorDirty.asStateFlow()

    // AI state
    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiSuggestion = MutableStateFlow<String?>(null)
    val aiSuggestion: StateFlow<String?> = _aiSuggestion.asStateFlow()

    private val _aiExplanation = MutableStateFlow<String?>(null)
    val aiExplanation: StateFlow<String?> = _aiExplanation.asStateFlow()

    // System Telemetry
    private val _localIp = MutableStateFlow("127.0.0.1")
    val localIp: StateFlow<String> = _localIp.asStateFlow()

    private val _batteryLevel = MutableStateFlow(85)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()

    private val _peers = MutableStateFlow<List<PeerCollaborator>>(emptyList())
    val peers: StateFlow<List<PeerCollaborator>> = _peers.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        initializeData()
        startTelemetryLoop()
    }

    private fun initializeData() {
        viewModelScope.launch {
            val count = dao.getFileCount()
            if (count == 0) {
                dao.insertWorkspace(SampleData.initialWorkspace)
                dao.insertServerConfig(SampleData.initialConfig)
                dao.insertFiles(SampleData.initialFiles)
                SampleData.initialBranches.forEach { dao.insertBranch(it) }
                SampleData.initialCommits.forEach { dao.insertCommit(it) }
                dao.insertRemote(SampleData.initialRemote)
                SampleData.initialSnapshots.forEach { dao.insertSyncSnapshot(it) }
            }

            _localIp.value = NetworkUtils.getLocalIpAddress()
            val (bat, chg) = NetworkUtils.getBatteryInfo(getApplication())
            _batteryLevel.value = bat
            _isCharging.value = chg
            _peers.value = encryptionManager.getSamplePeers()

            // Auto-start server & select default file
            val cfg = dao.getServerConfigSync() ?: SampleData.initialConfig
            serverManager.startServer(cfg.port, cfg.authToken, _localIp.value)
            sshManager.toggleSshDaemon(true, cfg.sshPort)

            val initialFile = dao.getFileByPath("server.js") ?: dao.getAllFiles().stateIn(this).value.firstOrNull()
            if (initialFile != null) {
                _currentSelectedFile.value = initialFile
                _editorContent.value = initialFile.content
            }
        }
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                serverManager.tickUptime()
                val (bat, chg) = NetworkUtils.getBatteryInfo(getApplication())
                _batteryLevel.value = bat
                _isCharging.value = chg
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectFile(file: WorkspaceFileEntity) {
        _currentSelectedFile.value = file
        _editorContent.value = file.content
        _isEditorDirty.value = false
        _aiSuggestion.value = null
        _aiExplanation.value = null
    }

    fun onEditorContentChange(newContent: String) {
        _editorContent.value = newContent
        _isEditorDirty.value = true
    }

    fun saveCurrentFile() {
        val file = _currentSelectedFile.value ?: return
        viewModelScope.launch {
            val updated = file.copy(
                content = _editorContent.value,
                lastModified = System.currentTimeMillis(),
                gitStatus = "MODIFIED"
            )
            dao.updateFile(updated)
            _currentSelectedFile.value = updated
            _isEditorDirty.value = false
            showMessage("File saved: ${file.name}")
        }
    }

    fun createNewFile(name: String) {
        viewModelScope.launch {
            val lang = when {
                name.endsWith(".py") -> "python"
                name.endsWith(".js") -> "javascript"
                name.endsWith(".ts") -> "typescript"
                name.endsWith(".kt") -> "kotlin"
                name.endsWith(".sh") -> "bash"
                name.endsWith(".json") -> "json"
                name.endsWith(".md") -> "markdown"
                else -> "text"
            }
            val newFile = WorkspaceFileEntity(
                id = UUID.randomUUID().toString(),
                path = name,
                name = name,
                content = "// $name created in OpenCode Mobile DevBox\n",
                language = lang,
                gitStatus = "UNTRACKED"
            )
            dao.insertFile(newFile)
            selectFile(newFile)
            showMessage("Created $name")
        }
    }

    fun deleteFile(file: WorkspaceFileEntity) {
        viewModelScope.launch {
            dao.deleteFileById(file.id)
            if (_currentSelectedFile.value?.id == file.id) {
                val nextFile = dao.getFileByPath("server.js")
                if (nextFile != null) selectFile(nextFile) else {
                    _currentSelectedFile.value = null
                    _editorContent.value = ""
                }
            }
            showMessage("Deleted ${file.name}")
        }
    }

    fun executeTerminalCommand(command: String) {
        viewModelScope.launch {
            terminalEngine.execute(command)
        }
    }

    fun clearTerminal() {
        terminalEngine.clear()
    }

    fun toggleServer(start: Boolean) {
        val cfg = serverConfig.value ?: SampleData.initialConfig
        if (start) {
            serverManager.startServer(cfg.port, cfg.authToken, _localIp.value)
            showMessage("Code Server listening on port ${cfg.port}")
        } else {
            serverManager.stopServer()
            showMessage("Code Server stopped")
        }
    }

    fun toggleSsh(start: Boolean) {
        val cfg = serverConfig.value ?: SampleData.initialConfig
        sshManager.toggleSshDaemon(start, cfg.sshPort)
        showMessage(if (start) "SSH Daemon listening on port ${cfg.sshPort}" else "SSH Daemon stopped")
    }

    fun requestAiCodeCompletion(instruction: String) {
        val file = _currentSelectedFile.value ?: return
        viewModelScope.launch {
            _aiLoading.value = true
            _aiSuggestion.value = null
            when (val result = aiEngine.completeCode(file.language, _editorContent.value, instruction)) {
                is AiCompletionResult.Success -> {
                    _aiSuggestion.value = result.text
                    val source = if (result.isFromCloud) "Gemini Cloud" else "Offline Assist"
                    showMessage("AI completion received from $source")
                }
                is AiCompletionResult.Error -> {
                    showMessage("AI Error: ${result.message}")
                }
            }
            _aiLoading.value = false
        }
    }

    fun acceptAiCompletion() {
        val suggestion = _aiSuggestion.value ?: return
        val current = _editorContent.value
        val separator = if (current.endsWith("\n")) "" else "\n\n"
        _editorContent.value = current + separator + suggestion
        _isEditorDirty.value = true
        _aiSuggestion.value = null
        showMessage("Accepted AI completion")
    }

    fun dismissAiCompletion() {
        _aiSuggestion.value = null
    }

    fun requestAiCodeExplanation() {
        val file = _currentSelectedFile.value ?: return
        viewModelScope.launch {
            _aiLoading.value = true
            when (val result = aiEngine.explainCode(file.language, _editorContent.value)) {
                is AiCompletionResult.Success -> {
                    _aiExplanation.value = result.text
                }
                is AiCompletionResult.Error -> {
                    showMessage("Explanation error: ${result.message}")
                }
            }
            _aiLoading.value = false
        }
    }

    fun dismissAiExplanation() {
        _aiExplanation.value = null
    }

    fun stageFile(fileId: String) {
        viewModelScope.launch {
            gitManager.stageFile(fileId)
            showMessage("File staged")
        }
    }

    fun unstageFile(fileId: String) {
        viewModelScope.launch {
            gitManager.unstageFile(fileId)
            showMessage("File unstaged")
        }
    }

    fun stageAllFiles() {
        viewModelScope.launch {
            gitManager.stageAll()
            showMessage("Staged all modified and untracked files")
        }
    }

    fun unstageAllFiles() {
        viewModelScope.launch {
            gitManager.unstageAll()
            showMessage("Unstaged all files")
        }
    }

    fun discardFileChanges(fileId: String) {
        viewModelScope.launch {
            gitManager.discardChanges(fileId)
            showMessage("Discarded working tree changes")
        }
    }

    fun commitChanges(message: String, description: String = "") {
        if (message.isBlank()) return
        viewModelScope.launch {
            val commit = gitManager.commitStaged(message, description = description)
            showMessage("Committed [${commit.id}]: $message")
        }
    }

    fun revertCommit(commit: GitCommitEntity) {
        viewModelScope.launch {
            val rev = gitManager.revertCommit(commit)
            showMessage("Reverted commit [${commit.id}] -> [${rev.id}]")
        }
    }

    fun pushToRemote() {
        viewModelScope.launch {
            delay(800)
            showMessage("Pushed commits to origin/main successfully")
        }
    }

    fun pullFromRemote() {
        viewModelScope.launch {
            delay(800)
            showMessage("Pulled latest changes from origin/main: Fast-forward")
        }
    }

    fun switchBranch(branchName: String) {
        viewModelScope.launch {
            gitManager.switchBranch(branchName)
            showMessage("Switched to branch $branchName")
        }
    }

    fun createBranch(branchName: String) {
        if (branchName.isBlank()) return
        viewModelScope.launch {
            gitManager.createBranch(branchName)
            showMessage("Created and switched to branch $branchName")
        }
    }

    fun createEncryptedCloudSnapshot() {
        viewModelScope.launch {
            val fileCount = files.value.size
            val snapshot = CloudSyncSnapshotEntity(
                id = "snap-${(1000..9999).random()}",
                timestamp = System.currentTimeMillis(),
                encryptedHash = "sha256:${UUID.randomUUID().toString().take(12)} (AES-256-GCM)",
                fileCount = fileCount,
                syncStatus = "SYNCED",
                activePeersCount = _peers.value.size
            )
            dao.insertSyncSnapshot(snapshot)
            showMessage("Created E2E encrypted cloud backup snapshot")
        }
    }

    private val _appTheme = MutableStateFlow(AppThemeType.CATPPUCCIN_MOCHA)
    val appTheme: StateFlow<AppThemeType> = _appTheme.asStateFlow()

    fun setAppTheme(theme: AppThemeType) {
        _appTheme.value = theme
        val matchingEditorTheme = when (theme) {
            AppThemeType.CATPPUCCIN_MOCHA -> EditorThemeType.CATPPUCCIN
            AppThemeType.NORD_FROST -> EditorThemeType.NORD
            AppThemeType.ROSE_PINE -> EditorThemeType.ROSE_PINE
            AppThemeType.GRUVBOX_DARK -> EditorThemeType.GRUVBOX
            AppThemeType.CYBER_DARK -> EditorThemeType.GITHUB_DARK
            AppThemeType.OLED_BLACK -> EditorThemeType.GITHUB_DARK
            AppThemeType.TOKYO_NIGHT -> EditorThemeType.TOKYO_NIGHT
            AppThemeType.DRACULA -> EditorThemeType.DRACULA
            AppThemeType.MONOKAI_PRO -> EditorThemeType.MONOKAI
            AppThemeType.SYNTHWAVE_84 -> EditorThemeType.DRACULA
        }
        updateEditorTheme(matchingEditorTheme)
        showMessage("Theme changed to ${theme.displayName}")
    }

    fun updateEditorTheme(theme: EditorThemeType) {
        viewModelScope.launch {
            val current = serverConfig.value ?: SampleData.initialConfig
            dao.insertServerConfig(current.copy(selectedEditorTheme = theme.name))
            showMessage("Applied editor theme: ${theme.displayName}")
        }
    }

    fun toggleLowBatteryMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = serverConfig.value ?: SampleData.initialConfig
            dao.insertServerConfig(current.copy(lowBatteryMode = enabled))
            showMessage(if (enabled) "Low Battery Governor Enabled (Throttled CPU, 30fps)" else "Full Performance Mode Enabled")
        }
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
