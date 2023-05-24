package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.email.EmailManager
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
            val emailService = EmailManager()
            val service = UserService(transactionManager, saltPasswordOperations, emailService)
            val password = "LKMSDOVCJ09Jouin09JN@"
            val email = "testSubject@email.com"
            service.createUser(email, password, role)
            val users = service.getAllUsers()
            //assertValues of every property of student with the expected values
            val retrievedUser = users.first { it.userInfo.email == email }
            assertTrue("User was created", retrievedUser.userInfo.email == email)
        }
    }

    @Test
    fun `create user with invalid email`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val saltPasswordOperations = SaltPasswordOperations(transactionManager)
            val service = UserService(transactionManager, saltPasswordOperations, EmailManager())
            val password = "LKMSDOVCJ09Jouin09JN@"
            val email = "testSubjectemail.com"
                val result = service.createUser(email, password, role)
                //verify that the result is Either.Left
                assertTrue("User was not created", result is Either.Left)
        }
    }


	@Test
	fun `create multiple users`() {
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
			val service = UserService(transactionManager,saltPasswordOperations, EmailManager())
			val password1 = "LKMSDOVCJ09Jouin09JN@1"
			val email1 = "sameemail@email.com"


			val password2 = "LKMSDOVCJ09Jouin09JN@"
			val email2 = "sameemail@email.com"

			service.createUser(email1, password1, role)
			val result = service.createUser(email2, password2, role)
			assertTrue("User was not created", result is Either.Left)
		}
	}

}
