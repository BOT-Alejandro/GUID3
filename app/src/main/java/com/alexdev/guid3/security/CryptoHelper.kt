package com.alexdev.guid3.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object CryptoHelper {
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val GCM_TAG_BITS = 128
    private const val KEY_BITS = 256
    private const val ITERATIONS = 120_000

    // Pepper y salt globales (mejor mover a backend o NDK si se busca más seguridad)
    private const val GLOBAL_SALT_B64 = "QmFzZVNlY3VyZVNhbHQtR1VJRDM=" // BaseSecureSalt-GUID3
    private const val PEPPER = "GUID3_APP_STATIC_PEPPER_v1"

    private fun deriveKey(uid: String): SecretKey {
        val saltBytes = Base64.decode(GLOBAL_SALT_B64, Base64.NO_WRAP)
        val spec = PBEKeySpec((uid + PEPPER).toCharArray(), saltBytes, ITERATIONS, KEY_BITS)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = skf.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    data class CipherResult(val cipherTextB64: String, val ivB64: String)

    fun encrypt(plainText: String, uid: String?): CipherResult {
        if (uid.isNullOrBlank()) {
            return CipherResult(plainText, "")
        }
        val key = deriveKey(uid)
        val cipher = Cipher.getInstance(AES_MODE)
        val iv = Random.Default.nextBytes(12)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        val ct = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return CipherResult(
            Base64.encodeToString(ct, Base64.NO_WRAP),
            Base64.encodeToString(iv, Base64.NO_WRAP)
        )
    }

    fun decrypt(cipherTextB64: String, ivB64: String, uid: String?): String? {
        return try {
            if (uid.isNullOrBlank()) return null
            val key = deriveKey(uid)
            val cipher = Cipher.getInstance(AES_MODE)
            val iv = Base64.decode(ivB64, Base64.NO_WRAP)
            val spec = GCMParameterSpec(GCM_TAG_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            val cipherBytes = Base64.decode(cipherTextB64, Base64.NO_WRAP)
            val plain = cipher.doFinal(cipherBytes)
            String(plain, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
}
