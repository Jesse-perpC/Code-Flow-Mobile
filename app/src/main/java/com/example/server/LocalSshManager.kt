package com.example.server

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

data class SshSession(
    val id: String,
    val clientIp: String,
    val clientUsername: String,
    val cipher: String,
    val connectedAt: Long,
    val terminalType: String = "xterm-256color"
)

data class SshDaemonStats(
    val isRunning: Boolean = false,
    val port: Int = 2222,
    val hostFingerprintEd25519: String = "SHA256:7mG89q10PksWvN42LkM09XhT/Jv1wE8bQ2y",
    val hostFingerprintRsa: String = "SHA256:4t7a0kLp12mX8jN4QzWvYp0k1m99o",
    val authorizedKeysCount: Int = 2,
    val activeSessions: List<SshSession> = emptyList(),
    val acceptedCiphers: List<String> = listOf("chacha20-poly1305@openssh.com", "aes256-gcm@openssh.com"),
    val e2eEncrypted: Boolean = true
)

class LocalSshManager {
    private val _stats = MutableStateFlow(
        SshDaemonStats(
            isRunning = true,
            activeSessions = listOf(
                SshSession(
                    id = "sess-8812",
                    clientIp = "192.168.1.145",
                    clientUsername = "developer",
                    cipher = "aes256-gcm@openssh.com",
                    connectedAt = System.currentTimeMillis() - 120000
                )
            )
        )
    )
    val stats: StateFlow<SshDaemonStats> = _stats.asStateFlow()

    fun toggleSshDaemon(enable: Boolean, port: Int = 2222) {
        _stats.value = _stats.value.copy(
            isRunning = enable,
            port = port,
            activeSessions = if (enable) _stats.value.activeSessions else emptyList()
        )
    }

    fun addAuthorizedKey(keySnippet: String) {
        _stats.value = _stats.value.copy(
            authorizedKeysCount = _stats.value.authorizedKeysCount + 1
        )
    }

    fun terminateSession(sessionId: String) {
        _stats.value = _stats.value.copy(
            activeSessions = _stats.value.activeSessions.filter { it.id != sessionId }
        )
    }

    fun getSshCommand(localIp: String): String {
        return "ssh developer@$localIp -p ${_stats.value.port}"
    }
}
