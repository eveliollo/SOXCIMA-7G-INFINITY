package com.example.data

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Log

object MemoriaInfinityHelper {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private val staticIv = "INFINITY-NONCE-1".toByteArray(Charsets.UTF_8) // 16 bytes

    fun sha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "sha256_fallback_error"
        }
    }

    fun encrypt(content: ByteArray, keyHex: String): ByteArray {
        return try {
            val keyBytes = hexToBytes(keyHex)
            val secretKey = SecretKeySpec(keyBytes.copyOf(32), "AES")
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(staticIv))
            cipher.doFinal(content)
        } catch (e: Exception) {
            Log.e("SoxcimaCrypto", "Encryption error", e)
            content
        }
    }

    fun decrypt(encrypted: ByteArray, keyHex: String): ByteArray {
        return try {
            val keyBytes = hexToBytes(keyHex)
            val secretKey = SecretKeySpec(keyBytes.copyOf(32), "AES")
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(staticIv))
            cipher.doFinal(encrypted)
        } catch (e: Exception) {
            Log.e("SoxcimaCrypto", "Decryption error", e)
            encrypted
        }
    }

    fun hexToBytes(hex: String): ByteArray {
        val normalized = hex.trim().replace(" ", "")
        val len = normalized.length
        if (len == 0) return ByteArray(0)
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            val firstDigit = Character.digit(normalized[i], 16)
            val secondDigit = Character.digit(normalized[i + 1], 16)
            data[i / 2] = ((firstDigit shl 4) + secondDigit).toByte()
        }
        return data
    }

    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
