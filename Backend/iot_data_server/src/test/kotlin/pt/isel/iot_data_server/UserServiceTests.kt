package pt.isel.iot_data_server

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

@SpringBootTest
class UserServiceTests {
	@Test
	fun `create user`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username = "user"
			val password = "password"

			service.createUser(username, password)

			val users = service.getAllUsers()
			assertTrue("User was not created", users.any { it.username == username })
		}
	}

	@Test
	fun `create multiple users`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username1 = "user1"
			val password1 = "password1"
			val username2 = "user2"
			val password2 = "password2"
			val username3 = "user3"
			val password3 = "password3"

			service.createUser(username1, password1)
			service.createUser(username2, password2)
			service.createUser(username3, password3)

			val users = service.getAllUsers()
			assertTrue("User was not created", users.any { it.username == username1 })
			assertTrue("User was not created", users.any { it.username == username2 })
			assertTrue("User was not created", users.any { it.username == username3 })
		}
	}

	@Test
	fun `create existing user`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username = "user"
			val password = "password"

			service.createUser(username, password)

			val users = service.getAllUsers()
			assertTrue("User was not created", users.any { it.username == username })

			assertThrows<Exception> { service.createUser(username, password) }
		}
	}
}
