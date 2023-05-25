package pt.isel.iot_data_server.service.user


import org.springframework.stereotype.Service
import pt.isel.iot_data_server.crypto.AESCipher
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.email.EmailManager
import java.util.*

@Service
class UserService(
    private val transactionManager: TransactionManager,
    private val saltPasswordOperations: SaltPasswordOperations,
    private val emailSenderService: EmailManager,
) {
    /**
     * Creates a new user.
     * @param password is optional. If not provided, the user will be created without a password.
     */
    fun createUser(email: String, password: String?, role: Role): UserCreationResult {
        return transactionManager.run {
            // generate random int
            val userId = UUID.randomUUID().toString()

            if(!isEmailValid(email))
                return@run Either.Left(CreateUserError.InvalidEmail)

            if (it.userRepo.existsEmail(email))
                return@run Either.Left(CreateUserError.EmailAlreadyExists)

            val userInfo = UserInfo(email, role)
            val newUser = User(userId, userInfo)
            it.userRepo.createUser(newUser)

            if (password !== null)
                saltPasswordOperations.hashPassAndPersist(password, userId)

            val tokenCreationResult = createAndGetToken(userInfo.email, password)
            if (tokenCreationResult is Either.Left)
                throw RuntimeException("Failed to create token for user with email: ${userInfo.email}")

            tokenCreationResult as Either.Right
            return@run Either.Right(userId to tokenCreationResult.value)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegexPattern = "^(.+)@(\\S+)$"
        return emailRegexPattern.toRegex().matches(email)
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

    fun getUserByEmail(email: String): User? {
        return transactionManager.run {
            return@run it.userRepo.getUserByEmailOrNull(email)
        }
    }

    /**
     * Creates a token for the user with the given email and password.
     * @throws RuntimeException if the user does not exist.
     */
    // TODO: what if the user has a password and the caller passes null as the password?
    fun createAndGetToken(email: String, password: String?): TokenCreationResult {
        return transactionManager.run {
            val user = it.userRepo.getUserByEmailOrNull(email)
                ?: return@run Either.Left(TokenCreationError.UserNotFound)

            // Only check the password if it was provided
            if (password !== null && !saltPasswordOperations.verifyPassword(email, password))
                return@run Either.Left(TokenCreationError.InvalidPassword)

            val token = UUID.randomUUID().toString()
            // val aesCipher = AESCipher("AES/CBC/PKCS5Padding", AES.generateIv())// todo store the iv in the db
            // saveEncryptedToken(aesCipher,token,user.id)

            // TODO: maybe use update token instead of delete and create
            it.userRepo.deleteTokenByUserId(user.id)
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
            it.userRepo.deleteAllPasswords()
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

    fun codeVerification(email: String, code: String): Boolean {
        return transactionManager.run {
            val codeFromDB = it.userRepo.getVerificationCode(email)
            if (codeFromDB == null) {
                return@run false
            }
            return@run codeFromDB == code
        }
    }

    fun generateVerificationCode(email: String): String {
        //generate a 5 character long code

        val code = (10000..99999).random().toString()

        //add an element to a map
        val bodyMessage = mapOf(
            "verification_code" to code
        )
        val subject = emptyMap<String, String>()


        addVerificationCode(email,code)
        emailSenderService.sendEmail(email, subject, bodyMessage, "VerificationCode")
        return code

    }

    fun addVerificationCode(email: String,code:String){
        return transactionManager.run {
            it.userRepo.addVerificationCode(email, code)
        }
    }
}






