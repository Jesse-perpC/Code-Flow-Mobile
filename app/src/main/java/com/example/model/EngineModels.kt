package com.example.model

enum class ExecutionEngine(
    val id: String,
    val title: String,
    val shortName: String,
    val repoUrl: String,
    val description: String,
    val badge: String,
    val creator: String
) {
    OPENCODE(
        id = "opencode",
        title = "OpenCode Server & Agent",
        shortName = "OpenCode",
        repoUrl = "https://github.com/anomalyco/opencode",
        description = "Autonomous AI coding agent, local HTTP daemon & interactive terminal tools by anomalyco",
        badge = "anomalyco/opencode",
        creator = "Anomaly Co"
    ),
    DEEPSEEK_HARNESS(
        id = "deepseek_harness",
        title = "DeepSeek Mobile Harness",
        shortName = "DeepSeek Harness",
        repoUrl = "https://github.com/techjarves/Mobile-Harness",
        description = "DeepSeek-Coder & R1 reasoning benchmark harness & mobile test runner by techjarves",
        badge = "techjarves/Mobile-Harness",
        creator = "TechJarves"
    )
}

data class HarnessAssertion(
    val name: String,
    val passed: Boolean,
    val durationMs: Long = 12
)

data class DeepSeekHarnessResult(
    val taskTitle: String,
    val modelName: String,
    val passed: Boolean,
    val executionTimeMs: Long,
    val memoryUsageMb: Double,
    val testsPassed: Int,
    val totalTests: Int,
    val reasoningChain: String,
    val generatedCode: String,
    val assertions: List<HarnessAssertion> = emptyList(),
    val isSavedToWorkspace: Boolean = false,
    val savedFileName: String? = null
)

data class AgentToolCall(
    val tool: String,
    val argument: String,
    val status: String,
    val output: String
)

data class OpenCodeAgentTaskState(
    val task: String,
    val isRunning: Boolean,
    val completedSteps: List<String>,
    val currentStep: String,
    val targetFilePath: String = "server.js",
    val proposedDiff: String? = null,
    val newFileContent: String? = null,
    val toolCalls: List<AgentToolCall> = emptyList(),
    val isApplied: Boolean = false
)
