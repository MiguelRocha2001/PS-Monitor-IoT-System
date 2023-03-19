package pt.isel.iot_data_server.service.user

import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import java.util.*

@Service
class UserService(
    private val transactionManager: TransactionManager,
) {
    fun createUser(username: String, password: String): UserCreationResult {
        return transactionManager.run {
            // generate random int
            val userId = Random().nextInt()
            val user = User(userId, username, password)

            if (it.repository.exists(user))
                return@run Either.Left(CreateUserError.UserAlreadyExists)

            it.repository.createUser(user)

            val tokenCreationResult = createAndGetToken(username, password)
            if (tokenCreationResult is Either.Left)
                throw RuntimeException("Failed to create token for user $username")

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
    fun createAndGetToken(username: String, password: String): TokenCreationResult {
        return transactionManager.run {
            val user = it.repository.getUserByUsername(username)
            val token = UUID.randomUUID().toString()

            it.repository.addToken(user.id, token)

            return@run Either.Right(token)
        }
    }
}