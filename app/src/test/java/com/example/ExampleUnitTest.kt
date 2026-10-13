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
}
