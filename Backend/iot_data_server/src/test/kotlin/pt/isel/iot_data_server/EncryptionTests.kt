package pt.isel.iot_data_server

import AESUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow


class EncryptionTests {

    @Test
    fun `encrypt and decrypt`() {
        val plaintext = "Hello World"
        val algorithm = "AES/CBC/PKCS5Padding"
        val intArray = intArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F)
        val secretKey = assertDoesNotThrow { AESUtil.getSecretKeyFromIntArray(intArray) }
        val iv = AESUtil.generateIv()

        val encrypted = assertDoesNotThrow { AESUtil.encrypt(algorithm, plaintext, secretKey, iv) }
        val decrypted = assertDoesNotThrow { AESUtil.decrypt(algorithm, encrypted, secretKey, iv) }

        assert(plaintext == decrypted)
    }

    @Test
    fun `encrypt and decrypt with separator`() {
        val algorithm = "AES/CBC/PKCS5Padding"
        val intArray = intArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F)
        val secretKey = assertDoesNotThrow { AESUtil.getSecretKeyFromIntArray(intArray) }

        val plaintext1 = "Hello World"
        val encrypted1 = "j5/d8O5D9lEEN9YQjWby3Q== yJ3vYX2doXDqpu5x/2mAow=="
        val plaintext2 = "migasrocha1@hotmail.com"
        val encrypted2 = "Kf2JAoYMvGoW8jEBxDVFWWAzo7NI0sZJzhQHTESL2sg= oFocAt0Nl0+QuIV/GmDSnw=="

        val decrypted = assertDoesNotThrow { AESUtil.decrypt(algorithm, encrypted1, secretKey, ' ') }
        val decrypted2 = assertDoesNotThrow { AESUtil.decrypt(algorithm, encrypted2, secretKey, ' ') }

        assert(plaintext1 == decrypted)
        assert(plaintext2 == decrypted2)
    }
}