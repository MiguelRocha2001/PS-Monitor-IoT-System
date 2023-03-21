package pt.isel.iot_data_server.service.user

import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
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

            val newUser = User(userId, userInfo)
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
}