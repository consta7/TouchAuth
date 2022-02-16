package com.example.touchauth.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.touchauth.common.EncryptedMessage
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object Cryptography {

    private const val androidKeyStore = "AndroidKeyStore"
    private const val keySize = 128
    private const val encryptionBlockMode = KeyProperties.BLOCK_MODE_GCM
    private const val encryptionPadding = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val encryptionAlgorithm = KeyProperties.KEY_ALGORITHM_AES

    private fun getOrCreateSecretKey(keyName : String, isUserAuthenticationRequire: Boolean) : SecretKey {
        val keyStore = KeyStore.getInstance(androidKeyStore)
        keyStore.load(null)
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(encryptionBlockMode)
            setEncryptionPaddings(encryptionPadding)
            setKeySize(keySize)
            setUserAuthenticationRequired(isUserAuthenticationRequire)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            encryptionAlgorithm,
            androidKeyStore
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }

    private fun getCipher() : Cipher {
        val transformation = "$encryptionAlgorithm/$encryptionBlockMode/$encryptionPadding"
        return Cipher.getInstance(transformation)
    }

    fun getInitializedCipherForEncryption(
        keyName: String,
        isUserAuthenticationRequire: Boolean
    ) : Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName, isUserAuthenticationRequire)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }

    fun getInitializedCipherForDecryption(
        keyName: String,
        initializationVector : ByteArray? = null,
        isUserAuthenticationRequire: Boolean
    ) : Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName, isUserAuthenticationRequire)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(keySize, initializationVector)
        )
        return cipher
    }

    fun decryptData(ciphertext : ByteArray, cipher : Cipher?) : String {
        val plaintext = cipher?.doFinal(ciphertext)
        return String(plaintext!!, Charset.forName("UTF-8"))
    }
    
    fun encryptData(plainText : String, cipher : Cipher) : EncryptedMessage {
        val cipherText = cipher
            .doFinal(plainText.toByteArray(Charset.forName("UTF-8")))
        return EncryptedMessage(cipherText, cipher.iv)
    }
}