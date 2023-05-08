package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.SaltPasswordOperations
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

@SpringBootTest
class UserServiceTests {
	private val role = Role.USER
	@Test
	fun `create user`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager, saltPasswordOperations)
			val username = "userGood"
			val password = "LKMSDOVCJ09Jouin09JN@"
			val email = "testSubject@email.com"
			val newUser = UserInfo(username, password, email, role)
			service.createUser(newUser)
			val users = service.getAllUsers()
			//assertValues of every property of student with the expected values
			val retrievedUser = users.first { it.userInfo.username == username}
			assertTrue("User was created", retrievedUser.userInfo.username == username)
			assertTrue("User was created", retrievedUser.userInfo.email == email)
		}
	}

	@Test
	fun `create user with invalid short name`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations)
			val username = "ustg"
			val password = "LKMSDOVCJ09Jouin09JN@"
			val email = "testSubject@email.com"
			try {
				val newInvalidUser = UserInfo(username, password, email, role)
				assert(false) //should not reach this line
			}catch (e: IllegalArgumentException){
				assertTrue("User was not created", e.message == "Username must be at least 5 characters long")
				assert(true)
			}
		}
	}

	@Test
	fun `create user with invalid email`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations)
			val username = "userGood"
			val password = "LKMSDOVCJ09Jouin09JN@"
			val email = "testSubjectemail.com"
			try {
				val newInvalidUser = UserInfo(username, password, email, role)
				assert(false) //should not reach this line
			}catch (e: IllegalArgumentException){
				assertTrue("User was not created", e.message == "Invalid email address")
				assert(true)
			}
		}
	}

	@Test
	fun `create multiple users`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations)
			val username1 = "userGood1"
			val password1 = "LKMSDOVCJ09Jouin09JN@1"
			val email1 = "testSubject1@email.com"
			val newUser1 = UserInfo(username1, password1, email1, role)

			val username2 = "userGood2"
			val password2 = "LKMSDOVCJ09Jouin09JN@"
			val email2 = "testSubject2@email.com"
			val newUser2 = UserInfo(username2, password2, email2, role)

			val username3 = "userGood3"
			val password3 = "LKMSDOVCJ09Jouin09JN@"
			val email3 = "testSubject3@email.com"
			val newUser3 = UserInfo(username3, password3, email3, role)

			val res1 = service.createUser(newUser1)
			val res2 = service.createUser(newUser2)
			val res3 = service.createUser(newUser3)
			assertTrue("User 1 was not created", res1 is Either.Right)
			assertTrue("User 2 was not created", res2 is Either.Right)
			assertTrue("User 3 was not created", res3 is Either.Right)

			val users = service.getAllUsers()

			assertTrue("User was not created", users.any { it.userInfo.email == email1 })
			assertTrue("User was not created", users.any { it.userInfo.email == email2 })
			assertTrue("User was not created", users.any { it.userInfo.email == email3 })
		}
	}

	@Test
	fun `create multiple users with same email`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val saltPasswordOperations = SaltPasswordOperations(transactionManager)
			val service = UserService(transactionManager,saltPasswordOperations)
			val username1 = "userGood1"
			val password1 = "LKMSDOVCJ09Jouin09JN@1"
			val email1 = "sameemail@email.com"
			val newUser1 = UserInfo(username1, password1, email1, role)


			val username2 = "userGood2"
			val password2 = "LKMSDOVCJ09Jouin09JN@"
			val email2 = "sameemail@email.com"
			val newUser2 = UserInfo(username2, password2, email2, role)

			service.createUser(newUser1)
			val result = service.createUser(newUser2)
			assertTrue("User was not created", result is Either.Left)
		}
	}

}
