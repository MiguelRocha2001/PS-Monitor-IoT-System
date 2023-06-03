package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.SaltPasswordOperations
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

@SpringBootTest
class UserServiceTests {
    private val role = Role.USER

    @Test
    fun `create user with password`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val saltPasswordOperations = SaltPasswordOperations(transactionManager)
            val emailService = EmailManager()
            val service = UserService(transactionManager, saltPasswordOperations, emailService)
            val password = "LKMSDOVCJ09Jouin09JN@"
            val email = "testSubject@email.com"

            val res = service.createUser(email, password, role)
			assertTrue(res is Either.Right)
			res as Either.Right

			val user = service.getUserByIdOrNull(res.value.first)
			assertNotNull(user)
			assertEquals(user?.userInfo?.email, email)
			assertEquals(user?.userInfo?.role, role)
        }
    }

	@Test
	fun `create user without password`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val emailService = EmailManager()
			val service = UserService(transactionManager, saltPasswordOperations, emailService)
			val password = null
			val email = "testSubject@email.com"

			val res = service.createUser(email, password, role)
			assertTrue(res is Either.Right)
			res as Either.Right

			val user = service.getUserByIdOrNull(res.value.first)
			assertNotNull(user)
			assertEquals(user?.userInfo?.email, email)
			assertEquals(user?.userInfo?.role, role)
		}
	}

    @Test
    fun `create user with invalid email`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val saltPasswordOperations = SaltPasswordOperations(transactionManager)
            val service = UserService(transactionManager, saltPasswordOperations, EmailManager())
            val password = "LKMSDOVCJ09Jouin09JN@"
            val email = "testSubjectemail.com"
			val res = service.createUser(email, password, role)
			//verify that the result is Either.Left
			assertTrue(res is Either.Left)
        }
    }

	@Test
	fun `create multiple valid users`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())
			val password1 = "LKMSDOVCJ09Jouin09JN@1"
			val email1 = "testSubject1@email.com"

			val password2 = "LKMSDOVCJ09Jouin09JN@"
			val email2 = "testSubject2@email.com"

			val password3 = "LKMSDOVCJ09Jouin09JN@"
			val email3 = "testSubject3@email.com"

			val res1 = service.createUser(email1, password1, role)
			val res2 = service.createUser(email2, password2, role)
			val res3 = service.createUser(email3, password3, role)

			assertTrue(res1 is Either.Right)
			assertTrue(res2 is Either.Right)
			assertTrue(res3 is Either.Right)

			val users = service.getAllUsers()

			assertTrue(users.any { it.userInfo.email == email1 })
			assertTrue(users.any { it.userInfo.email == email2 })
			assertTrue(users.any { it.userInfo.email == email3 })
		}
	}

	@Test
	fun `create multiple users with same email`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())
			val password1 = "LKMSDOVCJ09Jouin09JN@1"
			val email1 = "sameemail@email.com"


			val password2 = "LKMSDOVCJ09Jouin09JN@"
			val email2 = "sameemail@email.com"

			service.createUser(email1, password1, role)
			val result = service.createUser(email2, password2, role)
			assertTrue(result is Either.Left)
		}
	}

	@Test
	fun `Create token for user`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())

			val email = "testSubject@email.com"
			assertTrue(service.createUser(email, null, role) is Either.Right)
			val res = service.createAndGetToken(email, null)
			assertTrue(res is Either.Right)
			val token = (res as Either.Right).value

			val user = service.getUserByToken(token)
			assertNotNull(user)
			assertEquals(user?.userInfo?.email, email)
			assertEquals(user?.userInfo?.role, role)
		}
	}

	@Test
	fun `Cannot create token for invalid user`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())

			val email = "invalidEmail"
			val res = service.createAndGetToken(email, null)
			assertTrue(res is Either.Left)
		}
	}

	@Test
	fun `Create token with invalid password`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())

			val email = "testSubject@email.com"
			val password = "LKMSDOVCJ09Jouin09JN@1"
			assertTrue(service.createUser(email, password, role) is Either.Right)
			val res = service.createAndGetToken(email, "invalidPassword")
			assertTrue(res is Either.Left)
		}
	}

	@Test
	fun `Delete User by id`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())

			val email = "testSubject@email.com"
			service.createUser(email, null, role)

			val user = service.getUserByEmail(email)
			assertNotNull(user)

			service.deleteUser(user!!.id)

			assertNull(service.getUserByEmail(email))
		}
	}

	@Test
	fun `Delete All Users`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())

			service.deleteAllUsers()

			service.createUser("testSubject1@email.com", null, role)
			service.createUser("testSubject2@email.com", null, role)
			service.createUser("testSubject3@email.com", null, role)

			assertEquals(3, service.getAllUsers().size)

			service.deleteAllUsers()

			assertEquals(0, service.getAllUsers().size)
		}
	}

	@Test
	fun `Is email already registered`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())

			val email = "testSubject1@email.com"
			assertTrue(service.createUser(email, null, role) is Either.Right)

			assertTrue(service.isEmailAlreadyRegistered(email))
			assertFalse(service.isEmailAlreadyRegistered("testSubject2@email.com"))
		}
	}
}
