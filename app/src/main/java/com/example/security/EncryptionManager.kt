package com.example.security

import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

data class PeerCollaborator(
    val id: String,
    val name: String,
    val clientType: String,
    val isOnline: Boolean,
    val latencyMs: Int,
    val currentFile: String,
    val cursorPosition: String
)

object Base64Compat {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    fun encode(bytes: ByteArray): String {
        val out = StringBuilder((bytes.size * 4 + 2) / 3)
        var i = 0
        while (i < bytes.size) {
            val b0 = bytes[i].toInt() and 0xFF
            val b1 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else -1
            val b2 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else -1

            out.append(ALPHABET[b0 shr 2])
            if (b1 != -1) {
                out.append(ALPHABET[((b0 and 0x03) shl 4) or (b1 shr 4)])
                if (b2 != -1) {
                    out.append(ALPHABET[((b1 and 0x0F) shl 2) or (b2 shr 6)])
                    out.append(ALPHABET[b2 and 0x3F])
                } else {
                    out.append(ALPHABET[(b1 and 0x0F) shl 2])
                    out.append('=')
                }
            } else {
                out.append(ALPHABET[(b0 and 0x03) shl 4])
                out.append("==")
            }
            i += 3
        }
        return out.toString()
    }

    fun decode(str: String): ByteArray {
        val clean = str.replace("=", "").trim()
        val out = ByteArrayOutputStream()
        var buf = 0
        var bits = 0
        for (c in clean) {
            val idx = ALPHABET.indexOf(c)
            if (idx >= 0) {
                buf = (buf shl 6) or idx
                bits += 6
                if (bits >= 8) {
                    bits -= 8
                    out.write((buf shr bits) and 0xFF)
                }
            }
        }
        return out.toByteArray()
    }
}

class EncryptionManager {
    private val keyAlgorithm = "AES"
    private val cipherTransformation = "AES/GCM/NoPadding"
    private val gcmTagLengthBits = 128
    private val gcmIvLengthBytes = 12

    fun deriveKey(passphrase: String): SecretKey {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(passphrase.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, keyAlgorithm)
    }

    fun encryptPayload(plainText: String, secretKey: SecretKey): Pair<String, String> {
        val cipher = Cipher.getInstance(cipherTransformation)
        val iv = ByteArray(gcmIvLengthBytes)
        SecureRandom().nextBytes(iv)
        val spec = GCMParameterSpec(gcmTagLengthBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val cipherBase64 = Base64Compat.encode(cipherBytes)
        val ivBase64 = Base64Compat.encode(iv)
        return Pair(cipherBase64, ivBase64)
    }

    fun decryptPayload(cipherBase64: String, ivBase64: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(cipherTransformation)
        val iv = Base64Compat.decode(ivBase64)
        val cipherBytes = Base64Compat.decode(cipherBase64)
        val spec = GCMParameterSpec(gcmTagLengthBits, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val plainBytes = cipher.doFinal(cipherBytes)
        return String(plainBytes, Charsets.UTF_8)
    }

    fun computeChecksum(content: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(content.toByteArray(Charsets.UTF_8))
        return bytes.take(8).joinToString("") { "%02x".format(it) }
    }

    fun getSamplePeers(): List<PeerCollaborator> {
        return listOf(
            PeerCollaborator(
                id = "peer-01",
                name = "Alex (MacBook Pro)",
                clientType = "VS Code Remote SSH",
                isOnline = true,
                latencyMs = 18,
                currentFile = "server.js",
                cursorPosition = "Ln 14, Col 5"
            ),
            PeerCollaborator(
                id = "peer-02",
                name = "Sarah (iPad Pro)",
                clientType = "Safari Web IDE",
                isOnline = true,
                latencyMs = 26,
                currentFile = "api/analytics.py",
                cursorPosition = "Ln 22, Col 1"
            ),
            PeerCollaborator(
                id = "peer-03",
                name = "Continuous Sync Bot",
                clientType = "Cloud Mesh Relay",
                isOnline = true,
                latencyMs = 45,
                currentFile = "README.md",
                cursorPosition = "Ln 1, Col 1"
            )
        )
    }
}
