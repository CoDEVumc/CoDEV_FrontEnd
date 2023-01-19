package com.example.codev

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

/**
 * 안드로이드 앱 별 KeyStore 의 Key를 통해
 * 데이터를 암호화 - 복호화
 * base64 + 앱 별 고유키를 통한 암호화, 복호화
 * */
object AndroidKeyStoreUtil {

    // 모든 입력값은 2048비트(256바이트) 보다 클 수 없다.
    private const val KEY_LENGTH_BIT = 2048

    // KeyStore의 유효기간
    private const val VALIDITY_YEARS = 25

    private const val KEY_PROVIDER_NAME = "AndroidKeyStore"
    private const val KEYSOTRE_INSTANCE_TYPE = "AndroidKeyStore"

    private const val CIPHER_ALGORITHM =
        "${KeyProperties.KEY_ALGORITHM_RSA}/" +
                "${KeyProperties.BLOCK_MODE_ECB}/" +
                KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1

    private lateinit var keyEntry: KeyStore.Entry

    // Private only backing field
    @Suppress("ObjectPropertyName")
    private var _isSupported = false

    // 이미 init 진행했는지 체크.
    private val isSupported: Boolean
        get() = _isSupported

    private lateinit var appContext: Context

    internal fun init(applicationContext: Context) {
        if (isSupported) {
            Log.d("temp", "Already init twice!")
            return
        }

        this.appContext = applicationContext
        val alias = "${appContext.packageName}.rsakeypairs"
        val keyStore = KeyStore.getInstance(KEYSOTRE_INSTANCE_TYPE).apply {
            load(null)
        }

        var result: Boolean = if (keyStore.containsAlias(alias)) {
            true
        } else {
            // 안드로이드 M(API 23) 이상인 경우 RSA 알고리즘 사용 가능
            if (initAndroidM(alias)) {
                true
            } else {
                initAndroidL(alias)
            }
        }

        this.keyEntry = keyStore.getEntry(alias, null)
        _isSupported = result
    }

    /**
     * Android Version M (API 23) 이상 사용하는 KeyStore 설정 함수
     * */
    private fun initAndroidM(alias: String): Boolean {
        try {
            with(KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER_NAME), {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val spec = KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(KEY_LENGTH_BIT, F4))
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setDigests(KeyProperties.DIGEST_SHA512,
                            KeyProperties.DIGEST_SHA384,
                            KeyProperties.DIGEST_SHA256)
                        /*
                         * Setting true only permit the private key to be used if the user authenticated
                         * within the last five minutes.
                         */
                        .setUserAuthenticationRequired(false)
                        .build()

                    initialize(spec)
                    generateKeyPair()
                } else {
                    return false
                }
            })

            return true
        } catch (e: GeneralSecurityException) {
            // 아주 가끔 몇몇 기기들은 오류가 발생하기도 한다.
            return false
        }
    }

    /**
     * Android Version L, LOLLIPOP (API 21, 22) 에서 사용하는 KeyStore 설정 함수
     */
    private fun initAndroidL(alias: String): Boolean {
        try {
            with(KeyPairGenerator.getInstance("RSA", KEY_PROVIDER_NAME), {
                val start = Calendar.getInstance(Locale.ENGLISH)
                val end = Calendar.getInstance(Locale.ENGLISH).apply { add(Calendar.YEAR, VALIDITY_YEARS) }

                // API 23 이하인 디바이스를 위해 Deprecated code 사용
                val spec = KeyPairGeneratorSpec.Builder(appContext)
                    .setKeySize(KEY_LENGTH_BIT)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=hybridApp, OU=Android debug"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()

                initialize(spec)
                generateKeyPair()
            })

            return true
        } catch (e: GeneralSecurityException) {

            return false
        }
    }

    /**
     * Beware that input must be shorter than 256 bytes. The length limit of plainText could be dramatically
     * shorter than 256 letters in certain character encoding, such as UTF-8.
     */
    fun encrypt(plainText: String): String {
        if (!_isSupported) {
            return plainText
        }

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey)
        }
        val bytes = plainText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = cipher.doFinal(bytes)
        val base64EncryptedBytes = Base64.encode(encryptedBytes, Base64.DEFAULT)

        return String(base64EncryptedBytes)
    }

    fun decrypt(base64EncryptedCipherText: String): String {
        if (!_isSupported) {
            return base64EncryptedCipherText
        }

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).privateKey)
        }
        val base64EncryptedBytes = base64EncryptedCipherText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = Base64.decode(base64EncryptedBytes, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }
}