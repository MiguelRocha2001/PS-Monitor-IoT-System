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
    fun createUser(username: String, password: String) {
        transactionManager.run {
            getAllUsers().forEach { user ->
                if (user.username == username) {
                    throw Exception("User already exists")
                }
            }
            it.repository.createUser(username, password)
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

    fun createTokenAndGet(username: String, password: String): Either<String, String> {
        return transactionManager.run {
            val token = UUID.randomUUID().toString()
            it.repository.addToken(userId, token)
            return@run token
        }
    }
}