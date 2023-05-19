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
    fun createUser(email: String, password: String, role: Role): UserCreationResult {
        return transactionManager.run {
            // generate random int
            val userId = UUID.randomUUID().toString()

            if (it.userRepo.existsEmail(email))
                return@run Either.Left(CreateUserError.UserAlreadyExists)

            if (it.userRepo.existsEmail(email))
                return@run Either.Left(CreateUserError.EmailAlreadyExists)

            val passwordHash = saltPasswordOperations.saltAndHashPass(password, userId)
            val userInfo = UserInfo(email, passwordHash.hashedPassword, role)
            val newUser = User(userId, userInfo)
            it.userRepo.createUser(newUser)

            val tokenCreationResult = createAndGetToken(userInfo.email, password)
            if (tokenCreationResult is Either.Left)
                throw RuntimeException("Failed to create token for user with email: ${userInfo.email}")

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
     * Creates a token for the user with the given email and password.
     * @throws RuntimeException if the user does not exist.
     */
    fun createAndGetToken(email: String, password: String): TokenCreationResult {
        return transactionManager.run {
            val user = it.userRepo.getUserByEmailOrNull(email)
                ?: return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            if (!saltPasswordOperations.verifyPassword(email, password))
                return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            val token = UUID.randomUUID().toString()
            // val aesCipher = AESCipher("AES/CBC/PKCS5Padding", AES.generateIv())// todo store the iv in the db
            // saveEncryptedToken(aesCipher,token,user.id)
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






