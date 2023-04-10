package pt.isel.iot_data_server.crypto

import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec



data class AESCipherSuit(
    val algorithm: String,
    val separator: Char,
    val secretKey: SecretKey,
)


fun getAESCipherSuit(): AESCipherSuit {
    val symmetricKey = intArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F)
    val secretKey = AES.getSecretKeyFromIntArray(symmetricKey)
    val algorithm = "AES/CBC/PKCS5Padding"
    val separator = ' '

    return AESCipherSuit(algorithm, separator, secretKey)
}

data class AESCipher(
    val algorithm: String,
    val iv: IvParameterSpec,
){
  //  private val iv: IvParameterSpec = AES.generateIv()
    private val secretKey: SecretKey =
        System.getenv("AES_KEY")?.let { AES.getSecretKeyFromIntArray(it.split(",").map { it.toInt() }.toIntArray()) }
           ?: throw Exception("AES_KEY not found in environment variables")

    fun decrypt(token: String): String = AES.decrypt(algorithm, token, secretKey, iv)


    fun encrypt(token: String): String = AES.encrypt(algorithm, token, secretKey, iv)

}
