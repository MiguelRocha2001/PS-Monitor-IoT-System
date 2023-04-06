import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
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


class AESUtil {
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
    }
}