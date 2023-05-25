package pt.isel.iot_data_server.repo.relational_repo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.generateRandomPassword
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*


class UserRepoTests {
    private val role = Role.USER

    @Test
    fun `Create user with role USER and get`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo(generateRandomEmail(), Role.USER)

                val user = User(userId, userInfo)
                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])
            }
        }
    }

    @Test
    fun `Create user with role ADMIN and get`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo(generateRandomEmail(), Role.ADMIN)

                val user = User(userId, userInfo)
                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])
            }
        }
    }

    @Test
    fun `Create multiple users`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo(generateRandomEmail(), Role.USER)

                val user = User(userId, userInfo)
                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])

                val userId2 = UUID.randomUUID().toString()
                val userInfo2 = UserInfo(generateRandomEmail(), Role.USER)

                val user2 = User(userId2, userInfo2)
                usersRepo.createUser(user2)
                val users2 = usersRepo.getAllUsers()

                assertEquals(2, users2.size)
                assertEquals(user, users2[0])
                assertEquals(user2, users2[1])
            }
        }
    }

    @Test
    fun `Create token for user`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo(generateRandomEmail(), Role.USER)

                val user = User(userId, userInfo)
                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])

                val token = UUID.randomUUID().toString()
                usersRepo.createToken(userId, token)
                val userByToken = usersRepo.getUserByToken(userId)

                assertEquals(user, userByToken)
                assertEquals(token, usersRepo.getTokenFromUser(userId))
            }
        }
    }

    @Test
    fun `Email already exists`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val email = generateRandomEmail()

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo(email, Role.USER)
                val user = User(userId, userInfo)

                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])

                assertEquals(true, usersRepo.existsEmail(email))

                val anotherEmail = generateRandomEmail()
                assertNotEquals(email, anotherEmail)

                assertEquals(false, usersRepo.existsEmail(anotherEmail))
            }
        }
    }

    @Test
    fun `Get user by email`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val email = "some_email_12345@gmail.com"

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo(email, Role.USER)
                val user = User(userId, userInfo)

                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])

                assertEquals(user, usersRepo.getUserByEmailOrNull(email))

                assertEquals(null, usersRepo.getUserByEmailOrNull("some_email_54321@gmail.com"))
            }
        }
    }

    @Test
    fun `Delete all users`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo("some_email_1@gmail.com", Role.USER)

                val user = User(userId, userInfo)
                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])

                val userId2 = UUID.randomUUID().toString()
                val userInfo2 = UserInfo("some_email_2@gmail.com", Role.USER)

                val user2 = User(userId2, userInfo2)
                usersRepo.createUser(user2)
                val users2 = usersRepo.getAllUsers()

                assertEquals(2, users2.size)
                assertEquals(user, users2[0])
                assertEquals(user2, users2[1])

                usersRepo.deleteAllUsers()
                val users3 = usersRepo.getAllUsers()

                assertEquals(0, users3.size)
            }
        }
    }

    @Test
    fun `Delete user by id`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo("some_email_1@gmail.com", Role.USER)
                val user = User(userId, userInfo)

                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])

                usersRepo.deleteUser(userId)

                val users2 = usersRepo.getAllUsers()
                assertEquals(0, users2.size)
            }
        }
    }

    @Test
    fun `Delete all tokens`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId1 = UUID.randomUUID().toString()
                val userInfo2 = UserInfo("some_email_1@gmail.com", Role.USER)
                val user1 = User(userId1, userInfo2)

                val userId2 = UUID.randomUUID().toString()
                val userInfo3 = UserInfo("some_email_2@gmail.com", Role.USER)
                val user2 = User(userId2, userInfo3)

                usersRepo.createUser(user1)
                usersRepo.createUser(user2)

                val token1 = UUID.randomUUID().toString()
                val token2 = UUID.randomUUID().toString()

                usersRepo.createToken(userId1, token1)
                usersRepo.createToken(userId2, token2)

                assertEquals(token1, usersRepo.getTokenFromUser(userId1))
                assertEquals(token2, usersRepo.getTokenFromUser(userId2))

                usersRepo.deleteAllTokens()

                assertEquals(null, usersRepo.getTokenFromUser(userId1))
                assertEquals(null, usersRepo.getTokenFromUser(userId2))
            }
        }
    }
}