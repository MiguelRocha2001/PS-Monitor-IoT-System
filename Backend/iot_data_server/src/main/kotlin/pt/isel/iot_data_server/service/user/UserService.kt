package pt.isel.iot_data_server.service.user

import org.springframework.stereotype.Service
import pt.isel.iot_data_server.crypto.AES
import pt.isel.iot_data_server.crypto.AESCipher
import pt.isel.iot_data_server.domain.PasswordHash
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.spec.IvParameterSpec

@Service
class UserService(
    private val transactionManager: TransactionManager,
) {
    fun createUser(userInfo: UserInfo): UserCreationResult {
        return transactionManager.run {
            // generate random int
            val userId = Random().nextInt()

            if (it.repository.exists(userInfo.username))
                return@run Either.Left(CreateUserError.UserAlreadyExists)

            val passwordHash = hashPassword(userInfo.password) //TODO; Put on a function
            val salt = Base64.getEncoder().encodeToString(passwordHash.salt) // FIXME: Should be some form of byte array in the db,i added string for now because i dont know how to store byte array in db,tried with BYTEA but it didnt work
            it.repository.saveSalt(userId,salt)
            val newUserInfo = UserInfo(userInfo.username, passwordHash.hashedPassword, userInfo.email, userInfo.mobile)
            val newUser = User(userId, newUserInfo)
            it.repository.createUser(newUser)

            val tokenCreationResult = createAndGetToken(userInfo.username)
            if (tokenCreationResult is Either.Left)
                throw RuntimeException("Failed to create token for user ${userInfo.username}")

            tokenCreationResult as Either.Right
            return@run Either.Right(userId to tokenCreationResult.value)
        }
    }

    fun getAllUsers(): List<User> {
        return transactionManager.run {
            return@run it.repository.getAllUsers()
        }
    }

    fun getUserByToken(token: String): User? {
        return transactionManager.run {
            return@run it.repository.getUserByToken(token)
        }
    }

    fun getUserByEmailAddress(email: String): User? {
        return transactionManager.run {
            return@run it.repository.getUserByEmailAddress(email)
        }
    }

    private fun saveSalt(userId: Int, salt: String) {
        return transactionManager.run {
            return@run it.repository.saveSalt(userId, salt)
        }
    }

    /**
     * Creates a token for the user with the given username and password.
     * @throws RuntimeException if the user does not exist.
     */
    fun createAndGetToken(username: String): TokenCreationResult {
        return transactionManager.run {
            val user = it.repository.getUserByUsername(username)
            val token = UUID.randomUUID().toString()
           // val aesCipher = AESCipher("AES/CBC/PKCS5Padding", AES.generateIv())// todo store the iv in the db
          //  saveEncryptedToken(aesCipher,token,user.id)
            it.repository.addToken(user.id, token)

            return@run Either.Right(token)
        }
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(salt)
        return salt
    }

    private fun hashPassword(password: String, receivedSalt:ByteArray = ByteArray(0)): PasswordHash {
        val salt = receivedSalt.takeIf { it.isNotEmpty() } ?: generateSalt()
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        val hashedPasswordString = Base64.getEncoder().encodeToString(hashedPassword)
        return PasswordHash(salt, hashedPasswordString)
    }

    //Used in login to verify if the password is correct
    //username is used to get the stored pass from the database
    //password is the password received from the user (CLEAR TEXT)
    fun verifyPassword(username: String, password: String): Boolean = transactionManager.run {
        val storedSalt = Base64.getDecoder().decode(it.repository.getSalt(it.repository.getUserByUsername(username).id))
        val receivedHashPassword = hashPassword(password,storedSalt).hashedPassword
        val storedHashedPassword = it.repository.getUserByUsername(username).userInfo.password
        return@run storedHashedPassword == receivedHashPassword
       }

    fun saveEncryptedToken(aesCipher: AESCipher,plainToken :String, userId:Int) = transactionManager.run {
        val encryptedToken = aesCipher.encrypt(plainToken)
        return@run it.repository.addToken(userId, encryptedToken)
    }

    fun decryptToken(aesCipher: AESCipher,encryptedToken: String): String {
        return aesCipher.decrypt(encryptedToken)
    }


}






