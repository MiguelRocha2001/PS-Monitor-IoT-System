package pt.isel.iot_data_server.service.user


import org.springframework.stereotype.Service
import pt.isel.iot_data_server.crypto.AESCipher
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import java.util.*

@Service
class UserService(
    private val transactionManager: TransactionManager,
    private val saltPasswordOperations: SaltPasswordOperations
) {
    fun createUser(userInfo: UserInfo): UserCreationResult {
        return transactionManager.run {
            // generate random int
            val userId = UUID.randomUUID().toString()

            if (it.userRepo.existsUsername(userInfo.username))
                return@run Either.Left(CreateUserError.UserAlreadyExists)

            if (it.userRepo.existsEmail(userInfo.email))
                return@run Either.Left(CreateUserError.EmailAlreadyExists)

            val passwordHash = saltPasswordOperations.saltAndHashPass(userInfo.password, userId)
            val newUserInfo = UserInfo(userInfo.username, passwordHash.hashedPassword, userInfo.email, userInfo.role)
            val newUser = User(userId, newUserInfo)
            it.userRepo.createUser(newUser)

            val tokenCreationResult = createAndGetToken(userInfo.username)
            if (tokenCreationResult is Either.Left)
                throw RuntimeException("Failed to create token for user ${userInfo.username}")

            tokenCreationResult as Either.Right
            return@run Either.Right(userId to tokenCreationResult.value)
        }
    }

    fun getAllUsers(): List<User> {
        return transactionManager.run {
            return@run it.userRepo.getAllUsers()
        }
    }

    fun getUserByIdOrNull(userId: String): User? {
        return transactionManager.run {
            return@run it.userRepo.getUserByIdOrNull(userId)
        }
    }

    fun getUserByToken(token: String): User? {
        return transactionManager.run {
            return@run it.userRepo.getUserByToken(token)
        }
    }

    fun getUserByEmailAddress(email: String): User? {
        return transactionManager.run {
            return@run it.userRepo.getUserByEmailAddressOrNull(email)
        }
    }

    /**
     * Creates a token for the user with the given username and password.
     * @throws RuntimeException if the user does not exist.
     */
    fun createAndGetToken(username: String): TokenCreationResult {
        return transactionManager.run {
            val user = it.userRepo.getUserByUsernameOrNull(username)
                ?: return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            val token = UUID.randomUUID().toString()
           // val aesCipher = AESCipher("AES/CBC/PKCS5Padding", AES.generateIv())// todo store the iv in the db
          //  saveEncryptedToken(aesCipher,token,user.id)
            it.userRepo.createToken(user.id, token)

            return@run Either.Right(token)
        }
    }

    fun saveEncryptedToken(aesCipher: AESCipher, plainToken: String, userId: String) = transactionManager.run {
        val encryptedToken = aesCipher.encrypt(plainToken)
        return@run it.userRepo.createToken(userId, encryptedToken)
    }

    fun decryptToken(aesCipher: AESCipher, encryptedToken: String): String {
        return aesCipher.decrypt(encryptedToken)
    }

    /**
     * Used only for integration tests.
     * Deletes all users and tokens.
     */
    fun deleteAllUsers() {
        transactionManager.run {
            it.userRepo.deleteAllTokens()
            it.userRepo.deleteAllUsers()
        }
    }

    fun deleteUser(userId: String) {
        transactionManager.run {
            it.userRepo.deleteUser(userId)
        }
    }

    fun isEmailAlreadyRegistered(email: String): Boolean {
        return transactionManager.run {
            it.userRepo.existsEmail(email)
        }
    }
}






