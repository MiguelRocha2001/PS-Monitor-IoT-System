package pt.isel.iot_data_server.crypto

import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class AES(
    private val secretKey: SecretKey,
    private val algorithm: String,
    private val separator: Char
) {

    companion object {
        @Throws(NoSuchAlgorithmException::class)
        fun generateKey(n: Int): SecretKey {
            val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(n)
            return keyGenerator.generateKey()
        }

        @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
        fun getKeyFromPassword(password: String, salt: String): SecretKey {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 65536, 256)
            return SecretKeySpec(
                factory.generateSecret(spec)
                    .encoded, "AES"
            )
        }

        @Throws(IllegalArgumentException::class)
        fun getSecretKeyFromIntArray(key: IntArray): SecretKey {
            val keyBytes = ByteArray(key.size)
            for (i in key.indices) {
                keyBytes[i] = key[i].toByte()
            }
            return SecretKeySpec(keyBytes, "AES")
        }

        fun generateIv(): IvParameterSpec {
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            return IvParameterSpec(iv)
        }

        @Throws(
            NoSuchPaddingException::class,
            NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class,
            InvalidKeyException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class
        )
        fun encrypt(
            algorithm: String, input: String, key: SecretKey, iv: IvParameterSpec
        ): String {
            val cipher: Cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)
            val cipherText: ByteArray = cipher.doFinal(input.toByteArray())
            return Base64.getEncoder()
                .encodeToString(cipherText)
        }

        @Throws(
            NoSuchPaddingException::class,
            NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class,
            InvalidKeyException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class
        )
        fun decrypt(
            algorithm: String, cipherText: String, key: SecretKey, iv: IvParameterSpec
        ): String {
            val cipher: Cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
            val plainText: ByteArray = cipher.doFinal(
                Base64.getDecoder()
                    .decode(cipherText)
            )
            return String(plainText)
        }

        /**
         * @param separator the separator between the iv and the cipher text
         */
        fun decrypt(
            algorithm: String, cipherAndIvText: String, key: SecretKey, separator: Char
        ): String {
            val ivAndCipherText = cipherAndIvText.split(separator)
            val iv = IvParameterSpec(Base64.getDecoder().decode(ivAndCipherText[1]))
            val cipherText = ivAndCipherText[0]
            return decrypt(algorithm, cipherText, key, iv)
        }
    }

    fun decrypt(cipherAndIvText: String): String {
        return decrypt(algorithm, cipherAndIvText, secretKey, separator)
    }
}