package pt.isel.iot_data_server.service.user

import org.springframework.stereotype.Component
import pt.isel.iot_data_server.domain.PasswordHash
import pt.isel.iot_data_server.repository.TransactionManager
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

@Component
class SaltPasswordOperations(
    private val transactionManager: TransactionManager
) {
    fun hashPassAndPersist(password: String, userId: String) {
        val passwordHash = hashPassword(password)
        val salt = Base64.getEncoder().encodeToString(passwordHash.salt) // FIXME: Should be some form of byte array in the db,i added string for now because i dont know how to store byte array in db,tried with BYTEA but it didnt work
        transactionManager.run {
            return@run it.userRepo.storePasswordAndSalt(userId, passwordHash.hashedPassword, salt)
        }
    }

    private fun hashPassword(password: String, receivedSalt: ByteArray = ByteArray(0)): PasswordHash {
        val salt = receivedSalt.takeIf { it.isNotEmpty() } ?: generateSalt()
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        val hashedPasswordString = Base64.getEncoder().encodeToString(hashedPassword)
        return PasswordHash(salt, hashedPasswordString)
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(salt)
        return salt
    }

    //Used in login to verify if the password is correct
    //username is used to get the stored pass from the database
    //password is the password received from the user (CLEAR TEXT)
    fun verifyPassword(email: String, password: String): Boolean = transactionManager.run {
        val user = it.userRepo.getUserByEmailOrNull(email) ?: return@run false
        val (storedHashPassword, storedSalt) = it.userRepo.getPasswordAndSalt(user.id)

        val decodedSalt = Base64.getDecoder().decode(storedSalt)

        val receivedHashPassword = hashPassword(password, decodedSalt).hashedPassword
        return@run storedHashPassword == receivedHashPassword
    }
}