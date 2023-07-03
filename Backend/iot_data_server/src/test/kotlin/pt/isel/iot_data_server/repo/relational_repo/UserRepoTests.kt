package pt.isel.iot_data_server.repo.relational_repo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndDontRollback
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*


class UserRepoTests {
    @BeforeEach
    fun cleanup() {
        testWithTransactionManagerAndDontRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo
                usersRepo.deleteAllPasswords()
                usersRepo.deleteAllTokens()
                usersRepo.deleteAllUsers()
            }
        }
    }

    @Test
    fun `Create user with role USER and get`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val CLIENTInfo = UserInfo(generateRandomEmail(), Role.CLIENT)

                val user = User(userId, CLIENTInfo)
                usersRepo.createUser(user)

                assertEquals(user, usersRepo.getUserByIdOrNull(userId))
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
                val retrievedUser = usersRepo.getUserByIdOrNull(userId)

                assertEquals(user, retrievedUser)
            }
        }
    }

    @Test
    fun `Create multiple users`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val user1 = createUser(usersRepo, "some_email_1@gmail.com")
                val user2 = createUser(usersRepo, "some_email_2@gmail.com")

                val users2 = usersRepo.getAllUsers()

                assertTrue(users2.contains(user1))
                assertTrue(users2.contains(user2))
            }
        }
    }

    @Test
    fun `Create token for user`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val user = createUser(usersRepo,"some_email_1@gmail.com")

                assertNotNull(usersRepo.getUserByIdOrNull(user.id))

                val token = UUID.randomUUID().toString()
                usersRepo.createToken(user.id, token)
                val userByToken = usersRepo.getUserByToken(token)

                assertEquals(user, userByToken)
                assertEquals(token, usersRepo.getTokenFromUser(user.id))
            }
        }
    }

    @Test
    fun `Create token for invalid user`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo
                assertThrows<Exception> { usersRepo.createToken(
                    "invalid_user_id",
                    "some_token"
                )}
            }
        }
    }

    @Test
    fun `Email already exists`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val email = "some_email_1@gmail.com"
                val user = createUser(usersRepo, email)
                assertNotNull(usersRepo.getUserByIdOrNull(user.id))

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

                val email = "some_email_1@gmail.com"
                val user = createUser(usersRepo, email)
                assertNotNull(usersRepo.getUserByIdOrNull(user.id))

                assertEquals(user, usersRepo.getUserByEmailOrNull(email))
                assertEquals(null, usersRepo.getUserByEmailOrNull("invalid_email"))
            }
        }
    }

    @Test
    fun `Delete all users`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val email1 = "some_email_1@gmail.com"
                val user1 = createUser(usersRepo, email1)
                assertNotNull(usersRepo.getUserByIdOrNull(user1.id))

                val email2 = "some_email_2@gmail.com"
                val user2 = createUser(usersRepo, email2)
                assertNotNull(usersRepo.getUserByIdOrNull(user2.id))

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

                val email1 = "some_email_1@gmail.com"
                val user = createUser(usersRepo, email1)
                assertNotNull(usersRepo.getUserByIdOrNull(user.id))
                usersRepo.deleteUser(user.id)

                assertNull(usersRepo.getUserByIdOrNull(user.id))
            }
        }
    }

    @Test
    fun `Delete all tokens`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val email1 = "some_email_1@gmail.com"
                val user1 = createUser(usersRepo, email1)
                val userId1 = user1.id

                val email2 = "some_email_2@gmail.com"
                val user2 = createUser(usersRepo, email2)
                val userId2 = user2.id

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

    @Test
    fun `Create verification code`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                usersRepo.addVerificationCode("some_email_1@gmail.com", "some_code")
                assertEquals("some_code", usersRepo.getVerificationCode("some_email_1@gmail.com"))
            }
        }
    }

    @Test
    fun `Create password for valid user`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val email1 = "some_email_1@gmail.com"
                val user1 = createUser(usersRepo, email1)
                val userId1 = user1.id

                val password = "some_password"
                val salt = "some_salt"
                usersRepo.storePasswordAndSalt(userId1, password, salt)
                val (retrievedPassword, retrievedSalt) = usersRepo.getPasswordAndSalt(userId1)

                assertEquals(password, retrievedPassword)
                assertEquals(salt, retrievedSalt)
            }
        }
    }

    @Test
    fun `Create password for invalid user`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo
                assertThrows<Exception> {  usersRepo.storePasswordAndSalt(
                    "some_invalid_user_id",
                    "some_password",
                    "some_salt"
                )}
            }
        }
    }

    @Test
    fun `Delete all passwords`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val email1 = "some_email_1@gmail.com"
                val user1 = createUser(usersRepo, email1)
                val userId1 = user1.id

                val email2 = "some_email_2@gmail.com"
                val user2 = createUser(usersRepo, email2)
                val userId2 = user2.id

                usersRepo.storePasswordAndSalt(userId1, "some_password_1", "some_salt_1")
                usersRepo.storePasswordAndSalt(userId2, "some_password_2", "some_salt_2")

                val (password1, salt1) = usersRepo.getPasswordAndSalt(userId1)
                val (password2, salt2) = usersRepo.getPasswordAndSalt(userId2)

                assertEquals("some_password_1", password1)
                assertEquals("some_salt_1", salt1)
                assertEquals("some_password_2", password2)
                assertEquals("some_salt_2", salt2)

                usersRepo.deleteAllPasswords()

                assertThrows<Exception> { usersRepo.getPasswordAndSalt(userId1) }
                assertThrows<Exception> { usersRepo.getPasswordAndSalt(userId2) }
            }
        }
    }
}