package pt.isel.iot_data_server.service.user

import okhttp3.internal.userAgent
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.PasswordHash
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

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

            val passwordHash = hashPassword(userInfo.password)
            it.repository.saveSalt(userId, passwordHash.salt)
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

    /**
     * Creates a token for the user with the given username and password.
     * @throws RuntimeException if the user does not exist.
     */
    fun createAndGetToken(username: String): TokenCreationResult {
        return transactionManager.run {
            val user = it.repository.getUserByUsername(username)
            val token = UUID.randomUUID().toString()

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
    fun verifyPassword(username: String, password: String): Boolean = transactionManager.run {
        val storedSalt = it.repository.getSalt(it.repository.getUserByUsername(username).id)
        val receivedHashPassword = hashPassword(password,storedSalt)
        val storedHashedPassword = it.repository.getUserByUsername(username).userInfo.password
        return@run storedHashedPassword == receivedHashPassword.hashedPassword
       }
    }

