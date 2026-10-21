package com.example

import com.example.data.SampleData
import com.example.git.DiffLineType
import com.example.git.GitRepositoryManager
import com.example.security.EncryptionManager
import com.example.ui.theme.EditorThemeType
import com.example.ui.theme.EditorThemes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testEncryptionRoundTrip() {
        val manager = EncryptionManager()
        val passphrase = "super-secret-developer-passphrase"
        val key = manager.deriveKey(passphrase)
        val originalText = "const http = require('http'); // E2E Encrypted Payload"

        val (cipher, iv) = manager.encryptPayload(originalText, key)
        assertNotNull(cipher)
        assertNotNull(iv)

        val decrypted = manager.decryptPayload(cipher, iv, key)
        assertEquals(originalText, decrypted)
    }

    @Test
    fun testChecksumCalculation() {
        val manager = EncryptionManager()
        val checksum = manager.computeChecksum("hello-opencode")
        assertEquals(16, checksum.length)
    }

    @Test
    fun testEditorThemesPalette() {
        for (themeType in EditorThemeType.values()) {
            val palette = EditorThemes.getPalette(themeType)
            assertNotNull(palette.background)
            assertNotNull(palette.keyword)
            assertNotNull(palette.function)
            assertNotNull(palette.string)
        }
    }

    @Test
    fun testAppThemesPalette() {
        for (themeType in com.example.ui.theme.AppThemeType.values()) {
            val colors = com.example.ui.theme.AppThemes.getColors(themeType)
            assertNotNull(colors.primary)
            assertNotNull(colors.background)
            assertNotNull(colors.surface)
            assertNotNull(colors.textPrimary)
        }
    }

    @Test
    fun testSampleDataWorkspace() {
        assertTrue(SampleData.initialFiles.isNotEmpty())
        assertTrue(SampleData.initialFiles.any { it.name == "server.js" })
        assertTrue(SampleData.initialFiles.any { it.name == "analytics.py" })
        assertEquals("default-project", SampleData.initialWorkspace.id)
    }

    @Test
    fun testDeepSeekHarnessEngineExecution() = kotlinx.coroutines.runBlocking {
        val engine = com.example.ai.DeepSeekHarnessEngine()
        val result = engine.executeHarnessTest(
            taskTitle = "Algorithm: Binary Tree Level Order Traversal (BFS)",
            modelName = "DeepSeek-R1 (Reasoning Chain)"
        )
        assertNotNull(result)
        assertEquals("DeepSeek-R1 (Reasoning Chain)", result.modelName)
        assertTrue(result.reasoningChain.contains("<think>"))
        assertTrue(result.testsPassed > 0)
        assertEquals(3, result.totalTests)
    }

    @Test
    fun testOpenCodeAgentTaskExecution() = kotlinx.coroutines.runBlocking {
        val engine = com.example.ai.OpenCodeAgentEngine()
        val state = engine.runAgentTask(
            task = "Inspect and improve server.js",
            onProgress = { }
        )
        assertNotNull(state)
        assertEquals("server.js", state.targetFilePath)
        assertTrue(state.toolCalls.isNotEmpty())
        assertNotNull(state.proposedDiff)
        assertTrue(state.proposedDiff!!.contains("+++ b/server.js"))
    }
}
